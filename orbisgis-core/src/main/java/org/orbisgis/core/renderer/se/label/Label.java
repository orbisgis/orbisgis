/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.label;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.persistance.se.LabelType;

import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.persistance.se.LineLabelType;
import org.orbisgis.core.renderer.persistance.se.PointLabelType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 */
public abstract class Label implements SymbolizerNode, UomNode{

    public enum HorizontalAlignment {

        LEFT, CENTER, RIGHT;

        public static HorizontalAlignment fromString(String token) {
            if (token.equals("left"))
                return LEFT;
            
            if (token.equals("center"))
                return CENTER;

            return RIGHT; // default value
        }
    }

    public enum VerticalAlignment {

        TOP, MIDDLE, BASELINE, BOTTOM;

        public static VerticalAlignment fromString(String token) {
            if (token.equals("bottom"))
                return BOTTOM;
            if (token.equals("middle"))
                return MIDDLE;
            if (token.equals("baseline"))
                return BASELINE;
             return TOP;
        }
    }

	protected SymbolizerNode parent;
    protected Uom uom;
    protected StyledLabel label;
    protected HorizontalAlignment hAlign;
    protected VerticalAlignment vAlign;

    public static Label createLabelFromJAXBElement(JAXBElement<? extends LabelType> l) throws InvalidStyle {
        if (l.getDeclaredType() == PointLabelType.class) {
            return new PointLabel((JAXBElement<PointLabelType>)l);
        } else if (l.getDeclaredType() == LineLabelType.class) {
            return new LineLabel((JAXBElement<LineLabelType>)l);
        }

        return null;
    }

    protected Label(){
    }

	protected Label(LabelType t) throws InvalidStyle{
        if (t.getUnitOfMeasure() != null) {
            this.uom = Uom.fromOgcURN(t.getUnitOfMeasure());
        }

        if (t.getStyledLabel() != null) {
            this.setLabel(new StyledLabel(t.getStyledLabel()));
        }
	}

    protected Label(JAXBElement<? extends LabelType> l) throws InvalidStyle {
		this(l.getValue());
  }

	@Override
	public Uom getOwnUom(){
		return uom;
	}

    @Override
    public Uom getUom() {
        if (uom != null) {
            return uom;
        } else {
            return parent.getUom();
        }
    }

	@Override
    public void setUom(Uom uom) {
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

    public StyledLabel getLabel() {
        return label;
    }

    public final void setLabel(StyledLabel label) {
        this.label = label;
        label.setParent(this);
    }

    public abstract void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt) throws ParameterException, IOException;

    public abstract JAXBElement<? extends LabelType> getJAXBElement();

    public abstract String dependsOnFeature();
}
