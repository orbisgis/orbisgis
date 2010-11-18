package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;

import java.io.IOException;
import java.util.ArrayList;

import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.persistance.se.GraphicStrokeType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.RelativeOrientationType;
import org.orbisgis.core.renderer.se.GraphicNode;

import org.orbisgis.core.renderer.se.common.RelativeOrientation;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class GraphicStroke extends Stroke implements GraphicNode {

	public final static double MIN_LENGTH = 1; // In pixel !


    private GraphicCollection graphic;
    private RealParameter length;
    private RelativeOrientation orientation;

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
		if (this.length != null){
			this.length.setContext(RealParameterContext.nonNegativeContext);
		}
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
		RenderedImage createRendering = g.createRendering(mt.getCurrentRenderContext());

        if (g != null) {
            double segLength;

			double lineLength = ShapeHelper.getLineLength(shp);

            if (length != null) {
				segLength = Uom.toPixel(length.getValue(feat), getUom(), mt.getDpi(), mt.getScaleDenominator(), lineLength); // TODO 100%

                if (segLength <= GraphicStroke.MIN_LENGTH || segLength > lineLength) {
                    segLength = lineLength;
                }
            } else {
                segLength = (double) g.getWidth(); // TODO Take into account relative orientation ! (i.e normal  == width; line == height; portrayal == ??)
            }

			int nbSegments = (int)((lineLength / segLength) + 0.5);
			ArrayList<Shape> segments = ShapeHelper.splitLine(shp, nbSegments);

			for (Shape seg : segments){
				Point2D.Double pt = ShapeHelper.getLineMiddle(seg);
				g2.drawRenderedImage(createRendering, AffineTransform.getTranslateInstance(pt.x, pt.y));
			}
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
}
