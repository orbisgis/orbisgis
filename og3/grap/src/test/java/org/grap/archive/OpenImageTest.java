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
import ij.io.Opener;
import ij.plugin.TextReader;
import ij.process.ImageProcessor;

import org.grap.lut.LutGenerator;

public class OpenImageTest {
	public static void main(String[] args) {
		final String src1 = "../../datas2tests/geotif/440606.tif";
		final Opener opener = new Opener();
		final ImagePlus imp1 = opener.openImage(src1);
		imp1.getProcessor().setColorModel(LutGenerator.colorModel("fire"));
		imp1.show();
		System.out.println(imp1.getType() == ImagePlus.GRAY8);

		final String src2 = "../../datas2tests/grid/ijsample.asc";
		final TextReader textReader = new TextReader();
		final ImageProcessor ip2 = textReader.open(src2);
		final ImagePlus imp2 = new ImagePlus("", ip2);

		ip2.setColorModel(LutGenerator.colorModel("fire"));
		imp2.show();
		System.out.println(imp2.getType() == ImagePlus.GRAY32);

		int[] v = imp2.getPixel(300, 300);
		System.out.println(Float.intBitsToFloat(v[0]));
	}
}