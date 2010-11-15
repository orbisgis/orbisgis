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
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.classification.ProportionalMethod;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.classification.RangeMethod;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;

public class ClassificationTest extends TestCase {

	private DataSourceFactory dsf = new DataSourceFactory();

	// Data to test
	File landcover = new File("src/test/resources/data//landcover2000.shp");

	public void testStandard() throws Exception {

		DataSource ds = dsf.getDataSource(landcover);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();

		RangeMethod rm = new RangeMethod(sds, new RealAttribute("PTOT90"), 3);

		rm.disecStandard();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 0.05, 0.2));
		assertTrue(checkRange(ranges[1], 0.2, 0.4));
		assertTrue(checkRange(ranges[2], 0.4, 1));
		ds.close();

	}

	private boolean checkRange(Range range, double min, double max) {

		if ((range.getMinRange() == min) && (range.getMaxRange() == max)) {
			return true;
		}
		return false;
	}

	public void testInvalidStandardIntervals() throws Exception {
		DataSource ds = dsf.getDataSource(landcover);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();

		RangeMethod rm = new RangeMethod(sds, new RealAttribute("runoff_win"), 2);

		try {
			rm.disecStandard();
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}

		rm = new RangeMethod(sds, new RealAttribute("runoff_win"), 4);

		try {
			rm.disecStandard();
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		ds.close();
	}

	public void testInvalidMeanIntervals() throws Exception {
		DataSource ds = dsf.getDataSource(landcover);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();

		RangeMethod rm = new RangeMethod(sds, new RealAttribute("runoff_win"), 1);

		try {
			rm.disecMean();
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}

		rm = new RangeMethod(sds, new RealAttribute("runoff_win"), 3);

		try {
			rm.disecMean();
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}

		rm = new RangeMethod(sds, new RealAttribute("runoff_win"), 5);

		try {
			rm.disecMean();
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		sds.close();
	}

	public void testEquivalences() throws Exception {

		DataSource ds = dsf.getDataSource(landcover);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();

		RangeMethod rm = new RangeMethod(sds, new RealAttribute("PTOT90"), 4);

		rm.disecEquivalences();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 0.05, 0.4));
		assertTrue(checkRange(ranges[1], 0.4, 1));
		assertTrue(checkRange(ranges[2], 1, 1));
		assertTrue(checkRange(ranges[3], 1, 1));
		sds.close();
	}


	public void testMoyennes() throws Exception {

		DataSource ds = dsf.getDataSource(landcover);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();

		RangeMethod rm = new RangeMethod(sds, new RealAttribute("PTOT90"), 4);

		rm.disecMean();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 1, 310));
		assertTrue(checkRange(ranges[1], 310, 619));
		assertTrue(checkRange(ranges[2], 619, 929));
		assertTrue(checkRange(ranges[3], 929, 1237));
		sds.close();
	}

	public void testQuantiles() throws Exception {

		DataSource ds = dsf.getDataSource(landcover);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();

		RangeMethod rm = new RangeMethod(sds, new RealAttribute("PTOT90"), 4);

		rm.disecQuantiles();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 1, 310));
		assertTrue(checkRange(ranges[1], 310, 620));
		assertTrue(checkRange(ranges[2], 620, 930));
		assertTrue(checkRange(ranges[3], 930, 1237));
		sds.close();
	}

	public void testProportionalMethods() throws Exception {
		DataSource ds = dsf.getDataSource(landcover);
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();

		ProportionalMethod pm = new ProportionalMethod(sds, new RealAttribute("PTOT90"));

		pm.build(3000);

		int coefType = 1;
		assertTrue(pm.getLinearSize(1, coefType) == 1.5573125286997695);
		assertTrue(pm.getLinearSize(10, coefType) == 4.92465461940761);
		assertTrue(pm.getLinearSize(100, coefType) == 15.573125286997696);
		assertTrue(pm.getLinearSize(300, coefType) == 26.973444229715664);
		

		int sqrtFactor = 2;
		assertTrue(pm.getSquareSize(1, sqrtFactor, coefType) == 9.235665655783968);
		assertTrue(pm.getSquareSize(10, sqrtFactor, coefType) == 16.423594073684257);
		assertTrue(pm.getSquareSize(100, sqrtFactor, coefType) == 29.205739180069987);
		assertTrue(pm.getSquareSize(300, sqrtFactor, coefType) == 38.43691436395855);

		assertTrue(pm.getLogarithmicSize(1, coefType) == 0.0);
		assertTrue(pm.getLogarithmicSize(10, coefType) == 31.146906757706503);
		assertTrue(pm.getLogarithmicSize(100, coefType) == 44.048377962718746);
		assertTrue(pm.getLogarithmicSize(300, coefType) == 49.02172119415002);

	}
	

}
