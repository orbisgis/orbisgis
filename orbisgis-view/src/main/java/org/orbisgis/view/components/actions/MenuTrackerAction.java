package org.orbisgis.view.components.actions;

import javax.swing.Action;
import java.util.List;

/**
 * In order to release the action listeners from the targetComponent,
 * MenuTrackerAction store the actions created by the ActionFactoryService.
 * @author Nicolas Fortin
 */
public class MenuTrackerAction<TargetComponent> {
        private ActionFactoryService<TargetComponent> actionFactory;
        private List<Action> actions;

        public MenuTrackerAction(ActionFactoryService<TargetComponent> actionFactory, List<Action> actions) {
                this.actionFactory = actionFactory;
                this.actions = actions;
        }

        public ActionFactoryService<TargetComponent> getActionFactory() {
                return actionFactory;
        }

        public List<Action> getActions() {
                return actions;
        }
}
