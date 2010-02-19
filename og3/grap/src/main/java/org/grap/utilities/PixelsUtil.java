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
package org.grap.utilities;

import ij.ImagePlus;

import java.awt.geom.Point2D;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.RasterMetadata;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public class PixelsUtil {

	private GeoRaster geoRaster;
	private ImagePlus grapImagePlus;
	private double _2DX;
	private double _6DX;
	private float _DX_2;
	private double _4DX_2;
	private RasterMetadata rasterMedata;
	private float m_dDist[];

	/* neighbor's address */
	private final static int m_iOffsetX[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private final static int m_iOffsetY[] = { 1, 1, 0, -1, -1, -1, 0, 1 };

	public final static double DEG_45_IN_RAD = Math.PI / 180. * 45.;
	public final static double DEG_90_IN_RAD = Math.PI / 180. * 90.;
	public final static double DEG_180_IN_RAD = Math.PI;
	public final static double DEG_270_IN_RAD = Math.PI / 180. * 270.;
	public final static double DEG_360_IN_RAD = Math.PI * 2.;

	public PixelsUtil(final GeoRaster geoRaster) throws IOException {
		this.geoRaster = geoRaster;
		grapImagePlus = geoRaster.getImagePlus();

		rasterMedata = geoRaster.getMetadata();
		setConstants();
	}

	public LineString toPixel(final LineString lineString) {
		final Coordinate[] realWorldCoords = lineString.getCoordinates();
		final Coordinate[] pixelGridCoords = new Coordinate[realWorldCoords.length];
		for (int i = 0; i < pixelGridCoords.length; i++) {
			final Point2D p = geoRaster.fromRealWorldToPixel(
					realWorldCoords[i].x, realWorldCoords[i].y);
			pixelGridCoords[i] = new Coordinate(p.getX(), p.getY());
		}
		return new GeometryFactory().createLineString(pixelGridCoords);
	}

	public MultiLineString toPixel(final MultiLineString mls) {

		LineString[] lineStrings = new LineString[mls.getNumGeometries()];
		for (int k = 0; k < mls.getNumGeometries(); k++) {
			LineString ls = (LineString) mls.getGeometryN(k);

			lineStrings[k] = toPixel(ls);
		}
		return new GeometryFactory().createMultiLineString(lineStrings);
	}

	private boolean getSubMatrix3x3(int x, int y, double SubMatrix[]) {

		int i;
		int iDir;
		float z, z2;

		boolean result = false;
		z = grapImagePlus.getProcessor().getPixelValue(x, y);

		if (Float.isNaN(z)) {
		} else {
			// SubMatrix[4] = 0.0;
			for (i = 0; i < 4; i++) {

				iDir = 2 * i;
				z2 = grapImagePlus.getProcessor().getPixelValue(
						x + m_iOffsetX[iDir], y + m_iOffsetY[iDir]);
				if (!Float.isNaN(z2)) {
					SubMatrix[i] = z2 - z;
				} else {
					z2 = grapImagePlus.getProcessor().getPixelValue(
							x + m_iOffsetX[(iDir + 4) % 8],
							y + m_iOffsetY[(iDir + 4) % 8]);
					if (!Float.isNaN(z2)) {
						SubMatrix[i] = z - z2;
					} else {
						SubMatrix[i] = 0.0;
					}
				}
			}

			result = true;
		}
		return result;

	}

	private void setConstants() {

		int i;
		float dCellSize = geoRaster.getMetadata().getPixelSize_X();

		m_dDist = new float[8];

		for (i = 0; i < 8; i++) {
			m_dDist[i] = (float) Math.sqrt(m_iOffsetX[i] * dCellSize
					* m_iOffsetX[i] * dCellSize + m_iOffsetY[i] * dCellSize
					* m_iOffsetY[i] * dCellSize);
		}

		_2DX = dCellSize * 2.0;
		_6DX = dCellSize * 6.0;
		_DX_2 = dCellSize * dCellSize;
		_4DX_2 = 4.0 * _DX_2;

	}

	public double getSlope(int x, int y) {

		double zm[], G, H;

		zm = new double[4];

		if (getSubMatrix3x3(x, y, zm)) {
			G = (zm[0] - zm[2]) / _2DX;
			H = (zm[1] - zm[3]) / _2DX;
			return Math.atan(Math.sqrt(G * G + H * H));
		} else {
			return Double.NaN;
		}
	}

	public double getAspect(int x, int y) {

		double zm[], G, H, dAspect;

		zm = new double[4];

		if (getSubMatrix3x3(x, y, zm)) {
			G = (zm[0] - zm[2]) / _2DX;
			H = (zm[1] - zm[3]) / _2DX;
			if (G != 0.0) {
				dAspect = DEG_180_IN_RAD + Math.atan2(H, G);
			} else {
				dAspect = H > 0.0 ? DEG_270_IN_RAD : (H < 0.0 ? DEG_90_IN_RAD
						: -1.0);
			}
			return dAspect;
		} else {
			return Double.NaN;
		}
	}

	public int getDirToNextDownslopeCell(int x, int y) {

		int i, iDir = 0;
		double dSlope, dMaxSlope = 0;
		double z, z2;

		z = grapImagePlus.getProcessor().getPixelValue(x, y);

		if (Double.isNaN(z)) {
			return -1;
		}
		for (i = 0; i < 8; i++) {

			int xi = x + m_iOffsetY[i];
			int yi = y + m_iOffsetX[i];

			if ((xi < 0) || (yi < 0)) {
				z2 = Double.NaN;
			} else if ((xi > rasterMedata.getNCols() - 1)
					|| (yi > rasterMedata.getNRows() - 1)) {
				z2 = Double.NaN;
			} else {
				z2 = grapImagePlus.getProcessor().getPixelValue(xi, yi);
			}
			dSlope = (z - z2) / getDistToNeighborInDir(i);
			if (dSlope > dMaxSlope) {
				iDir = i;
				dMaxSlope = dSlope;
			}

		}

		if (dMaxSlope > 0) {
			return iDir;
		}

		return -1;

	}

	public float getDistToNeighborInDir(int iDir) {

		return m_dDist[iDir];

	}

}
