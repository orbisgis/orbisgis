/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.sif.components.filter;

/**
 *
 * @author Nicolas Fortin
 */
public class DefaultActiveFilter extends ActiveFilter {

        private String currentFilterValue;

        public DefaultActiveFilter(String factoryId, String currentFilterValue) {
                super(factoryId);
                this.currentFilterValue = currentFilterValue;
        }


        @Override
        public boolean equals(Object o) {
                if(!(o instanceof DefaultActiveFilter)) {
                        return false;
                }
                DefaultActiveFilter other = (DefaultActiveFilter)o;
                if(!other.currentFilterValue.equals(currentFilterValue)) {
                        return false;
                }
                return super.equals(o);
        }

        @Override
        public int hashCode() {
                int hash = 8 + super.hashCode();
                hash = 33 * hash + this.currentFilterValue.hashCode();
                return hash;
        }
        /**
         * Get the value of currentFilterValue
         *
         * @return the value of currentFilterValue
         */
        public String getCurrentFilterValue() {
                return currentFilterValue;
        }

        /**
         * Set the value of currentFilterValue This will fire the event
         * FilterChangeEvent on the {@link FilterFactoryManager}
         *
         * @param currentFilterValue new value of currentFilterValue
         */
        public void setCurrentFilterValue(String currentFilterValue) {
                this.currentFilterValue = currentFilterValue;
                //Do not send older value to execute again the filter even if the field is the same
                propertySupport.firePropertyChange(PROP_CURRENTFILTERVALUE, null, currentFilterValue);
        }
}
