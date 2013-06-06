package org.orbisgis.legend.thematic.categorize;

import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.structure.categorize.CategorizedColor;
import org.orbisgis.legend.structure.categorize.CategorizedLegend;
import org.orbisgis.legend.structure.categorize.CategorizedReal;
import org.orbisgis.legend.structure.categorize.CategorizedString;
import org.orbisgis.legend.thematic.PointParameters;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**                
 * This class intends to propose an API that is useful to handle {@link PointSymbolizer} instances that are made
 * of literal or categorized parameters.
 * @author Alexis Guéganno
 */
public class CategorizedPoint extends AbstractCategorizedLegend<PointParameters> {

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
        MarkGraphic mg;
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
        widthSymbol = new CategorizedReal(mg.getViewBox().getWidth());
        heightSymbol = new CategorizedReal(mg.getViewBox().getHeight());
        wkn = new CategorizedString(mg.getWkn());
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
        return "Categorized Point";
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
        MarkGraphic mg = (MarkGraphic) symbolizer.getGraphicCollection().getGraphic(0);
        if(strokeEnabled && !enable){
            mg.setStroke(null);
        } else if (!strokeEnabled && enable){
            PenStroke ps = new PenStroke();
            mg.setStroke(ps);
            colorStroke = new CategorizedColor(((SolidFill)ps.getFill()).getColor());
            opacityStroke = new CategorizedReal(((SolidFill)ps.getFill()).getOpacity());
            widthStroke = new CategorizedReal(ps.getWidth());
            dashStroke = new CategorizedString(ps.getDashArray());
        }
        strokeEnabled = enable;
    }
}
