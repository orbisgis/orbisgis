package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;

import net.opengis.se._2_0.core.GraphicStrokeType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.UomNode;

import org.orbisgis.core.renderer.se.common.RelativeOrientation;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class GraphicStroke extends Stroke implements GraphicNode, UomNode {

    public final static double MIN_LENGTH = 1; // In pixel !
    private GraphicCollection graphic;
    private RealParameter length;
    private RelativeOrientation orientation;
    private Uom uom;

    GraphicStroke(JAXBElement<GraphicStrokeType> elem) throws InvalidStyle {
        this(elem.getValue());
    }

    GraphicStroke(GraphicStrokeType gst) throws InvalidStyle {
        super(gst);

        if (gst.getGraphic() != null) {
            this.setGraphicCollection(new GraphicCollection(gst.getGraphic(), this));
        }

        if (gst.getLength() != null) {
            this.setLength(SeParameterFactory.createRealParameter(gst.getLength()));
        }

        if (gst.getRelativeOrientation() != null) {
            this.setRelativeOrientation(RelativeOrientation.readFromToken(gst.getRelativeOrientation()));
        } else {
            this.setRelativeOrientation(RelativeOrientation.NORMAL);
        }

    }

    public GraphicStroke() {
        super();
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
    public double getNaturalLength(SpatialDataSourceDecorator sds, long fid, Shape shp, MapTransform mt) throws ParameterException, IOException {
        double naturalLength;

        if (length != null) {
            double lineLength = ShapeHelper.getLineLength(shp);
            naturalLength = Uom.toPixel(length.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), lineLength);
            if (naturalLength <= GraphicStroke.MIN_LENGTH || naturalLength > lineLength) {
                naturalLength = lineLength;
            }
            return naturalLength;
        } else {
            return getGraphicWidth(sds, fid, mt);
        }
    }

    private double getGraphicWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        RelativeOrientation rOrient = this.getRelativeOrientation();
        Rectangle2D bounds = graphic.getBounds(sds, fid, false, mt);

        double gWidth = bounds.getWidth();
        double gHeight = bounds.getHeight();

        switch (rOrient) {
            case NORMAL:
            case NORMAL_UP:
                return gWidth;
            case LINE:
                return gHeight;
            case PORTRAYAL:
            default:
                return Math.sqrt(gWidth * gWidth + gHeight * gHeight);
        }
    }

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid,
            Shape shape, boolean selected, MapTransform mt, double offset)
            throws ParameterException, IOException {

        //if (g != null) {
            //System.out.println("GraphicStroke.draw");
            ArrayList<Shape> shapes;
            // if not using offset rapport, compute perpendiculat offset first
            if (!this.isOffsetRapport() && Math.abs(offset) > 0.0) {
                shapes = ShapeHelper.perpendicularOffset(shape, offset);
                // Setting offset to 0.0 let be sure the offset will never been applied twice!
                offset = 0.0;
                //System.out.println("Apply offset to graphic stroke!");
            } else {
                shapes = new ArrayList<Shape>();
                shapes.add(shape);
            }


            double gWidth = getGraphicWidth(sds, fid, mt);
            for (Shape shp : shapes) {
                //System.out.println("Process shape n°" + shapeCounter + "/" + shapes.size());
                double segLength = getNaturalLength(sds, fid, shp, mt);

                //System.out.println("SegLength <-> gWidth: " + segLength + "<->" + gWidth);
                double lineLength = ShapeHelper.getLineLength(shp);

                RelativeOrientation rOrient = this.getRelativeOrientation();
                ArrayList<Shape> segments = null;

                double nbSegments;

                //int nbToDraw;

                if (this.isLengthRapport()) {
                    nbSegments = (int) ((lineLength / segLength) + 0.5);
                    //System.out.println("  Length Rapport : Split line in " + (int) nbSegments + " parts");
                    segments = ShapeHelper.splitLine(shp, (int) nbSegments);
                    segLength = lineLength / nbSegments;
                    //nbToDraw = (int) nbSegments;
                } else {
                    nbSegments = lineLength / segLength;
                    //System.out.println("  No linear rapport: NbSegement: " + nbSegments);
                    //segLength = lineLength / nbSegments;
                    //System.out.println("    BUGGY ? (new) SegLength (?): " + (lineLength / nbSegments));
                    // Effective number of graphic to draw (skip the last one if not space left...)
                    //nbToDraw = (int) nbSegments;
                    //if (nbToDraw > 0) {
                    if (nbSegments > 0) {
                        // TODO remove half of extra space at the beginning of the line
                        //shp = ShapeHelper.splitLine(shp, (nbSegments - nbToDraw)/2.0).get(1);
                        segments = ShapeHelper.splitLineInSeg(shp, segLength);
                    }
                }

                if (segments != null) {
                    for (Shape seg : segments) {
                        ArrayList<Shape> oSegs;
                        if (this.isOffsetRapport() && Math.abs(offset) > 0.0) {
                            oSegs = ShapeHelper.perpendicularOffset(seg, offset);
                        } else {
                            oSegs = new ArrayList<Shape>();
                            oSegs.add(seg);
                        }

                        for (Shape oSeg : oSegs) {
                            if (oSeg != null) {

                                //System.out.println ("Going to plot a point:");
                                segLength = ShapeHelper.getLineLength(oSeg);
                                Point2D.Double pt = ShapeHelper.getPointAt(oSeg, segLength / 2);
                                AffineTransform at = AffineTransform.getTranslateInstance(pt.x, pt.y);

                                if (rOrient != RelativeOrientation.PORTRAYAL) {
                                    //Point2D.Double ptA = ShapeHelper.getPointAt(oSeg, 0.5 * (segLength - gWidth));
                                    //Point2D.Double ptB = ShapeHelper.getPointAt(oSeg, 0.75 * (segLength - gWidth));

                                    //System.out.println("pos ptA: " + (0.5 * (segLength - gWidth)));
                                    //System.out.println("pos ptB: " + (0.5 * (segLength + gWidth)));

                                    Point2D.Double ptA = ShapeHelper.getPointAt(oSeg, 0.5 * (segLength - gWidth));
                                    Point2D.Double ptB = ShapeHelper.getPointAt(oSeg, 0.5 * (segLength + gWidth));

                                    double theta = Math.atan2(ptB.y - ptA.y, ptB.x - ptA.x);
                                    //System.out.println("(" + ptA.x + ";" + ptA.y + ")" + "(" + ptB.x + ";" + ptB.y + ")" + "   => Angle: " + (theta / 0.0175));

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
                                    //System.out.println("  theta: " + theta);

                                    at.concatenate(AffineTransform.getRotateInstance(theta));
                                }

                                graphic.draw(g2, sds, fid, selected, mt, at);
                            }
                        }
                    }
                }
            }
        //}
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


        if (uom != null){
            s.setUom(uom.toURN());
        }

        if (graphic != null) {
            s.setGraphic(graphic.getJAXBElement());
        }

        if (length != null) {
            s.setLength(length.getJAXBParameterValueType());
        }

        if (orientation != null) {
            s.setRelativeOrientation(orientation.getJAXBType());
        }
        return s;
    }

    @Override
    public Uom getUom() {
        if (uom != null){
            return uom;
        } else {
            return parent.getUom();
        }
    }

    @Override
    public void setUom(Uom u) {
        uom = u;
    }

    @Override
    public Uom getOwnUom() {
        return uom;
    }
}
