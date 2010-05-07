package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.io.IOException;
import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.liteShape.LiteShape;


import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.Label;
import org.orbisgis.core.renderer.se.label.PointLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

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


    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid) throws ParameterException, IOException, DriverException {

        LiteShape shp = this.getLiteShape(sds, fid);

        if (label != null){
            label.draw(g2, shp, sds, fid);
        }
    }

    private Label label;
}
