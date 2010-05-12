/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.label;

import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.ExclusionZoneType;
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

    public abstract JAXBElement<? extends ExclusionZoneType> getJAXBInstance();

    protected SymbolizerNode parent;
    protected Uom uom;
}
