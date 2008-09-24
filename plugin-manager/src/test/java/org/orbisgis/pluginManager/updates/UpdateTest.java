package org.orbisgis.pluginManager.updates;

import java.io.File;

import junit.framework.TestCase;

public class UpdateTest extends TestCase {

	public void testTreeDifferences() throws Exception {
		File pub = new File("src/test/resources/updates/lastBinary");
		File next = new File("src/test/resources/updates/nextBinary");
		CreateUpdate cu = new CreateUpdate(pub, next);
		cu.diff();

		assertTrue(cu.getAdded().contains(new File(next, "newFolder")));
		assertTrue(cu.getModified().contains(new File(next, "someScript")));
		assertTrue(cu.getModified().contains(new File(next, "jars/modified.jar")));
		assertTrue(cu.getRemoved().contains(new File(pub, "oldFolder")));
	}
}
