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
package org.grap.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.grap.model.GeoProcessorType;

public class FileReaderFactory {
	private static Set<String> worldFileExtensions;
	static {
		worldFileExtensions = new HashSet<String>();
		worldFileExtensions.add("tif");
		worldFileExtensions.add("tiff");
		worldFileExtensions.add("jpg");
		worldFileExtensions.add("jpeg");
		worldFileExtensions.add("gif");
		worldFileExtensions.add("bmp");
		worldFileExtensions.add("png");
	}

	public static RasterReader create(final String fileName)
			throws FileNotFoundException, IOException {
		return create(fileName, GeoProcessorType.FLOAT);
	}

	public static RasterReader create(final String fileName,
			final GeoProcessorType geoProcessorType)
			throws FileNotFoundException, IOException {
		final String fileNameExtension = getFileNameExtension(fileName);

		if (fileNameExtension.startsWith("asc")) {
			return new EsriGRIDReader(fileName, geoProcessorType);
		} else if (worldFileExtensions.contains(fileNameExtension)) {
			return new WorldImageReader(fileName);
		} else if (fileNameExtension.endsWith("xyz")) {
			return new XYZ2DEMReader(fileName);
		} else {
			throw new RuntimeException("Unknown filename extension !");
		}
	}

	public static RasterReader create(final String fileName,
			final GeoProcessorType geoProcessorType,
			float pixelsize) throws FileNotFoundException, IOException {
		final String fileNameExtension = getFileNameExtension(fileName);

		if (fileNameExtension.endsWith("xyz")) {
			return new XYZ2DEMReader(fileName, pixelsize);
		} else {
			return create(fileName, geoProcessorType);
		}
	}

	private static String getFileNameExtension(final String fileName) {
		final int dotIndex = fileName.lastIndexOf('.');
		return fileName.substring(dotIndex + 1).toLowerCase();
	}
}