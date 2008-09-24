package org.orbisgis.pluginManager.updates;

import java.io.File;

import junit.framework.TestCase;

public class UpdateTest extends TestCase {

	private File pub;
	private File next;
	private File output;
	private CreateUpdate cu;

	@Override
	protected void setUp() throws Exception {
		pub = new File("src/test/resources/updates/lastBinary");
		next = new File("src/test/resources/updates/nextBinary");
		output = new File("target/updates");
		cu = new CreateUpdate(pub, next, output);
	}

	public void testTreeDifferences() throws Exception {
		cu.diff();

		assertTrue(cu.getAdded().contains(new File(next, "newFolder")));
		assertTrue(cu.getModified().contains(new File(next, "someScript")));
		assertTrue(cu.getModified().contains(
				new File(next, "jars/modified.jar")));
		assertTrue(cu.getRemoved().contains(new File(pub, "oldFolder")));
	}
	
	public void testCreateOutput() throws Exception {
		cu.create();
	}
}
