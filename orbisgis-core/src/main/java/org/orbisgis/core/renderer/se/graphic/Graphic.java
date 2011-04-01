package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import net.opengis.se._2_0.core.GraphicType;
import org.orbisgis.core.map.MapTransform;
import net.opengis.se._2_0.thematic.AxisChartType;
import net.opengis.se._2_0.core.ExternalGraphicType;
import net.opengis.se._2_0.core.MarkGraphicType;
import net.opengis.se._2_0.thematic.PieChartType;
import net.opengis.se._2_0.core.PointTextGraphicType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 * @todo create subclasses: AlternativeGraphic, GraphicReference
 * @author maxence
 */
public abstract class Graphic implements SymbolizerNode {

    protected SymbolizerNode parent;

    public static Graphic createFromJAXBElement(JAXBElement<? extends GraphicType> gr) throws InvalidStyle {
        try {
            if (gr.getDeclaredType() == ExternalGraphicType.class) {
                return new ExternalGraphic((JAXBElement<ExternalGraphicType>) gr);
            }

            if (gr.getDeclaredType() == MarkGraphicType.class) {
                return new MarkGraphic((JAXBElement<MarkGraphicType>) gr);
            }

            if (gr.getDeclaredType() == PointTextGraphicType.class) {
                return new PointTextGraphic((JAXBElement<PointTextGraphicType>) gr);
            }

            if (gr.getDeclaredType() == PieChartType.class) {
                return new PieChart((JAXBElement<PieChartType>) gr);
            }

            if (gr.getDeclaredType() == AxisChartType.class) {
                return new AxisChart((JAXBElement<AxisChartType>) gr);
            }

        } catch (IOException ex) {
            System.out.println ("Ex: " + ex);
            ex.printStackTrace(System.err);
            return null;
        }
        return null;
    }

	@Override
	public String toString(){
		return this.getClass().getSimpleName();
	}

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        this.parent = node;
    }

    /**
     * Return graphic bounds. Bounds center point shall match CRS origin !
     *
     *
     * @param sds
     * @param fid
     * @param selected
     * @param mt
     * @param at
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    public abstract Rectangle2D getBounds(SpatialDataSourceDecorator sds,
            long fid, MapTransform mt) throws ParameterException, IOException;

    public abstract void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, 
            boolean selected, MapTransform mt, AffineTransform at) throws ParameterException, IOException;

    public abstract String dependsOnFeature();
    public abstract JAXBElement<? extends GraphicType> getJAXBElement();
    public abstract void updateGraphic();
}
