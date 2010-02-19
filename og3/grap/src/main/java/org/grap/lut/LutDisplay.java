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
package org.grap.lut;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

public class LutDisplay {
	private ColorModel colorModel;

	public LutDisplay(final ColorModel colorModel) {
		this.colorModel = colorModel;
	}

	public ImagePlus getImagePlus() {
		final int w = 256;
		final int h = 20;
		final ImagePlus imagePlus = NewImage.createByteImage("Lut", w, h, 1, 0);

		if (colorModel instanceof IndexColorModel) {
			final ImageProcessor imageProcessor = imagePlus.getProcessor();
			final byte[] pixels = (byte[]) imageProcessor.getPixels();

			int j = 0;
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					pixels[j++] = (byte) x;
				}
			}
			imagePlus.getProcessor().setColorModel(colorModel);
			imagePlus.updateAndDraw();
		}

		return imagePlus;
	}

	public Image getImage() {
		return getImagePlus().getImage();
	}
}