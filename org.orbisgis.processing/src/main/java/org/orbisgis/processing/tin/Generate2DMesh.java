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
package org.orbisgis.processing.tin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Polygon;

// select Generate2DMesh() from points
// select generate2dmesh('-q30 -a120') from points

// select generate2dmesh('-a0.15 -q30') from faceTrouee
// select generate2dmesh('-q30 -a2000') from polygon2d
// select generate2dmesh('-q30 -a2000') from multilinestring2d
// select generate2dmesh('-p33 -a10000') from bv_sap

public class Generate2DMesh implements CustomQuery {
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		final String options = (0 == values.length) ? "" : values[0]
				.getAsString();
		final SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
				tables[0]);

		try {
			inSds.open();

			// convert the spatial datasource into a .node or a .poly temporal
			// file
			final String tempRadical = dsf.getTempFile();
			boolean isOfNodeFileType = TriangleUtilities
					.isOnlyComposedOfPointsOrMultiPoints(inSds);
			if (isOfNodeFileType) {
				final String nodeFileName = tempRadical + ".node";
				final NodeWriter nodeWriter = new NodeWriter(new File(
						nodeFileName), inSds);
				nodeWriter.write();
				nodeWriter.close();
			} else {
				final String polyFileName = tempRadical + ".poly";
				final PolyWriter polyWriter = new PolyWriter(new File(
						polyFileName), inSds);
				polyWriter.write();
				polyWriter.close();
			}

			// execute the Triangle black-box command
			final File tempDir = dsf.getTempDir();
			final String cmd = System.getenv("TRIANGLE_HOME") + File.separator
					+ "triangle-" + System.getProperty("os.name").toLowerCase();

			Process pcs;
			if (isOfNodeFileType) {
				pcs = Runtime.getRuntime().exec(
						new String[] { cmd, "-zc", options, tempRadical },
						null, tempDir);
			} else {
				pcs = Runtime.getRuntime().exec(
						new String[] { cmd, "-zpc", options, tempRadical },
						null, tempDir);
			}

			final BufferedReader pcsStdout = new BufferedReader(
					new InputStreamReader(pcs.getInputStream()));
			String line;
			while ((line = pcsStdout.readLine()) != null) {
				System.err.println(line);
			}
			pcsStdout.close();
			pcs.waitFor();

			// final InputStream pcsStdout = new BufferedInputStream(pcs
			// .getInputStream());
			// final byte[] b = new byte[1024];
			// pcsStdout.read(b);

			// read the produced .1.{node,poly,ele} files and convert them
			// into a list of JTS Geometry
			final String nodeFileName = tempRadical + ".1.node";
			final NodeReader nodeReader = new NodeReader(new File(nodeFileName));
			final List<Vertex> listOfVertices = nodeReader.read();
			nodeReader.close();

			// final String polyFileName = tempRadical + ".1.poly";
			// final PolyReader polyReader = new PolyReader(
			// new File(polyFileName), listOfVertices);
			// polyReader.read();
			// polyReader.close();

			final String eleFileName = tempRadical + ".1.ele";
			final EleReader eleReader = new EleReader(new File(eleFileName),
					listOfVertices);
			final List<Polygon> listOfTriangles = eleReader.read();
			eleReader.close();

			inSds.close();

			// using the list of JTS polygons, build and populate the resulting
			// ObjectMemory
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(MetadataUtilities.fromTablesToMetadatas(tables)));
			for (int i = 0; i < listOfTriangles.size(); i++) {
				driver.addValues(new Value[] { ValueFactory.createValue(i),
						ValueFactory.createValue(listOfTriangles.get(i)) });
			}
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (IOException e) {
			throw new ExecutionException(
					"Problem with the Runtime exec method !", e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (InterruptedException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "Generate a 2D Mesh using Jonathan Richard Shewchuk \"Two-Dimensional Quality Mesh Generator\"";
	}

	public String getName() {
		return "Generate2DMesh";
	}

	public String getSqlOrder() {
		return "select Generate2DMesh([a single string of triangle options (except -p and -c)]) from myTable";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		try {
			return new DefaultMetadata(new Type[] {
					TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.GEOMETRY,
							new Constraint[] { new GeometryConstraint(
									GeometryConstraint.POLYGON) }) },
					new String[] { "gid", "the_geom" });
		} catch (InvalidTypeException e) {
			throw new DriverException(
					"InvalidTypeException in metadata instantiation", e);
		}
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.STRING),
				new Arguments() };
	}
}