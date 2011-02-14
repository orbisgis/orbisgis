/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.label;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.PointLabelType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
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
        setLabel(new StyledLabel());
        rotation = new RealLiteral(0.0);

    }

    PointLabel(JAXBElement<PointLabelType> pl) throws InvalidStyle {
        super(pl);

        PointLabelType plt = pl.getValue();

        if (plt.getHorizontalAlignment() != null) {
            this.hAlign = HorizontalAlignment.fromString(SeParameterFactory.extractToken(plt.getHorizontalAlignment()));
        }

        if (plt.getVerticalAlignment() != null) {
            this.vAlign = VerticalAlignment.fromString(SeParameterFactory.extractToken(plt.getVerticalAlignment()));
        }

        if (plt.getExclusionZone() != null){
            setExclusionZone(ExclusionZone.createFromJAXBElement(plt.getExclusionZone()));
        }

        if (plt.getRotation() != null){
            setRotation(SeParameterFactory.createRealParameter(plt.getRotation()));
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
		if (this.rotation != null){
			this.rotation.setContext(RealParameterContext.realContext);
		}
    }

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt) throws ParameterException, IOException {
        RenderableGraphics l = this.label.getImage(sds, fid, selected, mt);

        // convert lineShape to a point
        // create AT according to rotation and exclusionZone

        double x = shp.getBounds2D().getCenterX() + l.getWidth()/2;
        double y = shp.getBounds2D().getCenterY() + l.getHeight()/2;
        
        if (this.exclusionZone != null){
            if (this.exclusionZone instanceof ExclusionRadius){
                double radius = ((ExclusionRadius)(this.exclusionZone)).getRadius().getValue(sds, fid);
                x += radius;
                y += radius;
            }
            else{
                x += ((ExclusionRectangle)(this.exclusionZone)).getX().getValue(sds, fid);
                y += ((ExclusionRectangle)(this.exclusionZone)).getY().getValue(sds, fid);
            }
        }

        g2.drawRenderedImage(l.createRendering(mt.getCurrentRenderContext()), AffineTransform.getTranslateInstance(x, y));
        
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

	@Override
	public String dependsOnFeature() {

        String result = "";
        if (label != null)
            result = label.dependsOnFeature();
        if (exclusionZone != null)
            result += " " + exclusionZone.dependsOnFeature();
        if (rotation != null)
            result += " " + rotation.dependsOnFeature();

        return result.trim();
	}
}
