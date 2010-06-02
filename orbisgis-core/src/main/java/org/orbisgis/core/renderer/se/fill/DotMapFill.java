package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Shape;

import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.DotMapFillType;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class DotMapFill extends Fill {

    DotMapFill(JAXBElement<DotMapFillType> f) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setMark(GraphicCollection mark) {
        this.mark = mark;
        mark.setParent(this);
    }

    public GraphicCollection getMark() {
        return mark;
    }

    public void setQuantityPerMark(RealParameter quantityPerMark) {
        this.quantityPerMark = quantityPerMark;
    }

    public RealParameter getQantityPerMark() {
        return quantityPerMark;
    }

    public void setTotalQuantity(RealParameter totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public RealParameter getTotalQantity() {
        return totalQuantity;
    }

    /**
     *
     * @param g2 draw within this graphics2d
     * @param shp fill this shape
     * @param ds feature came from this datasource
     * @param fid id of the feature to draw
     * @throws IOException
     */
    @Override
    public void draw(Graphics2D g2, Shape shp, DataSource ds, long fid) throws ParameterException, IOException {
        if (mark != null && totalQuantity != null && quantityPerMark != null) {
            RenderableGraphics m = mark.getGraphic(ds, fid);

            if (m != null) {
                double total = totalQuantity.getValue(ds, fid);
                double perMark = quantityPerMark.getValue(ds, fid);

                int n = (int) (total / perMark);

                // TODO IMPLEMENT
                // The graphics2d m has to be plotted n times within shp
            }
        }
    }

    @Override
    public DotMapFillType getJAXBType() {
        DotMapFillType f = new DotMapFillType();

        if (mark != null) {
            f.setGraphic(mark.getJAXBElement());
        }

        if (quantityPerMark != null) {
            f.setValuePerMark(quantityPerMark.getJAXBParameterValueType());
        }

        if (totalQuantity != null) {
            f.setValuePerMark(totalQuantity.getJAXBParameterValueType());
        }

        return f;
    }

    @Override
    public JAXBElement<DotMapFillType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createDotMapFill(this.getJAXBType());
    }
    private GraphicCollection mark;
    private RealParameter quantityPerMark;
    private RealParameter totalQuantity;
}
