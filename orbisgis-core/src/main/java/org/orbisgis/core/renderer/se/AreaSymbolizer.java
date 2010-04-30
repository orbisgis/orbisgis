package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.io.IOException;

import org.gdms.data.DataSource;

import org.orbisgis.core.renderer.liteShape.LiteShape;

import org.orbisgis.core.renderer.se.common.Uom;

import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;


public class AreaSymbolizer extends VectorSymbolizer {

    public AreaSymbolizer(){
        super();
        uom = Uom.MM;
        fill = new SolidFill();
        fill.setParent(this);
        stroke = new PenStroke();
        stroke.setParent(this);
    }

    public void setStroke(Stroke stroke){
        stroke.setParent(this);
        this.stroke = stroke;
    }

    public Stroke getStroke(){
        return stroke;
    }

    public void setFill(Fill fill){
        fill.setParent(this);
        this.fill = fill;
    }

    public Fill getFill(){
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
     * @param ds
     * @param fid
     * @throws ParameterException
     * @throws IOException error while accessing external resource
     * @todo make sure the geom is an area; implement p_offset
     */
    @Override
    public void draw(Graphics2D g2, DataSource ds, int fid) throws ParameterException, IOException{
        LiteShape shp = this.getLiteShape(ds, fid);

        if (fill != null){
            fill.draw(g2, shp, ds, fid);
        }

        if (stroke != null){

            if (perpendicularOffset != null){
                double offset = perpendicularOffset.getValue(ds, fid);
                // apply perpendicular offset
            }
            // TODO perpendicular offset !
            stroke.draw(g2, shp, ds, fid);
        }
    }



    private RealParameter perpendicularOffset;

    private Stroke stroke;
    private Fill fill;
}
