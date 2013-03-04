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
package org.orbisgis.legend.thematic.map;

import org.orbisgis.legend.structure.parameter.ParameterVisitor;
import org.orbisgis.legend.structure.recode.FieldAggregatorVisitor;
import org.orbisgis.legend.structure.recode.KeysRetriever;
import org.orbisgis.legend.structure.recode.SetFieldVisitor;
import org.orbisgis.legend.thematic.SymbolParameters;
import org.orbisgis.legend.thematic.SymbolizerLegend;

import java.util.*;

/**
 * This abstract class gathers methods that will be common to instances of unique value classifications and interval
 * classifications. It presents a mapping between the keys and configurations of the associated classification.
 * @author alexis
 */
public abstract class MappedLegend<T,U extends SymbolParameters> extends SymbolizerLegend implements Map<T,U> {

    /**
     * Apply the given visitor to all the Legend that are used in this {@code MappedLegend}.
     * @param rpv The visitor that will be used in each inner {@code MappedLegend}.
     */
    public abstract void applyGlobalVisitor(ParameterVisitor rpv);

    /**
     * Gets the analysis field.
     * @return The name of the analysis field.
     */
    public String getLookupFieldName(){
        FieldAggregatorVisitor fav = new FieldAggregatorVisitor();
        applyGlobalVisitor(fav);
        Set<String> fields = fav.getFields();
        if(fields.size() > 1){
            throw new IllegalStateException("We won't be able to handle a MappedLegend with two analysis fields.");
        } else {
            Iterator<String> it = fields.iterator();
            if(it.hasNext()){
                return it.next();
            } else {
                return "";
            }
        }
    }

    /**
     * Use {@code field} as the field name on which the analysis will be made.
     * @param field The new field name.
     */
    public void setAnalysisField(String field) {
        SetFieldVisitor sfv = new SetFieldVisitor(field);
        applyGlobalVisitor(sfv);
    }

    /**
     * Gets the keys currently used in the analysis.
     * @return The keys used in a Set of T.
     */
    @Override
    public SortedSet<T> keySet() {
        KeysRetriever kr = new KeysRetriever();
        applyGlobalVisitor(kr);
        return (SortedSet<T>) kr.getKeys();
    }

    /**
     * Returns the number of elements contained in this classification.
     * @return The number of elements contained in this classification.
     */
    @Override
    public int size(){
        return keySet().size();
    }

    /**
     * Returns true if there is nothing in this map.
     * @return {@code true} if we don't have any key-value mapping.
     */
    @Override
    public boolean isEmpty(){
        return keySet().isEmpty();
    }

    /**
     * Put all the entries found in the given map as entries in this MappedLegend.
     * @param input The input map.
     */
    @Override
    public void putAll(Map<? extends T, ? extends U> input){
        Set<? extends Map.Entry<? extends T, ? extends U>> entries = input.entrySet();
        for(Map.Entry<? extends T, ? extends U> m : entries){
            put(m.getKey(),m.getValue());
        }
    }

    /**
     * Gets a {@code Set} representation of the key-value mapping we have in this {@code MappedLegend}.
     * @return The mapping in a set of {@code Map.Entry}.
     */
    public Set<Map.Entry<T, U>> entrySet(){
        Set<T> keys = keySet();
        HashSet<Map.Entry<T,U>> out = new HashSet<Map.Entry<T,U>>();
        for(T s : keys){
            Map.Entry<T, U> ent = new MappedLegendEntry(s, get(s));
            out.add(ent);
        }
        return out;
    }

    /**
     * Removes all the entries in this classification.
     */
    public void clear() {
        Set<T> keys = keySet();
        for (T next : keys) {
            remove(next);
        }
    }

    /**
     * Checks if {@code lp} is a value contained in this {@code MappedLegend}
     * @param lp    The value we search
     * @return {@code true} if we have a mapping to {@code lp}.
     */
    @Override
    public boolean containsValue(Object lp){
        return values().contains(lp);
    }

    /**
     * Checks whether s is a key of this mapping.
     * @param s The key we search
     * @return true if we have a mapping with s as a key.
     * @throws ClassCastException if the key is of an inappropriate type for this map
     */
    @Override
    public boolean containsKey(Object s){
        return keySet().contains(s);
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Map){
            return entrySet().equals(((Map) o).entrySet());
        } else {
            return false;
        }
    }

    /**
     * Gets all the LineParameters stored in this RecodedLine, default one excepted.
     * @return The {@link org.orbisgis.legend.thematic.LineParameters} in a Collection.
     */
    public Collection<U> values(){
        Set<T> keys = keySet();
        LinkedList<U> out = new LinkedList<U>();
        for(T s : keys){
            out.add(get(s));
        }
        return out;
    }

    /**
     * MapEntry dedicated to MappedLegend instances.
     */
    protected class MappedLegendEntry implements Map.Entry<T, U>{

        private U lp;
        private final T s;

        public MappedLegendEntry(T s, U lp){
            this.s = s;
            this.lp = lp;
        }

        @Override
        public T getKey() {
            return s;
        }

        @Override
        public U getValue() {
            return lp;
        }

        @Override
        public U setValue(U value) {
            MappedLegend outer = MappedLegend.this;
            if(value == null){
                throw new NullPointerException("Null values are not allowed in RecodedLines.");
            }
            outer.put(s,value);
            U ret = lp;
            lp = value;
            return ret;
        }

        @Override
        public boolean equals(Object o){
            if(o instanceof Map.Entry){
                Map.Entry me = (Map.Entry) o;
                return (s == null? me.getKey() == null : s.equals(me.getKey())) &&
                            (lp == null ? me.getValue() == null : lp.equals(me.getValue()));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode(){
            return (s==null   ? 0 : s.hashCode()) ^ (lp==null ? 0 : lp.hashCode());
        }
    }
}
