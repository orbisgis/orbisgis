/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import javax.swing.JButton;
import org.orbisgis.core.sif.SIFDialog;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;

/**
 *
 * @author cleglaun
 */
public class OwsExportPlugin extends AbstractPlugIn {

    private SIFDialog exportOwsDialog;
    private JButton btn;
    
    public OwsExportPlugin() {
        this.btn = new JButton(Names.BUTTON_EXPORT_OWC_TITLE);
    }
    
    @Override
    public void initialize(PlugInContext context) throws Exception {
        WorkbenchContext wbcontext = context.getWorkbenchContext();
        wbcontext.getWorkbench().getFrame().getMainToolBar().addPlugIn(this, btn, context);
    }

    @Override
    public boolean execute(PlugInContext context) throws Exception {
        UIPanel panel;

        panel = new OwsExportPanel(context.getMapContext(), 
                new OWSContextExporterImpl(new OwsServiceImpl()), 
                new OwsFileExportListenerImpl(), new OwsServiceImpl());


        exportOwsDialog = UIFactory.getSimpleDialog(panel);
        exportOwsDialog.pack();
        exportOwsDialog.setVisible(true);
            
        return true;
    }

    @Override
    public boolean isEnabled() {
        boolean isEnabled = getPlugInContext().getMapContext() != null;
        btn.setEnabled(isEnabled);
        return isEnabled;
    }
    
    public class OwsFileExportListenerImpl implements OwsFileExportListener {

        public OwsFileExportListenerImpl() {
        }
        
        @Override
        public void fireOwsFileExported() {
            exportOwsDialog.setVisible(false);
        }
        
    }
}
