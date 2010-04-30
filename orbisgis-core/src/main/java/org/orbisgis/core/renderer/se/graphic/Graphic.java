package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.io.IOException;
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
     *
     * @param g2 draw the graphic within this Graphics2D
     * @param ds DataSource of this layer
     * @param fid id of the feature to draw
     * @throws ParameterException
     * @throws IOException
     */
    public abstract void drawGraphic(Graphics2D g2, DataSource ds, int fid) throws ParameterException, IOException;

    private SymbolizerNode parent;

    protected Transform transform;
    
    protected Uom uom;
    //private Transform transform;
}
