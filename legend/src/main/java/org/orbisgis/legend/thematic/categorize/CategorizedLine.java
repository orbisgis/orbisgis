package org.orbisgis.legend.thematic.categorize;

import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.structure.categorize.CategorizedColor;
import org.orbisgis.legend.structure.categorize.CategorizedLegend;
import org.orbisgis.legend.structure.categorize.CategorizedReal;
import org.orbisgis.legend.structure.categorize.CategorizedString;
import org.orbisgis.legend.thematic.LineParameters;

import java.util.LinkedList;
import java.util.List;

/**
 * This class intends to propose an API that is useful to handle {@link LineSymbolizer} instances that are made
 * of literal or categorized parameters.
 * @author Alexis Guéganno
 */
public class CategorizedLine extends AbstractCategorizedLegend<LineParameters> {

    private LineSymbolizer symbolizer;
    private CategorizedColor color;
    private CategorizedReal opacity;
    private CategorizedReal width;
    private CategorizedString dash;

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
    public LineParameters put(Double aDouble, LineParameters lineParameters) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LineParameters remove(Object o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
