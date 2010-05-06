package org.orbisgis.core.renderer.se.graphic;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.RenderContext;
import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 * @todo create subclasses: AlternativeGraphic, GraphicReference
 * @author maxence
 */
public abstract class Graphic implements SymbolizerNode {

    @Override
    public Uom getUom(){
        return uom;
    }

    public void setUom(Uom uom){
        this.uom = uom;
    }

    @Override
    public SymbolizerNode getParent(){
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node){
        parent = node;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
        transform.setParent(this);
    }

    /**
     * this method returns the particular subgraphic (BufferedImage)
     * 
     *
     * @param ds DataSource of this layer
     * @param fid id of the current feature to draw
     * @return a buffered image containing the ext-graphic (AT has not been applied)
     * @throws ParameterException
     * @throws IOException
     */
    public abstract RenderableGraphics getRenderableGraphics(DataSource ds, int fid)
            throws ParameterException, IOException;

    public static RenderableGraphics getNewRenderableGraphics(Rectangle2D bounds, double margin){
        

        RenderableGraphics rg = new RenderableGraphics(new Rectangle2D.Double(
                bounds.getMinX() - margin,
                bounds.getMinY() - margin,
                bounds.getWidth() + 2*margin,
                bounds.getHeight() + 2*margin));


        Color transparent = new Color(1f, 1f, 1f, 0.0f);
        rg.setBackground(transparent);
        rg.clearRect((int) rg.getMinX(), (int) rg.getMinY(), (int) rg.getWidth(), (int) rg.getHeight());

        return rg;

    }

    public abstract double getMaxWidth(DataSource ds, int fid) throws ParameterException, IOException;

    private SymbolizerNode parent;

    protected Transform transform;
    
    protected Uom uom;
    
    //private Transform transform;
}
