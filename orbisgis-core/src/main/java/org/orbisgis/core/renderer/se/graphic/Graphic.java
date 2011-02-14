package org.orbisgis.core.renderer.se.graphic;

import java.awt.geom.Rectangle2D;
import java.io.IOException;

import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.persistance.se.GraphicType;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.AxisChartType;
import org.orbisgis.core.renderer.persistance.se.ExternalGraphicType;
import org.orbisgis.core.renderer.persistance.se.MarkGraphicType;
import org.orbisgis.core.renderer.persistance.se.PieChartType;
import org.orbisgis.core.renderer.persistance.se.TextGraphicType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 * @todo create subclasses: AlternativeGraphic, GraphicReference
 * @author maxence
 */
public abstract class Graphic implements SymbolizerNode, UomNode {

    public static Graphic createFromJAXBElement(JAXBElement<? extends GraphicType> gr) throws InvalidStyle {

        try {
            if (gr.getDeclaredType() == ExternalGraphicType.class) {
                return new ExternalGraphic((JAXBElement<ExternalGraphicType>) gr);
            }

            if (gr.getDeclaredType() == MarkGraphicType.class) {
                return new MarkGraphic((JAXBElement<MarkGraphicType>) gr);
            }

            if (gr.getDeclaredType() == TextGraphicType.class) {
                return new TextGraphic((JAXBElement<TextGraphicType>) gr);
            }

            if (gr.getDeclaredType() == PieChartType.class) {
                return new PieChart((JAXBElement<PieChartType>) gr);
            }

            if (gr.getDeclaredType() == AxisChartType.class) {
                return new AxisChart((JAXBElement<AxisChartType>) gr);
            }

        } catch (IOException ex) {
            return null;
        }
        return null;
    }

	@Override
	public String toString(){
		return this.getClass().getSimpleName();
	}


    @Override
    public Uom getUom() {
        if (uom != null) {
            return this.uom;
        } else {
            return parent.getUom();
        }
    }

	@Override
	public Uom getOwnUom(){
		return uom;
	}

	@Override
    public void setUom(Uom uom) {
        this.uom = uom;
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        this.parent = node;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
        transform.setParent(this);
    }

    /**
     * this method returns the particular subgraphic (RenderableGraphic)
     * 
     *
     * @param ds DataSource of this layer
     * @param fid id of the current feature to draw
     * @return a buffered image containing the ext-graphic (AT has not been applied)
     * @throws ParameterException
     * @throws IOException
     */
    public abstract RenderableGraphics getRenderableGraphics(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt)
            throws ParameterException, IOException;

    /**
     * Create an empty RenderableGraphics based on specified bounds and margin
     * @param bounds graphics size and positions
     * @param margin margin (top, bottom, left and right) to add to bounds
     * @return new empty RenderableGraphcis
     */
    public static RenderableGraphics getNewRenderableGraphics(Rectangle2D bounds, double margin) throws ParameterException {

		double width = bounds.getWidth() + 2 * margin;
		double height = bounds.getHeight() + 2 * margin;

		if (width < 1.0){
			width = 1.0;
		}

		if (height < 1.0){
			height = 1.0;
		}

		//System.out.println ("Width: " + width  + " -- Height: " + height);

		RenderableGraphics rg  = null;
		try {
        	rg = new RenderableGraphics(new Rectangle2D.Double(
            	    bounds.getMinX() - margin,
                	bounds.getMinY() - margin,
	                width, height));
		}
		catch (Exception e){
			throw new ParameterException("Invalid bounds !");
		}

        return rg;
    }

    public abstract String dependsOnFeature();

    public abstract JAXBElement<? extends GraphicType> getJAXBElement();

    public abstract double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException;

    public abstract void updateGraphic();
    
    private SymbolizerNode parent;
    protected Transform transform;
    protected Uom uom;
    
    //private Transform transform;
}
