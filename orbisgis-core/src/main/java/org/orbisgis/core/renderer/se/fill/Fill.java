package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;


/**
 * Describe how to fill a shape
 *
 * @todo create subclasse FillReference
 *
 * @author maxence
 */
public abstract class Fill implements SymbolizerNode {

    @Override
    public void setParent(SymbolizerNode node){
        parent = node;
    }

    @Override
    public SymbolizerNode getParent(){
        return parent;
    }

    @Override
    public Uom getUom(){
        return parent.getUom();
    }

    /**
     *
     * @param g2 draw within this graphics2d
     * @param shp fill this shape
     * @param ds feature came from this datasource
     * @param fid id of the feature to draw
     * @throws ParameterException
     * @throws IOException
     */
    public abstract void draw(Graphics2D g2, Shape shp, DataSource ds, long fid) throws ParameterException, IOException;


    protected SymbolizerNode parent;
}
