/*
 * UrbSAT is a set of spatial functionalities to build morphological
 * and aerodynamic urban indicators. It has been developed on
 * top of GDMS and OrbisGIS. UrbSAT is distributed under GPL 3
 * license. It is produced by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of UrbSAT.
 *
 * UrbSAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UrbSAT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UrbSAT. If not, see <http://www.gnu.org/licenses/>.
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
package org.urbsat.landcoverIndicators.custom;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;

import com.vividsolutions.jts.geom.Geometry;

public class Density implements CustomQuery {

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {

		if (tables.length != 2)
			throw new ExecutionException(
					"CreateGrid only operates on two tables");
		if (values.length != 2)
			throw new ExecutionException(
					"CreateGrid only operates with two values");
		try {
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "index", "density" }, new Type[] {
							TypeFactory.createType(Type.INT),
							TypeFactory.createType(Type.DOUBLE) });

			final DataSource resultDs = dsf.getDataSource(driver);
			resultDs.open();
			final SpatialDataSourceDecorator parcels = new SpatialDataSourceDecorator(
					tables[0]);
			final SpatialDataSourceDecorator grid = new SpatialDataSourceDecorator(
					tables[1]);
			final String parcelFieldName = values[0].toString();
			final String gridFieldName = values[1].toString();
			grid.open();
			parcels.open();
			grid.setDefaultGeometry(gridFieldName);

			for (int i = 0; i < grid.getRowCount(); i++) {
				final Geometry cell = grid.getGeometry(i);
				final Value k = grid.getFieldValue(i, 1);
				final IndexQuery query = new SpatialIndexQuery(cell
						.getEnvelopeInternal(), parcelFieldName);
				final Iterator<PhysicalDirection> iterator = parcels
						.queryIndex(query);
				double area = 0;
				while (iterator.hasNext()) {
					final PhysicalDirection dir = (PhysicalDirection) iterator
							.next();
					final Value geom = dir.getFieldValue(parcels
							.getFieldIndexByName(parcelFieldName));
					final Geometry g = geom.getAsGeometry();
					final Geometry intersection = g.intersection(cell);
					area += intersection.getArea();
				}
				resultDs.insertFilledRow(new Value[] { k,
						ValueFactory.createValue(area / cell.getArea()) });
			}

			resultDs.commit();
			grid.cancel();
			parcels.cancel();
			return resultDs;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		} catch (FreeingResourcesException e) {
			throw new ExecutionException(e);
		} catch (NonEditableDataSourceException e) {
			throw new ExecutionException(e);
		}
		// call DENSITY from landcover2000, gdms1182439943162 values
		// ('the_geom', 'g.the_geom');
	}

	public String getName() {
		return "DENSITY";
	}

	public String getSqlOrder() {
		return "select Density('a.the_geom', 'b.the_geom') from myTable1 as a, myTable2 as b;";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
