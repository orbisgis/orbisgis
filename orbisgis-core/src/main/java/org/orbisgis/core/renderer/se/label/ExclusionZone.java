/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.label;

import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.ExclusionRadiusType;
import org.orbisgis.core.renderer.persistance.se.ExclusionRectangleType;
import org.orbisgis.core.renderer.persistance.se.ExclusionZoneType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;

/**
 *
 * @author maxence
 */
public abstract class ExclusionZone implements SymbolizerNode, UomNode {

    public abstract JAXBElement<? extends ExclusionZoneType> getJAXBElement();

    protected SymbolizerNode parent;
    protected Uom uom;

    public static ExclusionZone createFromJAXBElement(JAXBElement<? extends ExclusionZoneType> ezt) throws InvalidStyle {
        if (ezt.getDeclaredType() == ExclusionRadiusType.class){
            return new ExclusionRadius((JAXBElement<ExclusionRadiusType>)ezt);
        }
        else if (ezt.getDeclaredType() == ExclusionRectangleType.class){
            return new ExclusionRectangle((JAXBElement<ExclusionRectangleType>)ezt);
        }
        else
            return null;
    }

    @Override
    public Uom getUom() {
        if (uom == null)
            return parent.getUom();
        else
            return uom;
    }

	@Override
	public Uom getOwnUom(){
		return uom;
	}

	@Override
	public void setUom(Uom uom){
		this.uom = uom;
	}

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
    }

	public abstract String dependsOnFeature();
}
