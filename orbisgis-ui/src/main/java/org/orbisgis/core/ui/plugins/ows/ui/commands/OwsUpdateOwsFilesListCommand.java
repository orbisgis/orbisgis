package org.orbisgis.core.ui.plugins.ows.ui.commands;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.orbisgis.core.ui.plugins.ows.remote.OwsFileBasic;
import org.orbisgis.core.ui.plugins.ows.remote.OwsService;
import org.orbisgis.core.ui.plugins.ows.remote.OwsWorkspace;
import org.orbisgis.core.ui.plugins.ows.ui.OwsFileListModel;
import org.orbisgis.core.ui.plugins.ows.ui.OwsImportPanel;

/**
 *
 * @author cleglaun
 */
public class OwsUpdateOwsFilesListCommand implements OwsAsyncCommand {

    private final OwsService owsService;
    private final OwsFileListModel owsProjectsModel;
    private final OwsWorkspace workspace;

    public OwsUpdateOwsFilesListCommand(OwsService owsService, OwsFileListModel model, OwsWorkspace workspace) {
        this.owsService = owsService;
        this.owsProjectsModel = model;
        this.workspace = workspace;
    }
    
    
    @Override
    public void doJob() {
        GetAllOwsFilesWorker getAllOwsFilesWorker = new GetAllOwsFilesWorker(workspace);
        getAllOwsFilesWorker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {

                    try {
                        List<OwsFileBasic> owsFiles = ((GetAllOwsFilesWorker) evt.getSource()).get();
                        owsProjectsModel.updateAllItems(owsFiles);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(OwsImportPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(OwsImportPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        getAllOwsFilesWorker.execute();
    }
    
    private class GetAllOwsFilesWorker extends SwingWorker<List<OwsFileBasic>, Object> {
        
        private final OwsWorkspace workspace;
        
        public GetAllOwsFilesWorker(OwsWorkspace workspace) {
            this.workspace = workspace;
        }
        
        @Override
        protected List<OwsFileBasic> doInBackground() throws Exception {
            return owsService.getAllOwsFiles(workspace);
        }
    }
    
    public static OwsAsyncCommand buildCommand(OwsService owsService, OwsFileListModel model, 
            OwsWorkspace workspace) {
        return new OwsUpdateOwsFilesListCommand(owsService, model, workspace);
    }
    
}
