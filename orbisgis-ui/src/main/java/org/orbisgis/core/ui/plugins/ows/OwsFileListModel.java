/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.util.List;
import javax.swing.ListModel;

/**
 *
 * @author cleglaun
 */
public interface OwsFileListModel extends ListModel {
    
    /**
     * Replace all items with the content of the given list.
     * @param newItems The new items to add to the model
     */
    public void updateAllItems(List<OwsFileBasic> newItems);
    
    /**
     * Orders the list of ows files.
     */
    public void orderByOwsTitleAsc();
    
}
