package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.StrokeType;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.se.GraphicFillType;
import org.orbisgis.core.renderer.persistance.se.GraphicStrokeType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.PenStrokeType;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 * Style description for linear features (Area or Line)
 *
 * @todo create subclasses : TextStroke, CompoundStroke and StrokeReference
 * @author maxence
 */
public abstract class Stroke implements SymbolizerNode {

    /**
     * Create a new fill based on the jaxbelement
     *
     * @param s XML Fill
     * @return Java Fill
     */
    public static Stroke createFromJAXBElement(JAXBElement<? extends StrokeType> s){
        if (s.getDeclaredType() == PenStrokeType.class){
            return new PenStroke((JAXBElement<PenStrokeType>)s);
        }
        else if (s.getDeclaredType() == GraphicStrokeType.class){
            return new GraphicStroke((JAXBElement<GraphicStrokeType>)s);
        }

        // TODO Shoudl never occurs !
        return null;
    }

    public void setUom(Uom uom) {
        this.uom = uom;
    }

    @Override
    public Uom getUom() {
        if (uom == null) {
            return parent.getUom();
        } else {
            return uom;
        }
    }

    public void setPreGap(RealParameter gap) {
        preGap = gap;
    }

    public void setPostGap(RealParameter gap) {
        postGap = gap;
    }

    public RealParameter getPostGap() {
        return postGap;
    }

    public RealParameter getPreGap() {
        return preGap;
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
     * Return the max width of the underlaying stroke
     * @param ds
     * @param fid
     * @return
     */
    public abstract double getMaxWidth(DataSource ds, long fid) throws ParameterException, IOException;

    //public abstract void getStroke(DataSource ds, int fid);
    /**
     *
     * @param g2 draw within this graphics2d
     * @param shp stroke this shape (note this is note a LiteShape, because
     *        stroke can be used to delineate graphics (such as MarkGraphic,
     *        PieChart or AxisChart)
     * @param ds feature came from this datasource
     * @param fid id of the feature to draw
     * @throws ParameterException
     * @throws IOException
     */
    public abstract void draw(Graphics2D g2, Shape shp, DataSource ds, long fid) throws ParameterException, IOException;

    /**
     * Take into account preGap and postGap
     * @param shp
     * @return
     * @todo implements
     */
    public Shape getPreparedShape(Shape shp) {
        return shp;
    }

    public abstract JAXBElement<? extends StrokeType> getJAXBElement();

    protected void setJAXBProperties(StrokeType s) {
        if (postGap != null) {
            s.setPostGap(postGap.getJAXBParameterValueType());
        }
        if (preGap != null) {
            s.setPreGap(preGap.getJAXBParameterValueType());
        }
        if (uom != null) {
            s.setUnitOfMeasure(uom.toURN());
        }
    }

    protected Uom uom;
    protected RealParameter preGap;
    protected RealParameter postGap;
    protected SymbolizerNode parent;
}
