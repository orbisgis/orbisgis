package org.orbisgis.geoview.rasterProcessing.tin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.CustomQuery;

// select Generate2DMesh() from points
// select generate2dmesh('-q30 -a120') from points

public class Generate2DMesh implements CustomQuery {
	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (tables.length != 1) {
			throw new ExecutionException(getName()
					+ " only operates on one table :: " + getSqlOrder());
		}

		if (1 < values.length) {
			throw new ExecutionException(getName()
					+ " does not operates with more than one argument :: "
					+ getSqlOrder());
		}
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

			if (isOfNodeFileType) {
				Runtime.getRuntime().exec(
						new String[] { cmd, "-c", options, tempRadical }, null,
						tempDir);
			} else {
				Runtime.getRuntime().exec(
						new String[] { cmd, "-pc", options, tempRadical },
						null, tempDir);
			}

			// read the produced .1.{node,poly,ele} files and convert them
			// into the resulting ObjectMemory !
			final String nodeFileName = tempRadical + ".1.node";
			final NodeReader nodeReader = new NodeReader(new File(nodeFileName));
			final List<Vertex> listOfVertices = nodeReader.read();
			nodeReader.close();

			final String polyFileName = tempRadical + ".1.poly";
			final PolyReader polyReader = new PolyReader(
					new File(polyFileName), listOfVertices);

			inSds.cancel();
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (IOException e) {
			throw new ExecutionException(
					"Problem with the Runtime exec method !", e);
		}

		return null;
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
}