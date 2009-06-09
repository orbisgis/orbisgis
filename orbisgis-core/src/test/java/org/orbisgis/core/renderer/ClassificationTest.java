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
package org.orbisgis.core.renderer;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.orbisgis.core.renderer.classification.ProportionalMethod;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.classification.RangeMethod;

public class ClassificationTest extends TestCase {

	private DataSourceFactory dsf = new DataSourceFactory();

	// Data to test
	File src = new File("../../datas2tests/shp/bigshape2D/cantons.shp");
	File landcover = new File(
			"../../datas2tests/shp/mediumshape2D/landcover2000.shp");

	public void testStandard() throws Exception {

		DataSource ds = dsf.getDataSource(src);
		ds.open();

		RangeMethod rm = new RangeMethod(ds, "PTOT90", 3);

		rm.disecStandard();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 0, 2608));
		assertTrue(checkRange(ranges[1], 2608, 27377));
		assertTrue(checkRange(ranges[2], 27377, 807726));
		ds.close();

	}

	private boolean checkRange(Range range, int min, int max) {

		if ((range.getMinRange() == min) && (range.getMaxRange() == max)) {
			return true;
		}
		return false;
	}

	public void testInvalidStandardIntervals() throws Exception {
		DataSource ds = dsf.getDataSource(landcover);
		ds.open();

		RangeMethod rm = new RangeMethod(ds, "runoff_win", 2);

		try {
			rm.disecStandard();
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}

		rm = new RangeMethod(ds, "runoff_win", 4);

		try {
			rm.disecStandard();
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		ds.close();
	}

	public void testInvalidMeanIntervals() throws Exception {
		DataSource ds = dsf.getDataSource(landcover);
		ds.open();

		RangeMethod rm = new RangeMethod(ds, "runoff_win", 1);

		try {
			rm.disecMean();
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}

		rm = new RangeMethod(ds, "runoff_win", 3);

		try {
			rm.disecMean();
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}

		rm = new RangeMethod(ds, "runoff_win", 5);

		try {
			rm.disecMean();
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		ds.close();
	}

	public void testEquivalences() throws Exception {

		DataSource ds = dsf.getDataSource(src);
		ds.open();

		RangeMethod rm = new RangeMethod(ds, "PTOT90", 4);

		rm.disecEquivalences();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 0, 203533));
		assertTrue(checkRange(ranges[1], 203533, 422444));
		assertTrue(checkRange(ranges[2], 422444, 807726));
		assertTrue(checkRange(ranges[3], 807726, 807726));
		ds.close();
	}

//	public void testLandcoverEquivalences() throws Exception {
//
//		DataSource ds = dsf.getDataSource(landcover);
//		ds.open();
//
//		RangeMethod rm = new RangeMethod(ds, "runoff_win", 7);
//		rm.disecStandard();
//		testGoodIntervals(rm);
//
//		rm = new RangeMethod(ds, "runoff_win", 6);
//		rm.disecEquivalences();
//		testGoodIntervals(rm);
//
//		rm = new RangeMethod(ds, "runoff_win", 6);
//		rm.disecMean();
//		testGoodIntervals(rm);
//
//		rm = new RangeMethod(ds, "runoff_win", 6);
//		rm.disecQuantiles();
//		testGoodIntervals(rm);
//
//		ds.close();
//	}
//
//	private void testGoodIntervals(RangeMethod rm) {
//		Range[] ranges = rm.getRanges();
//		for (int i = 0; i < ranges.length - 1; i++) {
//			assertTrue(ranges[i].getMaxRange() <= ranges[i + 1].getMinRange());
//			assertTrue(ranges[i].getMaxRange() < ranges[i + 1].getMaxRange());
//
//		}
//	}

	public void testMoyennes() throws Exception {

		DataSource ds = dsf.getDataSource(src);
		ds.open();

		RangeMethod rm = new RangeMethod(ds, "PTOT90", 4);

		rm.disecMean();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 0, 6889));
		assertTrue(checkRange(ranges[1], 6889, 14989));
		assertTrue(checkRange(ranges[2], 14989, 33312));
		assertTrue(checkRange(ranges[3], 33312, 807726));
		ds.close();
	}

	public void testQuantiles() throws Exception {

		DataSource ds = dsf.getDataSource(src);
		ds.open();

		RangeMethod rm = new RangeMethod(ds, "PTOT90", 4);

		rm.disecQuantiles();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 0, 4971));
		assertTrue(checkRange(ranges[1], 4971, 9177));
		assertTrue(checkRange(ranges[2], 9177, 17889));
		assertTrue(checkRange(ranges[3], 17889, 807726));
		ds.close();
	}

	public void testProportionalMethods() throws Exception {
		DataSource ds = dsf.getDataSource(src);
		ds.open();

		ProportionalMethod pm = new ProportionalMethod(ds, "PTOT90");

		pm.build(3000);

		int coefType = 1;
		assertTrue(pm.getLinearSize(18155, coefType) == 8.211579893462739);
		assertTrue(pm.getLinearSize(3153, coefType) == 3.422083335566387);
		assertTrue(pm.getLinearSize(7096, coefType) == 5.1337580804787715);
		assertTrue(pm.getLinearSize(94162, coefType) == 18.701069025383298);
		;

		int sqrtFactor = 2;
		assertTrue(pm.getSquareSize(3871, sqrtFactor, coefType) == 14.411208598469774);
		assertTrue(pm.getSquareSize(32711, sqrtFactor, coefType) == 24.570730414560927);
		assertTrue(pm.getSquareSize(18747, sqrtFactor, coefType) == 21.378516067988976);
		assertTrue(pm.getSquareSize(7499, sqrtFactor, coefType) == 17.001821415072314);

		assertTrue(pm.getLogarithmicSize(8214, coefType) == 44.58703754496408);
		assertTrue(pm.getLogarithmicSize(32990, coefType) == 47.902581954727424);
		assertTrue(pm.getLogarithmicSize(3678, coefType) == 42.55341898566016);
		assertTrue(pm.getLogarithmicSize(94162, coefType) == 50.25912203356446);

	}
	//
	// public void testProportionalLogarithmicMethodInLandCover() throws
	// Exception {
	// DataSource ds = dsf.getDataSource(landcover);
	// ds.open();
	//
	// ProportionalMethod pm = new ProportionalMethod(ds, "runoff_win");
	// pm.build(3000);
	//
	// for (int i = 0; i < ds.getRowCount(); i++) {
	// assertTrue(pm.getLogarithmicSize(ds.getDouble(i, "runoff_win"), 1) !=
	// Double.POSITIVE_INFINITY);
	// }
	//
	// ds.close();
	//
	// }

}
