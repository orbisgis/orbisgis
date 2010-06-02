package org.orbisgis.core.renderer.se.graphic;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.GraphicType;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.se.AxisChartType;
import org.orbisgis.core.renderer.persistance.se.ExternalGraphicType;
import org.orbisgis.core.renderer.persistance.se.MarkGraphicType;
import org.orbisgis.core.renderer.persistance.se.PieChartType;
import org.orbisgis.core.renderer.persistance.se.TextGraphicType;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 * @todo create subclasses: AlternativeGraphic, GraphicReference
 * @author maxence
 */
public abstract class Graphic implements SymbolizerNode {

    public static Graphic createFromJAXBElement(JAXBElement<? extends GraphicType> gr) {

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
    public Uom getUom() {
        if (parent != null) {
            return parent.getUom();
        } else {
            return uom;
        }
    }

    public void setUom(Uom uom) {
        this.uom = uom;
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
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
     * this method returns the particular subgraphic (RenderableGraphic)
     * 
     *
     * @param ds DataSource of this layer
     * @param fid id of the current feature to draw
     * @return a buffered image containing the ext-graphic (AT has not been applied)
     * @throws ParameterException
     * @throws IOException
     */
    public abstract RenderableGraphics getRenderableGraphics(DataSource ds, long fid)
            throws ParameterException, IOException;

    /**
     * Create an empty RenderableGraphics based on specified bounds and margin
     * @param bounds graphics size and positions
     * @param margin margin (top, bottom, left and right) to add to bounds
     * @return new empty RenderableGraphcis
     */
    public static RenderableGraphics getNewRenderableGraphics(Rectangle2D bounds, double margin) {


        RenderableGraphics rg = new RenderableGraphics(new Rectangle2D.Double(
                bounds.getMinX() - margin,
                bounds.getMinY() - margin,
                bounds.getWidth() + 2 * margin,
                bounds.getHeight() + 2 * margin));

        return rg;

    }

    public abstract JAXBElement<? extends GraphicType> getJAXBElement();

    public abstract double getMaxWidth(DataSource ds, long fid) throws ParameterException, IOException;

    
    private SymbolizerNode parent;
    protected Transform transform;
    protected Uom uom;
    //private Transform transform;
}
