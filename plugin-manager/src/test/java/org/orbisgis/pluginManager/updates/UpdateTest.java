package org.orbisgis.pluginManager.updates;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;

import org.orbisgis.utils.FileUtils;

public class UpdateTest extends TestCase {

	private File pub;
	private File next;
	private File updateDir;
	private CreateUpdate cu;
	private File testPub;

	@Override
	protected void setUp() throws Exception {
		pub = new File("src/test/resources/updates/lastBinary");
		next = new File("src/test/resources/updates/nextBinary");
		updateDir = new File("target/updates");
		testPub = new File("target/testpub");
		cu = new CreateUpdate(pub, next, updateDir);
	}

	public void testTreeDifferences() throws Exception {
		cu.diff();

		assertTrue(cu.getAdded().contains(new File(next, "newFolder")));
		assertTrue(cu.getModified().contains(new File(next, "someScript")));
		assertTrue(cu.getModified().contains(
				new File(next, "jars/modified.jar")));
		assertTrue(cu.getRemoved().contains(new File(pub, "oldFolder")));
	}

	public void testApplyUpdate() throws Exception {
		// Create the update
		cu.create();

		// Move lastBinary to target/binary
		FileUtils.copyDirsRecursively(pub, testPub);

		// substitute variables in ant script
		File updateAntFile = new File(updateDir, "update.xml");
		FileInputStream fis = new FileInputStream(updateAntFile);
		DataInputStream dis = new DataInputStream(fis);
		byte[] buffer = new byte[dis.available()];
		dis.readFully(buffer);
		dis.close();
		String content = new String(buffer);
		content = content.replaceAll("\\Q[UPDATE_DIR]\\E", updateDir
				.getAbsolutePath());
		content = content.replaceAll("\\Q[ORBISGIS_HOME]\\E", testPub
				.getAbsolutePath());
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(
				updateAntFile));
		dos.write(content.getBytes());
		dos.close();

		// execute ant to target/binary
		cu.applyUpdate(updateDir, testPub);

		// compare folders target/binary and nextBinary
		compare(next, testPub);
	}

	private void compare(File dir1, File dir2) throws Exception {
		CreateUpdate cu = new CreateUpdate(dir1, dir2, null);
		cu.diff();
		assertTrue(cu.getAdded().size() == 0);
		assertTrue(cu.getModified().size() == 0);
		assertTrue(cu.getRemoved().size() == 0);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		FileUtils.deleteDir(testPub);
		FileUtils.deleteDir(updateDir);
	}
}
