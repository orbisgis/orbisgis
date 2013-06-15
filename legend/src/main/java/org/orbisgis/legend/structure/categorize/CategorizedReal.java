package org.orbisgis.legend.structure.categorize;

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.real.Categorize2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 * This class embeds a RealParameter that can be either a literal or a categorize. It is presented as a categorize
 * whatever the inner parameter is really. The main goal is to provide a transparent way to manage complex interval
 * classification without much pain while keeping the SE models simple.
 * @author Alexis Guéganno
 */
public class CategorizedReal extends CategorizedLegend{
    private RealParameter parameter = new RealLiteral();

    /**
     * Build a CategorizedReal from the given RealParameter.
     * @param sp The input parameter.
     */
    public CategorizedReal(RealParameter sp){
        setParameter(sp);
    }

    @Override
    public SeParameter getParameter() {
        return parameter;
    }

    /**
     * Replaces the inner RealParameter with the given one. {@code param} must either be a literal or a
     * Categorize2Real whose lookup value is a simple RealAttribute
     * @param param The new inner RealParameter used in this CategorizedReal.
     * @throws IllegalArgumentException if param can't be used to build a valid CategorizedReal
     */
    public void setParameter(RealParameter param) {
        if(param instanceof RealLiteral){
            parameter = param;
            fireTypeChanged();
        } else if(param instanceof Categorize2Real){
            RealParameter rp = ((Categorize2Real) param).getLookupValue();
            if(rp instanceof RealAttribute){
                parameter = param;
                field = ((RealAttribute) rp).getColumnName();
                fireTypeChanged();
            } else {
                throw new IllegalArgumentException("The given RealParameter instance can't be recognized as a " +
                        "valid CategorizedReal.");
            }
        } else {
            throw new IllegalArgumentException("The given RealParameter instance can't be recognized as a " +
                    "valid CategorizedReal.");
        }
    }

    /**
     * Gets the value obtained when the input data can't be processed for whatever reason.
     * @return The value directly as a Real.
     */
    public Double getFallbackValue(){
        if(parameter instanceof RealLiteral){
            return ((RealLiteral) parameter).getValue(null);
        } else {
            return ((Categorize2Real)parameter).getFallbackValue().getValue(null);
        }
    }

    /**
     * Sets the value obtained when the input data can't be processed for whatever reason.
     * @param value The new fallback value.
     */
    public void setFallbackValue(Double value) {
        if(parameter instanceof RealLiteral){
            ((RealLiteral) parameter).setValue(value);
        } else {
            ((Categorize2Real)parameter).setFallbackValue(new RealLiteral(value));
        }
    }

    /**
     * Gets the Double value associated to the key d. If d is not a valid key in the underlying mapping, this method
     * returns null.
     * @param d The key whose associated value is wanted
     * @return The value associated to {@code d} or null if {@code d} is not a valid key.
     */
    public Double get(Double d){
        if(parameter instanceof RealLiteral){
            return Double.isInfinite(d) && d < 0 ? ((RealLiteral) parameter).getValue(null) : null;
        } else {
            try{
                RealParameter sp = ((Categorize2Real)parameter).get(new RealLiteral(d));
                return sp == null ? null : sp.getValue(null);
            } catch (ParameterException pe){
                throw new IllegalArgumentException("Can't process the input value: "+d, pe);
            }
        }
    }

    /**
     * Put the couple (d,v) in this categorization.
     * @param d The key
     * @param v The value
     */
    public void put(Double d, Double v){
        if(d == null || v == null){
            throw new NullPointerException("Null values are not allowed in this mapping.");
        }
        if(parameter instanceof RealLiteral){
            if(Double.isInfinite(d) && d < 0){
                ((RealLiteral)parameter).setValue(v);
            } else {
                try{
                    Double current = parameter.getValue(null);
                    Categorize2Real c2s = new Categorize2Real(new RealLiteral(current),
                            new RealLiteral(current),
                            new RealAttribute(getField()));
                    c2s.put(new RealLiteral(d),new RealLiteral(v));
                    parameter = c2s;
                } catch (ParameterException pe){
                    throw new IllegalStateException("We've failed at retrieved the value of a literal. " +
                            "Something is going really wrong here.");
                }
            }
        } else {
            ((Categorize2Real)parameter).put(new RealLiteral(d), new RealLiteral(v));
        }
    }

    /**
     * Removes the mapping associated to d, if it exists and if it does not let the mapping empty.
     * @param d The threshold we want to remove.
     * @return  The value of the removed mapping, if any.
     * @throws IllegalStateException if, for whatever reason, one the key of the mapping appears not to be a literal.
     */
    public Double remove(Double d){
        if(d==null){
            throw new NullPointerException("The input threshold must not be null");
        }
        if(parameter instanceof RealLiteral){
            return null;
        } else {
            Categorize2Real c2s = (Categorize2Real) parameter;
            RealParameter ret = c2s.remove(new RealLiteral(d));
            if(ret == null){
                return null;
            } else if(c2s.getNumClasses()==1 && c2s.getFallbackValue().equals(c2s.get(0))){
                parameter = new RealLiteral(c2s.getFallbackValue().getValue(null));
            }
            if(ret instanceof RealLiteral){
                try{
                    return ret.getValue(null);
                } catch (ParameterException pe){
                    throw new IllegalStateException("We've failed at retrieved the value of a literal. " +
                            "Something is going really wrong here.");
                }
            } else {
                throw new IllegalStateException("We're not supposed to have values that are not RealLiteral in this categorize.");
            }
        }
    }


}
