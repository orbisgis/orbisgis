package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.PathIterator;
import java.util.*;

import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.DotMapFillType;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public final class DotMapFill extends Fill implements GraphicNode {

    DotMapFill(JAXBElement<DotMapFillType> f) throws InvalidStyle {
		DotMapFillType dmf = f.getValue();

		if (dmf.getGraphic() != null){
        	this.setGraphicCollection(new GraphicCollection(dmf.getGraphic(), this));
		}

		if (dmf.getValuePerMark() != null){
			this.setQuantityPerMark(SeParameterFactory.createRealParameter(dmf.getValuePerMark()));
		}

		if (dmf.getValueToRepresent() != null){
			this.setTotalQuantity(SeParameterFactory.createRealParameter(dmf.getValueToRepresent()));
		}
    }

	@Override
    public void setGraphicCollection(GraphicCollection mark) {
        this.mark = mark;
        mark.setParent(this);
    }

	@Override
    public GraphicCollection getGraphicCollection() {
        return mark;
    }

    public void setQuantityPerMark(RealParameter quantityPerMark) {
        this.quantityPerMark = quantityPerMark;
    }

    public RealParameter getQantityPerMark() {
        return quantityPerMark;
    }

    public void setTotalQuantity(RealParameter totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public RealParameter getTotalQantity() {
        return totalQuantity;
    }

	@Override
	public Paint getPaint(Feature feat, boolean selected, MapTransform mt) throws ParameterException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

    @Override
    public void draw(Graphics2D g2, Shape shp, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException {
        if (mark != null && totalQuantity != null && quantityPerMark != null) {
            RenderableGraphics gmark = mark.getGraphic(feat, selected, mt);

            if (gmark != null) {
                /* Pre-written code which gives the amount of times
                 * the mark should be printed
                 */
                double total = totalQuantity.getValue(feat);
                double perMark = quantityPerMark.getValue(feat);
                int n = (int) (total / perMark);
                //Mark width and height
                int gMarkWidth = (int)Math.round(gmark.getWidth());
                int gMarkHeight = (int)Math.round(gmark.getHeight());
                //Shape (polygon) bound rectangle
                int shapeWidth = (int)shp.getBounds2D().getWidth();
                int shapeHeight = (int)shp.getBounds2D().getHeight();
                //discretised shape rectangle (includes marks)
                int discreteWidth = shapeWidth/gMarkWidth;
                int discreteHeight = shapeHeight/gMarkHeight;
                //Transform shap to Polygon (not pretty but JTS shape object does
                //not implement the contains() method which is needed)
                //@TODO : transforming the shape to polygon is not the best sollution here
                //        The goal is to use the .contains() method which is not implemented in the
                //        com.vividsolutions.jts.awt.PolygonShape Class
                Polygon poly = ShapeToPolygon(shp);
                //Retreive list of all points which are included in the actual region of the shape
                ArrayList availableCoordinates = intiCoordinates(poly, discreteWidth, discreteHeight,  (int)shp.getBounds2D().getX(),  (int)shp.getBounds2D().getY(), gMarkWidth, gMarkHeight);
                //Actual list of coordinates which will be used to print the mark
                ArrayList Coordinates = new ArrayList();
                int NbrAvailableSpaces = availableCoordinates.size();
                //System.out.println("*****************************");
                //System.out.println("Nbr of marks to draw : " + n);
                //System.out.println("Available spots      : " + NbrAvailableSpaces);
                //System.out.println("Shape width & height : " + "(" + shapeWidth + ":" + shapeHeight + ")");
                //System.out.println("Mark width & height  : " + "(" + gMarkWidth + ":" + gMarkHeight + ")");
                //Check that there are enough points to print all n marks
                if(n < NbrAvailableSpaces)
                {
                    int cpt = 0;
                    int index = 0;
                    //Loop until n points have been selected
                    while (cpt < n)
                    {
                        //choose a random coordinate in the available points
                        index = random(0, availableCoordinates.size());
                        Coordinates.add( availableCoordinates.get(index));
                        //remove the chosen point from the list of available ones
                        availableCoordinates.remove(index);
                        //Increment counter
                        cpt++;
                    }
                }else if (n >= NbrAvailableSpaces)
                {
                    //print all available coordinates if the overall surface
                    //is to small or equal to the required surface
                    Coordinates.addAll(availableCoordinates);
                }else
                    System.out.println("WARNING (DotMapFill.java, draw()) : Not enough space to draw marks in shape");

                //Create the mark buffered image
                BufferedImage bImg = new BufferedImage(gMarkWidth, gMarkHeight, BufferedImage.TYPE_INT_ARGB);
                //Create graphics from the image
                Graphics2D tg = bImg.createGraphics();
                //Draw the mark to the image
                tg.drawRenderedImage(gmark.createRendering(mt.getCurrentRenderContext()), AffineTransform.getTranslateInstance(gMarkWidth/2, gMarkHeight/2));
                //Set clipping reagion to shape region
                g2.setClip(shp);
                //Loop through chosen coordinates
                for(int i= 0; i < Coordinates.size(); i++)
                {
                    //Retreive coordinates
                    String coord = (String)Coordinates.get(i);
                    //Split the string to x and y coordinates
                    String[] point = coord.split(":");
                    //Draw the mark at those coordinates
                    g2.drawImage(bImg, null, Integer.parseInt(point[0]), Integer.parseInt(point[1]));
                }
            }
        }
    }

    private ArrayList intiCoordinates(Polygon poly, int width, int height, int polyStartX, int polyStartY, int gMarkWidth, int gMarkHeight)
    {
        ArrayList coordinates = new ArrayList();
        for(int y= 0; y < height; y++)
        {
            for(int x= 0; x < width; x++)
            {
                /* Calculate the 5 coordinates for each mark to check if all
                 * fit in the polygon.
                 * Top left and right corners.
                 * Bottom left and right corners.
                 * Lastly the center point.
                 */
                Point center = new Point(polyStartX + x * gMarkWidth, polyStartY + y * gMarkHeight);
                Point topLeft = new Point(center.x - gMarkWidth / 2, center.y - gMarkHeight / 2);
                Point topRight = new Point(center.x + gMarkWidth / 2, center.y - gMarkHeight / 2);
                Point botRight = new Point(center.x + gMarkWidth / 2, center.y + gMarkHeight / 2);
                Point botLeft = new Point(center.x - gMarkWidth / 2, center.y + gMarkHeight / 2);
                //Make sure the points fit in the shape and add the center to the list of coordinates
                if(poly.contains(center) && poly.contains(topLeft) && poly.contains(topRight) && poly.contains(botRight) && poly.contains(botLeft))
                    coordinates.add("" + center.x + ":" + center.y);
            }
        }
        return coordinates;
    }

    private int random(int lower, int higher)
    {
        //Returns a random value v
        // v >= lower et v < higher
        return (int)(Math.random() * (higher-lower)) + lower;
    }

    private Polygon ShapeToPolygon(Shape shp)
    {
            Polygon p = new Polygon();
            PathIterator pathIterator = shp.getPathIterator(null);
            double[] seg = new double[6];
            while (!pathIterator.isDone()) {
                pathIterator.currentSegment(seg);
                p.addPoint((int) seg[0], (int) seg[1]);
                pathIterator.next();
            }
            return p;
    }
    
    @Override
    public boolean dependsOnFeature() {
        if (mark != null && this.mark.dependsOnFeature())
            return true;
        if (this.quantityPerMark != null && quantityPerMark.dependsOnFeature())
            return true;
        if (this.totalQuantity != null && totalQuantity.dependsOnFeature())
            return true;
        return false;
    }

    @Override
    public DotMapFillType getJAXBType() {
        DotMapFillType f = new DotMapFillType();

        if (mark != null) {
            f.setGraphic(mark.getJAXBElement());
        }

        if (quantityPerMark != null) {
            f.setValuePerMark(quantityPerMark.getJAXBParameterValueType());
        }

        if (totalQuantity != null) {
            f.setValuePerMark(totalQuantity.getJAXBParameterValueType());
        }

        return f;
    }

    @Override
    public JAXBElement<DotMapFillType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createDotMapFill(this.getJAXBType());
    }
    private GraphicCollection mark;
    private RealParameter quantityPerMark;
    private RealParameter totalQuantity;
}
