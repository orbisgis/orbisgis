package org.orbisgis.legend.thematic.categorize;

import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.structure.categorize.CategorizedColor;
import org.orbisgis.legend.structure.categorize.CategorizedLegend;
import org.orbisgis.legend.structure.categorize.CategorizedReal;
import org.orbisgis.legend.structure.categorize.CategorizedString;
import org.orbisgis.legend.thematic.AreaParameters;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Alexis Guéganno
 */
public class CategorizedArea extends AbstractCategorizedLegend<AreaParameters> {

    private CategorizedColor colorFill;
    private CategorizedReal opacityFill;
    private CategorizedReal opacityStroke = null;
    private CategorizedColor colorStroke = null;
    private CategorizedString dashStroke = null;
    private CategorizedReal widthStroke = null;
    private boolean strokeEnabled = false;
    private AreaSymbolizer symbolizer;


    public CategorizedArea(AreaSymbolizer as){
        symbolizer = as;
        Stroke str = as.getStroke();
        if(str instanceof PenStroke){
            strokeEnabled = true;
            PenStroke ps = (PenStroke) str;
            dashStroke = new CategorizedString(ps.getDashArray());
            widthStroke = new CategorizedReal(ps.getWidth());
            if(ps.getFill() instanceof SolidFill){
                SolidFill sf = (SolidFill) ps.getFill();
                colorStroke = new CategorizedColor(sf.getColor());
                opacityStroke = new CategorizedReal(sf.getOpacity());
            } else {
                throw new IllegalArgumentException("Can't recognize a Categorized symbol in the input symbolizer.");
            }
        } else if(str != null) {
                throw new IllegalArgumentException("Can't recognize a Categorized symbol in the input symbolizer.");
        }
        Fill f= as.getFill();
        if(f instanceof SolidFill){
            SolidFill sf = (SolidFill) f;
            colorFill = new CategorizedColor(sf.getColor());
            opacityFill = new CategorizedReal(sf.getOpacity());
        } else {
            throw new IllegalArgumentException("Can't recognize a Categorized symbol in the input symbolizer.");
        }
    }


    @Override
    public List<CategorizedLegend> getCategorizedLegend() {
        List<CategorizedLegend> ret = new LinkedList<CategorizedLegend>();
        if(strokeEnabled){
            ret.add(colorStroke);
            ret.add(opacityStroke);
            ret.add(widthStroke);
            ret.add(dashStroke);
        }
        ret.add(colorFill);
        ret.add(opacityFill);
        return ret;
    }

    @Override
    public Symbolizer getSymbolizer() {
        return symbolizer;
    }

    @Override
    public String getLegendTypeName() {
        return "Categorized Area";
    }

    @Override
    public AreaParameters get(Object o) {
        if(!(o instanceof Double)){
            throw new IllegalArgumentException("Keys are double values in this map");
        }
        Double d = (Double)o;
        double w;
        Color sc;
        String da;
        Double sop;
        if(strokeEnabled){
            w = widthStroke.getFromLower(d);
            sc = colorStroke.getFromLower(d);
            da = dashStroke.getFromLower(d);
            sop = opacityStroke.getFromLower(d);
        } else {
            w = .0;
            sc = Color.WHITE;
            da = "";
            sop = .0;
        }
        return new AreaParameters(sc,sop,w,da,colorFill.getFromLower(d),opacityFill.getFromLower(d));
    }

    @Override
    public AreaParameters put(Double d, AreaParameters ap) {
        forceMapping(d);
        AreaParameters ret = null;
        if(containsKey(d)){
            ret = get(d);
        }
        if(strokeEnabled){
            colorStroke.put(d, ap.getLineColor());
            opacityStroke.put(d, ap.getLineOpacity());
            widthStroke.put(d,ap.getLineWidth());
            dashStroke.put(d, ap.getLineDash());
        }
        colorFill.put(d, ap.getFillColor());
        opacityFill.put(d, ap.getFillOpacity());
        return ret;
    }

    private void forceMapping(Double d){
        Double upper = getNextThreshold(d);
        if(upper < Double.POSITIVE_INFINITY){
            if(strokeEnabled){
                Color  c = colorStroke.getFromLower(upper);
                Double aop = opacityStroke.getFromLower(upper);
                Double aw = widthStroke.getFromLower(upper);
                String ada = dashStroke.getFromLower(upper);
                colorStroke.put(upper, c);
                opacityStroke.put(upper,aop);
                widthStroke.put(upper,aw);
                dashStroke.put(upper, ada);
            }
            Color  c = colorFill.getFromLower(upper);
            Double aop = opacityFill.getFromLower(upper);
            colorFill.put(upper, c);
            opacityFill.put(upper,aop);
        }
    }

    @Override
    public AreaParameters remove(Object o) {
        if(!(o instanceof Double)){
            throw new IllegalArgumentException("Keys are double values in this map");
        }
        Double inp = (Double)o;
        forceMapping(inp);
        AreaParameters ret;
        if(containsKey(inp)){
            ret = get(inp);
        } else {
            return null;
        }
        if(strokeEnabled){
            colorStroke.remove(inp);
            opacityStroke.remove(inp);
            widthStroke.remove(inp);
            dashStroke.remove(inp);
        }
        colorFill.remove(inp);
        opacityFill.remove(inp);
        return ret;

    }

    /**
     * Return true if there is a stroke defined in the underlying symbolizer.
     * @return true if there is a stroke defined in the underlying symbolizer.
     */
    public boolean isStrokeEnabled() {
        return strokeEnabled;
    }

    /**
     * Enables or disables the use of the stroke of the inner symbolizer.
     * @param enable if true, the inner stroke will be enabled
     */
    public void setStrokeEnabled(boolean enable) {
        if(strokeEnabled && !enable){
            symbolizer.setStroke(null);
        } else if (!strokeEnabled && enable){
            PenStroke ps = new PenStroke();
            symbolizer.setStroke(ps);
            colorStroke = new CategorizedColor(((SolidFill)ps.getFill()).getColor());
            opacityStroke = new CategorizedReal(((SolidFill)ps.getFill()).getOpacity());
            widthStroke = new CategorizedReal(ps.getWidth());
            dashStroke = new CategorizedString(ps.getDashArray());
        }
        strokeEnabled = enable;
    }
}
