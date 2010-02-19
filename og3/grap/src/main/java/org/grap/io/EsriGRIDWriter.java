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

import ij.ImagePlus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.grap.model.RasterMetadata;

/**
 *
 * @author Thomas and Erwan
 *
 * This class is written to directly access the ESRI ascii grid format.
 *
 * The ASCII grid data file format comprises a few lines of header data followed
 * by lists of cell values. The header data includes the following keywords and
 * values:
 *
 * ncols : number of columns in the data set.
 *
 * nrows : number of rows in the data set.
 *
 * xllcorner : x-coordinate of the west border of the LowerLeft corner.
 *
 * yllcorner : y-coordinate of the south border of the LowerLeft corner.
 *
 * cellsize : size of the square cell of the data set.
 *
 * NODATA_value : arbitrary value assigned to unknown cells.
 *
 * Grap's meta-data matches the world file specifications as mentioned in the
 * wikipedia web page http://en.wikipedia.org/wiki/World_file
 *
 * Indeed, the first pixel of the pixels grid (with raw and column indexes both
 * equals to zero, at the upper left corner) corresponds, in the real world, to
 * the centroid of the corresponding UpperLeft rectangle.
 *
 * As defined in the ESRI Grid specifications wikipedia web page
 * http://en.wikipedia.org/wiki/ESRI_grid, the given lower left coordinates
 * (those that corresponds to the south-west edge of the LowerLeft rectangle in
 * the real world) have to be converted into the Grap UpperLeft centroid.
 *
 * Concerning the unique cellsize (pixel are square in ESRI Grid format), it is
 * converted into pixelSize_X (without any modification) and pixelSize_Y (using
 * the opposite -cellsize value).
 *
 * For example
 *
 * ncols 466
 *
 * nrows 448
 *
 * xllcorner 634592
 *
 * yllcorner 5588395
 *
 * cellsize 10
 *
 * NODATA_value -9999
 */
public class EsriGRIDWriter {

	private String fileName;

	private ImagePlus grapImagePlus;

	private RasterMetadata rasterMetadata;

	/**
	 * This class permits to save a georaster onto a asc esri grid format.
	 *
	 * @param fileName
	 * @param grapImagePlus
	 * @param rasterMetadata
	 */

	public EsriGRIDWriter(final String fileName, final ImagePlus grapImagePlus,
			final RasterMetadata rasterMetadata) {
		this.fileName = fileName;
		this.grapImagePlus = grapImagePlus;
		this.rasterMetadata = rasterMetadata;
	}

	public void save() {

		try {

			FileWriter f = new FileWriter(fileName);
			BufferedWriter fout = new BufferedWriter(f);
			DecimalFormat df = new DecimalFormat("##.###");
			df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
			df.setDecimalSeparatorAlwaysShown(true);

			int ncols = rasterMetadata.getNCols();
			int nrows = rasterMetadata.getNRows();

			fout.write("ncols " + Integer.toString(ncols));
			fout.newLine();
			fout.write("nrows " + Integer.toString(nrows));
			fout.newLine();
			fout.write("xllcorner "
					+ Double.toString(rasterMetadata.getEnvelope().getMinX()));
			fout.newLine();
			fout.write("yllcorner "
					+ Double.toString(rasterMetadata.getEnvelope().getMinY()));
			fout.newLine();
			fout.write("cellsize "
					+ Double.toString(rasterMetadata.getPixelSize_X()));
			fout.newLine();
			fout.write("nodata_value "
					+ Double.toString(rasterMetadata.getNoDataValue()));
			fout.newLine();

			if (grapImagePlus.getType() != ImagePlus.COLOR_RGB) {
				for (int i = 0; i < nrows; i++) {
					for (int j = 0; j < ncols; j++) {

						float dValue = grapImagePlus.getProcessor()
								.getPixelValue(j, i);

						if (Float.isNaN(dValue)) {
							fout.write(df.format(-9999f) + " ");
						} else {
							fout.write(df.format(dValue) + " ");
						}
					}
					fout.newLine();
				}
			}
			fout.close();
			f.close();

		} catch (Exception e) {
		}

	}

}