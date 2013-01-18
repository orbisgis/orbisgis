package org.orbisgis.omanager.ui;

import javax.swing.ListModel;

/**
 * @author Nicolas Fortin
 */
public interface ItemFilter<SubModel extends ListModel> {

    /**
     * @param model Sub ListModel instance
     * @param elementId Element id of sub list model
     * @return True to show the item, false to hide it.
     */
    boolean include(SubModel model,int elementId);
}
