/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.label;

import java.awt.Graphics2D;

import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.liteShape.LiteShape;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.PointLabelType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 * @todo implements
 */
public class PointLabel extends Label {

    /**
     *
     */
    public PointLabel() {
        setLabel(new StyledLabel());
        rotation = new RealLiteral(0.0);

    }

    PointLabel(JAXBElement<PointLabelType> pl) {
        super(pl);

        PointLabelType plt = pl.getValue();

        if (plt.getExclusionZone() != null){
            this.exclusionZone = ExclusionZone.createFromJAXBElement(plt.getExclusionZone());
        }

        if (plt.getRotation() != null){
            this.rotation = SeParameterFactory.createRealParameter(plt.getRotation());
        }

    }

    public ExclusionZone getExclusionZone() {
        return exclusionZone;
    }

    public void setExclusionZone(ExclusionZone exclusionZone) {
        this.exclusionZone = exclusionZone;
        exclusionZone.setParent(this);
    }

    public RealParameter getRotation() {
        return rotation;
    }

    public void setRotation(RealParameter rotation) {
        this.rotation = rotation;
    }

    @Override
    public void draw(Graphics2D g2, LiteShape shp, DataSource ds, long fid) throws ParameterException, IOException {
        RenderableGraphics l = this.label.getImage(ds, fid);

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
    public JAXBElement<PointLabelType> getJAXBElement() {
        PointLabelType pl = new PointLabelType();

        if (uom != null) {
            pl.setUnitOfMeasure(uom.toString());
        }

        if (exclusionZone != null) {
            pl.setExclusionZone(exclusionZone.getJAXBElement());
        }

        if (hAlign != null) {
            ParameterValueType h = new ParameterValueType();
            h.getContent().add(hAlign.toString());
            pl.setHorizontalAlignment(h);
        }

        if (hAlign != null) {
            ParameterValueType v = new ParameterValueType();
            v.getContent().add(vAlign.toString());
            pl.setHorizontalAlignment(v);
        }

        if (rotation != null) {
            pl.setRotation(rotation.getJAXBParameterValueType());
        }

        if (label != null) {
            pl.setStyledLabel(label.getJAXBType());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createPointLabel(pl);
    }

    private RealParameter rotation;
    private ExclusionZone exclusionZone;
}
