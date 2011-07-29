package org.orbisgis.core.renderer.se;

import org.orbisgis.core.renderer.se.common.Uom;

/**
 * SymbolizerNode allow to browse the styling tree
 * It's mainly used to fetch the nearest Uom definition of any element
 *
 * @todo extract getUom() and add void update(), then every element should implement this (even parameters)
 *
 * @author maxence
 */
public interface SymbolizerNode{
    /**
     * Get the unit of measure associated with the current node.
     * @return 
     */
    Uom getUom(); // todo extract !

    /**
     * get the parent of this current <code>SymbolizerNode</code>
     * @return 
     */
    SymbolizerNode getParent();

    /**
     * Set the parent of this <code>SymbolizerNode</code>
     * @param node 
     */
    void setParent(SymbolizerNode node);


    /*
     * this methos will be used to update all cached element.
     * It should be called after each style modification
     */
    //void update();
}
