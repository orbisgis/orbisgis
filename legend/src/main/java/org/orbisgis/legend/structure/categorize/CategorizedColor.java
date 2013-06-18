package org.orbisgis.legend.structure.categorize;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexis Guéganno
 */
public class CategorizedColor extends CategorizedLegend<Color>{
    private ColorParameter parameter = new ColorLiteral();

    /**
     * Build a CategorizedColor from the given ColorParameter.
     * @param sp The input parameter.
     */
    public CategorizedColor(ColorParameter sp){
        setParameter(sp);
    }

    @Override
    public SeParameter getParameter() {
        return parameter;
    }

    /**
     * Replaces the inner ColorParameter with the given one. {@code param} must either be a literal or a
     * Categorize2Color whose lookup value is a simple RealAttribute
     * @param param The new inner ColorParameter used in this CategorizedColor.
     * @throws IllegalArgumentException if param can't be used to build a valid CategorizedColor
     */
    public void setParameter(ColorParameter param) {
        if(param instanceof ColorLiteral){
            parameter = param;
            fireTypeChanged();
        } else if(param instanceof Categorize2Color){
            RealParameter rp = ((Categorize2Color) param).getLookupValue();
            if(rp instanceof RealAttribute){
                parameter = param;
                field = ((RealAttribute) rp).getColumnName();
                fireTypeChanged();
            } else {
                throw new IllegalArgumentException("The given ColorParameter instance can't be recognized as a " +
                        "valid CategorizedColor.");
            }
        } else {
            throw new IllegalArgumentException("The given ColorParameter instance can't be recognized as a " +
                    "valid CategorizedColor.");
        }
    }

    /**
     * Gets the value obtained when the input data can't be processed for whatever reason.
     * @return The value directly as a Color.
     */
    public Color getFallbackValue(){
        if(parameter instanceof ColorLiteral){
            return ((ColorLiteral) parameter).getColor(null);
        } else {
            return ((Categorize2Color)parameter).getFallbackValue().getColor(null);
        }
    }

    /**
     * Sets the value obtained when the input data can't be processed for whatever reason.
     * @param value The new fallback value.
     */
    public void setFallbackValue(Color value) {
        if(parameter instanceof ColorLiteral){
            ((ColorLiteral) parameter).setColor(value);
        } else {
            ((Categorize2Color)parameter).setFallbackValue(new ColorLiteral(value));
        }
    }

    @Override
    public Color get(Double d){
        if(parameter instanceof ColorLiteral){
            return Double.isInfinite(d) && d < 0 ? ((ColorLiteral) parameter).getColor(null) : null;
        } else {
            try{
                ColorParameter sp = ((Categorize2Color)parameter).get(new RealLiteral(d));
                return sp == null ? null : sp.getColor(null);
            } catch (ParameterException pe){
                throw new IllegalArgumentException("Can't process the input value: "+d, pe);
            }
        }
    }

    @Override
    public void put(Double d, Color v){
        if(d == null || v == null){
            throw new NullPointerException("Null values are not allowed in this mapping.");
        }
        if(parameter instanceof ColorLiteral){
            if(Double.isInfinite(d) && d < 0){
                ((ColorLiteral)parameter).setColor(v);
            } else {
                try{
                    Color current = parameter.getColor(null);
                    Categorize2Color c2s = new Categorize2Color(new ColorLiteral(current),
                            new ColorLiteral(current),
                            new RealAttribute(getField()));
                    c2s.put(new RealLiteral(d),new ColorLiteral(v));
                    parameter = c2s;
                    fireTypeChanged();
                } catch (ParameterException pe){
                    throw new IllegalStateException("We've failed at retrieved the value of a literal. " +
                            "Something is going really wrong here.");
                }
            }
        } else {
            ((Categorize2Color)parameter).put(new RealLiteral(d), new ColorLiteral(v));
        }
    }

    @Override
    public Color remove(Double d){
        if(d==null){
            throw new NullPointerException("The input threshold must not be null");
        }
        if(parameter instanceof ColorLiteral){
            return null;
        } else {
            Categorize2Color c2s = (Categorize2Color) parameter;
            ColorParameter ret = c2s.remove(new RealLiteral(d));
            if(ret == null){
                return null;
            } else if(c2s.getNumClasses()==1 && c2s.getFallbackValue().equals(c2s.get(0))){
                parameter = new ColorLiteral(c2s.getFallbackValue().getColor(null));
            }
            if(ret instanceof ColorLiteral){
                try{
                    return ret.getColor(null);
                } catch (ParameterException pe){
                    throw new IllegalStateException("We've failed at retrieved the value of a literal. " +
                            "Something is going really wrong here.");
                }
            } else {
                throw new IllegalStateException("We're not supposed to have values that are not ColorLiteral in this categorize.");
            }
        }
    }

    @Override
    public Color getFromLower(Double d){
        if(d==null){
            throw new NullPointerException("The input threshold must not be null");
        }
        if(parameter instanceof ColorLiteral){
            return ((ColorLiteral) parameter).getColor(null);
        } else {
            Color col = get(d);
            if(col == null){
                Categorize2Color c2s = (Categorize2Color) parameter;
                Map<String,Value> inp = new HashMap<String, Value>();
                inp.put(getField(), ValueFactory.createValue(d));
                try {
                    return c2s.getColor(inp);
                } catch (ParameterException e) {
                    throw new IllegalStateException("May this categorize need many fields ?");
                }
            } else {
                return col;
            }
        }
    }
}
