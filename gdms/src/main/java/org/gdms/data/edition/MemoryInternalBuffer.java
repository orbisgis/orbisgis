/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.data.edition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;

public class MemoryInternalBuffer implements InternalBuffer {

        private List<List<Value>> rows = new ArrayList<List<Value>>();
        private DataSource dataSource;

        public MemoryInternalBuffer(DataSource dataSource) {
                this.dataSource = dataSource;
        }

        private List<Value> getRow(Value[] values) {
                List<Value> row = new ArrayList<Value>();
                row.addAll(Arrays.asList(values));

                return row;
        }

        @Override
        public PhysicalRowAddress insertRow(ValueCollection pk, Value[] newRow) {
                rows.add(getRow(newRow));
                return new InternalBufferRowAddress(pk, this, rows.size() - 1,
                        dataSource);
        }

        @Override
        public void setFieldValue(int row, int fieldId, Value value) {
                rows.get(row).set(fieldId, value);
        }

        @Override
        public Value getFieldValue(int row, int fieldId) {
                Value v = rows.get(row).get(fieldId);
                if (v == null) {
                        return ValueFactory.createNullValue();
                } else {
                        return v;
                }
        }

        @Override
        public Value[] removeField(int index) {
                List<Value> ret = new ArrayList<Value>();
                for (int i = 0; i < rows.size(); i++) {
                        List<Value> row = rows.get(i);
                        ret.add(row.remove(index));
                }

                return ret.toArray(new Value[ret.size()]);
        }

        @Override
        public void addField() {
                Value nullValue = ValueFactory.createNullValue();
                for (int i = 0; i < rows.size(); i++) {
                        List<Value> row = rows.get(i);
                        row.add(nullValue);
                }
        }

        @Override
        public void restoreField(int fieldIndex, Value[] values) {
                for (int i = 0; i < rows.size(); i++) {
                        List<Value> row = rows.get(i);
                        row.add(fieldIndex, values[i]);
                }
        }
}
