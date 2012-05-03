
package org.orbisgis.view.docking.internals;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.common.action.CRadioGroup;
import bibliothek.gui.dock.event.DropDownActionListener;
import java.util.Set;
import org.orbisgis.view.docking.actions.CToggleButton;

/**
 * CDropDownAction is not suitable for CRadioGroup, this listener 
 * will desactivate all CRadioAction when an action occur on a CAction
 */
public class ButtonGroupActionListener implements DropDownActionListener {
    private CRadioGroup radioGroup;

    public ButtonGroupActionListener(CRadioGroup radioGroup) {
        this.radioGroup = radioGroup;
    }
    
    public void selectionChanged(DropDownAction action, Set<Dockable> dockables, DockAction selection) {
        CToggleButton tb = new CToggleButton();
        radioGroup.add(tb);
        tb.setSelected(true);
        radioGroup.remove(tb);
    }
    
}
