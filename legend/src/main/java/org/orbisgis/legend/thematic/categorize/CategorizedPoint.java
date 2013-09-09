package org.orbisgis.legend.thematic.categorize;

import net.opengis.se._2_0.core.ParameterValueType;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.SeExceptions;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
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
import org.orbisgis.legend.thematic.EnablesStroke;
import org.orbisgis.legend.thematic.OnVertexOnCentroid;
import org.orbisgis.legend.thematic.PointParameters;
import org.orbisgis.legend.thematic.uom.StrokeUom;
import org.orbisgis.legend.thematic.uom.SymbolUom;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**                
 * This class intends to propose an API that is useful to handle {@link PointSymbolizer} instances that are made
 * of literal or categorized parameters.
 * @author Alexis Guéganno
 */
public class CategorizedPoint extends AbstractCategorizedLegend<PointParameters>
        implements SymbolUom, EnablesStroke, OnVertexOnCentroid {

    private CategorizedColor colorFill;
    private CategorizedReal opacityFill;
    private CategorizedReal opacityStroke = null;
    private CategorizedColor colorStroke = null;
    private CategorizedString dashStroke = null;
    private CategorizedReal widthStroke = null;
    private CategorizedReal widthSymbol;
    private CategorizedReal heightSymbol;
    private CategorizedString wkn;
    private boolean strokeEnabled = false;
    private PointSymbolizer symbolizer;
    private static final I18n I18N = I18nFactory.getI18n(CategorizedPoint.class);
    public static final String NAME = I18N.tr("Interval Classification - Point");

    /**
     * Builds a new, empty, {@code CategorizedPoint}.
     */
    public CategorizedPoint(){
        this(new PointSymbolizer());
    }


    /**
     * Build a new CategorizedPoint from the given PointSymbolizer. This one must have been built with a SolidFill and a 
     * PenStroke containing a SolidFill. All the parameters must either be simple Categorize or Literal instances. If not,
     * Exception will be thrown.
     * @param sym The input PointSymbolizer
     * @throws IllegalArgumentException If the input symbolizer cannot be recognized as a CategorizedPoint.
     */
    public CategorizedPoint(PointSymbolizer sym){
        symbolizer = sym;
        Graphic g =sym.getGraphicCollection().getGraphic(0);
        final MarkGraphic mg;
        if(g instanceof MarkGraphic){
            mg = (MarkGraphic) g;
        } else {
            throw new IllegalArgumentException("Can't recognize a Categorized symbol in the input symbolizer.");
        }
        Stroke str = mg.getStroke();
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
        Fill f= mg.getFill();
        if(f instanceof SolidFill){
            SolidFill sf = (SolidFill) f;
            colorFill = new CategorizedColor(sf.getColor());
            opacityFill = new CategorizedReal(sf.getOpacity());
        } else {
            throw new IllegalArgumentException("Can't recognize a Categorized symbol in the input symbolizer.");
        }
        final ViewBox vb = mg.getViewBox();
        RealParameter rpw = vb.getWidth();
        RealParameter rph = vb.getHeight();
        try {
            if(rpw == null && rph == null) {
                rpw = new RealLiteral(MarkGraphic.DEFAULT_SIZE);
                rph = new RealLiteral(MarkGraphic.DEFAULT_SIZE);
                vb.setHeight(rph);
                vb.setWidth(rpw);
            } else if(rpw == null){
                ParameterValueType val = rph.getJAXBParameterValueType();
                rpw = SeParameterFactory.createRealParameter(val);
                vb.setWidth(rpw);
            } else if(rph == null){
                ParameterValueType val = rpw.getJAXBParameterValueType();
                rph = SeParameterFactory.createRealParameter(val);
                vb.setHeight(rph);
            }
        } catch (SeExceptions.InvalidStyle invalidStyle) {
            throw new IllegalStateException("We've failed to clone a valid SE Parameter !", invalidStyle);
        }
        widthSymbol = new CategorizedReal(vb.getWidth());
        heightSymbol = new CategorizedReal(vb.getHeight());
        wkn = new CategorizedString(mg.getWkn());
        feedListeners();
    }

    private void feedListeners(){
        final MarkGraphic mg = (MarkGraphic) symbolizer.getGraphicCollection().getGraphic(0);
        final ViewBox vb = mg.getViewBox();
        final SolidFill pointFill = (SolidFill) mg.getFill();
        TypeListener wsListener = new TypeListener() {
            @Override public void typeChanged(TypeEvent te) {
                RealParameter p = (RealParameter) te.getSource().getParameter();
                vb.setWidth(p);
            }
        };
        widthSymbol.addListener(wsListener);
        TypeListener hsListener = new TypeListener() {
            @Override public void typeChanged(TypeEvent te) {
                RealParameter p = (RealParameter) te.getSource().getParameter();
                vb.setHeight(p);
            }
        };
        heightSymbol.addListener(hsListener);
        TypeListener wknListener = new TypeListener() {
            @Override
            public void typeChanged(TypeEvent te) {
                StringParameter p = (StringParameter) te.getSource().getParameter();
                mg.setWkn(p);
            }
        };
        wkn.addListener(wknListener);
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
        final MarkGraphic mg = (MarkGraphic) symbolizer.getGraphicCollection().getGraphic(0);
        final PenStroke pointStroke = (PenStroke) mg.getStroke();
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
        ret.add(colorFill);
        ret.add(opacityFill);
        if(strokeEnabled){
            ret.add(colorStroke);
            ret.add(opacityStroke);
            ret.add(widthStroke);
            ret.add(dashStroke);
        }
        ret.add(widthSymbol);
        ret.add(heightSymbol);
        ret.add(wkn);
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
    public PointParameters get(Object o) {
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
        return new PointParameters(sc,sop,w,da,
                colorFill.getFromLower(d),opacityFill.getFromLower(d),
                widthSymbol.getFromLower(d),heightSymbol.getFromLower(d),wkn.getFromLower(d));
    }

    @Override
    public PointParameters put(Double d, PointParameters pp) {
        forceMapping(d);
        PointParameters ret = null;
        if(containsKey(d)){
            ret = get(d);
        }
        if(strokeEnabled){
            colorStroke.put(d, pp.getLineColor());
            opacityStroke.put(d, pp.getLineOpacity());
            widthStroke.put(d,pp.getLineWidth());
            dashStroke.put(d, pp.getLineDash());
        }
        colorFill.put(d, pp.getFillColor());
        opacityFill.put(d, pp.getFillOpacity());
        widthSymbol.put(d,pp.getWidth());
        heightSymbol.put(d,pp.getHeight());
        wkn.put(d,pp.getWkn());
        return ret;
    }

    @Override
    public PointParameters remove(Object o) {
        if(!(o instanceof Double)){
            throw new IllegalArgumentException("Keys are double values in this map");
        }
        Double inp = (Double)o;
        forceMapping(inp);
        PointParameters ret;
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
        widthSymbol.remove(inp);
        heightSymbol.remove(inp);
        wkn.remove(inp);
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
            Double w = widthSymbol.getFromLower(upper);
            Double h = heightSymbol.getFromLower(upper);
            String s = wkn.getFromLower(upper);
            widthSymbol.put(upper,w);
            heightSymbol.put(upper,h);
            wkn.put(upper,s);
        }
    }

    @Override
    public boolean isStrokeEnabled() {
        return strokeEnabled;
    }

    @Override
    public void setStrokeEnabled(boolean enable) {
        MarkGraphic mg = (MarkGraphic) symbolizer.getGraphicCollection().getGraphic(0);
        if(strokeEnabled && !enable){
            mg.setStroke(null);
            colorStroke = null;
            opacityStroke = null;
            widthStroke = null;
            dashStroke = null;
        } else if (!strokeEnabled && enable){
            PenStroke ps = new PenStroke();
            String fieldName = getLookupFieldName();
            mg.setStroke(ps);
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
    public void setFallbackParameters(PointParameters fallback) {
        if(strokeEnabled){
            colorStroke.setFallbackValue(fallback.getLineColor());
            opacityStroke.setFallbackValue(fallback.getLineOpacity());
            dashStroke.setFallbackValue(fallback.getLineDash());
            widthStroke.setFallbackValue(fallback.getLineWidth());
        }
        colorFill.setFallbackValue(fallback.getFillColor());
        opacityFill.setFallbackValue(fallback.getFillOpacity());
        widthSymbol.setFallbackValue(fallback.getWidth());
        heightSymbol.setFallbackValue(fallback.getHeight());
        wkn.setFallbackValue(fallback.getWkn());
    }

    @Override
    public PointParameters getFallbackParameters() {
        return new PointParameters(
                strokeEnabled ? colorStroke.getFallbackValue() : Color.WHITE,
                strokeEnabled ? opacityStroke.getFallbackValue() : .0,
                strokeEnabled ? widthStroke.getFallbackValue() : .0,
                strokeEnabled ? dashStroke.getFallbackValue() : "",
                colorFill.getFallbackValue(),
                opacityFill.getFallbackValue(),
                widthSymbol.getFallbackValue(),
                heightSymbol.getFallbackValue(),
                wkn.getFallbackValue());
    }

    @Override
    public void setOnVertex(){
        symbolizer.setOnVertex(true);
    }

    @Override
    public void setOnCentroid(){
        symbolizer.setOnVertex(false);
    }

    @Override
    public boolean isOnVertex(){
        return symbolizer.isOnVertex();
    }

    @Override
    public Uom getStrokeUom() {
        Stroke stroke = ((MarkGraphic) symbolizer.getGraphicCollection().getGraphic(0)).getStroke();
        if(stroke != null){
            return stroke.getUom();
        } else {
            return symbolizer.getUom();
        }
    }

    @Override
    public void setStrokeUom(Uom u) {
        Stroke stroke = ((MarkGraphic) symbolizer.getGraphicCollection().getGraphic(0)).getStroke();
        if(stroke != null){
            stroke.setUom(u);
        }
    }

    @Override
    public Uom getSymbolUom(){
        MarkGraphic mg = (MarkGraphic) symbolizer.getGraphicCollection().getChildren().get(0);
        return mg.getUom();
    }

    @Override
    public void setSymbolUom(Uom u){
        MarkGraphic mg = (MarkGraphic) symbolizer.getGraphicCollection().getChildren().get(0);
        mg.setUom(u);
    }

    @Override
    public String getLegendTypeId(){
        return "org.orbisgis.legend.thematic.categorize.CategorizedPoint";
    }
}
