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
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
package org.gdms.sql.customQuery.spatial.jgrapht;

import java.util.LinkedList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.jgrapht.GraphFactory;
import org.gdms.jgrapht.INode;
import org.gdms.jgrapht.Node2D;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.orbisgis.IProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class ShortestPath implements CustomQuery {
	private final static WKTReader wktReader = new WKTReader();

	public String getDescription() {
		return "Build the shortest path between two points on line network.";
	}

	public String getName() {
		return "ShortestPath";
	}

	public String getSqlOrder() {
		return "select ShortestPath('startPoint', 'endPoint') from myTable;";
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, argumentsTypes, 2);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[0], Type.STRING);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[1], Type.STRING);
	}

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			final SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
					tables[0]);
			inSds.open();

			final int rowCount = (int) inSds.getRowCount();
			final List<Geometry> geometries = new LinkedList<Geometry>();
			for (int i = 0; i < rowCount; i++) {
				geometries.add(inSds.getGeometry(i));
			}

			final Geometry startPoint = wktReader.read(values[0].getAsString());
			final Geometry endPoint = wktReader.read(values[1].getAsString());

			// graph computation does not take into account the z coordinates -
			// instantiation of a non-oriented graph :
			final WeightedGraph<INode, Geometry> u_graph = GraphFactory
					.createGraph(geometries, false);

			// to create an oriented graph :
			// WeightedGraph<INode, Geometry> d_graph =
			// GraphFactory.createDirectedGraph(geometries, false);

			// look for the shortest pathway
			final Node2D start = new Node2D(startPoint.getCoordinate());
			final Node2D end = new Node2D(endPoint.getCoordinate());
			final List<Geometry> shortestPath = DijkstraShortestPath
					.findPathBetween(u_graph, start, end);

			// build and populate the resulting driver
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			int k = 0;
			for (Geometry geometry : shortestPath) {
				driver.addValues(new Value[] { ValueFactory.createValue(k),
						ValueFactory.createValue(geometry) });
				k++;
			}
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (IncompatibleTypesException e) {
			throw new ExecutionException(e);
		} catch (ParseException e) {
			throw new ExecutionException(e);
		}
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"the_geom" });
	}

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 1);
		FunctionValidator.failIfNotSpatialDataSource(this, tables[0], 0);
	}
}