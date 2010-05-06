package org.orbisgis.core.renderer.se.fill;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.color.ColorHelper;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 * A solid fill fills a shape with a solid color (+opacity)
 *
 * @author maxence
 */
public class SolidFill extends Fill{

    public SolidFill(){
        this.color = new ColorLiteral();
        this.opacity = new RealLiteral(60.0);
    }

    public void setColor(ColorParameter color){
        this.color = color;
    }

    public ColorParameter getColor(){
        return color;
    }

    public void setOpactity(RealParameter opacity){
        this.opacity = opacity;
    }

    public RealParameter getOpacity(){
        return opacity;
    }


    @Override
    public void draw(Graphics2D g2, Shape shp, DataSource ds, int fid) throws ParameterException {
        if (color != null){

            Color c = color.getColor(ds, fid);
            double op = this.opacity.getValue(ds, fid);

            // Add opacity to the color 
            Color ac = ColorHelper.getColorWithAlpha(c, op);

            g2.setColor(ac);
            g2.fill(shp);
        }
    }

    private ColorParameter color;
    private RealParameter opacity;

}
