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

package org.orbisgis.legend.structure.recode;

import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameter;
import org.orbisgis.coremap.renderer.se.parameter.string.Recode2String;
import org.orbisgis.coremap.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.coremap.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.coremap.renderer.se.parameter.string.StringParameter;
import org.orbisgis.legend.structure.parameter.AbstractAttributeLegend;

/**
 * Wrapper for StringLiteral and Recode2String instances : both of them can be recognized as part of a unique value
 * analysis.
 *
 * @author Alexis, Sylvain PALOMINOS
 */
public class RecodedString extends RecodedLegend implements AbstractAttributeLegend {

    StringParameter parameter = new StringLiteral();

    /**
     * Gets the number of items defined in the inner StringParameter.
     * @return
     */
    public int size(){
        if(parameter instanceof StringLiteral){
            return 0;
        }
        else if(parameter instanceof Recode2String){
            return ((Recode2String)parameter).getNumMapItem();
        }
        return 0;
    }

    /**
     * Builds a new RecodedString using the given parameter.
     * @param param The original StringParameter
     * @throws IllegalArgumentException If param is neither a StringLiteral nor a Recode2String.
     */

    public RecodedString(StringParameter param){
        setParameter(param);
    }

    /**
     * Gets the inner parameter.
     **/
    public StringParameter getParameter(){
        return parameter;
    }

    /**
     * Sets paramter to s
     * @param s
     * @throws IllegalArgumentException if s is neither a Recode2String nor a StringLiteral
     */
    public void setParameter(SeParameter s){
        if(s instanceof StringLiteral){
            parameter = (StringLiteral)s;
            fireTypeChanged();
        }
        else if(s instanceof Recode2String){
            parameter = (Recode2String)s;
            field = getValueReference().getColumnName();
            fireTypeChanged();
        }
        else{
            throw new IllegalArgumentException("This class must be built from a  string recode or literal.");
        }
    }

    /**
     * Gets the field used to make the analysis
     * @return
     */
    @Override
    public String getLookupFieldName(){
        return field;
    }

    /**
     * Gets the field used to make the analysis
     * @return
     */
    @Override
    public void setLookupFieldName(String s){
        setField(s);
    }

    /**
     * Gets the Double value, if any, associated to {@code key} in the inner {@code
     * Recode}.
     * @param i
     * @return
     */
    public String getItemValue(int i){
        if(parameter instanceof StringLiteral){
            return ((StringLiteral)parameter).getValue(null);
        }
        else if(parameter instanceof Recode2String){
            if(((Recode2String)parameter).getMapItemValue(i) != null){
                try {
                    return ((Recode2String)parameter).getMapItemValue(i).getValue(null);
                } catch (ParameterException e) {
                }
            }
        }
        return null;
    }

    /**
     * Gets the value used when there is no match for a given parameter.
     * @return
     */
    public String getFallbackValue(){
        if(parameter instanceof StringLiteral){
            return ((StringLiteral)parameter).getValue(null);
        }
        else if(parameter instanceof Recode2String){
            return ((Recode2String)parameter).getFallbackValue().getValue(null);
        }
        return null;
    }

    /**
     * Sets the value that is used when no match is found for a given parameter.
     * @param s
     */
    public void setFallbackValue(String s){
        if(parameter instanceof StringLiteral){
            ((StringLiteral)parameter).setValue(s);
        }
        else if(parameter instanceof Recode2String){
            ((Recode2String)parameter).setFallbackValue(new StringLiteral(s));
        }
    }

    /**
     * Gets the Double value, if any, associated to {@code key} in the inner {@code
     * Recode}.
     * @param s
     * @return
     */
    public String getItemValue(String s){
        if(parameter instanceof StringLiteral){
            return ((StringLiteral)parameter).getValue(null);
        }
        else if(parameter instanceof Recode2String){
            try {
                StringParameter stringParameter = ((Recode2String)parameter).getMapItemValue(s);
                if(stringParameter != null){
                    return stringParameter.getValue(null);
                }
            } catch (ParameterException e) {
            }
        }
        return null;
    }
    /**
     * Gets the ith key of the inner {@code Recode}.
     * @param i
     * @return
     */
    public String getKey(int i){
        if(parameter instanceof StringLiteral){
            return "";
        }
        else if(parameter instanceof Recode2String){
            return ((Recode2String)parameter).getMapItemKey(i);
        }
        return "";
    }

    /**
     * Sets the ith key of the inner {@code Recode}.
     * @param i
     * @param key
     */
    public void setKey(int i, String key){
        if(parameter instanceof StringLiteral){
            throw new UnsupportedOperationException("A literal does not have a ith key.");
        }
        else if(parameter instanceof Recode2String){
            ((Recode2String)parameter).setKey(i, key);
        }
    }

    /**
     * Adds an item in the inner {@code Recode}.
     * @param key
     * @param value
     */
    public void addItem(String key, String value){
        if(parameter instanceof StringLiteral){
            Recode2String temp = new Recode2String(((StringLiteral)parameter), new StringAttribute(field));
            temp.addMapItem(key, new StringLiteral(value));
            setParameter(temp);
        }
        else if(parameter instanceof Recode2String){
            ((Recode2String)parameter).addMapItem(key, new StringLiteral(value));
        }
    }

    /**
     * Removes an item from the inner {@code Recode}.
     * @param i
     */
    public void removeItem(int i){
        if(parameter instanceof Recode2String){
            Recode2String recode2String = (Recode2String)parameter;
            recode2String.removeMapItem(i);
            if(recode2String.getNumMapItem() == 0){
                setParameter(new StringLiteral(recode2String.getFallbackValue().getValue(null)));
            }
        }
    }

    /**
     * Removes an item from the inner {@code Recode}.
     * @param key
     */
    public void removeItem(String key){
        if(parameter instanceof Recode2String){
            Recode2String recode2String = (Recode2String)parameter;
            recode2String.removeMapItem(key);
            if(recode2String.getNumMapItem() == 0){
                setParameter(new StringLiteral(recode2String.getFallbackValue().getValue(null)));
            }
        }
    }
}
