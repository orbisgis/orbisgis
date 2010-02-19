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

import java.io.File;
import java.io.IOException;

import org.grap.model.GeoProcessorType;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

public class BasicTest extends GrapTest {

	public void testGridWithoutHeader() throws Exception {
		try {
			final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
					+ "grid/ij3x3.asc");
			gr.open();
			assertTrue(false);
		} catch (IOException e) {
		}
	}

	public void testJPGReader() throws Exception {
		final GeoRaster gr = GeoRasterFactory
				.createGeoRaster("src/test/resources/smallChezineLambert.jpg");

		gr.open();
		assertTrue(true);
	}

	public void testXYZDEMReader() throws Exception {
		final GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "/xyz/MNT_Nantes_Lambert.xyz", GeoProcessorType.FLOAT, 10);
		gr.open();
		gr.save(tmpData + "xyzdem.tif");
	}

	public void testPNGWithoutWorldFile() throws Exception {
		try {
			final GeoRaster gr = GeoRasterFactory.createGeoRaster(internalData
					+ "noWorldFile.png");
			gr.open();
			gr.getType();
			assertTrue(false);
		} catch (IOException e) {
		}
	}

	public void testGrid2Tif() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/sample.asc");
		gr.open();
		final RasterMetadata originalMetadata = gr.getMetadata();
		final float[] pixels = gr.getFloatPixels();
		final File file = new File(tmpData + "1.tif");
		gr.save(file.getAbsolutePath());
		gr = GeoRasterFactory.createGeoRaster(file.getAbsolutePath());
		gr.open();
		final float[] tifPixels = gr.getFloatPixels();
		assertTrue(tifPixels.length == pixels.length);
		equals(pixels, tifPixels);
		final RasterMetadata newM = gr.getMetadata();

		assertTrue(newM.getEnvelope().equals(originalMetadata.getEnvelope()));
		assertTrue(newM.getNCols() == originalMetadata.getNCols());
		assertTrue(newM.getNRows() == originalMetadata.getNRows());
		assertTrue(newM.getPixelSize_X() == originalMetadata.getPixelSize_X());
		assertTrue(newM.getPixelSize_Y() == originalMetadata.getPixelSize_Y());
		assertTrue(newM.getRotation_X() == originalMetadata.getRotation_X());
		assertTrue(newM.getRotation_Y() == originalMetadata.getRotation_Y());
		assertTrue(newM.getXulcorner() == originalMetadata.getXulcorner());
		assertTrue(newM.getYulcorner() == originalMetadata.getYulcorner());
	}

	public void testGrid2Grid() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/sample.asc");
		gr.open();
		final RasterMetadata originalMetadata = gr.getMetadata();
		final float[] pixels = gr.getFloatPixels();

		final File file2 = new File(tmpData + "1.asc");
		gr.save(file2.getAbsolutePath());
		gr = GeoRasterFactory.createGeoRaster(file2.getAbsolutePath());
		gr.open();

		final float[] gridPixels = gr.getFloatPixels();
		assertTrue(gridPixels.length == pixels.length);
		equals(pixels, gridPixels);
		final RasterMetadata newM = gr.getMetadata();

		assertTrue(newM.getEnvelope().equals(originalMetadata.getEnvelope()));
		assertTrue(newM.getNCols() == originalMetadata.getNCols());
		assertTrue(newM.getNRows() == originalMetadata.getNRows());
		assertTrue(newM.getPixelSize_X() == originalMetadata.getPixelSize_X());
		assertTrue(newM.getPixelSize_Y() == originalMetadata.getPixelSize_Y());
		assertTrue(newM.getRotation_X() == originalMetadata.getRotation_X());
		assertTrue(newM.getRotation_Y() == originalMetadata.getRotation_Y());
		assertTrue(newM.getXulcorner() == originalMetadata.getXulcorner());
		assertTrue(newM.getYulcorner() == originalMetadata.getYulcorner());
	}

	public void testLoadSaveGrid() throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(externalData
				+ "grid/3x3.asc");
		gr.open();
		check3x3(gr);
		gr.save(tmpData + "1.png");

		gr = GeoRasterFactory.createGeoRaster(tmpData + "1.png");
		gr.open();
		check3x3(gr);
	}

	private void check3x3(GeoRaster gr) throws Exception {
		final ImagePlus grapImagePlus = gr.getImagePlus();
		float previous = -1;
		for (int y = 0; y < gr.getHeight(); y++) {
			for (int x = 0; x < gr.getWidth(); x++) {
				assertTrue(grapImagePlus.getProcessor().getPixelValue(x, y) > previous);
				previous = grapImagePlus.getProcessor().getPixelValue(x, y);
			}
		}
	}

}