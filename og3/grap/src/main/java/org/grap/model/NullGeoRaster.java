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

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.io.IOException;

import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.orbisgis.progress.IProgressMonitor;

class NullGeoRaster implements GeoRaster {
	static GeoRaster instance = new NullGeoRaster();

	/*
	 * We don't want instances of this class to be created by the user
	 */
	private NullGeoRaster() {
	}

	public GeoRaster doOperation(Operation operation, IProgressMonitor pm)
			throws OperationException {
		return instance;
	}

	public GeoRaster doOperation(Operation operation) {
		return instance;
	}

	public ImagePlus getImagePlus() {
		return null;
	}

	public RasterMetadata getMetadata() {
		return null;
	}

	public Point2D fromRealWorldToPixel(double mouseX,
			double mouseY) {
		return null;
	}

	public int getType() {
		return 0;
	}

	public void open() {
	}

	public Point2D fromPixelToRealWorld(int xpixel, int ypixel) {
		return null;
	}

	public void save(String dest) throws IOException {
	}

	public void setLUT(ColorModel LUTName) {
	}

	public void setLUT(ColorModel LUTName, final byte opacity) {
	}

	public void setNodataValue(float value) {
	}

	public void setRangeValues(double min, double max) {
	}

	public void setRangeColors(final double[] ranges, final Color[] colors) {
	}

	public void show() {
	}

	public boolean isEmpty() {
		return true;
	}

	public int getPixel(int x, int y) {
		return 0;
	}

	public GeoRaster convolve(float[] kernel, int focalMeanSizeX,
			int focalMeanSizeY) {
		return instance;
	}

	public GeoRaster convolve3x3(int[] kernel) {
		return instance;
	}

	public GeoRaster erode() {
		return instance;
	}

	public GeoRaster smooth() {
		return instance;
	}

	public ColorModel getOriginalColorModel() throws IOException {
		return null;
	}

	public ColorModel getColorModel() {
		return null;
	}

	public int getHeight() {
		return 0;
	}

	public double getMax() {
		return 0;
	}

	public double getMin() {
		return 0;
	}

	public int getWidth() {
		return 0;
	}

	public ColorModel getDefaultColorModel() {
		return null;
	}

	public double getNoDataValue() {
		return 0;
	}

	public byte[] getBytePixels() {
		return (byte[]) getImagePlus().getProcessor().getPixels();
	}

	public short[] getShortPixels() {
		return (short[]) getImagePlus().getProcessor().getPixels();
	}

	public float[] getFloatPixels() {
		return new float[0];
	}

	public int[] getIntPixels() {
		return new int[0];
	}

	public Image getImage(ColorModel cm) {
		return null;
	}

}