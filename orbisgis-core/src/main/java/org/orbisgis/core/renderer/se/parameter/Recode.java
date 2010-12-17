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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.se.MapItemType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.RecodeType;

import org.orbisgis.core.renderer.se.parameter.string.*;

public abstract class Recode<ToType extends SeParameter, FallbackType extends ToType> implements SeParameter {

    protected Recode(){
        mapItems = new ArrayList<MapItem<ToType>>();
    }

    public Recode(FallbackType fallbackValue, StringParameter lookupValue) {
        this.fallbackValue = fallbackValue;
        this.lookupValue = lookupValue;
        mapItems = new ArrayList<MapItem<ToType>>();
    }

    @Override
    public final boolean dependsOnFeature() {
        if (this.getLookupValue().dependsOnFeature()) {
            return true;
        }

        int i;
        for (i = 0; i < this.getNumMapItem(); i++) {
            if (this.getMapItemValue(i).dependsOnFeature()) {
                return true;
            }
        }

        return false;
    }

    public void setFallbackValue(FallbackType fallbackValue) {
        this.fallbackValue = fallbackValue;
    }

    public FallbackType getFallbackValue() {
        return fallbackValue;
    }

    // TODO  On doit pouvoir récuperer des string ou des couleurs
    public void setLookupValue(StringParameter lookupValue) {
        this.lookupValue = lookupValue;
    }

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
    public int addMapItem(String key, ToType value) {
        MapItem<ToType> item = new MapItem<ToType>(value, key);

        if (mapItems.contains(item)) {
			return -1;
        } else {
            mapItems.add(item);
        }

		return mapItems.size() - 1;
    }

    public ToType getMapItemValue(String key) {
        MapItem<ToType> item = new MapItem<ToType>(null, key);
        return mapItems.get(mapItems.indexOf(item)).getValue();
    }


	public MapItem<ToType> getMapItem(int i){
		return mapItems.get(i);
	}


    public ToType getMapItemValue(int i) {
        return mapItems.get(i).getValue();
    }

    public String getMapItemKey(int i) {
        return mapItems.get(i).getKey();
    }

    public void removeMapItem(String key) {
        MapItem<ToType> item = new MapItem<ToType>(null, key);
        mapItems.remove(item);
    }

	public void removeMapItem(int i){
		if (i >= 0 && i < mapItems.size()){
			mapItems.remove(i);
		}
	}

    public ToType getParameter(SpatialDataSourceDecorator sds, long fid) {
        try {
            String key = lookupValue.getValue(sds, fid);
            return getMapItemValue(key);
        } catch (Exception e) {
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
    public JAXBElement<? extends ExpressionType> getJAXBExpressionType() {
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
            mt.setKey(m.getKey());
            mi.add(mt);
        }
        ObjectFactory of = new ObjectFactory();
        return of.createRecode(r);
    }

    protected FallbackType fallbackValue;
    protected StringParameter lookupValue;
    protected ArrayList<MapItem<ToType>> mapItems; // TODO switch to hash table <k: String, v: ToType>
}
