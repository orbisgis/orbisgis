package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;

import java.io.IOException;

import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.se.GraphicStrokeType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.RelativeOrientationType;

import org.orbisgis.core.renderer.se.common.RelativeOrientation;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class GraphicStroke extends Stroke {

    GraphicStroke(JAXBElement<GraphicStrokeType> jAXBElement) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setGraphicCollection(GraphicCollection graphic) {
        this.graphic = graphic;
    }

    public GraphicCollection getGraphicCollection() {
        return graphic;
    }

    public void setLength(RealParameter length) {
        this.length = length;
    }

    public RealParameter getLength() {
        return length;
    }

    public void setRelativeOrientation(RelativeOrientation orientation) {
        this.orientation = orientation;
    }

    public RelativeOrientation getRelativeOrientation() {
        return orientation;
    }

    @Override
    public void draw(Graphics2D g2, Shape shp, DataSource ds, long fid, boolean selected) throws ParameterException, IOException {
        RenderableGraphics g = graphic.getGraphic(ds, fid, selected);

        if (g != null) {
            double l;
            if (length != null) {
                l = length.getValue(ds, fid);
                if (l <= 0.0) {
                    // TODO l \in R-* is forbiden ! Should throw, or set l = line.linearLength()
                    // for the time, let us l = graphic natural length...
                    l = (double) g.getWidth();
                }
            } else {
                l = (double) g.getWidth();
            }

            /* TODO implements :
             *
             * dont forget to take into account preGap and postGap !!!
             * Split the line in n part of linear length == l
             * for each part
             *   fetch the point at half the linear length
             *   plot g on this point, according to the orientation
             */
        }
    }

    @Override
    public double getMaxWidth(DataSource ds, long fid) throws IOException, ParameterException {
        return graphic.getMaxWidth(ds, fid);
    }

    
    @Override
    public boolean dependsOnFeature() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JAXBElement<GraphicStrokeType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createGraphicStroke(this.getJAXBType());
    }

    private GraphicStrokeType getJAXBType() {
        GraphicStrokeType s = new GraphicStrokeType();

        this.setJAXBProperties(s);

        if (graphic != null) {
            s.setGraphic(graphic.getJAXBElement());
        }
        if (length != null) {
            s.setLength(length.getJAXBParameterValueType());
        }
        if (orientation != null) {
            s.setRelativeOrientation(RelativeOrientationType.LINE);
        }
        return s;
    }
    private GraphicCollection graphic;
    private RealParameter length;
    private RelativeOrientation orientation;

}
