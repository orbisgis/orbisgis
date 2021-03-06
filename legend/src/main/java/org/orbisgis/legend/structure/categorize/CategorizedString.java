/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.legend.structure.categorize;

import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.string.Categorize2String;
import org.orbisgis.coremap.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.coremap.renderer.se.parameter.string.StringParameter;

import java.util.HashMap;
import java.util.Map;

/**
 * This class embeds a StringParameter that can be either a literal or a categorize. It is presented as a categorize
 * whatever the inner parameter is really. The main goal is to provide a transparent way to manage complex interval
 * classification without much pain while keeping the SE models simple.
 * @author Alexis Guéganno
 */
public class CategorizedString extends CategorizedLegend<String>{
    private StringParameter parameter = new StringLiteral();

    /**
     * Build a CategorizedString from the given StringParameter.
     * @param sp The input parameter.
     */
    public CategorizedString(StringParameter sp){
        setParameter(sp);
    }

    @Override
    public SeParameter getParameter() {
        return parameter;
    }

    /**
     * Replaces the inner StringParameter with the given one. {@code param} must either be a literal or a
     * Categorize2String whose lookup value is a simple RealAttribute
     * @param param The new inner StringParameter used in this CategorizedString.
     * @throws IllegalArgumentException if param can't be used to build a valid CategorizedString
     */
    public void setParameter(StringParameter param) {
        if(param instanceof StringLiteral){
            parameter = param;
            fireTypeChanged();
        } else if(param instanceof Categorize2String){
            RealParameter rp = ((Categorize2String) param).getLookupValue();
            if(rp instanceof RealAttribute){
                parameter = param;
                field = ((RealAttribute) rp).getColumnName();
                fireTypeChanged();
            } else {
                throw new IllegalArgumentException("The given StringParameter instance can't be recognized as a " +
                            "valid CategorizedString.");
            }
        } else {
                throw new IllegalArgumentException("The given StringParameter instance can't be recognized as a " +
                            "valid CategorizedString.");
        }
    }

    /**
     * Gets the value obtained when the input data can't be processed for whatever reason.
     * @return The value directly as a String.
     */
    public String getFallbackValue(){
        if(parameter instanceof StringLiteral){
            return ((StringLiteral) parameter).getValue(null);
        } else {
            return ((Categorize2String)parameter).getFallbackValue().getValue(null);
        }
    }

    /**
     * Sets the value obtained when the input data can't be processed for whatever reason.
     * @param value The new fallback value.
     */
    public void setFallbackValue(String value) {
        if(parameter instanceof StringLiteral){
            ((StringLiteral) parameter).setValue(value);
        } else {
            ((Categorize2String)parameter).setFallbackValue(new StringLiteral(value));
        }
    }

    @Override
    public String get(Double d){
        if(parameter instanceof StringLiteral){
            return Double.isInfinite(d) && d < 0 ? ((StringLiteral) parameter).getValue(null) : null;
        } else {
            try{
                StringParameter sp = ((Categorize2String)parameter).get(new RealLiteral(d));
                return sp == null ? null : sp.getValue(null);
            } catch (ParameterException pe){
                throw new IllegalArgumentException("Can't process the input value: "+d, pe);
            }
        }
    }

    @Override
    public void put(Double d, String v){
        if(d == null || v == null){
            throw new NullPointerException("Null values are not allowed in this mapping.");
        }
        if(parameter instanceof StringLiteral){
            if(Double.isInfinite(d) && d < 0){
                ((StringLiteral)parameter).setValue(v);
            } else {
                try{
                    String current = parameter.getValue(null);
                    Categorize2String c2s = new Categorize2String(new StringLiteral(current),
                            new StringLiteral(current),
                            new RealAttribute(getField()));
                    c2s.put(new RealLiteral(d),new StringLiteral(v));
                    parameter = c2s;
                    fireTypeChanged();
                } catch (ParameterException pe){
                    throw new IllegalStateException("We've failed at retrieved the value of a literal. " +
                            "Something is going really wrong here.");
                }
            }
        } else {
            ((Categorize2String)parameter).put(new RealLiteral(d), new StringLiteral(v));
        }
    }

    @Override
    public String remove(Double d){
        if(d==null){
            throw new NullPointerException("The input threshold must not be null");
        }
        if(parameter instanceof StringLiteral){
            return null;
        } else {
            Categorize2String c2s = (Categorize2String) parameter;
            StringParameter ret = c2s.remove(new RealLiteral(d));
            if(ret == null){
                return null;
            } else if(c2s.getNumClasses()==1 && c2s.getFallbackValue().equals(c2s.get(0))){
                parameter = new StringLiteral(c2s.getFallbackValue().getValue(null));
            }
            if(ret instanceof StringLiteral){
                try{
                    return ret.getValue(null);
                } catch (ParameterException pe){
                    throw new IllegalStateException("We've failed at retrieved the value of a literal. " +
                            "Something is going really wrong here.");
                }
            } else {
                throw new IllegalStateException("We're not supposed to have values that are not StringLiteral in this categorize.");
            }
        }
    }


    @Override
    public String getFromLower(Double d){
        if(d==null){
            throw new NullPointerException("The input threshold must not be null");
        }
        if(parameter instanceof StringLiteral){
            return ((StringLiteral) parameter).getValue(null);
        } else {
            String col = get(d);
            if(col == null){
                Categorize2String c2s = (Categorize2String) parameter;
                Map<String,Object> inp = new HashMap<>();
                inp.put(getField(), d);
                return c2s.getValue(inp);
            } else {
                return col;
            }
        }
    }

}
