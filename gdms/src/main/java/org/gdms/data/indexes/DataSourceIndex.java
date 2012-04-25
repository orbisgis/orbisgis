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
package org.gdms.data.indexes;

import java.io.File;
import java.io.IOException;

import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;

/**
 * An index over a DataSource.
 * 
 * @param <Q> 
 * @author Antoine Gourlay
 */
public interface DataSourceIndex<Q> extends AdHocIndex<Q> {

        /**
         * Deletes a row from the index.
         *
         * @param value
         * @param row
         * @throws IndexException
         */
        void deleteRow(Value value, int row) throws IndexException;

        /**
         * Inserts a row into the index.
         *
         * @param value
         * @param row
         * @throws IndexException
         */
        void insertRow(Value value, int row) throws IndexException;

        /**
         * Updates a field value in the index.
         *
         * @param oldGeometry
         * @param newGeometry
         * @param rowIndex
         * @throws IndexException
         */
        void setFieldValue(Value oldGeometry, Value newGeometry, int rowIndex)
                throws IndexException;

        /**
         * Gets the field this index is built on
         *
         * @return
         */
        String getFieldName();

        /**
         * Sets the name of the field to index
         *
         * @param fieldName
         */
        void setFieldName(String fieldName);

        /**
         * Indexes the specified field of the specified source
         *
         * @param dsf
         * @param ds
         * @param pm
         * @throws IndexException
         */
        void buildIndex(DataSourceFactory dsf, DataSet ds,
                ProgressMonitor pm) throws IndexException;

        /**
         * Loads the index information from the file
         *
         * @throws IndexException
         */
        void load() throws IndexException;

        /**
         * Stores the information of this index at disk
         *
         * @throws IndexException
         */
        void save() throws IndexException;

        /**
         * Sets the file related with the index
         *
         * @param file
         */
        void setFile(File file);

        /**
         * Gets the file this index uses for storage
         *
         * @return
         */
        File getFile();

        /**
         * Frees all the resources used by the index
         *
         * @throws IOException
         */
        void close() throws IOException;

        /**
         * Returns true if the method is called between an invocation to buildIndex
         * or load and an invocation to close
         *
         * @return
         */
        boolean isOpen();
}
