/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.AbstractUIPanel;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.progress.ProgressMonitor;

/**
 *
 * @author cleglaun
 */
public class OwsExportPanel extends AbstractUIPanel {

    private static final Dimension LABELS_DIMENSION = new Dimension(100, 20);
    private JPanel panel;
    private String validateInputMessage;
    private OwsFileExportListener owsFileExportListener;
    private OWSContextExporter owsFileExporter;
    private MapContext mapContext;
    
    private final JTextField txtTitle;
    private final JTextArea txtDescription;
    private final JTextField txtCrs;
    
    
    public OwsExportPanel(MapContext mapContext, OWSContextExporter owsFileExporter, 
            OwsFileExportListener owsFileExportListener) {
        validateInputMessage = "";
        this.owsFileExportListener = owsFileExportListener;
        this.owsFileExporter = owsFileExporter;
        this.mapContext = mapContext;
        
        
        txtTitle = new JTextField(15) {
            {
                setText("");
            }
        };
        txtDescription = new JTextArea(5, 15) {
            {
                setText("");
                setBorder(txtTitle.getBorder());
            }
        };
        txtCrs = new JTextField(10) {
            {
                setText("");
            }
        };
        
        final JLabel lblTitle = new JLabel(Names.LABEL_OWS_TITLE + ": ") {
            {
                setPreferredSize(LABELS_DIMENSION);
            }
        };
        final JLabel lblDescription = new JLabel(Names.LABEL_OWS_DESCRIPTION + ": ") {
            {
                setPreferredSize(LABELS_DIMENSION);
            }
        };
        final JLabel lblCrs = new JLabel(Names.LABEL_OWS_CRS + ": ") {
            {
                setPreferredSize(LABELS_DIMENSION);
            }
        };
        
        
        
        final JPanel pnlTitle = new JPanel() {
            {
                add(lblTitle);
                add(txtTitle);
            }
        };
        
        final JPanel pnlDescription = new JPanel() {
            {
                add(lblDescription);
                add(new JScrollPane(txtDescription));
            }
        };
        
        final JPanel pnlCrs = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
            {
                add(lblCrs);
                add(txtCrs);
            }
        };
        
        final JButton cmdExportAs = new JButton(Names.LABEL_OWS_EXPORT_AS_BUTTON) {
            {
                addActionListener(new ExportAsButtonActionListener());
            }
        };
        final JButton cmdExport = new JButton(Names.LABEL_OWS_EXPORT_BUTTON);
        
        final JPanel pnlCommands = new JPanel() {
            {
                add(cmdExportAs);
                add(cmdExport);
            }
        };

        
        
        panel = new JPanel() {
            {
                add(pnlTitle);
                add(pnlDescription);
                add(pnlCrs);
                add(pnlCommands);
            }
        };
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    }
    
    @Override
    public String getTitle() {
        return Names.LABEL_EXPORT_OWS_PROJECT;
    }

    @Override
    public String validateInput() {
        return validateInputMessage;
    }

    @Override
    public Component getComponent() {
        return panel;
    }
    
    private class ExportAsButtonActionListener implements ActionListener {

        private BackgroundManager bm;
        
        private ExportAsButtonActionListener() {
            this.bm = Services.getService(BackgroundManager.class);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            this.bm.backgroundOperation(new BackgroundJob() {

                @Override
                public void run(ProgressMonitor pm) {
                    OwsExportPanel.this.owsFileExporter.exportProjectAs(txtTitle.getText(), 
                            txtDescription.getText(), txtCrs.getText(), 
                            OwsExportPanel.this.mapContext.getBoundingBox(), 
                            OwsExportPanel.this.mapContext.getLayers());
                    
                    OwsExportPanel.this.owsFileExportListener.fireOwsFileExported();
                }

                @Override
                public String getTaskName() {
                    return "Exporting project...";
                }
            });
            

        }
    }
}
