package org.orbisgis.core.renderer.se.graphic;

import java.awt.Dimension;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class ViewBox implements SymbolizerNode {

    public ViewBox(RealParameter width) {
        this.x = width;
    }

    public void setWidth(RealParameter width) {
        x = width;
    }

    public RealParameter getWidth() {
        return x;
    }

    public void setHeight(RealParameter height) {
        y = height;
    }

    public RealParameter getHeight() {
        return y;
    }

    @Override
    public Uom getUom() {
        return parent.getUom();
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
    }

    public boolean dependsOnFeature() {
        return (x != null && x.dependsOnFeature()) || (y != null && y.dependsOnFeature());
    }

    /**
     * Return the final dimension described by this view box, in [px].
     * @param ds DataSource, i.e. the layer
     * @param fid feauture id
     * @param ratio requierd final ratio (if either width or height isn't defined)
     * @return
     * @throws ParameterException
     */
    public Dimension getDimension(DataSource ds, long fid, double ratio) throws ParameterException {
        double dx, dy;

        if (x != null && y != null) {
            dx = x.getValue(ds, fid);
            dy = y.getValue(ds, fid);
        } else if (x != null) {
            dx = x.getValue(ds, fid);
            dy = dx/ratio;
        } else if (y != null) {
            dy = y.getValue(ds, fid);
            dx = dy*ratio;
        } else { // nothing is defined => 10x10uom
            dx = 10.0;
            dy = 10.0;
        }
        
        dx = Uom.toPixel(dx, this.getUom(), MapEnv.getScaleDenominator()); // TODO DPI SCAPE !
        dy = Uom.toPixel(dy, this.getUom(), MapEnv.getScaleDenominator());

        return new Dimension((int)dx, (int)dy);
    }
    private SymbolizerNode parent;
    private RealParameter x;
    private RealParameter y;
}
