package org.orbisgis.geoview.rasterProcessing.tin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.gdms.driver.DriverException;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Coordinate;

class NodeReader extends AbstractReader {
	private Scanner in = null;

	NodeReader(final File file) throws FileNotFoundException {
		if (file.exists() && file.canRead()) {
			in = new Scanner(file);
			in.useLocale(Locale.US); // essential to read float values
		}
	}

	@Override
	Scanner getIn() {
		return in;
	}

	List<Vertex> read() throws DriverException {
		if (null != in) {
			final int numberOfVertices = nextInteger();
			nextInteger(); // useless dimension (always equal to 2)
			final int numberOfAttributes = nextInteger();
			final int numberOfBoundaryMarkers = nextInteger();

			if (1 != numberOfAttributes) {
				PluginManager
						.error("NodeReader: there is more than one attribute in the .1.node file !");
			}

			final List<Vertex> listOfVertices = new ArrayList<Vertex>(
					numberOfVertices);

			for (int i = 0; i < numberOfVertices; i++) {
				nextInteger(); // useless vertexId...
				final double x = nextDouble();
				final double y = nextDouble();
				final long gid = (long) nextDouble();

				listOfVertices.add(new Vertex(new Coordinate(x, y), gid));

				if (1 == numberOfBoundaryMarkers) {
					nextInteger(); // useless boundary marker...
				}
			}
			return listOfVertices;
		}
		return null;
	}
}