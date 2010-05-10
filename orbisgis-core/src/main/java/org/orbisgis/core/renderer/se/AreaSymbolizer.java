package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.io.IOException;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

import org.orbisgis.core.renderer.liteShape.LiteShape;

import org.orbisgis.core.renderer.se.common.Uom;

import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;

public class AreaSymbolizer extends VectorSymbolizer {

    public AreaSymbolizer() {
        super();
        uom = Uom.MM;
        fill = new SolidFill();
        fill.setParent(this);
        stroke = new PenStroke();
        stroke.setParent(this);
    }

    public void setStroke(Stroke stroke) {
        if (stroke != null) {
            stroke.setParent(this);
        }
        this.stroke = stroke;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setFill(Fill fill) {
        if (fill != null) {
            fill.setParent(this);
        }
        this.fill = fill;
    }

    public Fill getFill() {
        return fill;
    }

    public RealParameter getPerpendicularOffset() {
        return perpendicularOffset;
    }

    public void setPerpendicularOffset(RealParameter perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }

    /**
     *
     * @param g2
     * @param sds
     * @param fid
     * @throws ParameterException
     * @throws IOException error while accessing external resource
     * @throws DriverException
     * @todo make sure the geom is an area; implement p_offset
     */
    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid) throws ParameterException, IOException, DriverException {
        LiteShape shp = this.getLiteShape(sds, fid);

        if (fill != null) {
            fill.draw(g2, shp, sds, fid);
        }

        if (stroke != null) {
            if (perpendicularOffset != null) {
                double offset = perpendicularOffset.getValue(sds, fid);
                // apply perpendicular offset to shp !
            }
            // TODO perpendicular offset !
            stroke.draw(g2, shp, sds, fid);
        }
    }



    private RealParameter perpendicularOffset;
    private Stroke stroke;
    private Fill fill;
}
