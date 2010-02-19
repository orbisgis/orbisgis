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
package org.grap.archive;

import ij.ImagePlus;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

/*
 * -Xmx3072m -XX:+AggressiveHeap
 */

public class GetGeoRasterInformation {
	private final static String PREF = "../../datas2tests/";
	private final static Object[] LIST = new Object[] {//
	// IndexCM + GRAY8
			PREF + "geotif/440606.tif", //
			PREF + "geotif/440808.tif", //
			PREF + "orbisgis_tuto/raster/f018_024.tif", //
			PREF + "orbisgis_tuto/raster/f018_025.tif", //
			// IndexCM + GRAY32
			PREF + "geotif/out.tif", //
			PREF + "geotif/3x3_origin.tif", //
			PREF + "MNT_Nantes.tif",//
			// DirectCM + COLOR_RGB
			PREF + "geotif/LeHavre.tif", //
			// "/home/leduc/data/Nantes/Nantes_est.tif", //

			// IndexCM + GRAY*
			new byte[0], //
			new short[0], //
			new float[0], //
	};

	private static String fromGeoRasterTypeToImagePlusTypeName(
			final int typeCode) {
		switch (typeCode) {
		case ImagePlus.GRAY8:
			return "GRAY8";
		case ImagePlus.GRAY16:
			return "GRAY16";
		case ImagePlus.GRAY32:
			return "GRAY32";
		case ImagePlus.COLOR_256:
			return "COLOR_256";
		case ImagePlus.COLOR_RGB:
			return "COLOR_RGB";
		}
		return "--- UNKNOWN ---";
	}

	public GetGeoRasterInformation(final Object object)
			throws FileNotFoundException, IOException {
		GeoRaster gr = null;

		if (object instanceof String) {
			gr = GeoRasterFactory.createGeoRaster((String) object);
		} else {
			final String className = object.getClass().getSimpleName();
			if (className.equals("byte[]")) {
				gr = GeoRasterFactory.createGeoRaster((byte[]) object,
						new RasterMetadata(0.5, 0.5, 1, -1, 0, 0));
			} else if (className.equals("short[]")) {
				gr = GeoRasterFactory.createGeoRaster((short[]) object,
						new RasterMetadata(0.5, 0.5, 1, -1, 0, 0));
			} else if (className.equals("float[]")) {
				gr = GeoRasterFactory.createGeoRaster((float[]) object,
						new RasterMetadata(0.5, 0.5, 1, -1, 0, 0));
			}
		}

	}

	public static void main(String[] args) throws Exception {
		for (Object img : LIST) {
			new GetGeoRasterInformation(img);
		}
	}
}