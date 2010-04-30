/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.label;

import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;

/**
 *
 * @author maxence
 */
public abstract class ExclusionZone implements SymbolizerNode {

    @Override
    public Uom getUom() {
        if (uom == null)
            return parent.getUom();
        else
            return uom;


    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
    }

    protected SymbolizerNode parent;
    protected Uom uom;
}
