package org.orbisgis.view.components.actions;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import javax.swing.Action;
import javax.swing.SwingUtilities;

/**
 * Register action to an ActionCommands
 * @author Nicolas Fortin
 */
public class MenuItemServiceTracker<TargetComponent> extends ServiceTracker<MenuItemService<TargetComponent>,Action> {
        private static enum ACTION_EVT { ADDED, MODIFIED, REMOVED};
        private ActionCommands ac;
        private BundleContext bc;
        private TargetComponent targetInstance;
        private static final Logger LOGGER = Logger.getLogger(MenuItemServiceTracker.class);

        /**
         * @param context Bundle context
         * @param serviceInterface The interface.class of the tracked service
         * @param actionCommands Where to put Tracked actions.
         */
        public MenuItemServiceTracker(BundleContext context,Class<MenuItemService<TargetComponent>> serviceInterface,ActionCommands actionCommands, TargetComponent targetInstance) {
                super(context,serviceInterface,null);
                ac = actionCommands;
                bc = context;
                this.targetInstance = targetInstance;
        }

        @Override
        public Action addingService(ServiceReference<MenuItemService<TargetComponent>> reference) {
                return processOperation(new SwingOperation(reference));
        }

        @Override
        public void modifiedService(ServiceReference<MenuItemService<TargetComponent>> reference, Action service) {
                processOperation(new SwingOperation(reference,service,ACTION_EVT.MODIFIED));
        }

        @Override
        public void removedService(ServiceReference<MenuItemService<TargetComponent>> reference, Action service) {
                processOperation(new SwingOperation(reference, service, ACTION_EVT.REMOVED));
        }


        private Action processOperation(SwingOperation operation) {
                if(SwingUtilities.isEventDispatchThread()) {
                        operation.run();
                } else {
                        try {
                                SwingUtilities.invokeAndWait(operation);
                        } catch(Exception ex) {
                                LOGGER.error(ex.getLocalizedMessage(),ex);
                        }
                }
                return operation.getAction();
        }
        /**
         * Guaranty that the Add/Update/Remove actions operations are done on swing thread.
         */
        private class SwingOperation implements Runnable {
                private ServiceReference<MenuItemService<TargetComponent>> reference;
                private Action action;
                private ACTION_EVT operation;

                private SwingOperation(ServiceReference<MenuItemService<TargetComponent>> reference, Action action, ACTION_EVT operation) {
                        this.reference = reference;
                        this.action = action;
                        this.operation = operation;
                }

                private SwingOperation(ServiceReference<MenuItemService<TargetComponent>> reference) {
                        this.reference = reference;
                        this.operation = ACTION_EVT.ADDED;
                }

                /**
                 * @return Action
                 */
                public Action getAction() {
                        return action;
                }

                @Override
                public void run() {
                        switch(operation) {
                                case REMOVED:
                                        ac.removeAction(action);
                                        break;
                                case MODIFIED: //Remove then Add
                                        ac.removeAction(action);
                                case ADDED:
                                        MenuItemService<TargetComponent> service = bc.getService(reference);
                                        action = service.getAction(targetInstance);
                                        ac.addAction(action);
                                        break;
                        }
                }
        }
}
