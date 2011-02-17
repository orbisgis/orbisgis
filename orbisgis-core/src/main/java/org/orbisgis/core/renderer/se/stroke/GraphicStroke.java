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
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.persistance.se.GraphicStrokeType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.common.RelativeOrientation;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class GraphicStroke extends Stroke implements GraphicNode {

    public final static double MIN_LENGTH = 1; // In pixel !
    private GraphicCollection graphic;
    private RealParameter length;
    private RelativeOrientation orientation;

    GraphicStroke(JAXBElement<GraphicStrokeType> elem) throws InvalidStyle {
        GraphicStrokeType gst = elem.getValue();

        if (gst.getGraphic() != null) {
            this.setGraphicCollection(new GraphicCollection(gst.getGraphic(), this));
        }

        if (gst.getLength() != null) {
            this.setLength(SeParameterFactory.createRealParameter(gst.getLength()));
        }

        if (gst.getRelativeOrientation() != null) {
            this.setRelativeOrientation(RelativeOrientation.readFromToken(gst.getRelativeOrientation().value()));
        } else {
            this.setRelativeOrientation(RelativeOrientation.NORMAL);
        }

    }

    public GraphicStroke() {
        this.graphic = new GraphicCollection();
        MarkGraphic mg = new MarkGraphic();
        mg.setTo3mmCircle();
        graphic.addGraphic(mg);
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
        if (this.length != null) {
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
        if (orientation != null) {
            return orientation;
        } else {
            return RelativeOrientation.PORTRAYAL;
        }
    }

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt) throws ParameterException, IOException {
        RenderableGraphics g = graphic.getGraphic(sds, fid, selected, mt);
        RenderedImage createRendering = g.createRendering(mt.getCurrentRenderContext());

        if (g != null) {
            double segLength;

            double gWidth = g.getWidth();
            double lineLength = ShapeHelper.getLineLength(shp);

            RelativeOrientation rOrient = this.getRelativeOrientation();

            if (length != null) {
                segLength = Uom.toPixel(length.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), lineLength); // TODO 100%

                if (segLength <= GraphicStroke.MIN_LENGTH || segLength > lineLength) {
                    segLength = lineLength;
                }
            } else {
                switch (rOrient) {
                    case NORMAL:
                    case NORMAL_UP:
                        segLength = gWidth;
                        break;
                    case LINE:
                    case LINE_UP:
                        segLength = g.getHeight();
                        break;
                    case PORTRAYAL:
                    default:
                        segLength = Math.sqrt(gWidth * gWidth + g.getHeight() * g.getHeight());
                        break;

                }
            }

            int nbSegments = (int) ((lineLength / segLength) + 0.5);

            segLength = lineLength / nbSegments;

            ArrayList<Shape> segments = ShapeHelper.splitLine(shp, nbSegments);

            for (Shape seg : segments) {
                Point2D.Double pt = ShapeHelper.getPointAt(seg, segLength / 2);
                AffineTransform at = AffineTransform.getTranslateInstance(pt.x, pt.y);


                if (rOrient != RelativeOrientation.PORTRAYAL) {
                    Point2D.Double ptA = ShapeHelper.getPointAt(seg, 0.5 * (segLength - gWidth));
                    Point2D.Double ptB = ShapeHelper.getPointAt(seg, 0.75 * (segLength - gWidth));

                    double theta = Math.atan2(ptB.y - ptA.y, ptB.x - ptA.x);
                    //System.out.println("("+ ptA.x + ";" + ptA.y +")"  + "(" + ptB.x + ";" + ptB.y+ ")" + "   => Angle: " + (theta/0.0175));

                    switch (rOrient) {
                        case LINE:
                            theta += 0.5 * Math.PI;
                            break;
                        case NORMAL_UP:
                            if (theta < -Math.PI / 2 || theta > Math.PI / 2) {
                                theta += Math.PI;
                            }
                            break;
                    }

                    at.concatenate(AffineTransform.getRotateInstance(theta));
                }
                g2.drawRenderedImage(createRendering, at);
            }
        }
    }

    @Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws IOException, ParameterException {
        RenderableGraphics g = graphic.getGraphic(sds, fid, false, mt);
        if (g != null) {
            return Math.max(Math.max(g.getWidth(), Math.abs(g.getMinX())), Math.max(g.getHeight(), Math.abs(g.getMinY())));
        } else {
            return 0.0;
        }
        //return graphic.getMaxWidth(sds, fid, mt);
        //return Math.max(Math.abs( g.getMinX() ), Math.abs(g.getMinY()));
    }

    @Override
    public double getMinLength(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        if (length != null) {
            return length.getValue(sds, fid);
        } else {
            return graphic.getMaxWidth(sds, fid, mt);
        }
    }

    @Override
    public String dependsOnFeature() {
        String result = "";
        if (graphic != null) {
            result += " " + graphic.dependsOnFeature();
        }
        if (length != null) {
            result += " " + length.dependsOnFeature();
        }

        return result.trim();
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
            s.setRelativeOrientation(orientation.getJaxbType());
        }
        return s;
    }
}
