/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import org.orbisgis.core.ui.plugins.ows.ui.OwsUpdateOwsFilesListCommand;
import org.orbisgis.core.ui.plugins.ows.ui.OwsUpdateComboBoxWorkspacesCommand;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.bind.JAXBElement;
import net.opengis.ows_context.OWSContextType;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.sif.AbstractUIPanel;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.progress.ProgressMonitor;
import org.w3c.dom.Node;

/**
 * Display a panel which allows the user to select an ows context file
 * @author CŽdric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public class OwsImportPanel extends AbstractUIPanel {

    private static final Dimension LABELS_DIMENSION = new Dimension(100, 20);
    private static final OwsWorkspace DEFAULT_WORKSPACE = new OwsWorkspace("default");
    
    private String validateInputMessage;
    private JButton cmdImportOwsContext;
    private JPanel panel;
    private OwsFileImportListener owsFileImportListener;
    private final OwsService owsService;
    private JList listOwsProjects;
    private final JComboBox cmbWorkspaces;
    private OwsFileListModel owsProjectsModel;
    private OwsWorkspaceComboBoxModel owsWorkspacesModel;
    
    public OwsImportPanel(OwsFileImportListener owsFileImportListener, 
            OwsService owsService) {
        this.owsFileImportListener = owsFileImportListener;
        this.owsService = owsService;
        this.validateInputMessage = "";
        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout());
        this.cmdImportOwsContext = new JButton(Names.LABEL_OWS_IMPORT_BUTTON);
        this.cmdImportOwsContext.addActionListener(new ImportButtonActionListener());
        
        this.owsProjectsModel = new OwsFileListModelImpl();
        this.listOwsProjects = new JList(this.owsProjectsModel);

        this.listOwsProjects.setCellRenderer(new OwsFileBasicListRenderer());

        JScrollPane listScroller = new JScrollPane(this.listOwsProjects);
        listScroller.setPreferredSize(new Dimension(200, 200));
        listScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        this.owsWorkspacesModel = new OwsWorkspaceComboBoxModelImpl();
        this.cmbWorkspaces = new JComboBox(owsWorkspacesModel);
        this.cmbWorkspaces.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                OwsWorkspace selectedWorkspace = (OwsWorkspace) cmbWorkspaces.getSelectedItem();
                updateOwsFilesListModel(selectedWorkspace);
            }
        });
        
        JPanel panelTop = new JPanel() {
            {
                add(new JLabel(Names.LABEL_OWS_WORKSPACE + ": ") {
                    {
                        setPreferredSize(LABELS_DIMENSION);
                    }
                });
                add(cmbWorkspaces);
            }
        };
        
        JPanel panelMiddle = new JPanel();
        panelMiddle.add(new JLabel(Names.LABEL_OWS_PROJECTS));
        panelMiddle.add(listScroller);
        
        JPanel panelBottom = new JPanel();
        panelBottom.add(this.cmdImportOwsContext);
        
     
        this.panel.add(panelTop, BorderLayout.NORTH);
        this.panel.add(panelMiddle, BorderLayout.CENTER);
        this.panel.add(this.cmdImportOwsContext, BorderLayout.SOUTH);
        
        updateOwsWorkspacesComboBoxModel();
        updateOwsFilesListModel(DEFAULT_WORKSPACE);
    }
    
    @Override
    public String getTitle() {
        return Names.LABEL_OWS_IMPORT_PROJECT;
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
    
    /**
     * Executes a swing asynchronous task that updates the ows contexts list
     * belonging to the given workspace.
     * @param workspace An existing workspace
     */
    private void updateOwsFilesListModel(OwsWorkspace workspace) {
        OwsUpdateOwsFilesListCommand.buildCommand(owsService, owsProjectsModel, workspace).doJob();
    }
    
    /**
     * Updates the workspaces combo box model.
     */
    private void updateOwsWorkspacesComboBoxModel() {
        OwsUpdateComboBoxWorkspacesCommand.buildCommand(owsService, owsWorkspacesModel).doJob();
    }
    
    private class ImportButtonActionListener implements ActionListener {

        private BackgroundManager bm;
        private ImportButtonActionListener() {
            this.bm = Services.getService(BackgroundManager.class);
        }
        
        @Override
        public void actionPerformed(ActionEvent ae) {
            validateInputMessage = "";
            if (listOwsProjects.getSelectedIndex() >= 0) {

                final OWSContextImporter importer = new OWSContextImporterImpl();

                this.bm.backgroundOperation(new BackgroundJob() {

                    @Override
                    public void run(ProgressMonitor pm) {
                        
                        OwsWorkspace selectedWorkspace = (OwsWorkspace) cmbWorkspaces.getSelectedItem();
                        Node owsContextNode =
                                OwsImportPanel.this.owsService.getOwsFile(selectedWorkspace,
                                ((OwsFileBasic) listOwsProjects.getSelectedValue()).getId());

                        JAXBElement<OWSContextType> owsContext = importer.unmarshallOwsContext(owsContextNode);
                        OwsImportPanel.this.owsFileImportListener.fireOwsExtracted(owsContext);
                    }

                    @Override
                    public String getTaskName() {
                        return Names.LABEL_OWS_EXTRACTING_CONTEXT_STATUS;
                        //return I18N.getString("orbisgis.org.orbisgis.zoomToLayer"); //$NON-NLS-1$
                    }
                });

            } else {
                validateInputMessage = Names.LABEL_OWS_MUST_SELECT_FILE;
                OwsImportPanel.this.validateInput();
            }
        }
    } 
    
    /**
     * Item renderer for the list showing the ows context files.
     */
    private class OwsFileBasicListRenderer extends DefaultListCellRenderer {

        public OwsFileBasicListRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            OwsFileBasic owsFile = (OwsFileBasic) value;
            setText(owsFile.getId() + " - " + owsFile.getOwsTitle());

            return this;
        }
    }
}
