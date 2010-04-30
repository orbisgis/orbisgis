package org.orbisgis.core.renderer.se.graphic;


import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class Slice implements SymbolizerNode {

    public Fill getFill() {
        return fill;
    }

    public void setFill(Fill fill) {
        this.fill = fill;
        fill.setParent(this);
    }

    public RealParameter getGap() {
        return gap;
    }

    public void setGap(RealParameter gap) {
        this.gap = gap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealParameter getValue() {
        return value;
    }

    public void setValue(RealParameter value) {
        this.value = value;
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
    
    private String name;
    private RealParameter value;
    private Fill fill;
    private RealParameter gap;
    private SymbolizerNode parent;

}
