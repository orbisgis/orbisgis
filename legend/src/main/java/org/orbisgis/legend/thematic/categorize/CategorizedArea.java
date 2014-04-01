package org.orbisgis.legend.thematic.categorize;

import org.orbisgis.coremap.renderer.se.AreaSymbolizer;
import org.orbisgis.coremap.renderer.se.Symbolizer;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.coremap.renderer.se.fill.Fill;
import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.string.StringParameter;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.coremap.renderer.se.stroke.Stroke;
import org.orbisgis.legend.structure.categorize.CategorizedColor;
import org.orbisgis.legend.structure.categorize.CategorizedLegend;
import org.orbisgis.legend.structure.categorize.CategorizedReal;
import org.orbisgis.legend.structure.categorize.CategorizedString;
import org.orbisgis.legend.structure.recode.type.TypeEvent;
import org.orbisgis.legend.structure.recode.type.TypeListener;
import org.orbisgis.legend.thematic.AreaParameters;
import org.orbisgis.legend.thematic.EnablesStroke;
import org.orbisgis.legend.thematic.uom.StrokeUom;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * This class intends to propose an API that is useful to handle {@link AreaSymbolizer} instances that are made
 * of literal or categorized parameters.
 * @author Alexis Guéganno
 */
public class CategorizedArea extends AbstractCategorizedLegend<AreaParameters>
        implements EnablesStroke {

    private CategorizedColor colorFill;
    private CategorizedReal opacityFill;
    private CategorizedReal opacityStroke = null;
    private CategorizedColor colorStroke = null;
    private CategorizedString dashStroke = null;
    private CategorizedReal widthStroke = null;
    private boolean strokeEnabled = false;
    private AreaSymbolizer symbolizer;
    private static final I18n I18N = I18nFactory.getI18n(CategorizedLine.class);
    public static final String NAME = I18N.tr("Interval Classification - Area");

    /**
     * Builds a new, empty, {@code CategorizedArea}.
     */
    public CategorizedArea(){
        this(new AreaSymbolizer());
    }

    /**
     * Build a new CategorizedArea from the given AreaSymbolizer. This one must have been built with a SolidFill and a
     * PenStroke containing a SolidFill. All the parameters must either be simple Categorize or Literal instances. If not,
     * Exception will be thrown.
     * @param as The input AreaSymbolizer
     * @throws IllegalArgumentException If the input symbolizer cannot be recognized as a CategorizedArea.
     */
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
        feedListeners();
    }

    private void feedListeners(){
        final SolidFill pointFill = (SolidFill) symbolizer.getFill();
        TypeListener cfListener = new TypeListener() {
            @Override
            public void typeChanged(TypeEvent te) {
                ColorParameter p = (ColorParameter) te.getSource().getParameter();
                pointFill.setColor(p);
            }
        };
        colorFill.addListener(cfListener);
        TypeListener ofListener = new TypeListener() {
            @Override public void typeChanged(TypeEvent te) {
                RealParameter p = (RealParameter) te.getSource().getParameter();
                pointFill.setOpacity(p);
            }
        };
        opacityFill.addListener(ofListener);
        if(strokeEnabled){
            feedStrokeListeners();
        }
    }

    private void feedStrokeListeners(){
        final PenStroke pointStroke = (PenStroke) symbolizer.getStroke();
        final SolidFill strokeFill = (SolidFill) pointStroke.getFill();
        TypeListener psColListener = new TypeListener() {
            @Override public void typeChanged(TypeEvent te) {
                ColorParameter p = (ColorParameter) te.getSource().getParameter();
                strokeFill.setColor(p);
            }
        };
        colorStroke.addListener(psColListener);
        TypeListener psOpListener = new TypeListener() {
            @Override public void typeChanged(TypeEvent te) {
                RealParameter p = (RealParameter) te.getSource().getParameter();
                strokeFill.setOpacity(p);
            }
        };
        opacityStroke.addListener(psOpListener);
        TypeListener dashListener = new TypeListener() {
            @Override public void typeChanged(TypeEvent te) {
                StringParameter p = (StringParameter) te.getSource().getParameter();
                pointStroke.setDashArray(p);
            }
        };
        dashStroke.addListener(dashListener);
        TypeListener widthListener = new TypeListener() {
            @Override public void typeChanged(TypeEvent te) {
                RealParameter p = (RealParameter) te.getSource().getParameter();
                pointStroke.setWidth(p);
            }
        };
        widthStroke.addListener(widthListener);
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
        return NAME;
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

    @Override
    public void setFallbackParameters(AreaParameters fallback) {
        if(strokeEnabled){
            colorStroke.setFallbackValue(fallback.getLineColor());
            opacityStroke.setFallbackValue(fallback.getLineOpacity());
            dashStroke.setFallbackValue(fallback.getLineDash());
            widthStroke.setFallbackValue(fallback.getLineWidth());
        }
        colorFill.setFallbackValue(fallback.getFillColor());
        opacityFill.setFallbackValue(fallback.getFillOpacity());
    }

    @Override
    public AreaParameters getFallbackParameters() {
        return new AreaParameters(
                strokeEnabled ? colorStroke.getFallbackValue() : Color.WHITE,
                strokeEnabled ? opacityStroke.getFallbackValue() : .0,
                strokeEnabled ? widthStroke.getFallbackValue() : .0,
                strokeEnabled ? dashStroke.getFallbackValue() : "",
                colorFill.getFallbackValue(),
                opacityFill.getFallbackValue());
    }

    @Override
    public boolean isStrokeEnabled() {
        return strokeEnabled;
    }

    @Override
    public void setStrokeEnabled(boolean enable) {
        if(strokeEnabled && !enable){
            symbolizer.setStroke(null);
        } else if (!strokeEnabled && enable){
            PenStroke ps = new PenStroke();
            String fieldName = getLookupFieldName();
            symbolizer.setStroke(ps);
            colorStroke = new CategorizedColor(((SolidFill)ps.getFill()).getColor());
            opacityStroke = new CategorizedReal(((SolidFill)ps.getFill()).getOpacity());
            widthStroke = new CategorizedReal(ps.getWidth());
            dashStroke = new CategorizedString(ps.getDashArray());
            colorStroke.setLookupFieldName(fieldName);
            opacityStroke.setLookupFieldName(fieldName);
            widthStroke.setLookupFieldName(fieldName);
            dashStroke.setLookupFieldName(fieldName);
            feedStrokeListeners();
        }
        strokeEnabled = enable;
    }

    @Override
    public Uom getStrokeUom() {
        return symbolizer.getUom();
    }

    @Override
    public void setStrokeUom(Uom u) {
        symbolizer.setUom(u);
    }

    @Override
    public String getLegendTypeId(){
        return "org.orbisgis.legend.thematic.categorize.CategorizedArea";
    }
}
