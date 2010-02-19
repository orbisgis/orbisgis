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
package org.grap.processing.operation;

import org.grap.io.GrapTest;
import org.grap.model.GeoRaster;
import org.grap.processing.cellularAutomata.cam.ICA;
import org.grap.processing.cellularAutomata.cam.ICAFloat;
import org.grap.processing.cellularAutomata.cam.ICAN;
import org.grap.processing.cellularAutomata.cam.ICAShort;
import org.grap.processing.cellularAutomata.parallelImpl.PCAN;
import org.grap.processing.cellularAutomata.seqImpl.SCAN;
import org.grap.processing.cellularAutomata.useless.CAGetAllSubWatershed;
import org.grap.processing.cellularAutomata.useless.CASlopesAccumulation;
import org.grap.processing.cellularAutomata.useless.CASlopesDirections;

public class CANImplementationsTest extends GrapTest {
	private GeoRaster geoRasterSrc;
	private short[] slopesDirections = null;

	protected void setUp() throws Exception {
		super.setUp();
		geoRasterSrc = sampleDEM;
	}

	public void testSeqAndParImplementations() throws Exception {
		// load the DEM
		// geoRasterSrc.open();
		// final float[] DEM = geoRasterSrc.getPixelProvider().getProcessor().getFloatPixels();
		// final int nrows = geoRasterSrc.getHeight();
		// final int ncols = geoRasterSrc.getWidth();

		final float[] DEM = new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, };
		final int nrows = 5;
		final int ncols = 5;

		compareParAndSeqImpl(new CASlopesDirections(DEM, nrows, ncols));
		// TODO
		// compareParAndSeqImpl(new CASlope(DEM, nrows, ncols));
		compareParAndSeqImpl(new CASlopesAccumulation(slopesDirections, nrows,
				ncols));
		compareParAndSeqImpl(new CAGetAllSubWatershed(slopesDirections, nrows,
				ncols));
	}

	private void compareParAndSeqImpl(final ICA ca) {
		final ICAN scan = new SCAN(ca);
		final ICAN pcan = new PCAN(ca);

		System.out.println(ca.getClass().getSimpleName());
		final int scanNbOfIter = scan.getStableState();
		final int pcanNbOfIter = pcan.getStableState();
		// TODO assertTrue(scanNbOfIter == pcanNbOfIter);

		if (ca instanceof ICAShort) {
			final short[] seq = (short[]) scan.getCANValues();
			final short[] par = (short[]) pcan.getCANValues();
			assertTrue(seq.length == par.length);
			for (int i = 0; i < seq.length; i++) {
				assertTrue(seq[i] == par[i]);
			}
		} else if (ca instanceof ICAFloat) {
			final float[] seq = (float[]) scan.getCANValues();
			final float[] par = (float[]) pcan.getCANValues();
			assertTrue(seq.length == par.length);
			for (int i = 0; i < seq.length; i++) {
				assertTrue(seq[i] == par[i]);
			}
		} else {
			assertTrue(false);
		}

		if (ca instanceof CASlopesDirections) {
			slopesDirections = (short[]) scan.getCANValues();
		}
	}
}