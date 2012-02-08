/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBElement;
import net.opengis.ows_context.OWSContextType;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.sif.AbstractUIPanel;
import org.orbisgis.progress.ProgressMonitor;
import org.w3c.dom.Node;

/**
 * Display a panel which allows the user to select an ows context file
 * @author CŽdric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public class OwsImportPanel extends AbstractUIPanel {

    private String validateInputMessage;
    private JButton cmdImportOwsContext;
    private JPanel panel;
    private OwsFileImportListener owsFileImportListener;
    private OwsService owsService;
    private JList list;
    private OwsFileListModel listModel;
    
    public OwsImportPanel(OwsFileImportListener owsFileImportListener, 
            OwsService owsService) {
        this.owsFileImportListener = owsFileImportListener;
        this.owsService = owsService;
        this.validateInputMessage = "";
        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout());
        this.cmdImportOwsContext = new JButton("Import OWS Context file");
        this.cmdImportOwsContext.addActionListener(new ImportButtonActionListener());
        
        this.listModel = new OwsFileListModelImpl();
        this.list = new JList(this.listModel);

        this.list.setCellRenderer(new OwsFileBasicListRenderer());

        JScrollPane listScroller = new JScrollPane(this.list);
        listScroller.setPreferredSize(new Dimension(200, 200));
        listScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel panelTop = new JPanel();
        panelTop.add(new JLabel("OWC File path: "));
        panelTop.add(listScroller);
        
        JPanel panelBottom = new JPanel();
        panelBottom.add(this.cmdImportOwsContext);
        
        this.panel.add(panelTop, BorderLayout.CENTER);
        this.panel.add(this.cmdImportOwsContext, BorderLayout.SOUTH);
        
        updateListModel();
    }
    
    @Override
    public String getTitle() {
        return "Import an OWS file";
    }

    @Override
    public String validateInput() {
        return this.validateInputMessage;
    }

    @Override
    public Component getComponent() {
        return panel;
    }

    public OwsFileImportListener getOwsFileImportListener() {
        return owsFileImportListener;
    }

    public void setOwsFileImportListener(OwsFileImportListener owsFileImportListener) {
        this.owsFileImportListener = owsFileImportListener;
    }
    
    private void updateListModel() {

        GetAllOwsFilesWorker worker = new GetAllOwsFilesWorker();
        worker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {

                    try {
                        List<OwsFileBasic> owsFiles = ((GetAllOwsFilesWorker) evt.getSource()).get();
                        listModel.updateAllItems(owsFiles);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(OwsImportPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(OwsImportPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        worker.execute();
    }
    
    private class ImportButtonActionListener implements ActionListener {

        private BackgroundManager bm;
        private ImportButtonActionListener() {
            this.bm = Services.getService(BackgroundManager.class);
        }
        
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (list.getSelectedIndex() > 0) {
                
                final OWSContextImporter importer = new OWSContextImporterImpl();
                
                this.bm.backgroundOperation(new BackgroundJob() {

                    @Override
                    public void run(ProgressMonitor pm) {
                        Node owsContextNode = 
                                OwsImportPanel.this.owsService.getOwsFile(((OwsFileBasic)list.getSelectedValue()).getId());

                        JAXBElement<OWSContextType> owsContext = importer.unmarshallOwsContext(owsContextNode);
                        OwsImportPanel.this.owsFileImportListener.fireOwsExtracted(owsContext);
                    }

                    @Override
                    public String getTaskName() {
                        return "Extracting OWS Context file";
                        //return I18N.getString("orbisgis.org.orbisgis.zoomToLayer"); //$NON-NLS-1$
                    }
                });

            }
            else {
                validateInputMessage = "You must select a file";
                OwsImportPanel.this.validateInput();
            }
        }
    } 
    
    private class OwsFileBasicListRenderer extends DefaultListCellRenderer {

        public OwsFileBasicListRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            OwsFileBasic owsFile = (OwsFileBasic) value;
            setText(owsFile.getId() + " - " + owsFile.getOwsTitle());
            
            return this;
        }
    }
    
    private class GetAllOwsFilesWorker extends SwingWorker<List<OwsFileBasic>, Object> {
        @Override
        protected List<OwsFileBasic> doInBackground() throws Exception {
            return owsService.getAllOwsFiles();
        }
    }
}
