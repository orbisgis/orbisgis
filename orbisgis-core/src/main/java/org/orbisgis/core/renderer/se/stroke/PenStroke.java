package org.orbisgis.core.renderer.se.stroke;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.io.IOException;
import java.util.ArrayList;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.MapEnv;
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
public class PenStroke extends Stroke {

    public enum LineCap {

        BUTT, ROUND, SQUARE;
    }

    public enum LineJoin {

        MITRE, ROUND, BEVEL;
    }

    /**
     * Create a standard undashed 0.1mm-wide opaque black stroke
     */
    public PenStroke() {
        setColor(new ColorLiteral(Color.BLACK));
        setWidth(new RealLiteral(0.1));
        this.setUom(Uom.MM);
        setOpacity(new RealLiteral(100.0));
        dashArray = new ArrayList<Double>();
        updateBasicStroke();
    }

    /**
     * default painter is either a solid color or a stipple
     * @return true when default is color, false when it's the stipple
     */
    public boolean useColor() {
        return useColor;
    }

    /**
     * Indicates which painter to use
     * If default painter is undefined, a random solid color is used
     * @param useColor true => use color; false => use stipple
     */
    public void setUseColor(boolean useColor) {
        this.useColor = useColor;
        updateBasicStroke();
    }

    public void setColor(ColorParameter color) {
        this.color = color;
        useColor = true;
        updateBasicStroke();
    }

    public ColorParameter getColor() {
        return color;
    }

    public void setStipple(GraphicFill stipple) {
        this.stipple = stipple;
        useColor = false;
        updateBasicStroke();
    }

    public GraphicFill getStipple() {
        return stipple;
    }

    public void setLineCap(LineCap cap) {
        lineCap = cap;updateBasicStroke();
    }

    public LineCap getLineCap() {
        return lineCap;
    }

    public void setLineJoin(LineJoin join) {
        lineJoin = join;
        updateBasicStroke();
    }

    public LineJoin getLineJoin() {
        return lineJoin;
    }

    public void setOpacity(RealParameter opacity) {
        this.opacity = opacity;
        updateBasicStroke();
    }

    public RealParameter getOpacity() {
        return this.opacity;
    }

    public void setWidth(RealParameter width) {
        this.width = width;
    }

    public RealParameter getWidth() {
        return this.width;
    }

    public RealParameter getDashOffset() {
        return dashOffset;
    }

    public void setDashOffset(RealParameter dashOffset) {
        this.dashOffset = dashOffset;
    }



    private void updateBasicStroke() {
        try{
            bStroke = createBasicStroke(null, 0);
        }
        catch (Exception e){
            this.bStroke = null;
        }
    }

    private BasicStroke createBasicStroke(DataSource ds, long fid) throws ParameterException{

        int cap;
        if (this.lineCap == null) {
            cap = BasicStroke.CAP_SQUARE;
        } else {
            switch (this.lineCap) {
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
        }

        int join;
        if (this.lineJoin == null) {
            join = BasicStroke.JOIN_BEVEL;
        } else {
            switch (this.lineJoin) {
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
        }

        double w = 1.0;

        if (width != null) {
            w = width.getValue(ds, fid);
            // TODO add scale and dpi
            w = Uom.toPixel(w, getUom(), MapEnv.getScaleDenominator());
        }

        // can handle width, cap, join and dash array
        return new BasicStroke((float) w, cap, join);
    }

    public BasicStroke getBasicStroke(DataSource ds, long fid) throws ParameterException {
        if (bStroke != null){
            return bStroke;
        }
        else{
            return this.createBasicStroke(ds, fid);
        }
    }
    


    /*
    public void setDashArray(String dashArray){
    TODO implment dashArray
    }
     */
    @Override
    public void draw(Graphics2D g2, Shape shp, DataSource ds, long fid) throws ParameterException, IOException {

        // remove preGap, postGap from the line
        Shape shape = this.getPreparedShape(shp);

        BasicStroke stroke = null;

        if (this.bStroke == null){
            stroke = this.createBasicStroke(ds, fid);
        }
        else
            stroke = this.bStroke;

        g2.setStroke(stroke);


        if (this.useColor == false) {
            if (stipple != null) {
                TexturePaint tp = stipple.getStipplePainter(ds, fid);
                g2.setPaint(tp);
            } else {
                // TOOD Warn Stiple has to be used, but is undefined
            }
        } else {
            Color c;

            if (this.color != null) {
                c = color.getColor(ds, fid);
            } else {
                // TOOD Warn Color has to be used, but is undefined (Using a random one)
                c = new ColorLiteral().getColor(ds, fid);
            }

            double op = this.opacity.getValue(ds, fid);

            Color ac = ColorHelper.getColorWithAlpha(c, op);
            g2.setPaint(ac);
        }


        g2.draw(shape);
    }

    @Override
    public double getMaxWidth(DataSource ds, long fid) throws ParameterException {
        if (this.width != null) {
            return Uom.toPixel(width.getValue(ds, fid), this.getUom(), MapEnv.getScaleDenominator());
        } else {
            return 0.0;
        }
    }
    
    private ColorParameter color;
    private GraphicFill stipple;
    private boolean useColor;
    private RealParameter opacity;
    private RealParameter width;
    private LineJoin lineJoin;
    private LineCap lineCap;
    private ArrayList<Double> dashArray;
    private RealParameter dashOffset;

    private BasicStroke bStroke;
}
