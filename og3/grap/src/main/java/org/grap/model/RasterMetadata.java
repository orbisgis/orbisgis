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
package org.grap.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.Serializable;

import com.vividsolutions.jts.geom.Envelope;

public class RasterMetadata implements Serializable {
	private double xulcorner = 0;

	private double yulcorner = 0;

	/** Resolution for the pixel. */

	private float pixelSize_X = 1;

	private float pixelSize_Y = 1;

	private float noDataValue = Float.NaN;

	private int ncols = 0;

	private int nrows = 0;

	private Envelope envelope;

	/** Rotation on the first dimension. */

	private double rotationX = 0.0;

	/** Rotation on the second dimension. */
	private double rotationY = 0.0;

	private AffineTransform affineTransform;

	private AffineTransform inverseTransform;

	/**
	 * Builds a raster metadata object
	 *
	 * @param upperLeftX
	 *            center of the upper left pixel
	 * @param upperLeftY
	 *            center of the upper left pixel
	 * @param pixelSize_X
	 * @param pixelSize_Y
	 *            Size of the pixel in the y-axis. Negative value if it grows
	 *            upwards (world files)
	 * @param ncols
	 * @param nrows
	 * @param noDataValue
	 */
	public RasterMetadata(final double upperLeftX, final double upperLeftY,
			final float pixelSize_X, final float pixelSize_Y, int ncols,
			int nrows, float noDataValue) {
		this(upperLeftX, upperLeftY, pixelSize_X, pixelSize_Y, ncols, nrows, 0,
				0, noDataValue);
	}

	/**
	 * Builds a raster metadata object
	 *
	 * @param upperLeftX
	 *            center of the upper left pixel
	 * @param upperLeftY
	 *            center of the upper left pixel
	 * @param pixelSize_X
	 * @param pixelSize_Y
	 *            Size of the pixel in the y-axis. Negative value if it grows
	 *            upwards (world files)
	 * @param ncols
	 * @param nrows
	 */
	public RasterMetadata(final double upperLeftX, final double upperLeftY,
			final float pixelSize_X, final float pixelSize_Y, int ncols,
			int nrows, final double colRotation, final double rowRotation,
			float noDataValue) {
		this.xulcorner = upperLeftX;
		this.yulcorner = upperLeftY;
		this.pixelSize_X = pixelSize_X;
		this.pixelSize_Y = pixelSize_Y;
		this.rotationX = rowRotation;
		this.rotationY = colRotation;
		this.nrows = nrows;
		this.ncols = ncols;
		this.noDataValue = noDataValue;
		calculateAffineTransform();
	}

	public RasterMetadata(double upperLeftX, double upperLeftY,
			float pixelSize_X, float pixelSize_Y, int ncols, int nrows,
			double rotationX, double rotationY) {
		this(upperLeftX, upperLeftY, pixelSize_X, pixelSize_Y, ncols, nrows,
				rotationX, rotationY, Float.NaN);
	}

	public RasterMetadata(double upperLeftX, double upperLeftY,
			float pixelSizeX, float pixelSizeY, int ncols, int nrows) {
		this(upperLeftX, upperLeftY, pixelSizeX, pixelSizeY, ncols, nrows,
				Float.NaN);
	}

	/**
	 * returns pixel's width.
	 *
	 * @return type int.
	 */

	public float getPixelSize_X() {
		return pixelSize_X;
	}

	/**
	 * returns pixel's height.
	 *
	 * @return type int.
	 */

	public float getPixelSize_Y() {
		return pixelSize_Y;
	}

	public String toString() {
		return new String("RasterMetadata with corners (" + xulcorner + ","
				+ yulcorner + ") and envelope ( " + getEnvelope() + ")");
	}

	/**
	 * Gets the no-data-value specified in the source
	 *
	 * @return
	 */
	public float getNoDataValue() {
		return noDataValue;
	}

	/**
	 * Gets the number of columns in the raster
	 *
	 * @return
	 */
	public int getNCols() {
		return ncols;
	}

	/**
	 * Gets the number of rows in the raster
	 *
	 * @return
	 */
	public int getNRows() {
		return nrows;
	}

	/**
	 * Gets the real world extent of this raster
	 *
	 * @return
	 */
	public Envelope getEnvelope() {
		if (envelope == null) {
			envelope = computeEnvelope();
		}
		return envelope;
	}

	private Envelope computeEnvelope() {
		double xm, xM, ym, yM;
		xm = xulcorner - pixelSize_X / 2;
		xM = xm + (ncols * pixelSize_X);

		yM = yulcorner - pixelSize_Y / 2;
		ym = yM + (nrows * pixelSize_Y);

		return new Envelope(xm, xM, ym, yM);
	}

	/**
	 * Gets the rotation of the raster
	 *
	 * @return
	 */
	public double getRotation_X() {
		return rotationX;
	}

	/**
	 * Gets the rotation of the raster
	 *
	 * @return
	 */
	public double getRotation_Y() {
		return rotationY;
	}

	/**
	 * @return center of the upper left pixel (x coordinate)
	 */
	public double getXulcorner() {
		return xulcorner;
	}

	/**
	 * @return center of the upper left pixel (y coordinate)
	 */
	public double getYulcorner() {
		return yulcorner;
	}

	private void calculateAffineTransform() {
		affineTransform = AffineTransform.getRotateInstance(rotationX,
				rotationY);
		affineTransform.translate(xulcorner, yulcorner);
		affineTransform.scale(pixelSize_X, pixelSize_Y);
	}

	/**
	 * Transforms the specified real world coordinate to a pixel coordinate
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Point2D toPixel(final double x, final double y) {
		final Point2D ptInPixelGrid = getInverse().transform(
				new Point2D.Double(x, y), null);
		return new Point2D.Double(Math.round(ptInPixelGrid.getX()), Math
				.round(ptInPixelGrid.getY()));
	}

	private AffineTransform getInverse() {
		if (null == inverseTransform) {
			try {
				inverseTransform = affineTransform.createInverse();
			} catch (NoninvertibleTransformException e) {
				throw new RuntimeException(e);
			}
		}
		return inverseTransform;
	}

	/**
	 * Transforms the specified pixel coordinate to a real world coordinate
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Point2D toWorld(final int x, final int y) {
		return affineTransform.transform(new Point2D.Double(x, y), null);
	}

	/**
	 * Clones this metadata instance
	 *
	 * @return
	 */
	public RasterMetadata duplicate() {
		final RasterMetadata ret = new RasterMetadata(xulcorner, yulcorner,
				pixelSize_X, pixelSize_Y, ncols, nrows, rotationX, rotationY,
				this.noDataValue);
		ret.envelope = new Envelope(getEnvelope());
		ret.calculateAffineTransform();

		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RasterMetadata) {
			final RasterMetadata rm = (RasterMetadata) obj;
			return affineTransform.equals(rm.affineTransform);
		} else {
			return false;
		}
	}
}