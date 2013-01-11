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
package org.gdms.driver.shapefile;

import org.gdms.data.schema.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.dbf.RowProvider;

public class DBFRowProvider implements RowProvider {

	private DataSet dataSource;

	private DBFMetadata metadata;

	public DBFRowProvider(DataSet ds) throws DriverException {
		this.dataSource = ds;
		this.metadata = new DBFMetadata(dataSource.getMetadata());
	}

        @Override
	public Metadata getMetadata() throws DriverException {
		return metadata;
	}

        @Override
	public Value[] getRow(long index) throws DriverException {
		Value[] ret = new Value[getMetadata().getFieldCount()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = dataSource.getFieldValue(index, metadata.getMapping().get(i));
		}
		return ret;
	}

        @Override
	public long getRowCount() throws DriverException {
		return dataSource.getRowCount();
	}

}
