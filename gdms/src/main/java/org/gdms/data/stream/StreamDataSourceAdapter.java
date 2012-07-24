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
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.data.stream;

import com.vividsolutions.jts.geom.Envelope;

import org.gdms.data.DriverDataSource;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.StreamDriver;
import org.gdms.source.Source;

/**
 * Adapter to the DataSource interface for stream drivers.
 *
 * @author Antoine Gourlay
 * @author Vincent Dépériers
 */
public class StreamDataSourceAdapter extends DriverDataSource {

        private StreamDriver driver;
        private StreamSource def;

        /**
         * Creates a new StreamDataSourceAdapter.
         *
         * @param src the underlying source
         * @param def the stream info
         * @param driver the stream driver
         */
        public StreamDataSourceAdapter(Source src, StreamSource def, StreamDriver driver) {
                super(src);
                this.def = def;
                this.driver = driver;
        }

        @Override
        public void open() throws DriverException {
                driver.open(def);
                fireOpen(this);
        }

        @Override
        public void close() throws DriverException {
                driver.close();
                fireCancel(this);
        }

        /**
         * Save the data in the stream driver
         *
         * @param ds
         * @throws DriverException
         */
        @Override
        public void saveData(DataSet ds) throws DriverException {
                throw new UnsupportedOperationException();
        }

        /**
         * @return the driver of the Stream.
         */
        @Override
        public StreamDriver getDriver() {
                return driver;
        }

        @Override
        public void syncWithSource() throws DriverException {
                driver.close();
                driver.open(def);
        }

        /**
         * This method is used by the {@code Renderer} to know whether or not it is
         * dealing with a stream datasource
         *
         * @return
         */
        @Override
        public boolean isStream() {
                return true;
        }

        @Override
        public Envelope getFullExtent() throws DriverException {
                return getStream(0).getEnvelope();
        }
}
