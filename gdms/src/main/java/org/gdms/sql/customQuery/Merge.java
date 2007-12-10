/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.strategies.FirstStrategy;

public class Merge implements CustomQuery {
	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		final long start = System.currentTimeMillis();

		if (tables.length < 2)
			throw new ExecutionException(
					"Merge only operates on two or more spatial tables");
		if (values.length != 0)
			throw new ExecutionException("Merge does not accept any field name");

		DataSource resultDs = null;
		try {
			long idx = 0;
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "gid", "the_geom" }, new Type[] {
							TypeFactory.createType(Type.INT),
							TypeFactory.createType(Type.GEOMETRY) });
			for (DataSource table : tables) {
				final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
						table);
				sds.open();
				final int rowCount = (int) sds.getRowCount();
				final int spatialFieldIndex = sds.getSpatialFieldIndex();
				for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
					final Value gv = sds.getFieldValue(rowIndex,
							spatialFieldIndex);
					driver.addValues(new Value[] {
							ValueFactory.createValue(idx++), gv });
				}
				sds.cancel();
			}

			// register the new driver
			final String outDsName = dsf.getSourceManager().nameAndRegister(
					driver);

			// spatial index for the new grid
			dsf.getIndexManager().buildIndex(outDsName, "the_geom",
					SpatialIndex.SPATIAL_INDEX);
			FirstStrategy.indexes = true;

			System.err.println("Merge : "
					+ (System.currentTimeMillis() - start) + " ms");

			return dsf.getDataSource(outDsName);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (InvalidTypeException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		} catch (IndexException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		}
	}

	public String getName() {
		return "Merge";
	}

	public String getSqlOrder() {
		return "select Merge() from myTable1, ..., myTableN;";
	}

	public String getDescription() {
		return "Merge the contents of a set of tables. That is to say: "
				+ "concatenate all default spatial geometric fields one after "
				+ "another for all tables.";
	}
}