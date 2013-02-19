/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.parameter;

import java.util.*;
import java.util.Map.Entry;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.MapItemType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import net.opengis.se._2_0.core.RecodeType;
import org.apache.log4j.Logger;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Recoding is defined as the "transformation of discrete values to any other values".
 * It is a way to map values of one type to values of another (but eventually the same)
 * type.
 * @author Maxence Laurent, Alexis Guéganno
 * @param <ToType> One of the SeParameter child types.
 * @param <FallbackType> The literal type associated to ToType. it is used to define the default value,
 * when an input value can't be processed for whatever reason.
 */
public abstract class Recode<ToType extends SeParameter, FallbackType extends ToType> extends AbstractSymbolizerNode
                implements SeParameter {
    private static final I18n I18N = I18nFactory.getI18n(Recode.class);
    private static final Logger LOGGER = Logger.getLogger(Recode.class);
    
    private FallbackType fallbackValue;
    private StringParameter lookupValue;
    private LinkedHashMap<String, ToType> mapItems;

    /**
     * Build a new instance of Recode with an empty map of items.
     */
    protected Recode(){
        mapItems = new LinkedHashMap<String, ToType>();
    }

    /**
     *
     * @param fallbackValue
     * @param lookupValue
     */
    public Recode(FallbackType fallbackValue, StringParameter lookupValue) {
        this.fallbackValue = fallbackValue;
        this.lookupValue = lookupValue;
        mapItems = new LinkedHashMap<String, ToType>();
    }

    /**
     * Set the value that will be used if a data can't be processed well.
     * @param fallbackValue
     */
    public void setFallbackValue(FallbackType fallbackValue) {
        this.fallbackValue = fallbackValue;
        if(this.fallbackValue != null){
                this.fallbackValue.setParent(this);
        }
    }

    /**
     * Get the value that will be used if a data can't be processed well.
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
        if(this.lookupValue != null){
                this.lookupValue.setParent(this);
        }
    }

    /**
     * Get the value that will be used to retrieve data to be processed.
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
     * Add a new map item or set the value associated to {@code key} to {@code
     * value}, if key is already stored in this recode.
     * @param key
     * @param value
     */
    public void addMapItem(String key, ToType value) {
            mapItems.put(key, value);
            value.setParent(this);
    }

    /**
     * Get the value associated to the <code>key</code> given in argument, if any.
     * @param key
     * @return
     * a {@code ToType} instance if something has been found in the map. {@code
     * null} otherwise.
     */
    public ToType getMapItemValue(String key) {
        return mapItems.get(key);
    }

    /**
     * Gets the set containing the keys that define this Recode.
     * @return A deep copy of the inner key set.
     */
    public Set<String> getKeys() {
        return new HashSet<String>(mapItems.keySet());
    }

    /**
     * Get the ith <code>MapItem</code> in this <code>Recode</code> instance
     * @param i
     * @return
     */
    public Map.Entry<String, ToType> getMapItem(int i){
        Set<Map.Entry<String, ToType>> entries = mapItems.entrySet();
        Iterator<Map.Entry<String, ToType>> it = entries.iterator();
        int index=0;
        while(it.hasNext()){
            Map.Entry<String, ToType> cur = it.next();
            if(index == i){
                return cur;
            }
            index++;
        }
        return null;
    }

    /**
     * Get the value stored in the ith <code>MapItem</code> in this <code>Recode</code> instance.
     * @param i
     * @return
     */
    public ToType getMapItemValue(int i) {
        return getMapItem(i).getValue();
    }

    /**
     * Get the key stored in the ith <code>MapItem</code> in this <code>Recode</code> instance.
     * @param i
     * @return
     */
    public String getMapItemKey(int i) {
        return getMapItem(i).getKey();
    }

    /**
     * Remove the <code>MapItem</code> whose key is <code>key</code>
     * @param key
     */
    public void removeMapItem(String key) {
        mapItems.remove(key);
    }
    /**
     * Remove the ith <code>MapItem</code>
     * @param i
     */
	public void removeMapItem(int i){
        String key = getMapItemKey(i);
        removeMapItem(key);
	}

        /**
         * Get the value associated to the key sored in {@code sds} at index
         * {@code fid}.
         * @param sds
         * @param fid
         * @return
         * A {@code ToType} instance. If the feature found in {@code sds} at
         * {@code fid} does not match anything in the underlying map, the {@code
         * fallBackValue} is returned.</p>
         * <p>If an error of any kind is catched, the {@code fallBackValue} is
         * returned, and a message is print using the {@code Logger}.
         */
    public ToType getParameter(DataSet sds, long fid) {
        String key = "";
        try {
            key = lookupValue.getValue(sds, fid);
            ToType ret = getMapItemValue(key);
            return ret == null ? fallbackValue : ret;
        } catch (Exception e) {
            LOGGER.error(I18N.tr("Fallback ({0})",key),e);
            return fallbackValue;
        }
    }

    /**
    * Get the value associated to the key sored in {@code map}. The needed value
    * will be retrieved using the 
    * @param map
    * @return
    * A {@code ToType} instance. If the feature found in {@code sds} at
    * {@code fid} does not match anything in the underlying map, the {@code
    * fallBackValue} is returned.</p>
    * <p>If an error of any kind is catched, the {@code fallBackValue} is
    * returned, and a message is print using the {@code Logger}.
    */
    public ToType getParameter(Map<String,Value> map) {
        String key = "";
        try {
            key = lookupValue.getValue(map);
            ToType ret = getMapItemValue(key);
            return ret == null ? fallbackValue : ret;
        } catch (Exception e) {
            LOGGER.error(I18N.tr("Fallback ({0})",key),e);
            return fallbackValue;
        }
    }

    /**
     * Set the ith key of the underlying {@code LinkedHashMap} to key. As we
     * don't want to modify the order of the associated {@code LinkedList}, we
     * are forced to re-create the {@code LinkedHashMap}. It is really, really,
     * inefficient.
     * @param index
     * @param key
     */
    public void setKey(int index, String key){
        Set<Map.Entry<String, ToType>> entries = mapItems.entrySet();
        Iterator<Map.Entry<String, ToType>> it = entries.iterator();
        LinkedHashMap lhm = new LinkedHashMap(mapItems.size());
        int i = 0;
        while(it.hasNext()){
            Map.Entry<String, ToType> cur = it.next();
            String k = cur.getKey();
            ToType tt = cur.getValue();
            if(index == i){
                lhm.put(key, tt);
            } else {
                lhm.put(k, tt);
            }
            i++;
        }
        mapItems = lhm;
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
        Iterator<Map.Entry<String, ToType>> it = mapItems.entrySet().iterator();
        net.opengis.fes._2.ObjectFactory off = new net.opengis.fes._2.ObjectFactory();
        while(it.hasNext()) {
            Map.Entry<String, ToType> m = it.next();
            MapItemType mt = new MapItemType();
            mt.setValue(m.getValue().getJAXBParameterValueType());
            mt.setKey(m.getKey());
            mi.add(mt);
        }
        ObjectFactory of = new ObjectFactory();
        return of.createRecode(r);
    }

    @Override
    public List<SymbolizerNode> getChildren() {
        List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
        ls.add(lookupValue);
        for(Entry<String,ToType> e : mapItems.entrySet()){
                ls.add(e.getValue());
        }
        return ls;
    }


}
