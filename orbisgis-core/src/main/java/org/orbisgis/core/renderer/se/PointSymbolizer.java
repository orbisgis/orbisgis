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
import org.orbisgis.core.renderer.se.transform.Transform;

public class PointSymbolizer extends VectorSymbolizer {

    public PointSymbolizer() {
        graphic = new GraphicCollection();
        graphic.setParent(this);
        uom = Uom.MM;
    }

    public PointSymbolizer(JAXBElement<PointSymbolizerType> st) {
        PointSymbolizerType ast = st.getValue();

        if (ast.getGeometry() != null) {
            // TODO
        }

        if (ast.getUnitOfMeasure() != null) {
            this.uom = Uom.fromOgcURN(ast.getUnitOfMeasure());
        }

        if (ast.getTransform() != null) {
            this.setTransform(new Transform(ast.getTransform()));
        }

        if (ast.getGraphic() != null) {
            this.setGraphic(new GraphicCollection(ast.getGraphic()));

        }
    }

    public GraphicCollection getGraphic() {
        return graphic;
    }

    public void setGraphic(GraphicCollection graphic) {
        this.graphic = graphic;
        graphic.setParent(this);
    }

    /**
     * @todo convert the_geom to a point feature; plot img over the point
     */
    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid) throws ParameterException, IOException, DriverException {
        if (graphic != null && graphic.getNumGraphics() > 0) {
            Point2D pt = this.getPointShape(sds, fid);

            RenderableGraphics rg = graphic.getGraphic(sds, fid);

            if (rg != null) {
                double x = 0, y = 0;

                x = pt.getX();
                y = pt.getY();

                // Draw the graphic right over the point !
                g2.drawRenderedImage(rg.createRendering(MapEnv.getCurrentRenderContext()), AffineTransform.getTranslateInstance(x, y));
            }
        }
    }

    @Override
    public JAXBElement<PointSymbolizerType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        PointSymbolizerType s = of.createPointSymbolizerType();

        this.setJAXBProperty(s);


        if (this.uom != null) {
            s.setUnitOfMeasure(this.getUom().toURN());
        }

        if (transform != null) {
            s.setTransform(transform.getJAXBType());
        }


        if (graphic != null) {
            s.setGraphic(graphic.getJAXBElement());
        }

        return of.createPointSymbolizer(s);
    }
    private GraphicCollection graphic;
}
