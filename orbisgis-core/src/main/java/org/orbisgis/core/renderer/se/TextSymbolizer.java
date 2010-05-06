package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.io.IOException;
import org.gdms.data.DataSource;
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
    public void draw(Graphics2D g2, DataSource ds, int fid) throws ParameterException, IOException{

        LiteShape shp = this.getLiteShape(ds, fid);

        if (label != null){
            label.draw(g2, shp, ds, fid);
        }
    }

    private Label label;
}
