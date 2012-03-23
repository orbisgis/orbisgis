/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import org.orbisgis.core.ui.plugins.ows.remote.OwsWorkspace;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 * This model stands for a list of workspaces available in a data store. It is
 * used to show them in a combo box.
 * 
 * @author cleglaun
 */
public class OwsWorkspaceComboBoxModelImpl extends DefaultComboBoxModel
        implements OwsWorkspaceComboBoxModel {
    
    
    @Override
    public void updateAllItems(List<OwsWorkspace> newWorkspaces) {
        int oldSize = super.getSize();
        super.removeAllElements();
        super.fireIntervalRemoved(this, 0, oldSize);

        for (OwsWorkspace workspace : newWorkspaces) {
            super.addElement(workspace);
        }
        super.fireIntervalAdded(this, 0, super.getSize());
    }
}
