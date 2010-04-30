package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SymbolizerNode;
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
     */
    public BufferedImage getGraphic(DataSource ds, int fid) throws ParameterException, IOException{

        // How to know the size before drawing ?
        // since the size of a graphic is only known after applying ViewBox and AT
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();

        Iterator<Graphic> it = graphics.iterator();
        while (it.hasNext()){
            Graphic g = it.next();
            g.drawGraphic(g2, ds, fid);
        }
        g2.finalize(); // TOOD not sure...
        return img;
    }

    private ArrayList<Graphic> graphics;
    private SymbolizerNode parent;

}

