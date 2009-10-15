/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.customQuery.spatial.geometry.update;

import java.util.LinkedList;
import java.util.ListIterator;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.geometryUtils.CoordinatesUtils;
import org.gdms.geometryUtils.GeometryEditor;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class InsertPoint implements CustomQuery {

	GeometryFactory gf = new GeometryFactory();

	public String getName() {
		return "InsertPoint";
	}

	public String getSqlOrder() {
		return "select InsertPoint(the_geom, point) from myTable;";
	}

	public String getDescription() {
		return "Insert a point along a line";
	}

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					tables[0]);
			final SpatialDataSourceDecorator sdspts = new SpatialDataSourceDecorator(
					tables[1]);
			sds.open();
			sdspts.open();

			final String spatialFieldName = values[0].toString();
			sds.setDefaultGeometry(spatialFieldName);

			final String spatialFieldNamePts = values[1].toString();
			sds.setDefaultGeometry(spatialFieldNamePts);

			ObjectMemoryDriver driver = new ObjectMemoryDriver(sds
					.getMetadata());

			long rowCount = sds.getRowCount();

			LinkedList<Coordinate> pts = new LinkedList<Coordinate>();
			for (int i = 0; i < sdspts.getRowCount(); i++) {

				pts.add(sdspts.getGeometry(i).getCoordinate());
			}

			ListIterator<Coordinate> ptsIterator = pts.listIterator();

			sdspts.close();

			for (int i = 0; i < rowCount; i++) {

				Value[] vals = sds.getRow(i);

				Geometry geom = sds.getGeometry(i);

				LinkedList<Geometry> list = new LinkedList<Geometry>();
				list.add(geom);

				while (ptsIterator.hasNext()) {
					Coordinate pt = ptsIterator.next();

					Geometry newGeom = GeometryEditor.insertVertex(geom, pt);

					if (newGeom != null) {

						if (!list.contains(newGeom)) {
							list.add(newGeom);
							pts.remove();
						}
					}

				}

				vals[sds.getFieldIndexByName(sds.getDefaultGeometry())] = ValueFactory
						.createValue(list.getFirst());

				driver.addValues(vals);

			}

			sds.close();

			return driver;
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return null;
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY,
				TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY,
				Argument.GEOMETRY) };
	}
}