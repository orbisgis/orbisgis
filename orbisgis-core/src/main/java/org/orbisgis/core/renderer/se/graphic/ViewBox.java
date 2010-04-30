package org.orbisgis.core.renderer.se.graphic;

import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class ViewBox implements SymbolizerNode {

    public void setWidth(RealParameter width){
        x = width;
    }
    
    public RealParameter getWidth(){
        return x;
    }

    public void setHeight(RealParameter height){
        y = height;
    }
    
    public RealParameter getHeight(){
        return y;
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

    public boolean dependsOnFeature(){
        return (x != null && x.dependsOnFeature()) || (y != null && y.dependsOnFeature());
    }

    private SymbolizerNode parent;
    private RealParameter x;
    private RealParameter y;

}
