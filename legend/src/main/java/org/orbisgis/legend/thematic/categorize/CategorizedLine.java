package org.orbisgis.legend.thematic.categorize;

import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.structure.categorize.CategorizedColor;
import org.orbisgis.legend.structure.categorize.CategorizedLegend;
import org.orbisgis.legend.structure.categorize.CategorizedReal;
import org.orbisgis.legend.structure.categorize.CategorizedString;
import org.orbisgis.legend.structure.recode.type.TypeEvent;
import org.orbisgis.legend.structure.recode.type.TypeListener;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.uom.StrokeUom;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * This class intends to propose an API that is useful to handle {@link LineSymbolizer} instances that are made
 * of literal or categorized parameters.
 * @author Alexis Guéganno
 */
public class CategorizedLine extends AbstractCategorizedLegend<LineParameters> implements StrokeUom{

    private LineSymbolizer symbolizer;
    private CategorizedColor color;
    private CategorizedReal opacity;
    private CategorizedReal width;
    private CategorizedString dash;

    /**
     * Builds a new, empty, {@code CategorizedLine}.
     */
    public CategorizedLine(){
        this(new LineSymbolizer());
    }

    /**
     * Build a new CategorizedLine from the given LineSymbolizer. This one must have been built with a PenStroke
     * containing a SolidFill. All the parameters must either be simple Categorize or Literal instances. If not,
     * Exception will be thrown.
     * @param ls The input LineSymbolizer
     * @throws IllegalArgumentException If the input symbolizer cannot be recognized as a CategorizedLine.
     */
    public CategorizedLine(LineSymbolizer ls){
        symbolizer = ls;
        Stroke str = ls.getStroke();
        if(str instanceof PenStroke){
            PenStroke ps = (PenStroke) str;
            dash = new CategorizedString(ps.getDashArray());
            width = new CategorizedReal(ps.getWidth());
            if(ps.getFill() instanceof SolidFill){
                SolidFill sf = (SolidFill) ps.getFill();
                color = new CategorizedColor(sf.getColor());
                opacity = new CategorizedReal(sf.getOpacity());
            } else {
                throw new IllegalArgumentException("Can't recognize a Categorized symbol in the input symbolizer.");
            }
        } else {
            throw new IllegalArgumentException("Can't recognize a Categorized symbol in the input symbolizer.");
        }
        feedListeners();
    }

    private void feedListeners(){
        final PenStroke pointStroke = (PenStroke) symbolizer.getStroke();
        final SolidFill strokeFill = (SolidFill) pointStroke.getFill();
        TypeListener psColListener = new TypeListener() {
            @Override public void typeChanged(TypeEvent te) {
                ColorParameter p = (ColorParameter) te.getSource().getParameter();
                strokeFill.setColor(p);
            }
        };
        color.addListener(psColListener);
        TypeListener psOpListener = new TypeListener() {
            @Override public void typeChanged(TypeEvent te) {
                RealParameter p = (RealParameter) te.getSource().getParameter();
                strokeFill.setOpacity(p);
            }
        };
        opacity.addListener(psOpListener);
        TypeListener dashListener = new TypeListener() {
            @Override public void typeChanged(TypeEvent te) {
                StringParameter p = (StringParameter) te.getSource().getParameter();
                pointStroke.setDashArray(p);
            }
        };
        dash.addListener(dashListener);
        TypeListener widthListener = new TypeListener() {
            @Override public void typeChanged(TypeEvent te) {
                RealParameter p = (RealParameter) te.getSource().getParameter();
                pointStroke.setWidth(p);
            }
        };
        width.addListener(widthListener);
    }

    @Override
    public List<CategorizedLegend> getCategorizedLegend() {
        List<CategorizedLegend> ret = new LinkedList<CategorizedLegend>();
        ret.add(color);
        ret.add(opacity);
        ret.add(width);
        ret.add(dash);
        return ret;
    }

    @Override
    public void setFallbackParameters(LineParameters fallback) {
        color.setFallbackValue(fallback.getLineColor());
        opacity.setFallbackValue(fallback.getLineOpacity());
        dash.setFallbackValue(fallback.getLineDash());
        width.setFallbackValue(fallback.getLineWidth());
    }

    @Override
    public LineParameters getFallbackParameters() {
        return new LineParameters(
                color.getFallbackValue(),
                opacity.getFallbackValue(),
                width.getFallbackValue(),
                dash.getFallbackValue());
    }

    @Override
    public Symbolizer getSymbolizer() {
        return symbolizer;
    }

    @Override
    public String getLegendTypeName() {
        return "Categorized Line";
    }

    @Override
    public LineParameters get(Object o) {
        if(!(o instanceof Double)){
            throw new IllegalArgumentException("Keys are double values in this map");
        }
        Double d = (Double)o;
        return new LineParameters(color.getFromLower(d),opacity.getFromLower(d),width.getFromLower(d),dash.getFromLower(d));
    }

    @Override
    public LineParameters put(Double d, LineParameters lp) {
        forceMapping(d);
        LineParameters ret = null;
        if(containsKey(d)){
            ret = get(d);
        }
        color.put(d,lp.getLineColor());
        opacity.put(d,lp.getLineOpacity());
        width.put(d,lp.getLineWidth());
        dash.put(d, lp.getLineDash());
        return  ret;
    }

    private void forceMapping(Double d){
        Double upper = getNextThreshold(d);
        if(upper < Double.POSITIVE_INFINITY){
            Color  c = color.getFromLower(upper);
            Double aop = opacity.getFromLower(upper);
            Double aw = width.getFromLower(upper);
            String ada = dash.getFromLower(upper);
            color.put(upper, c);
            opacity.put(upper,aop);
            width.put(upper,aw);
            dash.put(upper, ada);
        }
    }

    @Override
    public LineParameters remove(Object o) {
        if(!(o instanceof Double)){
            throw new IllegalArgumentException("Keys are double values in this map");
        }
        Double d = (Double)o;
        forceMapping(d);
        LineParameters ret = null;
        if(containsKey(d)){
            ret = get(d);
        }
        color.remove(d);
        opacity.remove(d);
        width.remove(d);
        dash.remove(d);
        return ret;
    }

    /**
     * Gets the Uom used for the inner Stroke.
     * @return The unit of measure used to compute the width of the stroke.
     */
    public Uom getStrokeUom(){
        return symbolizer.getStroke().getUom();
    }

    /**
     * Gets the Uom used for the inner Stroke.
     * @param u The unit of measure used to compute the width of the stroke.
     */
    public void setStrokeUom(Uom u){
        symbolizer.getStroke().setUom(u);
    }

    @Override
    public String getLegendTypeId(){
        return "org.orbisgis.legend.thematic.categorize.CategorizedLine";
    }
}
