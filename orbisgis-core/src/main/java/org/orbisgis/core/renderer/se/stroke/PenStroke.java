package org.orbisgis.core.renderer.se.stroke;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.GraphicFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.color.ColorHelper;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 * Basic stroke for linear features
 * @todo implement dasharray/dashoffset
 * @author maxence
 */
public class PenStroke extends Stroke{

    public PenStroke(){
        setColor(new ColorLiteral());
        width = new RealLiteral(1.0);
        opacity = new RealLiteral(60.0);
    }


    public void setColor(ColorParameter color){
        this.color = color;
        useColor = true;
    }

    public ColorParameter getColor(){
        return color;
    }

    public void setStipple(GraphicFill stipple){
        this.stipple = stipple;
        useColor = false;
    }

    public GraphicFill getStipple(){
        return stipple;
    }
    

    public void setLineCap(LineCap cap){
        lineCap = cap;
    }

    public LineCap getLineCap(){
        return lineCap;
    }


    public void setLineJoin(LineJoin join){
        lineJoin = join;
    }

    public LineJoin getLineJoin(){
        return lineJoin;
    }

    public void setOpacity(RealParameter opacity){
        this.opacity = opacity;
    }
    
    public RealParameter getOpacity(){
        return this.opacity;
    }


    public void setWidth(RealParameter width){
        this.width = width;
    }

    public RealParameter getWidth(){
        return this.width;
    }


    public RealParameter getDashOffset() {
        return dashOffset;
    }

    public void setDashOffset(RealParameter dashOffset) {
        this.dashOffset = dashOffset;
    }


    // TODO DASH offset and array
    /*
     * public void setDashArray(String dashArray){
     *   String = "1,3,2,4,6,2,4"
     *   }
     *
     */

    
    @Override
    public void draw(Graphics2D g2, Shape shp, DataSource ds, int fid) throws ParameterException{

        int cap;
        switch (this.lineCap){
            case BUTT:
                cap = BasicStroke.CAP_BUTT;
                break;
            case ROUND:
                cap = BasicStroke.CAP_BUTT;
                break;
            case SQUARE:
            default:
                cap = BasicStroke.CAP_SQUARE;
                break;
        }

        int join;
        switch (this.lineJoin){
            case MITRE:
                join = BasicStroke.JOIN_MITER;
                break;
            case ROUND:
                join = BasicStroke.JOIN_ROUND;
                break;
            case BEVEL:
            default:
                join = BasicStroke.JOIN_BEVEL;
                break;
        }

        double w = 1.0;
        
        if (width == null){
            w = width.getValue(ds, fid);
            // TODO add scale and dpi to the draw stack
            Uom.toPixel(w, getUom(), 96, 25000);
        }

        // can handle color+opacity, width, cap, join and dash array
        BasicStroke bStroke = new BasicStroke((float) w, cap, join); // TODO Create the BasicStroke !

        Color c = color.getColor(ds, fid);
        double op = this.opacity.getValue(ds, fid);
        Color ac = ColorHelper.getColorWithAlpha(c, op);


            g2.setColor(ac);

        // TODO Stipple ? could be done with g2.clip(shape) ?
        /**
         * En quelque mots : un PenStroke avec Stipple c'est:
         *    1) On génère le GraphicFill pour qu'il recouvre toute la ligne
         *    2) On créer un mask avec la ligne + width
         *    3) Ce qui est afficher est l'intersection de 1) et 2)
         */

        // shp -> preGap postGap 

        g2.setStroke(bStroke);
        g2.draw(shp);
    }

    

    private ColorParameter color;
    private GraphicFill stipple;

    private boolean useColor;

    private RealParameter opacity;
    private RealParameter width;
    private LineJoin lineJoin;
    private LineCap lineCap;
    private ArrayList<Integer> dashArray;
    private RealParameter dashOffset;
}
