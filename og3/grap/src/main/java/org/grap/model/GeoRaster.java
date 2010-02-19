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

import ij.ImagePlus;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.io.IOException;

import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

/**
 * <p>
 * Interface to access raster data. The raster data accessible through this
 * interface is the pixel array and the information to situate the raster on the
 * world.
 * </p>
 * <p>
 * It is possible to specify one value as no-data-value so that all the pixels
 * equal to that value will be retrieved as no-data-value. What no-data-value is
 * depends on the pixel type (byte, short, float or int). There are constants
 * specifying the value for each pixel type. Note that this functionality is not
 * available for RGB images
 * </p>
 * <p>
 * The pixel type depends on the image type so that ImagePlus.GRAY8 and
 * ImagePlus.COLOR_256 images will contain bytes as pixel values,
 * ImagePlus.GRAY16 will contain shorts, ImagePlus.GRAY32 floats and
 * ImagePlus.COLOR_RGB will contain ints as pixel values
 * </p>
 *
 * @author Fernando Gonzalez Cortes
 */
public interface GeoRaster {

	/**
	 * Value returned by byte rasters (ImagePlus.COLOR_256 and ImagePlus.GRAY8)
	 * when a pixel contains no data
	 */
	static final byte BYTE_NO_DATA_VALUE = Byte.MIN_VALUE;

	/**
	 * Value returned by short rasters (ImagePlus.GRAY16) when a pixel contains
	 * no data
	 */
	static final short SHORT_NO_DATA_VALUE = Short.MIN_VALUE;

	/**
	 * Value returned by float rasters (ImagePlus.GRAY32) when a pixel contains
	 * no data
	 */
	static final float FLOAT_NO_DATA_VALUE = -9999;

	/**
	 * Opens the raster.
	 *
	 * @throws IOException
	 */
	public abstract void open() throws IOException;

	/**
	 * Gets the raster metadata
	 *
	 * @return
	 */
	public abstract RasterMetadata getMetadata();

	/**
	 * Set the range of valid values in this raster. All the values in the
	 * raster outside the interval specified by the min and max parameters will
	 * treated as no-data-value. If no range is to be applied, Float.NaN should
	 * be specified in both arguments. This method is not valid for RGB images:
	 * ImagePlus.COLOR_RGB
	 *
	 * @param min
	 *            Minimum valid value (inclusive)
	 * @param max
	 *            Maximum valid value (inclusive)
	 * @throws IOException
	 * @throws UnsupportedOperationException
	 *             If this raster is RGB
	 */
	public abstract void setRangeValues(final double min, final double max)
			throws IOException, UnsupportedOperationException;

	/**
	 * Specifies a value as no-data-value. To specify no no-data-value Float.NaN
	 * should be specified as a parameter. This method is not valid for RGB
	 * images: ImagePlus.COLOR_RGB
	 *
	 *
	 * @param value
	 *            Value to be treated as no-data-value
	 * @throws IOException
	 * @throws UnsupportedOperationException
	 *             If this raster is RGB
	 */
	public abstract void setNodataValue(final float value) throws IOException,
			UnsupportedOperationException;

	/**
	 * Transforms the specified pixel in raster coordinates to real world
	 * coordinates
	 *
	 * @param xpixel
	 * @param ypixel
	 * @return
	 */
	public abstract Point2D fromPixelToRealWorld(final int xpixel,
			final int ypixel);

	/**
	 * Transforms the specified real world coordinate into a raster pixel
	 * coordinate
	 *
	 * @param realWorldX
	 * @param realWorldY
	 * @return
	 */
	public abstract Point2D fromRealWorldToPixel(final double realWorldX,
			final double realWorldY);

	/**
	 * Saves this raster in the specified destination
	 *
	 * @param dest
	 * @throws IOException
	 */
	public abstract void save(final String dest) throws IOException;

	/**
	 * show the raster. Only for debugging purposes
	 *
	 * @throws IOException
	 */
	public abstract void show() throws IOException;

	/**
	 * Executes the specified operation on this raster
	 *
	 * @param operation
	 *            Operation to be applied
	 * @return
	 * @throws OperationException
	 *             If the operation couldn't be applied
	 */
	public abstract GeoRaster doOperation(final Operation operation)
			throws OperationException;

	/**
	 * Executes the specified operation on this raster
	 *
	 * @param operation
	 *            Operation to be applied
	 * @param pm
	 *            Instance to report progress
	 * @return
	 * @throws OperationException
	 *             If the operation couldn't be applied
	 */
	public abstract GeoRaster doOperation(final Operation operation,
			IProgressMonitor pm) throws OperationException;

	/**
	 * @return ImagePlus.COLOR_256, ImagePlus.COLOR_RGB, ImagePlus.GRAY8,
	 *         ImagePlus.GRAY16, ImagePlus.GRAY32
	 *
	 * @throws IOException
	 * @throws
	 */
	public abstract int getType() throws IOException;

	/**
	 * This raster contains no information
	 *
	 * @return
	 */
	public abstract boolean isEmpty();

	/**
	 * Gets the minimum value in the raster taking into account the already
	 * specified no-data-value
	 *
	 * @return
	 * @throws IOException
	 */
	public abstract double getMin() throws IOException;

	/**
	 * Gets the maximum value in the raster taking into account the already
	 * specified no-data-value
	 *
	 * @return
	 * @throws IOException
	 */
	public abstract double getMax() throws IOException;

	/**
	 * Gets the raster width in pixels, this is, the number of columns
	 *
	 * @return
	 * @throws IOException
	 */
	public abstract int getWidth() throws IOException;

	/**
	 * Gets the raster height in pixels, this is, the number of rows
	 *
	 * @return
	 * @throws IOException
	 */
	public abstract int getHeight() throws IOException;

	/**
	 * Gets an ImageJ object containing all the pixels. This method is time
	 * consuming and the return object is memory consuming. It's a good practice
	 * to call it once and set it to null once it has been used.
	 *
	 * @return
	 * @throws IOException
	 */
	public ImagePlus getImagePlus() throws IOException;

	/**
	 * Gets this raster default color model
	 *
	 * @return
	 * @throws
	 * @throws IOException
	 */
	public abstract ColorModel getDefaultColorModel() throws IOException;

	/**
	 * Gets the value treated as no-data-value.
	 *
	 * @return The no-data-value or Float.NaN if no no-data-value is specified.
	 * @throws IOException
	 */
	public abstract double getNoDataValue() throws IOException;

	/**
	 * Gets the pixels as an array of bytes
	 *
	 * @return
	 * @throws IOException
	 * @throws ClassCastException
	 *             if this raster doesn't contain bytes, this is, getType()
	 *             returns a value different from ImagePlus.GRAY8 or
	 *             ImagePlus.COLOR_256
	 */
	public abstract byte[] getBytePixels() throws IOException;

	/**
	 * Gets the pixels as an array of shorts
	 *
	 * @return
	 * @throws IOException
	 * @throws ClassCastException
	 *             if this raster doesn't contain shorts, this is, getType()
	 *             returns a value different from ImagePlus.GRAY16
	 */
	public abstract short[] getShortPixels() throws IOException;

	/**
	 * Gets the pixels as an array of floats
	 *
	 * @return
	 * @throws IOException
	 * @throws ClassCastException
	 *             if this raster doesn't contain floats, this is, getType()
	 *             returns a value different from ImagePlus.GRAY32
	 */
	public abstract float[] getFloatPixels() throws IOException;

	/**
	 * Gets the pixels as an array of integers
	 *
	 * @return
	 * @throws IOException
	 * @throws ClassCastException
	 *             if this raster doesn't contain integers, this is, getType()
	 *             returns a value different from ImagePlus.COLOR_RGB
	 */
	public abstract int[] getIntPixels() throws IOException;

	/**
	 * Gets an image of all the raster. Those pixels equals to no-data-value and
	 * those outside the range values are painted transparent
	 *
	 * @param cm
	 * @return
	 * @throws IOException
	 */
	public Image getImage(ColorModel cm) throws IOException;
}