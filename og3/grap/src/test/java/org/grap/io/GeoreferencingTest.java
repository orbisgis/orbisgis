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

import java.awt.geom.Point2D;

import junit.framework.TestCase;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

public class GeoreferencingTest extends TestCase {
	private static GeoRaster sampleRaster;

	static {
		final double upperLeftX = 1234.56;
		final double upperLeftY = 987.65;
		final float pixelSize_X = 2f;
		final float pixelSize_Y = -10.25f;
		final int nrows = 33;
		final int ncols = 57;

		final byte[] values = new byte[nrows * ncols];
		for (int i = 0; i < nrows * ncols; i++) {
			values[i] = (byte) i;
		}

		final RasterMetadata rmd = new RasterMetadata(upperLeftX, upperLeftY,
				pixelSize_X, pixelSize_Y, ncols, nrows);
		sampleRaster = GeoRasterFactory.createGeoRaster(values, rmd);
	}

	public void testToPixel() throws Exception {
		final RasterMetadata md = sampleRaster.getMetadata();

		final float halfPixelSize_X = md.getPixelSize_X() / 2;
		final float halfPixelSize_Y = Math.abs(md.getPixelSize_Y()) / 2;
		final float deltaX = md.getPixelSize_X() / 10;
		final float deltaY = Math.abs(md.getPixelSize_Y()) / 10;

		for (int r = 0; r < md.getNRows(); r++) {
			final double y = md.getYulcorner() + r * md.getPixelSize_Y();
			for (int c = 0; c < md.getNCols(); c++) {
				final double x = md.getXulcorner() + c * md.getPixelSize_X();
				for (float aleaR = -halfPixelSize_Y + deltaX; aleaR <= halfPixelSize_Y; aleaR += deltaX) {
					for (float aleaC = -halfPixelSize_X + deltaY; aleaC < halfPixelSize_X; aleaC += deltaY) {
						final Point2D p = sampleRaster.fromRealWorldToPixel(x
								+ aleaC, y + aleaR);
						assertTrue(c == p.getX());
						assertTrue(r == p.getY());
					}
				}
			}
		}
	}

	public void testToWorld() throws Exception {
		final RasterMetadata md = sampleRaster.getMetadata();

		for (int r = 0; r < md.getNRows(); r++) {
			final double y = md.getYulcorner() + r * md.getPixelSize_Y();
			for (int c = 0; c < md.getNCols(); c++) {
				final double x = md.getXulcorner() + c * md.getPixelSize_X();
				final Point2D p = sampleRaster.fromPixelToRealWorld(c, r);
				assertTrue(x == p.getX());
				assertTrue(y == p.getY());
			}
		}
	}
}