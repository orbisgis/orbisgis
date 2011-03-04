package org.orbisgis.core.renderer.se.common;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;

import java.io.IOException;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.Services;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.HaloType;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.utils.I18N;

public final class Halo implements SymbolizerNode, UomNode, FillNode {

    public Halo() {
        setFill(new SolidFill());
        setRadius(new RealLiteral(5));
    }

    public Halo(Fill fill, RealParameter radius) {
        this.fill = fill;
        this.radius = radius;
    }

    public Halo(HaloType halo) throws InvalidStyle {
        if (halo.getFill() != null) {
            this.setFill(Fill.createFromJAXBElement(halo.getFill()));
        }

        if (halo.getRadius() != null) {
            this.setRadius(SeParameterFactory.createRealParameter(halo.getRadius()));
        }

        if (halo.getUnitOfMeasure() != null) {
            this.setUom(Uom.fromOgcURN(halo.getUnitOfMeasure()));
        }
    }

    @Override
    public Uom getUom() {
        if (uom == null) {
            return getParent().getUom();
        } else {
            return uom;
        }
    }

    @Override
    public Uom getOwnUom() {
        return uom;
    }

    @Override
    public void setUom(Uom uom) {
        this.uom = uom;
    }

    public void setRadius(RealParameter radius) {
        this.radius = radius;
        if (this.radius != null) {
            this.radius.setContext(RealParameterContext.realContext);
        }
    }

    @Override
    public void setFill(Fill fill) {
        this.fill = fill;
        fill.setParent(this);
    }

    @Override
    public Fill getFill() {
        return fill;
    }

    public RealParameter getRadius() {
        return radius;
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
    }

    /**
     * Return the halo radius in pixel
     * @param sds
     * @param fid
     * @param mt
     * @return
     * @throws ParameterException
     */
    public double getHaloRadius(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException {
        return Uom.toPixel(radius.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null); // TODO 100%
    }

    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, boolean selected, Shape shp, MapTransform mt, boolean substract) throws ParameterException, IOException {
        if (radius != null && fill != null) {
            double r = this.getHaloRadius(sds, fid, mt);
            Area initialArea = new Area(shp);

            if (r > 0.0) {
                for (Shape halo : ShapeHelper.perpendicularOffset(shp, r)) {

                    // TODO only fill  (halo - shp) !!!
                    if (halo != null) {
                        Area aHalo = new Area(halo);

                        if (substract){
                            aHalo.subtract(initialArea);
                        }

                        fill.draw(g2, sds, fid, aHalo, selected, mt);

                    } else {
                        Services.getErrorManager().error(I18N.getString("orbisgis.org.orbisgis.renderer.cannotCreatePerpendicularOffset"));
                    }
                }
            }
        }
    }

    public String dependsOnFeature() {

        return (radius.dependsOnFeature() + " " + fill.dependsOnFeature()).trim();
    }

    public HaloType getJAXBType() {
        HaloType h = new HaloType();

        if (fill != null) {
            h.setFill(fill.getJAXBElement());
        }

        if (radius != null) {
            h.setRadius(radius.getJAXBParameterValueType());
        }

        if (uom != null) {
            h.setUnitOfMeasure(uom.toURN());
        }

        return h;
    }
    private Uom uom;
    private RealParameter radius;
    private Fill fill;
    private SymbolizerNode parent;
}
