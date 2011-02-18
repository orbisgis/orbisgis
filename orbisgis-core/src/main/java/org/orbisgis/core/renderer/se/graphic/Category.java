package org.orbisgis.core.renderer.se.graphic;

import org.orbisgis.core.renderer.persistance.se.CategoryType;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

/**
 *
 * @author maxence
 * @todo add support for stacked bar (means category fill / stroke are mandatory) and others are forbiden
 */
public final class Category implements SymbolizerNode, FillNode, StrokeNode, GraphicNode {

    private RealParameter measure;

    /* in order to draw bars, optionnal */
    private Fill fill;
    private Stroke stroke;

    /* In order to draw points, optionnal */
    private GraphicCollection graphic;
    private SymbolizerNode parent;

    private String name;

    public Category() {
        graphic = new GraphicCollection();
        graphic.setParent(this);
        name = "";
    }

    public Category(CategoryType c) throws InvalidStyle {
        if (c.getFill() != null){
            setFill(Fill.createFromJAXBElement(c.getFill()));
        }

        if (c.getGraphic() != null){
            setGraphicCollection(new GraphicCollection(c.getGraphic(), this));
        }

        if (c.getStroke() != null){
            setStroke(Stroke.createFromJAXBElement(c.getStroke()));
        }

        if (c.getMeasure() != null){
            setMeasure(SeParameterFactory.createRealParameter(c.getMeasure()));
        }

        if (c.getName() != null){
            setName(c.getName());
        }
    }

	@Override
    public Fill getFill() {
        return fill;
    }

	@Override
    public void setFill(Fill fill) {
        this.fill = fill;
        fill.setParent(this);
    }

	@Override
    public Stroke getStroke() {
        return stroke;
    }

	@Override
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
        stroke.setParent(this);
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

	@Override
	public GraphicCollection getGraphicCollection() {
		return graphic;
	}

	@Override
	public void setGraphicCollection(GraphicCollection graphic) {
		this.graphic = graphic;
	}

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public RealParameter getMeasure() {
        return measure;
    }

    public void setMeasure(RealParameter measure) {
        this.measure = measure;
    }
}
