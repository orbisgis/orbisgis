/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */


package org.orbisgis.core.renderer.se.parameter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.LiteralType;
import net.opengis.se._2_0.core.MapItemType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import net.opengis.se._2_0.core.RecodeType;
import org.gdms.data.DataSource;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

/**
 * Recoding is defined as the "transformation of discrete values to any other values".
 * It is a way to map values of one type to values of another (but eventually the same)
 * type.
 * @author maxence, alexis
 * @param <ToType> One of the SeParameter child types.
 * @param <FallbackType> The literal type associated to ToType. it is used to define the default value,
 * when an input value can't be processed for whatever reason.
 */
public abstract class Recode<ToType extends SeParameter, FallbackType extends ToType> implements SeParameter {

    private FallbackType fallbackValue;
    private StringParameter lookupValue;
    private ArrayList<MapItem<ToType>> mapItems; // TODO switch to hash table <k: String, v: ToType>
    
    /**
     * Build a new instance of Recode with an empty map of items.
     */
    protected Recode(){
        mapItems = new ArrayList<MapItem<ToType>>();
    }

    /**
     * 
     * @param fallbackValue
     * @param lookupValue 
     */
    public Recode(FallbackType fallbackValue, StringParameter lookupValue) {
        this.fallbackValue = fallbackValue;
        this.lookupValue = lookupValue;
        mapItems = new ArrayList<MapItem<ToType>>();
    }

    @Override
    public final HashSet<String> dependsOnFeature() {
        HashSet<String> out = this.getLookupValue().dependsOnFeature();
        for (int i = 0; i < this.getNumMapItem(); i++) {
            out.addAll(this.getMapItemValue(i).dependsOnFeature());
        }
        return out;
    }

    /**
     * Set the value that will be used if a data can't be processed well.
     * @param fallbackValue 
     */
    public void setFallbackValue(FallbackType fallbackValue) {
        this.fallbackValue = fallbackValue;
    }

    /**
     * Get the value that will be used if a data can't be processed well.
     * @param fallbackValue 
     */
    public FallbackType getFallbackValue() {
        return fallbackValue;
    }

    /**
     * Set the value that will be used to retrieve data to be processed.
     * @param lookupValue 
     */
    // TODO  On doit pouvoir récuperer des string ou des couleurs
    public void setLookupValue(StringParameter lookupValue) {
        this.lookupValue = lookupValue;
    }

    /**
     * Get the value that will be used to retrieve data to be processed.
     * @param lookupValue 
     */
    public StringParameter getLookupValue() {
        return lookupValue;
    }

    /**
     * Return the number of unique value defined within the function.
     *  @return number of unique value
     */
    public int getNumMapItem() {
        return mapItems.size();
    }

    /**
     * Add a new map item
     * @param key
     * @param value
     * @return index of new map item or -1 when key already exists
     */
    public int addMapItem(Literal key, ToType value) {
        MapItem<ToType> item = new MapItem<ToType>(value, key);

        if (mapItems.contains(item)) {
			return -1;
        } else {
            mapItems.add(item);
        }

		return mapItems.size() - 1;
    }

    /**
     * Get the value associated to the <code>key</code> given in argument, if any.
     * @param key
     * @return 
     */
    public ToType getMapItemValue(Literal key) {
        MapItem<ToType> item = new MapItem<ToType>(null, key);
        return mapItems.get(mapItems.indexOf(item)).getValue();
    }

    /**
     * Get the ith <code>MapItem</code> in this <code>Recode</code> instance
     * @param i
     * @return 
     */
    public MapItem<ToType> getMapItem(int i){
        return mapItems.get(i);
    }

    /**
     * Get the value stored in the ith <code>MapItem</code> in this <code>Recode</code> instance.
     * @param i
     * @return 
     */
    public ToType getMapItemValue(int i) {
        return mapItems.get(i).getValue();
    }

    /**
     * Get the key stored in the ith <code>MapItem</code> in this <code>Recode</code> instance.
     * @param i
     * @return 
     */
    public Literal getMapItemKey(int i) {
        return mapItems.get(i).getKey();
    }

    /**
     * Remove the <code>MapItem</code> whose key is <code>key</code>
     * @param key 
     */
    public void removeMapItem(Literal key) {
        MapItem<ToType> item = new MapItem<ToType>(null, key);
        mapItems.remove(item);
    }
    /**
     * Remove the ith <code>MapItem</code>
     * @param key 
     */
	public void removeMapItem(int i){
		if (i >= 0 && i < mapItems.size()){
			mapItems.remove(i);
		}
	}

        /**
         * Get the value associated to the key sored in {@code sds} at index
         * {@code fid}.
         * @param sds
         * @param fid
         * @return 
         */
    public ToType getParameter(DataSource sds, long fid) {
        String key = "";
        try {
            key = lookupValue.getValue(sds, fid);
            return getMapItemValue(new StringLiteral(key));
        } catch (Exception e) {
            Services.getOutputManager().println("Fallback (" + key + "): " + e, Color.DARK_GRAY);
            return fallbackValue;
        }
    }


    @Override
    public ParameterValueType getJAXBParameterValueType()
    {
        ParameterValueType p = new ParameterValueType();
        p.getContent().add(this.getJAXBExpressionType());
        return p;
    }

    @Override
    public JAXBElement<?> getJAXBExpressionType() {
        RecodeType r = new RecodeType();

        if (fallbackValue != null) {
            r.setFallbackValue(fallbackValue.toString());
        }
        if (lookupValue != null) {
            r.setLookupValue(lookupValue.getJAXBParameterValueType());
        }

        List<MapItemType> mi = r.getMapItem();

        for (MapItem<ToType> m : mapItems) {
            MapItemType mt = new MapItemType();
            mt.setValue(m.getValue().getJAXBParameterValueType());
            mt.setKey((LiteralType)(m.getKey().getJAXBExpressionType().getValue()));
            mi.add(mt);
        }
        ObjectFactory of = new ObjectFactory();
        return of.createRecode(r);
    }
}
