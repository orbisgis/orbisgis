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
package org.orbisgis.legend.structure.recode;

import java.awt.Color;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.ValueReference;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.legend.structure.parameter.AbstractAttributedRPLegend;
import org.orbisgis.legend.structure.parameter.ParameterLegend;

/**
 * LegendStructure associated to a {@code ColorParameter} set using a {@code
 * Recode2Color} instance.
 * @author Alexis Guéganno
 */
public class Recode2ColorLegend extends AbstractAttributedRPLegend implements ParameterLegend {

        private Recode2Color recode;

        /**
         * Build this {@code Recode2ColorLegend}, using the {@code Recode2Color}
         * instance given in argument.
         * @param rc
         */
        public Recode2ColorLegend(Recode2Color rc) {
                this.recode = rc;
        }

        @Override
        public ValueReference getValueReference() {
                return (StringAttribute)recode.getLookupValue();
        }
        /**
         * Get the {@code Recode2Color} instance associated to this {@code
         * Recode2ColorLegend}.
         * @return
         */
        public Recode2Color getRecode() {
                return recode;
        }

        @Override
        public SeParameter getParameter() {
                return getRecode();
        }


        /**
         * Gets the number of items contained in the inner {@code Recode}
         * instance.
         * @return
         */
        public int getNumItems(){
                return recode.getNumMapItem();
        }

        /**
         * Gets the Color value, if any, associated to {@code key} in the inner {@code
         * Recode}.
         * @param key
         * @return
         */
        public Color getItemValue(String key){
                try {
                        ColorParameter cp = recode.getMapItemValue(key);
                        return cp != null ? cp.getColor(null) : null;
                } catch (ParameterException ex) {
                        throw new IllegalStateException("Are you sure the values"
                                + "of your recode are literal values ?");
                }
        }

        /**
         * Gets the Color value, if any, associated to {@code key} in the inner {@code
         * Recode}.
         * @param i
         * @return
         */
        public Color getItemValue(int i){
                try {
                        return recode.getMapItemValue(i).getColor(null);
                } catch (ParameterException ex) {
                        throw new IllegalStateException("Are you sure the values"
                                + "of your recode are literal values ?");
                }
        }

        /**
         * Gets the ith key of the inner {@code Recode}.
         * @param i
         * @return
         */
        public String getKey(int i){
                return recode.getMapItemKey(i);
        }

        /**
         * Sets the ith key of the inner {@code Recode}.
         * @param i
         * @param newKey
         */
        public void setKey(int i, String newKey){
                recode.setKey(i, newKey);
        }

        /**
         * Adds an item in the inner {@code Recode}.
         * @param key
         * @param value
         */
        public void addItem(String key, Color value){
                recode.addMapItem(key, new ColorLiteral(value));
        }

        /**
         * Removes an item from the inner {@code Recode}.
         * @param i
         */
        public void removeItem(int i){
                recode.removeMapItem(i);
        }

        /**
         * Removes an item from the inner {@code Recode}.
         * @param key
         */
        public void removeItem(String key){
                recode.removeMapItem(key);
        }

}
