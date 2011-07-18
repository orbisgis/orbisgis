/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.driver;

import org.gdms.data.schema.Metadata;
import org.gdms.data.values.Value;

/**
 * Interface that defines the read methods in gdms
 * 
 * @author Fernando Gonzalez Cortes
 */
public interface ReadAccess {

        int X = 0;
        int Y = 1;
        int Z = 2;
        int TIME = 3;

        /**
         * Get the value in the row according to a column
         *
         * @param rowIndex
         *            row
         * @param fieldId
         *            column
         *
         * @return subclase de Value con el valor del origen de datos. Never null
         *         (use ValueFactory.createNullValue() instead)
         *
         * @throws DriverException
         *             Si se produce un error accediendo al DataSource
         */
        Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException;

        /**
         * Get the number of elements in the source of data
         *
         * @return
         *
         * @throws DriverException
         *             If some error happens accessing the data source
         */
        long getRowCount() throws DriverException;

        /**
         * returns the scope of the data source.
         *
         * @param dimension
         *            Currently X, Y, Z or can be anything that a driver
         *            implementation is waiting for, for example: TIME
         * @return An array two elements indicating the bounds of the dimension. Can
         *         return null if the source is not bounded or the bounds are not
         *         known
         * @throws DriverException
         */
        Number[] getScope(int dimension) throws DriverException;

        /**
         * Gets the metadata of this batch of rows
         * @return a Metadata object
         * @throws DriverException
         */
        Metadata getMetadata() throws DriverException;
}
