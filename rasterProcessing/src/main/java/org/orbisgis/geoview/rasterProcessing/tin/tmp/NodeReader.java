package org.orbisgis.geoview.rasterProcessing.tin.tmp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.NotNullConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.UniqueConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

class NodeReader {
	private static GeometryFactory geometryFactory = new GeometryFactory();
	private Scanner in = null;
	private int numberOfAttributes;
	private int numberOfBoundaryMarkers;

	public NodeReader(final File file) throws FileNotFoundException {
		if (file.exists() && file.canRead()) {
			in = new Scanner(file);
			in.useLocale(Locale.US); // essential to read float values
		}
	}

	private String nextThatIsNotAComment() throws DriverException {
		while (in.hasNext()) {
			final String tmp = in.next();
			if (tmp.startsWith("#")) {
				in.nextLine();
			} else {
				return tmp;
			}
		}
		throw new DriverException("NodeReader: format failure - i miss a token");
	}

	private int nextInteger() throws DriverException {
		return new Integer(nextThatIsNotAComment());
	}

	private double nextDouble() throws DriverException {
		return new Double(nextThatIsNotAComment());
	}

	public List<Value[]> read() throws DriverException {
		if (null != in) {
			final int numberOfVertices = nextInteger();
			final int dimension = nextInteger();
			numberOfAttributes = nextInteger();
			numberOfBoundaryMarkers = nextInteger();

			final List<Value[]> rows = new ArrayList<Value[]>(numberOfVertices);
			final int numberOfColumns = 1 + dimension + numberOfAttributes
					+ numberOfBoundaryMarkers;

			for (int i = 0; i < numberOfVertices; i++) {
				final List<Value> columns = new ArrayList<Value>(
						numberOfColumns);

				final int vertexId = nextInteger();
				columns.add(ValueFactory.createValue(vertexId));

				final double x = nextDouble();
				final double y = nextDouble();
				columns.add(ValueFactory.createValue(geometryFactory
						.createPoint(new Coordinate(x, y))));

				for (int j = 0; j < numberOfAttributes; j++) {
					columns.add(ValueFactory.createValue(nextDouble()));
				}

				if (1 == numberOfBoundaryMarkers) {
					columns.add(ValueFactory.createValue(nextInteger()));
				}

				rows.add(columns.toArray(new Value[0]));
			}
			return rows;
		}
		return null;
	}

	public Metadata getMetadata() throws DriverException {
		final DefaultMetadata metadata = new DefaultMetadata();
		try {
			metadata.addField("id", Type.INT, new Constraint[] {
					new UniqueConstraint(), new NotNullConstraint() });
			metadata.addField("the_geom", Type.GEOMETRY,
					new Constraint[] { new GeometryConstraint(
							GeometryConstraint.POINT_2D) });
			for (int j = 0; j < numberOfAttributes; j++) {
				metadata.addField("attribute_" + j, Type.DOUBLE);
			}

			if (1 == numberOfBoundaryMarkers) {
				metadata.addField("Boundary_marker", Type.INT);
			}
		} catch (InvalidTypeException e) {
			throw new RuntimeException("Bug in the driver", e);
		}
		return metadata;
	}

	public void close() {
		in.close();
	}

	public static void main(String[] args) throws FileNotFoundException,
			DriverException {
		final NodeReader nr = new NodeReader(
				new File(
						"/import/leduc/dev/c/triangle-1.6/ex/nuagePointsAvecAttributs.node"));
		for (Value[] cols : nr.read()) {
			System.out.println(cols[1].getAsGeometry());
		}
		nr.close();
	}
}