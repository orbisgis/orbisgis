/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.util.List;
import javax.swing.ComboBoxModel;

/**
 *
 * @author cleglaun
 */
public interface OwsWorkspaceComboBoxModel extends ComboBoxModel {
    /**
     * Replace all workspaces with the content of the given list.
     * @param newWorkspaces The new workspaces to add to the model
     */
    public void updateAllItems(List<OwsWorkspace> newWorkspaces);
}
