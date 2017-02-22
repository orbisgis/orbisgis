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
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.Recode2Real;
import org.orbisgis.coremap.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.legend.structure.parameter.AbstractAttributeLegend;
import org.orbisgis.legend.structure.parameter.NumericLegend;

/**
 * @author Sylvain PALOMINOS
  */
public class RecodedReal extends RecodedLegend implements AbstractAttributeLegend, NumericLegend {

    RealParameter parameter = new RealLiteral();

    public RecodedReal(RealParameter param){
        setParameter(param);
    }

    public RealParameter getParameter(){
        return parameter;
    }

    /**
     * Sets parameter to s
     * @param s
     * @throws IllegalArgumentException if s is neither a Recode2String nor a StringLiteral
     */
    public void setParameter(SeParameter s){
        if(s instanceof RealLiteral){
            parameter = (RealLiteral)s;
            fireTypeChanged();
        }
        else if(s instanceof Recode2Real){
            parameter = (Recode2Real)s;
            field = getValueReference().getColumnName();
            fireTypeChanged();
        }
        else{
            throw new IllegalArgumentException("This class must be built from a  string recode or literal.");
        }
    }

    public int size(){
        if(parameter instanceof RealLiteral){
            return 0;
        }
        else if(parameter instanceof Recode2Real){
            return ((Recode2Real)parameter).getNumMapItem();
        }
        return 0;
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
    public Double getItemValue(int i){
        if(parameter instanceof RealLiteral){
            return ((RealLiteral)parameter).getValue(null);
        }
        else if(parameter instanceof Recode2Real){
            try {
                return ((Recode2Real)parameter).getMapItemValue(i).getValue(null);
            } catch (ParameterException e) {
            }
        }
        return null;
    }

    /**
     * Gets the Double value, if any, associated to {@code key} in the inner {@code
     * Recode}.
     * @param s
     * @return
     */
    public Double getItemValue(String s){
        if(parameter instanceof RealLiteral){
            return ((RealLiteral)parameter).getValue(null);
        }
        else if(parameter instanceof Recode2Real){
            try {
                RealParameter realParameter = ((Recode2Real)parameter).getMapItemValue(s);
                if(realParameter instanceof RealParameter){
                    return realParameter.getValue(null);
                }
                else{
                    return Double.NaN;
                }
            } catch (ParameterException e) {
            }
        }
        return Double.NaN;
    }

    /**
     * Gets the value used when there is no match for a given parameter.
     * @return
     */
    public Double getFallbackValue(){
        if(parameter instanceof RealLiteral){
            return ((RealLiteral)parameter).getValue(null);
        }
        else if(parameter instanceof Recode2Real){
            return ((Recode2Real)parameter).getFallbackValue().getValue(null);
        }
        return Double.NaN;
    }

    /**
     * Sets the value that is used when no match is found for a given parameter.
     * @param d
     */
    public void setFallbackValue(Double d){
        if(parameter instanceof RealLiteral){
            ((RealLiteral)parameter).setValue(d);
        }
        else if(parameter instanceof Recode2Real){
            ((Recode2Real)parameter).setFallbackValue(new RealLiteral(d));
        }
    }

    /**
     * Gets the ith key of the inner {@code Recode}.
     * @param i
     * @return
     */
    public String getKey(int i){
        if(parameter instanceof RealLiteral){
            return "";
        }
        else if(parameter instanceof Recode2Real){
            return ((Recode2Real)parameter).getMapItemKey(i);
        }
        return "";
    }

    /**
     * Sets the ith key of the inner {@code Recode}.
     * @param i The range of the key in the map.
     * @param key The new ith key
     */
    public void setKey(int i, String key){
        if(parameter instanceof RealLiteral){
            throw new UnsupportedOperationException("A literal does not have a ith key.");
        }
        else if(parameter instanceof Recode2Real){
            ((Recode2Real)parameter).setKey(i, key);
        }
    }

    /**
     * Adds an item in the inner {@code Recode}.
     * @param key Adds the new
     * @param value
     */
    public void addItem(String key, Double value){
        if(parameter instanceof RealLiteral){
            Recode2Real temp = new Recode2Real(((RealLiteral)parameter), new StringAttribute(field));
            temp.addMapItem(key, new RealLiteral(value));
            setParameter(temp);
        }
        else if(parameter instanceof Recode2Real){
            ((Recode2Real)parameter).addMapItem(key, new RealLiteral(value));
        }
    }

    /**
     * Removes an item from the inner {@code Recode}.
     * @param i The index of the item to be removed.
     */
    public void removeItem(int i){
        if(parameter instanceof Recode2Real){
            Recode2Real recode2Real = (Recode2Real)parameter;
            recode2Real.removeMapItem(i);
            if(recode2Real.getNumMapItem() == 0){
                setParameter(new RealLiteral(recode2Real.getFallbackValue().getValue(null)));
            }
        }
    }

    /**
     * Removes an item from the inner {@code Recode}.
     * @param key The key of the item to be removed.
     */
    public void removeItem(String key){
        if(parameter instanceof Recode2Real){
            Recode2Real recode2Real = (Recode2Real)parameter;
            recode2Real.removeMapItem(key);
            if(recode2Real.getNumMapItem() == 0){
                setParameter(new RealLiteral(recode2Real.getFallbackValue().getValue(null)));
            }
        }
    }
}
