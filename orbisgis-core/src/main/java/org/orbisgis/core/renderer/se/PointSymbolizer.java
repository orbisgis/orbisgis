package org.orbisgis.core.renderer.se;


import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.io.IOException;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.liteShape.LiteShape;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;


public class PointSymbolizer extends VectorSymbolizer {

    public PointSymbolizer(){
        graphic = new GraphicCollection();
        graphic.setParent(this);
        graphic.addGraphic(new MarkGraphic());
        uom = Uom.MM;
    }

    /**
     *
     * @param g2
     * @param ds
     * @param fid
     * @throws ParameterException
     * @todo convert the_geom to a point feature; plot img over the point
     */
    @Override
    public void draw(Graphics2D g2, DataSource ds, int fid) throws ParameterException, IOException{

        /*
         * TODO Implement
         */
        if (graphic != null){
            LiteShape shp = this.getLiteShape(ds, fid);
            
            // convert shp to a point !
            /*
             * line -> point : middle point
             * area -> point : a point inside (farest from perimeter or interior ring)
             * Should this step be done before converting the JTS geom to LiteShape ? 
             */

            BufferedImage img = graphic.getGraphic(ds, fid);

            // Plot img over shp !
        }
    }

    
    private GraphicCollection graphic;
}
