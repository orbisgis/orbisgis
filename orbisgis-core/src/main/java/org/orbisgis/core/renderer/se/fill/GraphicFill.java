package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class GraphicFill extends Fill{

    public GraphicFill(){
    }

    public void setGraphic(GraphicCollection graphic){
        this.graphic = graphic;
        graphic.setParent(this);
    }

    public GraphicCollection getGraphic(){
        return graphic;
    }

    public void setUom(Uom uom){
        this.uom = uom;
    }

    @Override
    public Uom getUom(){
        if (uom == null)
            return parent.getUom();
        else
            return uom;
    }

    public void setGapX(RealParameter gap){
        gapX = gap;
    }
    public void setGapY(RealParameter gap){
        gapY = gap;
    }

    public RealParameter getGapX(){
        return gapX;
    }
    public RealParameter getGapY(){
        return gapY;
    }


    /**
     *
     * @param g2 draw within this graphics2d
     * @param shp fill this shape
     * @param ds feature came from this datasource
     * @param fid id of the feature to draw
     */
    @Override
    public void draw(Graphics2D g2, Shape shp, DataSource ds, int fid) throws ParameterException, IOException {

        BufferedImage img = graphic.getGraphic(ds, fid);

        double gX = 0.0;
        double gY = 0.0;

        if (gapX != null){
           gX = gapX.getValue(ds, fid);
           if (gX < 0.0)
               gX = 0.0;
        }

        if (gapY != null){
           gY = gapY.getValue(ds, fid);
           if (gY < 0.0)
               gY = 0.0;
        }

        // TODO IMPLEMENT
        // Create a mosaic with img, moisaic should be consistent with neighbours
        // It's like the mosaic covers the whole map but is only visible through particular feature
    }

    private GraphicCollection graphic;
    private Uom uom;
    private RealParameter gapX;
    private RealParameter gapY;
}
