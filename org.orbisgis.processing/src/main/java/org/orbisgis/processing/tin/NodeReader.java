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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.gdms.driver.DriverException;
import org.orbisgis.Services;

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
				Services
						.getErrorManager()
						.error(
								"NodeReader: there is more than one attribute in the .1.node file !");
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