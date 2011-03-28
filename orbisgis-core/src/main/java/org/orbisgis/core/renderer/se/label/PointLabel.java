/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.label;

import com.vividsolutions.jts.awt.PolygonShape;
import com.vividsolutions.jts.geom.Envelope;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;

import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import net.opengis.se._2_0.core.PointLabelType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 *
 * @author maxence
 */
public final class PointLabel extends Label {

    private RealParameter rotation;
    private ExclusionZone exclusionZone;

    /**
     *
     */
    public PointLabel() {
        setLabel(new StyledText());
        rotation = new RealLiteral(0.0);

    }

    public PointLabel(PointLabelType plt) throws InvalidStyle {
        super(plt);

        if (plt.getHorizontalAlignment() != null) {
            this.hAlign = HorizontalAlignment.fromString(SeParameterFactory.extractToken(plt.getHorizontalAlignment()));
        }

        if (plt.getVerticalAlignment() != null) {
            this.vAlign = VerticalAlignment.fromString(SeParameterFactory.extractToken(plt.getVerticalAlignment()));
        }

        if (plt.getExclusionZone() != null) {
            setExclusionZone(ExclusionZone.createFromJAXBElement(plt.getExclusionZone()));
        }

        if (plt.getRotation() != null) {
            setRotation(SeParameterFactory.createRealParameter(plt.getRotation()));
        }


    }

    PointLabel(JAXBElement<PointLabelType> pl) throws InvalidStyle {
        this(pl.getValue());
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
        if (this.rotation != null) {
            this.rotation.setContext(RealParameterContext.realContext);
        }
    }

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid,
            Shape shp, boolean selected, MapTransform mt, RenderContext perm)
            throws ParameterException, IOException {
        double x;
        double y;

        // TODO RenderPermission !
        double deltaX = 0;
        double deltaY = 0;

        if (shp instanceof PolygonShape) {
            //shp = perm.getValidShape(shp, 0);
            // TODO Validate !
            x = shp.getBounds2D().getCenterX();
            y = shp.getBounds2D().getCenterY();
        } else {
            Rectangle2D bounds = this.label.getBounds(g2, sds, fid, mt);
            x = shp.getBounds2D().getCenterX() + bounds.getWidth() / 2;
            y = shp.getBounds2D().getCenterY() - bounds.getHeight() / 2;

            if (this.exclusionZone != null) {
                if (this.exclusionZone instanceof ExclusionRadius) {
                    double radius = ((ExclusionRadius) (this.exclusionZone)).getRadius().getValue(sds, fid);
                    radius = Uom.toPixel(radius, getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                    deltaX = radius;
                    deltaY = radius;
                } else {
                    deltaX = ((ExclusionRectangle) (this.exclusionZone)).getX().getValue(sds, fid);
                    deltaY = ((ExclusionRectangle) (this.exclusionZone)).getY().getValue(sds, fid);

                    deltaX = Uom.toPixel(deltaX, getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                    deltaY = Uom.toPixel(deltaY, getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                }
            }
        }

        AffineTransform at = AffineTransform.getTranslateInstance(x, y);

        label.draw(g2, sds, fid, selected, mt, at, perm);
    }

    @Override
    public JAXBElement<PointLabelType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createPointLabel(getPointLabelType());
    }

    public PointLabelType getPointLabelType() {
        PointLabelType pl = new PointLabelType();

        if (uom != null) {
            pl.setUom(uom.toString());
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
            pl.setStyledText(label.getJAXBType());
        }
        return pl;
    }

    @Override
    public String dependsOnFeature() {

        String result = "";
        if (label != null) {
            result = label.dependsOnFeature();
        }
        if (exclusionZone != null) {
            result += " " + exclusionZone.dependsOnFeature();
        }
        if (rotation != null) {
            result += " " + rotation.dependsOnFeature();
        }

        return result.trim();
    }
}
