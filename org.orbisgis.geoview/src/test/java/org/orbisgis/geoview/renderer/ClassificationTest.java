package org.orbisgis.geoview.renderer;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.geoview.renderer.classification.ProportionalMethod;
import org.orbisgis.geoview.renderer.classification.Range;
import org.orbisgis.geoview.renderer.classification.RangeMethod;

public class ClassificationTest extends TestCase {

	private DataSourceFactory dsf = new DataSourceFactory();

	private void populateWith(ObjectMemoryDriver omd, int... values) {
		for (int i : values) {
			omd.addValues(new Value[] { ValueFactory.createValue(i) });
		}
	}

	public void testStandard() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "classField" }, new Type[] { TypeFactory
						.createType(Type.DOUBLE) });

		populateWith(omd, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		RangeMethod rm = new RangeMethod(dsf.getDataSource(omd), "classField",
				5);

		rm.disecStandard();

		Range[] ranges = rm.getRanges();

		// TODO Check the ranges
		assertTrue(checkRange(ranges[0], 0, 2));
	}

	private boolean checkRange(Range range, int min, int max) {
		// TODO Auto-generated method stub
		return false;
	}

	public void testEquivalences() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "classField" }, new Type[] { TypeFactory
						.createType(Type.DOUBLE) });

		populateWith(omd, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		RangeMethod rm = new RangeMethod(dsf.getDataSource(omd), "classField",
				5);

		rm.disecEquivalences();

		Range[] ranges = rm.getRanges();

		// TODO Check the ranges
	}

	public void testMoyennes() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "classField" }, new Type[] { TypeFactory
						.createType(Type.DOUBLE) });

		populateWith(omd, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		RangeMethod rm = new RangeMethod(dsf.getDataSource(omd), "classField",
				5);

		rm.disecMoyennes();

		Range[] ranges = rm.getRanges();

		// TODO Check the ranges
	}

	public void testQuantiles() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "classField" }, new Type[] { TypeFactory
						.createType(Type.DOUBLE) });

		populateWith(omd, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		RangeMethod rm = new RangeMethod(dsf.getDataSource(omd), "classField",
				5);

		rm.disecQuantiles();

		Range[] ranges = rm.getRanges();

		// TODO Check the ranges
	}

	public void testProportionalMethods() throws Exception {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(
				new String[] { "classField" }, new Type[] { TypeFactory
						.createType(Type.DOUBLE) });

		populateWith(omd, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		ProportionalMethod pm = new ProportionalMethod(dsf.getDataSource(omd),
				"classField");

		int coefType = 1;
		assertTrue(pm.getLinearSize(0, coefType) == 0);
		assertTrue(pm.getLinearSize(0, coefType) == 0);
		assertTrue(pm.getLinearSize(0, coefType) == 0);
		assertTrue(pm.getLinearSize(0, coefType) == 0);

		int sqrtFactor = 0;
		assertTrue(pm.getSquareSize(0, sqrtFactor, coefType) == 0);
		assertTrue(pm.getSquareSize(0, sqrtFactor, coefType) == 0);
		assertTrue(pm.getSquareSize(0, sqrtFactor, coefType) == 0);
		assertTrue(pm.getSquareSize(0, sqrtFactor, coefType) == 0);

		assertTrue(pm.getLogarithmicSize(0, coefType) == 0);
		assertTrue(pm.getLogarithmicSize(0, coefType) == 0);
		assertTrue(pm.getLogarithmicSize(0, coefType) == 0);
		assertTrue(pm.getLogarithmicSize(0, coefType) == 0);

		// TODO Check the proportional sizes
	}
}
