package org.orbisgis.core.renderer.se.graphic;

import net.opengis.se._2_0.thematic.SliceType;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public class Slice implements SymbolizerNode, FillNode {

	@Override
    public Fill getFill() {
        return fill;
    }

	@Override
    public void setFill(Fill fill) {
        this.fill = fill;
        fill.setParent(this);
    }

    public RealParameter getGap() {
        return gap;
    }

    public void setGap(RealParameter gap) {
        this.gap = gap;
		if (gap != null){
			gap.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
		}
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
		if (value != null){
			value.setContext(RealParameterContext.REAL_CONTEXT);
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

    public SliceType getJAXBType() {
        SliceType s = new SliceType();

        if (fill != null) {
            s.setFill(fill.getJAXBElement());
        }
        if (gap != null) {
            s.setGap(gap.getJAXBParameterValueType());
        }
        if (name != null) {
            s.setName(name);
        }
        if (value != null) {
            s.setValue(value.getJAXBParameterValueType());
        }

        return s;
    }

    public String dependsOnFeature() {
        String result = "";
        if (fill != null){
            result += " " + fill.dependsOnFeature();
        }
        if (value != null){
            result += " " + value.dependsOnFeature();
        }
        if (gap != null)
            result += " " + gap.dependsOnFeature();

        return result.trim();
    }

    private String name;
    private RealParameter value;
    private Fill fill;
    private RealParameter gap;
    private SymbolizerNode parent;
}
