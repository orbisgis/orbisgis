package org.orbisgis.core.renderer.se.fill;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.SolidFillType;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
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

    /**
     * fill with random color 60% opaque
     */
    public SolidFill(){
        this(new ColorLiteral(), new RealLiteral(60.0));
    }

    /**
     * fill with specified color 60% opaque
     * @param c 
     */
    public SolidFill(Color c){
        this(new ColorLiteral(c), new RealLiteral(60.0));
    }

    /**
     * fill with specified color and opacity
     * @param c
     * @param opacity
     */
    public SolidFill(Color c, double opacity){
        this(new ColorLiteral(c), new RealLiteral(opacity));
    }

    /**
     * fill with specified color and opacity
     * @param c
     * @param opacity
     */
    public SolidFill(ColorParameter c, RealParameter opacity){
        this.color = c;
        this.opacity = opacity;
    }

    public SolidFill(JAXBElement<SolidFillType> sf){
        if (sf.getValue().getColor() != null){
            this.color = SeParameterFactory.createColorParameter(sf.getValue().getColor());
        }

        if (sf.getValue().getOpacity() != null){
            this.opacity = SeParameterFactory.createRealParameter(sf.getValue().getOpacity());
        }
    }

    public void setColor(ColorParameter color){
        this.color = color;
    }

    public ColorParameter getColor(){
        return color;
    }

    public void setOpacity(RealParameter opacity){
        this.opacity = opacity;
    }

    public RealParameter getOpacity(){
        return opacity;
    }


    @Override
    public void draw(Graphics2D g2, Shape shp, DataSource ds, long fid) throws ParameterException {
        if (color != null){
            Color c = color.getColor(ds, fid);
            Double op = 100.0;
            
            if (this.opacity != null){
                op = this.opacity.getValue(ds, fid);
            }
            
            // Add opacity to the color 
            Color ac = ColorHelper.getColorWithAlpha(c, op);
            g2.setColor(ac);
            g2.fill(shp);
        }
    }

    @Override
    public String toString(){
        return "Color: " + color + " alpha: " + opacity;
    }

    @Override
    public SolidFillType getJAXBType(){
        SolidFillType f = new SolidFillType();

        if (color != null){
            f.setColor(color.getJAXBParameterValueType());
        }
        if (opacity != null){
            f.setOpacity(opacity.getJAXBParameterValueType());
        }

        return f;
    }

    @Override
    public JAXBElement<SolidFillType> getJAXBElement(){
        ObjectFactory of = new ObjectFactory();
        return of.createSolidFill(this.getJAXBType());
    }

    private ColorParameter color;
    private RealParameter opacity;

}
