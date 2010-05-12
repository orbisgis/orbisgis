package org.orbisgis.core.renderer.se;


import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.PointSymbolizerType;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;

import org.orbisgis.core.renderer.se.parameter.ParameterException;


public class PointSymbolizer extends VectorSymbolizer {

    public PointSymbolizer(){
        graphic = new GraphicCollection();
        graphic.setParent(this);
        uom = Uom.MM;
    }

    public GraphicCollection getGraphic() {
        return graphic;
    }

    public void setGraphic(GraphicCollection graphic) {
        this.graphic = graphic;
    }

    /**
     *
     * @param g2
     * @param sds
     * @param fid
     * @throws ParameterException
     * @todo convert the_geom to a point feature; plot img over the point
     */
    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid) throws ParameterException, IOException, DriverException{
        if (graphic != null && graphic.getNumGraphics() > 0){
            Point2D pt = this.getPointLiteShape(sds, fid);

            RenderableGraphics rg = graphic.getGraphic(sds, fid);

            double x = 0, y = 0; // <- the point where to plot the graphic, should be computed according to the shape and the graphic size

            x = pt.getX();
            y = pt.getY();

            // Draw the graphic right over the point !
            g2.drawRenderedImage(rg.createRendering(MapEnv.getCurrentRenderContext()), AffineTransform.getTranslateInstance(x, y));

            // Plot img over shp !
        }
    }


    @Override
    public JAXBElement<PointSymbolizerType> getJAXBInstance() {
        ObjectFactory of = new ObjectFactory();
        PointSymbolizerType s = of.createPointSymbolizerType();
        
        this.setJAXBProperty(s);


        s.setUnitOfMeasure(this.getUom().toURN());

        if (transform != null) {
            s.setTransform(transform.getJAXBType());
        }


        if (graphic != null) {
            s.setGraphic(graphic.getJAXBInstance());
        }

        return of.createPointSymbolizer(s);
    }
    
    private GraphicCollection graphic;
}
