package org.orbisgis.core.renderer.se.graphic;


import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.RenderContext;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.jai.RenderableGraphics;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.RenderContextFactory;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * This class doesn't exists within XSD. Actually, it the CompositeGraphic element which has been move up
 *
 * @author maxence
 */
public class GraphicCollection implements SymbolizerNode {

    public GraphicCollection(){
        graphics = new ArrayList<Graphic>();    }

    public int getNumGraphics(){
        return graphics.size();
    }

    public Graphic getGraphic(int i){
        return graphics.get(i);
    }
    
    public void addGraphic(Graphic graphic){
        graphics.add(graphic);
        graphic.setParent(this);
    }

    public void delGraphic(Graphic graphic){
        if (graphics.remove(graphic)){
        }
        else{
            // TODO Throw error
        }

    }

    
    @Override
    public Uom getUom() {
        return parent.getUom();
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
    }


    /**
     * Convert a graphic collection to a drawable Graphics2D.
     * If this collection doesn't depends on features, this g2 
     * can be used for all features.
     *
     * Callers to this method are (will be...) :
     *    - GraphicStroke
     *    - GraphicFill
     *    - DensityFill
     *    - DotMapFill
     *    - AxisChart
     *    - PointSymbolizer
     *
     *
     * @param ds DataSource of the layer
     * @param fid id of the feature to draw
     * @return a specific image for this feature
     * @throws ParameterException
     * @throws IOException
     */
    public RenderableGraphics getGraphic(DataSource ds, long fid) throws ParameterException, IOException{
        RenderContext ctc = RenderContextFactory.getContext();

        ArrayList<RenderableGraphics> images = new ArrayList<RenderableGraphics>();
        


        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;

        // First, retrieve all graphics composing the collection
        // and fetch the min/max x, y values
        Iterator<Graphic> it = graphics.iterator();
        while (it.hasNext()){
            Graphic g = it.next();
            RenderableGraphics img = g.getRenderableGraphics(ds, fid);
            images.add(img);
            if (img.getMinX() < xmin)
                xmin = img.getMinX();
            if (img.getMinY() < ymin)
                ymin = img.getMinY();
            if (img.getMinX() + img.getWidth() > xmax)
                xmax = img.getMinX() + img.getWidth();
            if (img.getMinY() + img.getHeight() > ymax)
                ymax = img.getMinY() + img.getHeight();
        }

        RenderableGraphics rg = new RenderableGraphics(new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin));

        Color transparent = new Color(1f, 1f, 1f, 0.0f);
        rg.setBackground(transparent);
        rg.clearRect((int) rg.getMinX(), (int) rg.getMinY(), (int) rg.getWidth(), (int) rg.getHeight());

        Iterator<RenderableGraphics> it2 = images.iterator();

        while (it2.hasNext()){
            RenderableGraphics g = it2.next();
            rg.drawRenderedImage(g.createRendering(ctc), new AffineTransform());
        }

        return rg;
    }


    private ArrayList<Graphic> graphics;
    private SymbolizerNode parent;

    public double getMaxWidth(DataSource ds, long fid) throws ParameterException, IOException {
        double maxWidth = 0.0;

        Iterator<Graphic> it = graphics.iterator();
        while (it.hasNext()){
            Graphic g = it.next();
            maxWidth = Math.max(g.getMaxWidth(ds, fid), maxWidth);
        }
        return maxWidth;
    }

}

