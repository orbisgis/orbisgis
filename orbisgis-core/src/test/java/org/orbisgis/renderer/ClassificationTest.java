package org.orbisgis.renderer;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.orbisgis.renderer.classification.ProportionalMethod;
import org.orbisgis.renderer.classification.Range;
import org.orbisgis.renderer.classification.RangeMethod;

public class ClassificationTest extends TestCase {

	private DataSourceFactory dsf = new DataSourceFactory();

	//Data to test
	File src = new File("../../datas2tests/shp/bigshape2D/cantons.shp");
	File landcover = new File("../../datas2tests/shp/mediumshape2D/landcover2000.shp");


	public void testStandard() throws Exception {

		DataSource ds = dsf.getDataSource(src);
		ds.open();

		RangeMethod rm = new RangeMethod(ds, "PTOT90",
				3);

		rm.disecStandard();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 0, 2608));
		assertTrue(checkRange(ranges[1], 2608, 27377));
		assertTrue(checkRange(ranges[2], 27377, 807726));
		ds.cancel();

	}

	private boolean checkRange(Range range, int min, int max) {

		if ((range.getMinRange() == min)&& (range.getMaxRange()==max)){
			return true;
		}
		return false;
	}
//
//	public void testTwoIntervals() throws Exception {
//
//		DataSource ds = dsf.getDataSource(src);
//		ds.open();
//
//		RangeMethod rm = new RangeMethod(ds, "runoff_win",
//				2);
//
//		rm.disecEquivalences();
//		ds.cancel();
//	}
//
	public void testEquivalences() throws Exception {

		DataSource ds = dsf.getDataSource(src);
		ds.open();

		RangeMethod rm = new RangeMethod(ds, "PTOT90",
				4);

		rm.disecEquivalences();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 0, 203533));
		assertTrue(checkRange(ranges[1], 203533, 422444));
		assertTrue(checkRange(ranges[2], 422444, 807726));
		assertTrue(checkRange(ranges[3], 807726, 807726));
		ds.cancel();
	}

	public void testMoyennes() throws Exception {

		DataSource ds = dsf.getDataSource(src);
		ds.open();

		RangeMethod rm = new RangeMethod(ds, "PTOT90",
				4);

		rm.disecMoyennes();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 0, 6889));
		assertTrue(checkRange(ranges[1], 6889, 14989));
		assertTrue(checkRange(ranges[2], 14989, 33312));
		assertTrue(checkRange(ranges[3], 33312, 807726));
		ds.cancel();
	}

	public void testQuantiles() throws Exception {

		DataSource ds = dsf.getDataSource(src);
		ds.open();

		RangeMethod rm = new RangeMethod(ds, "PTOT90",
				4);

		rm.disecQuantiles();

		Range[] ranges = rm.getRanges();
		assertTrue(checkRange(ranges[0], 0, 4971));
		assertTrue(checkRange(ranges[1], 4971, 9177));
		assertTrue(checkRange(ranges[2], 9177, 17889));
		assertTrue(checkRange(ranges[3], 17889, 807726));
		ds.cancel();
	}

	public void testProportionalMethods() throws Exception {
		DataSource ds = dsf.getDataSource(src);
		ds.open();

		ProportionalMethod pm = new ProportionalMethod(ds,
				"PTOT90");

		pm.build(3000);


		int coefType = 1;
		assertTrue(pm.getLinearSize(18155, coefType) == 8.211579893462739);
		assertTrue(pm.getLinearSize(3153, coefType) == 3.422083335566387);
		assertTrue(pm.getLinearSize(7096, coefType) == 5.1337580804787715);
		assertTrue(pm.getLinearSize(94162, coefType) == 18.701069025383298);;


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



}
