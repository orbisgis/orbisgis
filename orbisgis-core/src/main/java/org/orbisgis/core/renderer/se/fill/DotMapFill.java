package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.DotMapFillType;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public final class DotMapFill extends Fill implements GraphicNode {

    DotMapFill(JAXBElement<DotMapFillType> f) {
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
    public void draw(Graphics2D g2, Shape shp, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException {
        if (mark != null && totalQuantity != null && quantityPerMark != null) {
            RenderableGraphics m = mark.getGraphic(feat, selected, mt);

            if (m != null) {
                /* Pre-written code which gives the amount of times
                 * the mark should be printed
                 */
                double total = totalQuantity.getValue(feat);
                double perMark = quantityPerMark.getValue(feat);
                int n = (int) (total / perMark);
                //Mark width and height
                int mWidth = (int)Math.round(m.getWidth());
                int mHeight = (int)Math.round(m.getHeight());
                // Shape (world) width and height expressed as Mark size multiplyers.
                // wWidth * wHeight = nbr times Mark can fit in shape
                int wWidth = (int)shp.getBounds2D().getWidth()/mWidth;
                int wHeight = (int)shp.getBounds2D().getHeight()/mHeight;
                //Shape start location
                int x = (int)shp.getBounds2D().getX();
                int y = (int)shp.getBounds2D().getY();
                //Layout marks randomly on the 2D surface
                int[][] world = layoutMarks(shp, x, y, wWidth, wHeight, mWidth, mHeight, n);
                //Create the mark buffered image
                BufferedImage bImg = new BufferedImage(mWidth, mHeight, BufferedImage.TYPE_INT_ARGB);
                //Create graphics from the image
                Graphics2D tg = bImg.createGraphics();
                //Draw the mark to the image
                tg.drawRenderedImage(m.createRendering(mt.getCurrentRenderContext()), AffineTransform.getTranslateInstance(mWidth/2, mHeight/2));
                //Set clipping reagion to shape region
                g2.setClip(shp);
                //Loop threw 2D surface and print MARK where value == 1
                for(int i = 0; i < wWidth; i++)
                    for(int j = 0; j <wHeight; j++)
                    {
                        if(world[i][j] == 1)
                            g2.drawImage(bImg, null, x + i * mWidth, y + j * mHeight);
                    }
                // NOT SURE what the next phrase means:
                // The graphics2d m has to be plotted n times within shp
                // method shp.Contains(double x, double y) is not implemented in
                // the jts library so don't really see how to detect marks are
                // contained in the actual shape and not its boundries.
                // For the moment, marks are layed out randomly in the boundry
                // rectangle
            }
        }
    }

    private int[][] layoutMarks(Shape shp, int wX, int wY, int wWidth, int wHeight, int mWidth, int mHeight, int numberOfMarks)
    {
        //Create a 2D array representing the 2D world
        int[][] world = new int[wWidth][wHeight];
        //Initialize values to 0
        for(int i = 0; i < wWidth; i++)
        {
            world[i] = new int[wHeight];
            for(int j = 0; j < wHeight; j++)
                world[i][j] = 0;
        }
        //Check available space compared to amount of marks which have to be printed
        int totalCaseDispo = wWidth * wHeight;
        if((totalCaseDispo - numberOfMarks) > 0)
        {
            int cpt = 0;
            //Collect the numberOfMarks points
            while(cpt < numberOfMarks)
            {
                //Get a random point
                int[] newPoint = getPoint(0, wWidth, 0, wHeight);
                //This next line cannot be used for the moment because
                //the method contains() is not implemented.
                /*while(!shp.contains(wX + newPoint[0] * mWidth, wY + newPoint[1] * mHeight))
                      newPoint = getPoint(0, wWidth, 0, wHeight);*/

                //Check if no collision with an other mark
                if(!collision(world, wWidth, wHeight, newPoint))
                    cpt++;
            }
        }
        else if ((totalCaseDispo - numberOfMarks) == 0)
        {
            //Available space = amount of marks
            //Set all free spaces to 1
            for(int i = 0; i < wWidth; i++)
                for(int j = 0; j < wHeight; j++)
                    world[i][j] = 1;
        }
        else
        {
            //Cannot display the specified amount of marks on the
            //shape surface. Surface must be grater or mark size should be lower.
            System.out.println("#@°#~^Cannot draw RenderableGraphics objects " + numberOfMarks + " times on the total surface " + totalCaseDispo + ". Total surface is to small.");
        }

        //Print array to console
        /*
        for(int i = 0; i < wWidth; i++)
        {
            for(int j = 0; j < wHeight; j++)
                System.out.print(world[i][j]);
            System.out.println("");
        }*/
        return world;
    }

    private boolean collision(int[][] world, int wWidth, int wHeight, int[] point)
    {
        //System.out.println("    DETECT COL. de (" + point[0] + ":" + point[1] + ")");
        //Loop while the spot is not available
        while(world[point[0]][point[1]] != 0)
        {
            //System.out.println("      COLLISION");
            //if collision, increase x or y if possible (world bounderies)
            //else just send back true for collision
            //a new set of coordinates will be sent
            if(point[0] + 1 < wWidth)
                point[0]++;
            else if(point[1] + 1 < wHeight)
                point[1]++;
            else
            {
                //System.out.println("      NOUVELLE PROPOSITION DE POINT RANDOM");
                return true;
            }
            //System.out.println("      NOUVEAU POINT (" + point[0] + ":" + point[1] + ")");
        }
        //Set empty case to occupied
        world[point[0]][point[1]] = 1;
        return false;
    }

    private int[] getPoint(int xMin, int xMax, int yMin, int yMax)
    {
        int[] Result = new int[2];
        //Get X random value
        Result[0] = random(xMin, xMax);
        //Get Y random value
        Result[1] = random(yMin, yMax);
        return Result;
    }

    private int random(int lower, int higher)
    {
        //Returns a random value v
        // v >= lower et v < higher
        return (int)(Math.random() * (higher-lower)) + lower;
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
