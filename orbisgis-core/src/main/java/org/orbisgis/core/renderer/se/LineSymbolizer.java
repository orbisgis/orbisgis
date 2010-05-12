package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.LineSymbolizerType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.liteShape.LiteShape;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;

/**
 * Define a style for line features (or area feature's perimeter...)
 * Only conains a stroke
 *
 * @todo add perpendicular offset
 *
 * @author maxence
 */
public class LineSymbolizer extends VectorSymbolizer {

    public LineSymbolizer() {
        super();
        uom = Uom.MM;
        stroke = new PenStroke();
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
        stroke.setParent(this);
    }

    public RealParameter getPerpendicularOffset() {
        return perpendicularOffset;
    }

    public void setPerpendicularOffset(RealParameter perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }

    /**
     *
     * @param g2
     * @param sds
     * @param fid
     * @throws ParameterException
     * @throws IOException
     * @todo make sure the geom is a line or an area; implement p_offset
     */
    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid) throws ParameterException, IOException, DriverException {
        if (stroke != null) {
            LiteShape shp = this.getLiteShape(sds, fid);

            if (perpendicularOffset != null) {
                double offset = perpendicularOffset.getValue(sds, fid);
                // apply perpendicular offset
            }

            // TODO perpendicular offset !
            stroke.draw(g2, shp, sds, fid);
        }
    }

    @Override
    public JAXBElement<LineSymbolizerType> getJAXBInstance() {
        ObjectFactory of = new ObjectFactory();
        LineSymbolizerType s = of.createLineSymbolizerType();
        
        this.setJAXBProperty(s);


        s.setUnitOfMeasure(this.getUom().toURN());

        if (transform != null) {
            s.setTransform(transform.getJAXBType());
        }


        if (this.perpendicularOffset != null) {
            s.setPerpendicularOffset(perpendicularOffset.getJAXBParameterValueType());
        }

        if (stroke != null) {
            s.setStroke(stroke.getJAXBInstance());
        }


        return of.createLineSymbolizer(s);
    }

    private RealParameter perpendicularOffset;
    private Stroke stroke;
}
