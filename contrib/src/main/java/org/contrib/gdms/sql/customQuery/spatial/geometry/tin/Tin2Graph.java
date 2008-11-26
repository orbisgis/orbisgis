package org.contrib.gdms.sql.customQuery.spatial.geometry.tin;

import org.contrib.algorithm.triangulation.tin2graph.GEdge;
import org.contrib.algorithm.triangulation.tin2graph.GFace;
import org.contrib.algorithm.triangulation.tin2graph.GMap;
import org.contrib.algorithm.triangulation.tin2graph.GVertex;
import org.contrib.algorithm.triangulation.tin2graph.MapOfMap;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;

public class Tin2Graph implements CustomQuery {
	@Override
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {

		final GMap<GVertex> vertices = new GMap<GVertex>();
		final GMap<GEdge> edges = new GMap<GEdge>();
		final GMap<GFace> faces = new GMap<GFace>();

		final MapOfMap vtxToEdg = new MapOfMap();

		try {
			SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
					tables[0]);
			long rowCount = inSds.getRowCount();
			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				final Value[] rowValues = inSds.getRow(rowIndex);

				// 1st of all, add the 3 new edges to the corresponding map
				final int triGid = rowValues[0].getAsInt();
				final int[] vtxGid = new int[] { rowValues[2].getAsInt(),
						rowValues[3].getAsInt(), rowValues[4].getAsInt() };
				final int[] neighGid = new int[] { rowValues[5].getAsInt(),
						rowValues[6].getAsInt(), rowValues[7].getAsInt() };
				final int[] vtxConstraintsGid = new int[] {
						rowValues[8].getAsInt(), rowValues[9].getAsInt(),
						rowValues[10].getAsInt() };
				final int[] edgConstraintsGid = new int[] {
						rowValues[11].getAsInt(), rowValues[12].getAsInt(),
						rowValues[13].getAsInt() };
				final int[] edgGid = new int[3];

				for (int i = 0; i < vtxGid.length; i++) {
					final int curr = vtxGid[i];
					final int next = vtxGid[(i + 1) % 3];

					Integer edgIdx = vtxToEdg.get(curr, next);
					if (null == edgIdx) {
						// add a new edge
						edgIdx = edges.size();
						vtxToEdg.put(curr, next, edgIdx);
						edges.put(edgIdx, new GEdge(vertices, curr, next,
								edgConstraintsGid[i]));
					}
					edgGid[i] = edgIdx;

					if (curr < next) {
						edges.get(edgIdx).setIncidentTriangleLeft(triGid);
						edges.get(edgIdx).setIncidentTriangleRight(neighGid[i]);
					} else {
						edges.get(edgIdx).setIncidentTriangleLeft(neighGid[i]);
						edges.get(edgIdx).setIncidentTriangleRight(triGid);
					}
				}

				// then, add the 3 new vertices to the corresponding map
				final Coordinate[] coordinates = rowValues[1].getAsGeometry()
						.getCoordinates();

				for (int i = 0; i < vtxGid.length; i++) {
					if (!vertices.containsKey(vtxGid[i])) {
						vertices.put(vtxGid[i], new GVertex(coordinates[i],
								vtxConstraintsGid[i]));
					}
					vertices.get(vtxGid[i]).addAGedge(edgGid[(i + 2) % 3]);
					vertices.get(vtxGid[i]).addAGedge(edgGid[i]);
				}

				// at least, add the new face to the corresponding map
				if (faces.containsKey(triGid)) {
					throw new RuntimeException("Unreachable source code");
				} else {
					faces.put(triGid,
							new GFace(vertices, edges, vtxGid, edgGid));
				}
			}

			vertices.store(GVertex.class, dsf, tables[0].getName());
			edges.store(GEdge.class, dsf, tables[0].getName());
			faces.store(GFace.class, dsf, tables[0].getName());

			return null;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		} catch (NonEditableDataSourceException e) {
			throw new ExecutionException(e);
		} catch (InstantiationException e) {
			throw new ExecutionException(e);
		} catch (IllegalAccessException e) {
			throw new ExecutionException(e);
		}
	}

	@Override
	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	@Override
	public String getDescription() {
		return "Transform a set of triangles into a topological graph";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments() };
	}

	@Override
	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return null;
	}

	@Override
	public String getName() {
		return "Tin2Graph";
	}

	@Override
	public String getSqlOrder() {
		return "select " + getName() + "() from triangles;";
	}
}