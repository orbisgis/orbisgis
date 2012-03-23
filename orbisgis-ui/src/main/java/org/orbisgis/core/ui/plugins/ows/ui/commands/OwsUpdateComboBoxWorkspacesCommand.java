package org.orbisgis.core.ui.plugins.ows.ui.commands;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.orbisgis.core.ui.plugins.ows.remote.OwsService;
import org.orbisgis.core.ui.plugins.ows.remote.OwsWorkspace;
import org.orbisgis.core.ui.plugins.ows.OwsWorkspaceComboBoxModel;
import org.orbisgis.core.ui.plugins.ows.ui.OwsImportPanel;

/**
 *
 * @author cleglaun
 */
public class OwsUpdateComboBoxWorkspacesCommand implements OwsAsyncCommand {

    private final OwsService owsService;
    private final OwsWorkspaceComboBoxModel owsWorkspacesModel;
    
    public OwsUpdateComboBoxWorkspacesCommand(OwsService owsService, OwsWorkspaceComboBoxModel model) {
        this.owsService = owsService;
        this.owsWorkspacesModel = model;
    }
    
    @Override
    public void doJob() {
        GetAllOwsWorkspacesWorker getAllOwsWorkspacesWorker = new GetAllOwsWorkspacesWorker();
        getAllOwsWorkspacesWorker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                    try {
                        List<OwsWorkspace> owsWorkspaces = ((GetAllOwsWorkspacesWorker) evt.getSource()).get();
                        owsWorkspacesModel.updateAllItems(owsWorkspaces);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(OwsImportPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(OwsImportPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        getAllOwsWorkspacesWorker.execute();
    }
    
    
    private class GetAllOwsWorkspacesWorker extends SwingWorker<List<OwsWorkspace>, Object> {
        
        @Override
        protected List<OwsWorkspace> doInBackground() throws Exception {
            return owsService.getAllOwsWorkspaces();
        }
    }
    
    public static OwsAsyncCommand buildCommand(OwsService owsService, OwsWorkspaceComboBoxModel model) {
        return new OwsUpdateComboBoxWorkspacesCommand(owsService, model);
    }
}
