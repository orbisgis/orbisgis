package org.orbisgis.geoview.rasterProcessing.tin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class EleReader extends AbstractReader {
	private Scanner in = null;
	private List<Vertex> listOfVertices;
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	EleReader(final File file, final List<Vertex> listOfVertices)
			throws FileNotFoundException {
		if (file.exists() && file.canRead()) {
			in = new Scanner(file);
			in.useLocale(Locale.US); // essential to read float values
		}
		this.listOfVertices = listOfVertices;
	}

	@Override
	Scanner getIn() {
		return in;
	}

	List<Polygon> read() throws DriverException {
		final int numberOfTriangles = nextInteger();
		final int numberOfNodesPerTriangle = nextInteger();
		final int numberOfAttributes = nextInteger();
		final List<Polygon> listOfTriangles = new ArrayList<Polygon>(
				numberOfTriangles);

		for (int i = 0; i < numberOfTriangles; i++) {
			nextInteger(); // useless triangleId

			final Coordinate[] coordinates = new Coordinate[numberOfNodesPerTriangle + 1];
			for (int node = 0; node < numberOfNodesPerTriangle; node++) {
				coordinates[node] = listOfVertices.get(nextInteger()).coordinate;
			}
			coordinates[numberOfNodesPerTriangle] = coordinates[0];
			final LinearRing shell = geometryFactory
					.createLinearRing(coordinates);
			listOfTriangles.add(geometryFactory.createPolygon(shell, null));

			for (int attr = 0; attr < numberOfAttributes; attr++) {
				nextDouble(); // useless attribute
			}
		}
		return listOfTriangles;
	}
}