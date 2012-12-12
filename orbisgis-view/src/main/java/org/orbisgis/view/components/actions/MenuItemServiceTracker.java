package org.orbisgis.view.components.actions;

import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Register action to an ActionCommands
 * @author Nicolas Fortin
 */
//MenuItemServiceTracker<TargetComponent,ActionService extends MenuTrackerAction<TargetComponent>> extends ServiceTracker<ActionService,ActionService> {
public class MenuItemServiceTracker<TargetComponent,ActionService extends ActionFactoryService<TargetComponent>> extends ServiceTracker<ActionService,MenuTrackerAction<TargetComponent>> {
        private static enum ACTION_EVT { ADDED, MODIFIED, REMOVED}
        private ActionCommands ac;
        private BundleContext bc;
        private TargetComponent targetInstance;
        private static final Logger LOGGER = Logger.getLogger(MenuItemServiceTracker.class);

        /**
         * @param context Bundle context
         * @param serviceInterface The interface.class of the tracked service
         * @param actionCommands Where to put Tracked actions.
         */
        public MenuItemServiceTracker(BundleContext context,Class<ActionService> serviceInterface,ActionCommands actionCommands, TargetComponent targetInstance) {
                super(context,serviceInterface,null);
                ac = actionCommands;
                bc = context;
                this.targetInstance = targetInstance;
        }

        @Override
        public MenuTrackerAction<TargetComponent> addingService(ServiceReference<ActionService> reference) {
            return processOperation(new SwingOperation(reference));
        }

        @Override
        public void modifiedService(ServiceReference<ActionService> reference, MenuTrackerAction<TargetComponent> service) {
            processOperation(new SwingOperation(reference,service,ACTION_EVT.MODIFIED));
        }

        @Override
        public void removedService(ServiceReference<ActionService> reference, MenuTrackerAction<TargetComponent> service) {
            processOperation(new SwingOperation(reference, service, ACTION_EVT.REMOVED));
        }

        private MenuTrackerAction<TargetComponent> processOperation(SwingOperation operation) {
                if(SwingUtilities.isEventDispatchThread()) {
                        operation.run();
                } else {
                        try {
                                SwingUtilities.invokeAndWait(operation);
                        } catch(Exception ex) {
                                LOGGER.error(ex.getLocalizedMessage(),ex);
                        }
                }
                return operation.getService();
        }
        /**
         * Guaranty that the Add/Update/Remove actions operations are done on swing thread.
         */
        private class SwingOperation implements Runnable {
                private ServiceReference<ActionService> reference;
                private MenuTrackerAction<TargetComponent> generatedActions;
                private ACTION_EVT operation;

                private SwingOperation(ServiceReference<ActionService> reference, MenuTrackerAction<TargetComponent> action, ACTION_EVT operation) {
                        this.reference = reference;
                        this.generatedActions = action;
                        this.operation = operation;
                }

                private SwingOperation(ServiceReference<ActionService> reference) {
                        this.reference = reference;
                        this.operation = ACTION_EVT.ADDED;
                }

                public MenuTrackerAction<TargetComponent> getService() {
                        return generatedActions;
                }
                private void removeService() {
                        for(Action action : generatedActions.getActions()) {
                                ac.removeAction(action);
                        }
                        generatedActions.getActionFactory().disposeActions(targetInstance,generatedActions.getActions());
                }
                @Override
                public void run() {
                        switch(operation) {
                                case REMOVED:
                                        removeService();
                                        break;
                                case MODIFIED: //Remove then Add
                                        removeService();
                                case ADDED:
                                        ActionService service = bc.getService(reference);
                                        generatedActions = new MenuTrackerAction<TargetComponent>(service,service.createActions(targetInstance));
                                        for(Action action : generatedActions.getActions()) {
                                                ac.addAction(action);
                                        }
                                        break;
                        }
                }
        }
}
