package org.gdms.newFunctionalities;

import org.gdms.SourceTest;

public class NoEmptyDataSetTest extends SourceTest {

	public void testNoEmptyDataSetTest() throws Exception {
		testNoEmptyDataSetTest(false);
		testNoEmptyDataSetTest(true);
	}

	private void testNoEmptyDataSetTest(boolean writingTest) throws Exception {
		setWritingTests(writingTest);
		assertTrue(super.getSmallResources().length > 0);
		assertTrue(super.getResourcesWithPK().length > 0);
		assertTrue(super.getDBResources().length > 0);
		assertTrue(super.getSmallResourcesWithNullValues().length > 0);
		assertTrue(super.getResourcesWithNumericField().length > 0);
		assertTrue(super.getResourcesWithNullValues().length > 0);
		assertTrue(super.getResourcesWithRepeatedRows().length > 0);
		assertTrue(super.getSpatialResources().length > 0);
		assertTrue(super.getAnyNonSpatialResource() != null);
		assertTrue(super.getAnySpatialResource() != null);
	}

}
