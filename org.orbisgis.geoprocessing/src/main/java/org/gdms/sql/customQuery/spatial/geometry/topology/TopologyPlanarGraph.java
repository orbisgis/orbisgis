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
package org.gdms.sql.customQuery.spatial.geometry.topology;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.geoalgorithm.orbisgis.topology.PlanarGraph;
import org.orbisgis.progress.IProgressMonitor;

public class TopologyPlanarGraph implements CustomQuery {

	public String EDGES = "edges";

	public String getName() {
		return "PlanarGraph";
	}

	public String getSqlOrder() {
		return "select PlanarGraph(the_geom) from myTable;";
	}

	public String getDescription() {
		return "Build a planar graph based on polygons";
	}

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					tables[0]);
			sds.open();

			final String spatialFieldName = values[0].toString();
			sds.setDefaultGeometry(spatialFieldName);

			sds.close();

			PlanarGraph planarGraph = new PlanarGraph();

			ObjectMemoryDriver edges = planarGraph.createEdges(sds, pm);

			ObjectMemoryDriver nodes = planarGraph.createNodes(edges);

			ObjectMemoryDriver faces = planarGraph.createFaces(edges);

			dsf.getSourceManager().register(
					dsf.getSourceManager().getUniqueName("edges"), edges);


			dsf.getSourceManager().register(
					dsf.getSourceManager().getUniqueName("nodes"), nodes);

			dsf.getSourceManager().register(
					dsf.getSourceManager().getUniqueName("faces"), faces);

			return null;
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (NonEditableDataSourceException e) {
			throw new ExecutionException(e);
		}
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return null;
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY) };
	}
}