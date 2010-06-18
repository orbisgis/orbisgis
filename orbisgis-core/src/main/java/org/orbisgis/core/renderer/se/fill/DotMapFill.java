package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Shape;

import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.DotMapFillType;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public final class DotMapFill extends Fill {

    DotMapFill(JAXBElement<DotMapFillType> f) {
		DotMapFillType dmf = f.getValue();

		if (dmf.getGraphic() != null){
        	this.setMark(new GraphicCollection(dmf.getGraphic(), this));
		}

		if (dmf.getValuePerMark() != null){
			this.setQuantityPerMark(SeParameterFactory.createRealParameter(dmf.getValuePerMark()));
		}

		if (dmf.getValueToRepresent() != null){
			this.setTotalQuantity(SeParameterFactory.createRealParameter(dmf.getValueToRepresent()));
		}
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

    @Override
    public void draw(Graphics2D g2, Shape shp, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException {
        if (mark != null && totalQuantity != null && quantityPerMark != null) {
            RenderableGraphics m = mark.getGraphic(feat, selected, mt);

            if (m != null) {
                double total = totalQuantity.getValue(feat);
                double perMark = quantityPerMark.getValue(feat);

                int n = (int) (total / perMark);

				System.out.println ("DotMapFill not yet implemented (n: " + n + ")");
                // TODO handle selected ! 
                // TODO IMPLEMENT
                // The graphics2d m has to be plotted n times within shp
            }
        }
    }
    
    @Override
    public boolean dependsOnFeature() {
        if (mark != null && this.mark.dependsOnFeature())
            return true;
        if (this.quantityPerMark != null && quantityPerMark.dependsOnFeature())
            return true;
        if (this.totalQuantity != null && totalQuantity.dependsOnFeature())
            return true;
        return false;
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
