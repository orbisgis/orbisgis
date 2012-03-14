/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * 
 * Team leader : Erwan BOCHER, scientific researcher,
 * 
 * User support leader : Gwendall Petit, geomatic engineer.
 * 
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
package org.gdms.data;

import java.util.Iterator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Iterator for a {@link DataSource }.
 * @author Antoine Gourlay
 */
public class DataSourceIterator implements Iterator<Value[]> {

        private DataSource ds;
        private long index;
        private long count;

        /**
         * Creates a new DataSourceIterator based on an EditionDecorator
         * @param ds
         */
        public DataSourceIterator(DataSource ds) {
                this.ds = ds;
                try {
                        count = ds.getRowCount();
                } catch (DriverException ex) {
                        throw new IllegalStateException(ex);
                }
        }

        @Override
        public boolean hasNext() {
                return index < count;
        }

        @Override
        public Value[] next() {
                Value[] v;
                try {
                        v = ds.getRow(index);
                } catch (DriverException ex) {
                        throw new IllegalStateException(ex);
                }
                index++;
                return v;
        }

        /**
         * Updates the current value (the last obtained using {@link #next() }).
         * @param newValue the new row
         * @throws DriverException if there is an error updating the row
         */
        public void update(Value[] newValue) throws DriverException {
                long id = index - 1;
                for (int i = 0; i < newValue.length; i++) {
                        ds.setFieldValue(id, i, newValue[i]);
                }
        }

        /**
         * Updates one field of the current value (the last obtained using {@link #next() }).
         * @param newValue the new value
         * @param i the field id
         * @throws DriverException  if there is an error updating the row
         */
        public void replace(Value newValue, int i) throws DriverException {
                ds.setFieldValue(index - 1, i, newValue);
        }

        @Override
        public void remove() {
                try {
                        ds.deleteRow(index - 1);
                } catch (DriverException ex) {
                        throw new IllegalStateException(ex);
                }
                index--;
                count--;
        }
}
