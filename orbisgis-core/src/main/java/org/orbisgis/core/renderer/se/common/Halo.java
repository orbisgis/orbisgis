package org.orbisgis.core.renderer.se.common;

import java.awt.Graphics2D;
import java.awt.Shape;

import java.io.IOException;

import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.HaloType;

import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public final class Halo implements SymbolizerNode {

    public Halo() {
        setFill(new SolidFill());
        setRadius(new RealLiteral(5));
    }

    public Halo(Fill fill, RealParameter radius) {
        this.fill = fill;
        this.radius = radius;
    }

    public Halo(HaloType halo) {
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

    public void setUom(Uom uom) {
        this.uom = uom;
    }

    public void setRadius(RealParameter radius) {
        this.radius = radius;
    }

    public void setFill(Fill fill) {
        this.fill = fill;
        fill.setParent(this);
    }

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

    public void draw(Graphics2D g2, Shape shp, Feature feat, MapTransform mt) throws ParameterException, IOException {
        if (radius != null && fill != null) {
            double r = radius.getValue(feat);

            if (r > 0.0) {
                r = Uom.toPixel(r, getUom(), mt.getDpi(), mt.getScaleDenominator(), 0.0); // TODO  DPI & Scale

                Shape haloShp = shp; // TODO poffset !!

                fill.draw(g2, haloShp, feat, false, mt);

                throw new UnsupportedOperationException("Not supported yet. Need PerpendiularOffset");
            }
        }
    }

    public boolean dependsOnFeature() {
        if (this.fill.dependsOnFeature()){
            return true;
        }
        if (this.radius.dependsOnFeature()){
            return true;
        }
        return false;
    }

    public HaloType getJAXBType() {
        HaloType h = new HaloType();

        h.setFill(fill.getJAXBElement());
        h.setRadius(radius.getJAXBParameterValueType());
        h.setUnitOfMeasure(uom.toURN());

        return h;
    }
    private Uom uom;
    private RealParameter radius;
    private Fill fill;
    private SymbolizerNode parent;
}
