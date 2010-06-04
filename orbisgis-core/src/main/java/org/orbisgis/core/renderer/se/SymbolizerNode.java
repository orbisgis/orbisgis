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
    Uom getUom(); // todo extract !

    SymbolizerNode getParent();

    void setParent(SymbolizerNode node);


    /*
     * this methos will be used to update all cached element.
     * It should be called after each style modification
     */
    //void update();
}
