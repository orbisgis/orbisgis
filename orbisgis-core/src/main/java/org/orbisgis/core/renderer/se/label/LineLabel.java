/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.label;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;

import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.persistance.se.LineLabelType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

/**
 *
 * @author maxence
 * @todo implements
 */
public class LineLabel extends Label {

    public LineLabel(LineLabelType t) throws InvalidStyle {
		super(t);

        if (t.getHorizontalAlignment() != null) {
            this.hAlign = HorizontalAlignment.fromString(SeParameterFactory.extractToken(t.getHorizontalAlignment()));
        }

        if (t.getVerticalAlignment() != null) {
            this.vAlign = VerticalAlignment.fromString(SeParameterFactory.extractToken(t.getVerticalAlignment()));
        }
	}

    public LineLabel(JAXBElement<LineLabelType> l) throws InvalidStyle {
		this(l.getValue());
    }

    /**
     *
     */
    @Override
    public void draw(Graphics2D g2, Shape shp, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException {
        
        RenderableGraphics l = this.label.getImage(feat, selected, mt);


        // convert lineShape to a point
        // create AT according to rotation and exclusionZone

        /*g2.drawImage(label,
                     new AffineTransformOp(AT,
                                           AffineTransformOp.TYPE_BICUBIC),
                      -label.getWidth() / 2,
                      -label.getHeight() / 2);

         */

    }


    @Override
    public JAXBElement<LineLabelType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createLineLabel(this.getJAXBType());
	}

    public LineLabelType getJAXBType() {
        LineLabelType ll = new LineLabelType();

        if (uom != null) {
            ll.setUnitOfMeasure(uom.toString());
        }

        if (hAlign != null) {
            ParameterValueType h = new ParameterValueType();
            h.getContent().add(hAlign.toString());
            ll.setHorizontalAlignment(h);
        }

        if (hAlign != null) {
            ParameterValueType v = new ParameterValueType();
            v.getContent().add(vAlign.toString());
            ll.setHorizontalAlignment(v);
        }

        if (label != null) {
            ll.setStyledLabel(label.getJAXBType());
        }

		return ll;
    }

	@Override
	public boolean dependsOnFeature() {
		return label != null && label.dependsOnFeature();
	}
}
