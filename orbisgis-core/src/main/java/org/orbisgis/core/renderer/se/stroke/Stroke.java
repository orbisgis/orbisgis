package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;

import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.CompoundStrokeType;
import net.opengis.se._2_0.core.ExtensionParameterType;
import net.opengis.se._2_0.core.ExtensionType;
import net.opengis.se._2_0.core.GraphicStrokeType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.PenStrokeType;
import net.opengis.se._2_0.core.StrokeType;
import net.opengis.se._2_0.core.TextStrokeType;

import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.Services;


import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;

/**
 * Style description for linear features (Area or Line)
 *
 * @todo create subclasses : TextStroke, CompoundStroke and StrokeReference
 * @author maxence
 */
public abstract class Stroke implements SymbolizerNode {

    protected static OutputManager logger = Services.getOutputManager();

    protected SymbolizerNode parent;

    private boolean linearRapport;
    private boolean offsetRapport;


    protected Stroke(){
        linearRapport = false;
        offsetRapport = false;
    }

    protected Stroke (StrokeType s){
        this();

        if (s.getExtension() != null) {
            for (ExtensionParameterType param : s.getExtension().getExtensionParameter()) {
                if (param.getName().equalsIgnoreCase("linearRapport")) {
                    linearRapport = param.getContent().equalsIgnoreCase("on");
                } else if (param.getName().equalsIgnoreCase("offsetRapport")) {
                    offsetRapport = param.getContent().equalsIgnoreCase("on");
                }
            }
        }

    }

    /**
     * Create a new stroke based on the jaxbelement
     *
     * @param s XML Stroke
     * @return Java Stroke
     */
    public static Stroke createFromJAXBElement(JAXBElement<? extends StrokeType> s) throws InvalidStyle{
        if (s.getDeclaredType() == PenStrokeType.class){
            return new PenStroke((JAXBElement<PenStrokeType>)s);
        } else if (s.getDeclaredType() == GraphicStrokeType.class){
            return new GraphicStroke((JAXBElement<GraphicStrokeType>)s);
        }else if (s.getDeclaredType() == CompoundStrokeType.class){
			return new CompoundStroke((JAXBElement<CompoundStrokeType>)s);
        }else if (s.getDeclaredType() == TextStrokeType.class){
			return new TextStroke((JAXBElement<TextStrokeType>)s);
		}

        // TODO Shoudl never occurs !
        return null;
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
    }

    /**
     * when delineating closed shapes (i.e. a ring), indicate, whether or not,
     * the length of stroke elements shall be scaled in order to make the pattern
     * appear a integral # of time. This will make the junction more aesthetical
     *
     * @return whether or not stroke elems lenght shall be scaled
     */
    public boolean isLengthRapport() {
        return linearRapport;
    }

    public void setLengthRapport(boolean lengthRapport) {
        this.linearRapport = lengthRapport;
    }

    /**
     * When delineating a line with a perpendicular offset, indicate whether or not
     * stroke element shall following the initial line (rapport=true) or should only
     * be bases on the offseted line (rapport=false);
     *
     * @return true if offseted element shall follow initial line
     */
    public boolean isOffsetRapport() {
        return offsetRapport;
    }

    public void setOffsetRapport(boolean offsetRapport) {
        this.offsetRapport = offsetRapport;
    }

    /**
     *
     * @param g2 draw within this graphics2d
     * @param sds the spatial data source
     * @param fid feature id within sds
     * @param shp stroke this shape (note this is note a JTS Geometry, because
     *        stroke can be used to delineate graphics (such as MarkGraphic,
     *        PieChart or AxisChart)
     * @param selected emphasis or not the stroke (e.g invert colours)
     * @param mt the well known mapTransform 
     * @param  offset perpendicular offset to apply
     * @throws ParameterException
     * @throws IOException
     */
    public abstract void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt, double offset) throws ParameterException, IOException;

    public abstract JAXBElement<? extends StrokeType> getJAXBElement();

    protected final void setJAXBProperties(StrokeType s) {

        /*if (getOwnUom() != null) {
            s.setUnitOfMeasure(uom.toURN());
        }*/

        ObjectFactory of = new ObjectFactory();
        ExtensionType exts = of.createExtensionType();

        ExtensionParameterType linRap = of.createExtensionParameterType();
        linRap.setName("linearRapport");
        if (this.linearRapport){
            linRap.setContent("on");
        } else {
            linRap.setContent("off");
        }
        exts.getExtensionParameter().add(linRap);

        ExtensionParameterType offRap = of.createExtensionParameterType();
        offRap.setName("offsetRapport");
        if (this.offsetRapport){
            offRap.setContent("on");
        } else {
            offRap.setContent("off");
        }
        exts.getExtensionParameter().add(offRap);


        s.setExtension(exts);
    }

    /**
     * Returns the stroke pattern natural length, in pixel unit
     */
    public abstract Double getNaturalLength(SpatialDataSourceDecorator sds, long fid, Shape shp, MapTransform mt) throws ParameterException, IOException;

    public abstract String dependsOnFeature();
}
