package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;

import java.io.IOException;

import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.persistance.se.GraphicStrokeType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.RelativeOrientationType;
import org.orbisgis.core.renderer.se.GraphicNode;

import org.orbisgis.core.renderer.se.common.RelativeOrientation;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public final class GraphicStroke extends Stroke implements GraphicNode {

    GraphicStroke(JAXBElement<GraphicStrokeType> elem) {
		GraphicStrokeType gst = elem.getValue();

		if (gst.getGraphic() != null){
            this.setGraphicCollection(new GraphicCollection(gst.getGraphic(), this));
		}

		if (gst.getLength() != null){
			this.setLength(SeParameterFactory.createRealParameter(gst.getLength()));
		}

		if (gst.getRelativeOrientation() != null){
			this.setRelativeOrientation(RelativeOrientation.valueOf(gst.getRelativeOrientation().value()));
			System.out.println ("RelativeOrientation: " + this.getRelativeOrientation());
		}
    }

	@Override
    public void setGraphicCollection(GraphicCollection graphic) {
        this.graphic = graphic;
    }

	@Override
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
    public void draw(Graphics2D g2, Shape shp, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException {
        RenderableGraphics g = graphic.getGraphic(feat, selected, mt);

        if (g != null) {
            double l;
            if (length != null) {
                l = length.getValue(feat);
                if (l <= 0.0) {
                    // TODO l \in R-* is forbiden ! Should throw, or set l = line.linearLength()
                    // for the time, let us l = graphic natural length...
                    l = (double) g.getWidth();
                }
            } else {
                l = (double) g.getWidth();
            }

			System.out.println ("GraphicStroke not yet implemented");

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
    public double getMaxWidth(Feature feat, MapTransform mt) throws IOException, ParameterException {
        return graphic.getMaxWidth(feat, mt);
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
