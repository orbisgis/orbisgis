/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2016 IRSTV (FR CNRS 2488)
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

import java.awt.Color;

import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameter;
import org.orbisgis.coremap.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.coremap.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.coremap.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.coremap.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.legend.structure.parameter.AbstractAttributeLegend;

/**
 * RecodedColor class translated from the old Scala code.
 *
 * @author Sylvain PALOMINOS
 */
public class RecodedColor extends RecodedLegend implements AbstractAttributeLegend {

    private ColorParameter parameter = new ColorLiteral();

    /**
     * Tries to build a RecodedColor using the {@code ColorParameter} given in
     * argument.
     * @param colorParameter
     */
    public RecodedColor(ColorParameter colorParameter){
        setParameter(colorParameter);
    }

    /**
     * Gets the underlying parameter.
     * @return
     */
    public ColorParameter getParameter(){
        return parameter;
    }

    /**
     * Sets parameter to s
     * @param seParameter
     * @throws IllegalArgumentException if s is neither a Recode2String nor a StringLiteral
     */
    public void setParameter(SeParameter seParameter){
        if(seParameter instanceof ColorLiteral){
            parameter = (ColorLiteral)seParameter;
            fireTypeChanged();
        }
        else if(seParameter instanceof Recode2Color){
            parameter = (Recode2Color)seParameter;
            field = getValueReference().getColumnName();
            fireTypeChanged();
        }
        else{
            throw new IllegalArgumentException("This class must be built from a  string recode or literal.");
        }
    }

    /**
     * Gets the number of elements registered in this analysis.
     * @return
     */
    public int size(){
        if(parameter instanceof Recode2Color){
            return ((Recode2Color)parameter).getNumMapItem();
        }
        else{
            return 0;
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
     * Gets the Color value, if any, associated to {@code key} in the inner {@code
     * Recode}.
     * @param i
     * @return
     */
    public Color getItemValue(int i){
        if(parameter instanceof ColorLiteral){
            return ((ColorLiteral)parameter).getColor(null);
        }
        else if(parameter instanceof Recode2Color){
            if(((Recode2Color)parameter).getMapItemValue(i) != null){
                try {
                    return ((Recode2Color)parameter).getMapItemValue(i).getColor(null);
                } catch (ParameterException e) {
                }
            }
        }
        return null;
    }

    /**
     * Gets the Color value, if any, associated to {@code key} in the inner {@code
     * Recode}.
     * @param s
     * @return
     */
    public Color getItemValue(String s) {
        if(parameter instanceof ColorLiteral){
            return ((ColorLiteral)parameter).getColor(null);
        }
        else if(parameter instanceof Recode2Color){
            if(((Recode2Color)parameter).getMapItemValue(s) != null){
                try {
                    return ((Recode2Color)parameter).getMapItemValue(s).getColor(null);
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
    public Color getFallbackValue(){
        if(parameter instanceof ColorLiteral){
            return ((ColorLiteral)parameter).getColor(null);
        }
        else if(parameter instanceof Recode2Color){
            return ((Recode2Color)parameter).getFallbackValue().getColor(null);
        }
        return null;
    }

    /**
     * Sets the value that is used when no match is found for a given parameter.
     * @param c
     */
    public void setFallbackValue(Color c){
        if(parameter instanceof ColorLiteral){
            ((ColorLiteral)parameter).setColor(c);
        }
        else if(parameter instanceof Recode2Color){
            ((Recode2Color)parameter).setFallbackValue(new ColorLiteral(c));
        }
    }

    /**
     * Gets the ith key of the inner {@code Recode}.
     * @param i
     * @return
     */
    public String getKey(int i){
        if(parameter instanceof ColorLiteral){
            return "";
        }
        else if(parameter instanceof Recode2Color){
            return ((Recode2Color)parameter).getMapItemKey(i);
        }
        return "";
    }

    /**
     * Sets the ith key of the inner {@code Recode}.
     * @param i
     * @param key
     */
    public void setKey(int i, String key){
        if(parameter instanceof ColorLiteral){
            throw new UnsupportedOperationException("A literal does not have a ith key.");
        }
        else if(parameter instanceof Recode2Color){
            ((Recode2Color)parameter).setKey(i, key);
        }
    }

    /**
     * Adds an item in the inner {@code Recode}.
     * @param key
     * @param value
     */
    public void addItem(String key, Color value){
        if(parameter instanceof ColorLiteral){
            Recode2Color temp = new Recode2Color((ColorLiteral)parameter, new StringAttribute(field));
            temp.addMapItem(key, new ColorLiteral(value));
            setParameter(temp);
        }
        else if(parameter instanceof Recode2Color){
            ((Recode2Color)parameter).addMapItem(key, new ColorLiteral(value));
        }
    }

    /**
     * Removes an item from the inner {@code Recode}.
     * @param i
     */
    public void removeItem(int i){
        if(parameter instanceof Recode2Color){
            Recode2Color recode2Color = ((Recode2Color)parameter);
            recode2Color.removeMapItem(i);
            if(recode2Color.getNumMapItem() == 0){
                setParameter(new ColorLiteral(recode2Color.getFallbackValue().getColor(null)));
            }
        }
    }

    /**
     * Removes an item from the inner {@code Recode}.
     * @param key
     */
    public void removeItem(String key){
        if(parameter instanceof Recode2Color){
            Recode2Color recode2Color = (Recode2Color)parameter;
            recode2Color.removeMapItem(key);
            if(recode2Color.getNumMapItem() == 0){
                setParameter(new ColorLiteral(recode2Color.getFallbackValue().getColor(null)));
            }
        }
    }
}
