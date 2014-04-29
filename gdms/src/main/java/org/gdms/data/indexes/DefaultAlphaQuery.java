/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.indexes;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

/**
 * Default implementation for an {@code AlphaQuery}.
 * @author Antoine Gourlay
 */
public class DefaultAlphaQuery implements AlphaQuery {

        private Value min;
        private boolean minIncluded;
        private boolean maxIncluded;
        private Value max;
        private String[] fieldNames;

        public DefaultAlphaQuery(String[] fieldNames, Value exactValue) {
                this(fieldNames, exactValue, true, exactValue, true);
        }
        
        public DefaultAlphaQuery(String fieldName, Value exactValue) {
                this(fieldName, exactValue, true, exactValue, true);
        }
        
        public DefaultAlphaQuery(String fieldName, Value min, boolean minIncluded,
                Value max, boolean maxIncluded) {
                this(new String[] { fieldName }, min, minIncluded, max, maxIncluded);
        }

        public DefaultAlphaQuery(String[] fieldNames, Value min, boolean minIncluded,
                Value max, boolean maxIncluded) {
                this.min = min;
                this.minIncluded = minIncluded;
                this.max = max;
                this.maxIncluded = maxIncluded;
                this.fieldNames = fieldNames;

                if (this.min == null) {
                        this.min = ValueFactory.createNullValue();
                }

                if (this.max == null) {
                        this.max = ValueFactory.createNullValue();
                }
        }

        @Override
        public String[] getFieldNames() {
                return fieldNames;
        }

        @Override
        public boolean isStrict() {
                return true;
        }

        @Override
        public Value getMin() {
                return min;
        }

        @Override
        public boolean isMinIncluded() {
                return minIncluded;
        }

        @Override
        public boolean isMaxIncluded() {
                return maxIncluded;
        }

        @Override
        public Value getMax() {
                return max;
        }
}
