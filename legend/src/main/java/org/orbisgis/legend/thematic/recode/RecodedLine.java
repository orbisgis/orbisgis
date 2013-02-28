/*
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
package org.orbisgis.legend.thematic.recode;

import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.structure.recode.*;
import org.orbisgis.legend.structure.stroke.RecodedPenStroke;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.uom.StrokeUom;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Wrapper for lines made of a {@code PenStroke} where parameters are made of
 * {@code Recode} instances on a common field or of {@code Literal}.
 * @author Alexis Guéganno
 */
public class RecodedLine extends AbstractRecodedLegend implements StrokeUom, Map<String, LineParameters> {

        private final LineSymbolizer ls;
        private final RecodedPenStroke ps;

        /**
         * Default constructor. Builds a {@code RecodedLine} from scratch using a constant {@link LineSymbolizer} that
         * embeds a constant {@link PenStroke}.
         */
        public RecodedLine(){
            this(new LineSymbolizer());
        }

        /**
         * Builds a new {@code RecodedLine} instance from the given {@link LineSymbolizer}. Take care to validate the
         * configuration of the symbolizer before calling this constructor.
         * @param sym The original LineSymbolizer.
         * @throws ClassCastException
         * @throws UnsupportedOperationException If the inner stroke is not a {@link PenStroke} instance.
         * @throws IllegalStateException If the inner parameters have different analysis fields.
         */
        public RecodedLine(LineSymbolizer sym){
            ls=sym;
            Stroke p = ls.getStroke();
            if(p instanceof PenStroke){
                ps=new RecodedPenStroke((PenStroke)p);
                FieldAggregatorVisitor fav = new FieldAggregatorVisitor();
                applyGlobalVisitor(fav);
                String f = getAnalysisField();
                if(!f.isEmpty()){
                    SetFieldVisitor sfv = new SetFieldVisitor(f);
                    applyGlobalVisitor(sfv);
                }
            } else {
                throw new UnsupportedOperationException("Can't build a RecodedLine with such a Stroke: "+p.getClass().getName());
            }
        }

        /**
         * Gets the wrapper that manages the width of the line.
         * @return the recoded width
         */
        public RecodedReal getLineWidth(){
                return ps.getWidthLegend();
        }

        /**
         * Gets the wrapper that manages the opacity of the line.
         * @return the recoded opacity
         */
        public RecodedReal getLineOpacity(){
                return (RecodedReal) ps.getFillLegend().getFillOpacityLegend();
        }

        /**
         * Gets the wrapper that manages the color of the line.
         * @return the recoded color
         */
        public RecodedColor getLineColor(){
                return (RecodedColor) ps.getFillLegend().getFillColorLegend();
        }

        /**
         * Gets the wrapper that manages the dash pattern of the line.
         * @return the recoded dash
         */
        public RecodedString getLineDash() {
                return ps.getDashLegend();
        }

        /**
         * Gets the {@link LineParameters} that is used to build the line associated to {@code key}.
         * @param objKey The key of the map
         * @return The {@link LineParameters} instance associated to {@code key}.
         */
        public LineParameters get(Object objKey){
            String key = (String) objKey;
            Color c = getLineColor().getItemValue(key);
            c = c==null ? getLineColor().getFallbackValue() : c;
            Double op = getLineOpacity().getItemValue(key);
            op = op==null ? getLineOpacity().getFallbackValue() : op;
            Double w = getLineWidth().getItemValue(key);
            w = w==null ? getLineWidth().getFallbackValue() : w;
            String d = getLineDash().getItemValue(key);
            d = d==null ? getLineDash().getFallbackValue() : d;
            return new LineParameters(c,op,w,d);
        }

        /**
         * Puts the given line configuration in the unique values managed by this {@code RecodedLine}. If there was
         * already a value associated to {@code key}, the former configuration is returned by the method.
         * @param  key Key with which the specified value is to be associated
         * @param value  Value to be associated with the specified key
         * @return The former configuration associated to {@code key}, if any, {@code null} otherwise.
         * @throws NullPointerException if key or value are null.
         */
        public LineParameters put(String key, LineParameters value){
            if(key == null || value == null){
                throw new NullPointerException("We don't manage null as key");
            }
            LineParameters ret = keySet().contains(key) ? get(key) : null;
            getLineColor().addItem(key, value.getLineColor());
            getLineOpacity().addItem(key, value.getLineOpacity());
            getLineWidth().addItem(key, value.getLineWidth());
            getLineDash().addItem(key, value.getLineDash());
            return ret;
        }

        /**
         * Put all the entries found in the given map as entries in this RecodedLine.
         * @param input The input map.
         */
        public void putAll(Map<? extends String, ? extends LineParameters> input){
            Set<? extends Map.Entry<? extends String, ? extends LineParameters>> entries = input.entrySet();
            for(Map.Entry<? extends String, ? extends LineParameters> m : entries){
                put(m.getKey(),m.getValue());
            }
        }

        /**
         * Checks whether s is a key of this unique value mapping.
         * @param s The key we search
         * @return true if we have a mapping with s as a key.
         * @throws ClassCastException if the key is of an inappropriate type for this map
         */
        public boolean containsKey(Object s){
            return keySet().contains(s);
        }

        /**
         * Gets all the LineParameters stored in this RecodedLine, default one excepted.
         * @return The {@link LineParameters} in a Collection.
         */
        public Collection<LineParameters> values(){
            Set<String> keys = keySet();
            LinkedList<LineParameters> out = new LinkedList<LineParameters>();
            for(String s : keys){
                out.add(get(s));
            }
            return out;
        }

        /**
         * Checks if {@code lp} is a value contained in this {@code AbstractRecodedLegend}
         * @param lp    The value we search
         * @return {@code true} if we have a mapping to {@code lp}.
         */
        public boolean containsValue(Object lp){
            return values().contains(lp);
        }

        /**
         * Gets a {@code Set} representation of the key-value mapping we have in this {@code AbstractRecodedLegend}.
         * @return The mapping in a set of {@code Map.Entry}.
         */
        public Set<Map.Entry<String, LineParameters>> entrySet(){
            Set<String> keys = keySet();
            HashSet<Map.Entry<String,LineParameters>> out = new HashSet<Map.Entry<String,LineParameters>>();
            for(String s : keys){
                Map.Entry<String, LineParameters> ent = new RecodeMapEntry(s, get(s));
                out.add(ent);
            }
            return out;
        }

        @Override
        public boolean equals(Object o){
            if(o instanceof Map){
                return entrySet().equals(((Map) o).entrySet());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode(){
            int ret = 0;
            Set<Map.Entry<String, LineParameters>> entries = entrySet();
            for(Map.Entry m : entries){
                ret += m.hashCode();
            }
            return ret;
        }


        /**
         * Removes the mapping for the given key.
         * @param objKey The key we want to remove from this unique value analysis. If it's not a String, a
         *               {@link ClassCastException} will be thrown.
         * @return  The value previously associated to the given key, or null if there was not.
         */
        public LineParameters remove(Object objKey){
            String key = (String) objKey;
            if(key == null){
                throw new NullPointerException("We don't manage null as key");
            }
            LineParameters ret = keySet().contains(key) ? get(key) : null;
            getLineColor().removeItem(key);
            getLineDash().removeItem(key);
            getLineOpacity().removeItem(key);
            getLineWidth().removeItem(key);
            return ret;
        }

        /**
         * Removes all the entries in this unique value classification.
         */
        public void clear() {
            Set<String> keys = keySet();
            for (String next : keys) {
                remove(next);
            }
        }

        @Override
        public Symbolizer getSymbolizer() {
                return ls;
        }

        @Override
        public String getLegendTypeName() {
                return "Recoded Line";
        }

        @Override
        public Uom getStrokeUom() {
                return ls.getStroke().getUom();
        }

        @Override
        public void setStrokeUom(Uom u) {
                ls.getStroke().setUom(u);
        }

        @Override
        public List<RecodedLegend> getRecodedLegends() {
            return ps.getRecodedLegends();
        }

        protected class RecodeMapEntry implements Map.Entry<String, LineParameters>{

            private LineParameters lp;
            private final String s;

            public RecodeMapEntry(String s, LineParameters lp){
                this.s = s;
                this.lp = lp;
            }

            @Override
            public String getKey() {
                return s;
            }

            @Override
            public LineParameters getValue() {
                return lp;
            }

            @Override
            public LineParameters setValue(LineParameters value) {
                RecodedLine outer = RecodedLine.this;
                if(value == null){
                    throw new NullPointerException("Null values are not allowed in RecodedLines.");
                }
                outer.put(s,value);
                LineParameters ret = lp;
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
