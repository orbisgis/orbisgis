package org.orbisgis.core.renderer.se.common;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class Halo implements SymbolizerNode {

    public Halo(Fill fill, RealParameter radius) {
        this.fill = fill;
        this.radius = radius;
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

    public void draw(Graphics2D g2, Shape shp, DataSource ds, int fid) throws ParameterException, IOException {
        if (radius != null && fill != null) {
            double r = radius.getValue(ds, fid);

            if (r > 0.0) {
                r = Uom.toPixel(r, getUom(), 96, 25000); // DPI & Scale

                Shape haloShp = shp; // TODO poffset !!

                fill.draw(g2, haloShp, ds, fid);
            }
        }
    }
    private Uom uom;
    private RealParameter radius;
    private Fill fill;
    private SymbolizerNode parent;
}
