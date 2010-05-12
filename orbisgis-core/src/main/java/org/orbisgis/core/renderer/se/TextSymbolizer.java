package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.TextSymbolizerType;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.liteShape.LiteShape;


import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.Label;
import org.orbisgis.core.renderer.se.label.PointLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class TextSymbolizer extends VectorSymbolizer {

    public TextSymbolizer(){
        label = new PointLabel();
        uom = Uom.MM;
    }


    public void setLabel(Label label){
        label.setParent(this);
        this.label = label;
    }

    public Label getLabel(){
        return label;
    }

    public RealParameter getPerpendicularOffset() {
        return perpendicularOffset;
    }

    public void setPerpendicularOffset(RealParameter perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }


    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid) throws ParameterException, IOException, DriverException {

        LiteShape shp = this.getLiteShape(sds, fid);

        if (label != null){
            label.draw(g2, shp, sds, fid);
        }
    }

    
    @Override
    public JAXBElement<TextSymbolizerType> getJAXBInstance() {

        ObjectFactory of = new ObjectFactory();
        TextSymbolizerType s = of.createTextSymbolizerType();

        this.setJAXBProperty(s);


        s.setUnitOfMeasure(this.getUom().toURN());

        if (transform != null) {
            s.setTransform(transform.getJAXBType());
        }


        if (perpendicularOffset != null) {
            s.setPerpendicularOffset(perpendicularOffset.getJAXBParameterValueType());
        }

        if (label != null) {
            s.setLabel(label.getJAXBInstance());
        }

        return of.createTextSymbolizer(s);
    }


    private RealParameter perpendicularOffset;
    private Label label;
}
