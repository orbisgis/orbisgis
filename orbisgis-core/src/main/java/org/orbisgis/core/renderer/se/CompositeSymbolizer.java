/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.CompositeSymbolizerType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.SymbolizerType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;

/**
 * This is the entry point of the <code>Symbolizer</code>'s structure in a <code>Rule</code>
 * A <code>CompositeSymbolize</code> is a collection that embedded other <code>Symbolizer</code>s,
 * and that is not directly associated to any rendering or representation hint.
 * @author maxence
 */
public final class CompositeSymbolizer implements SymbolizerNode {

        private ArrayList<Symbolizer> symbolizers;
        private SymbolizerNode parent;
        
        /**
         * Build a new, empty, CompositeSymbolizer.
         */
        public CompositeSymbolizer() {
                symbolizers = new ArrayList<Symbolizer>();
        }

        /**
         * Build a new <code>CompositeSymbolizer</code>, finding its attributes and inner
         * elements in a JAXBElement.
         * @param st
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public CompositeSymbolizer(JAXBElement<? extends SymbolizerType> st) throws InvalidStyle {
                symbolizers = new ArrayList<Symbolizer>();

                if (st.getDeclaredType() == net.opengis.se._2_0.core.CompositeSymbolizerType.class) {
                        JAXBElement<CompositeSymbolizerType> jcs = (JAXBElement<CompositeSymbolizerType>) st;

                        for (JAXBElement<? extends SymbolizerType> s : jcs.getValue().getSymbolizer()) {
                                if (s.getDeclaredType() == net.opengis.se._2_0.core.CompositeSymbolizerType.class) {
                                        // If the sub-symbolizer is another collection : inline all
                                        CompositeSymbolizer cs2 = new CompositeSymbolizer(s);
                                        for (Symbolizer s2 : cs2.symbolizers) {
                                                this.addSymbolizer(s2);
                                        }
                                } else {
                                        Symbolizer symb = Symbolizer.createSymbolizerFromJAXBElement(s);
                                        this.addSymbolizer(symb);
                                }
                        }
                } else {
                        this.addSymbolizer(Symbolizer.createSymbolizerFromJAXBElement(st));
                }
        }
        
        /**
         * Get a Jaxb representation of this <code>CompositeSymbolizer</code>.
         * @return 
         */
        public JAXBElement<? extends SymbolizerType> getJAXBElement() {
                if (symbolizers.size() == 1) {
                        return symbolizers.get(0).getJAXBElement();
                } else if (symbolizers.size() > 1) {
                        ObjectFactory of = new ObjectFactory();
                        CompositeSymbolizerType cs = of.createCompositeSymbolizerType();
                        List<JAXBElement<? extends SymbolizerType>> sList = cs.getSymbolizer();
                        for (Symbolizer s : symbolizers) {
                                sList.add(s.getJAXBElement());
                        }

                        return of.createCompositeSymbolizer(cs);
                } else {
                        return null;
                }
        }

        /*
        public void draw(Graphics2D g2, DataSource sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException, DriverException{
        for (Symbolizer s : this.symbolizers){
        if (s instanceof VectorSymbolizer){
        ((VectorSymbolizer)s).draw(g2, sds, fid, selected, mt, null, null);
        }
        }
        }*/

        /**
         * Get the list of <code>Symbolizer</code>s contained in this <code>CompositeSymbolizer</code>
         * @return 
         */
        public List<Symbolizer> getSymbolizerList() {
                return this.symbolizers;
        }

        /**
         * Add a <code>Symbolizer</code> to the list contained in this <code>CompositeSymbolizer</code>
         * @param s 
         */
        public void addSymbolizer(Symbolizer s) {
                symbolizers.add(s);
                s.setParent(this);
                if (s.getLevel() < 0) {
                        s.setLevel(symbolizers.size());
                }
        }

        /**
         * Adds a <code>Symbolizer</code> to the list contained in this
         * <code>CompositeSymbolizer</code> at position i.
         * @param s
         */
        public void addSymbolizer(int i, Symbolizer s) {
                symbolizers.add(i, s);
                s.setParent(this);
                if (s.getLevel() < 0) {
                        s.setLevel(symbolizers.size());
                }
        }

        /**
         * Sets the value of the ith {@code Symbolizer} to {@code s}.
         * @param i
         * @param s
         */
        public void setSymbolizer(int i, Symbolizer s){
                symbolizers.set(i, s);
        }

        /**
         * @deprecated
         */
        public void moveSymbolizerDown(Symbolizer s) {
                int index = symbolizers.indexOf(s);
                if (index > -1 && index < symbolizers.size() - 1) {
                        symbolizers.remove(index);
                        symbolizers.add(index + 1, s);
                }
        }

        /**
         * @deprecated
         */
        public void moveSymbolizerUp(Symbolizer s) {
                int index = symbolizers.indexOf(s);
                if (index > 0) {
                        symbolizers.remove(index);
                        symbolizers.add(index - 1, s);
                }
        }

        /**
         * Remove the <code>Symbolizer s</code> to the list contained in this <code>CompositeSymbolizer</code>
         * @param s 
         */
        public void removeSymbolizer(Symbolizer s) {
                symbolizers.remove(s);
        }

        /**
         * As a collection of <code>Symbolizer</code>s, a <code>CompositeSymbolize</code>
         * is not associated to any Uom. Returns always <code>null</code>
         * @return 
         *      null
         */
        @Override
        public Uom getUom() {
                return null;
        }

        @Override
        public SymbolizerNode getParent() {
                return parent;
        }

        @Override
        public void setParent(SymbolizerNode rule) {
                this.parent = rule;
        }

    @Override
    public HashSet<String> dependsOnFeature() {
        HashSet<String> ret = new HashSet<String>();
        for(Symbolizer symb : symbolizers){
            ret.addAll(symb.dependsOnFeature());
        }
        return ret;
    }

    @Override
    public UsedAnalysis getUsedAnalysis(){
            //We get an empty UsedAnalysis - we'll merge everything.
            UsedAnalysis ua = new UsedAnalysis();
            for(Symbolizer s : symbolizers){
                    ua.merge(s.getUsedAnalysis());
            }
            return ua;
    }
}