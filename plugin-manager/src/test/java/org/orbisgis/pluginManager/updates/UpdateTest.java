package org.orbisgis.pluginManager.updates;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.orbisgis.pluginManager.updates.persistence.Update;
import org.orbisgis.pluginManager.updates.persistence.UpdateSite;
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
		cu = new CreateUpdate(pub, next, updateDir, this.getClass()
				.getResource("nonExistingVersion"), "1.0.0", "Valencia",
				"This is the best OG version ever");
	}

	public void testTreeDifferences() throws Exception {
		cu.diff();

		assertTrue(cu.getAdded().contains(new File(next, "newFolder")));
		assertTrue(cu.getModified().contains(new File(next, "someScript")));
		assertTrue(cu.getModified().contains(
				new File(next, "jars/modified.bin")));
		assertTrue(cu.getRemoved().contains(new File(pub, "oldFolder")));
	}

	public void testModifySiteDescriptor() throws Exception {
		updateDir.mkdirs();
		cu.modifySiteDescriptor(updateDir, this.getClass().getResource(
				"existingVersion"), "");
		assertTrue(containsVersion("1.0.0", "Valencia", 1));
		cu.modifySiteDescriptor(updateDir, this.getClass().getResource(
				"nonExistingDescriptor"), "");
		assertTrue(containsVersion("1.0.0", "Valencia", 1));
		cu.modifySiteDescriptor(updateDir, this.getClass().getResource(
				"nonExistingVersion"), "");
		assertTrue(containsVersion("1.0.0", "Valencia", 2));
		assertTrue(containsVersion("0.9", "Xirivella", 2));
	}

	private boolean containsVersion(String versions, String versionName,
			int versionCount) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(UpdateSite.class
				.getPackage().getName());
		UpdateSite us = (UpdateSite) context.createUnmarshaller().unmarshal(
				new File(updateDir, UpdateUtils.SITE_UPDATES_FILE_NAME));
		List<Update> updateList = us.getUpdate();
		int updateCount = updateList.size();
		if (updateCount != versionCount) {
			throw new RuntimeException("bug!");
		}
		for (int i = 0; i < updateCount; i++) {
			Update update = updateList.get(i);
			if (update.getVersionNumber().equals(versions)) {
				assertTrue(update.getVersionName().equals(versionName));
				return true;
			}
		}

		return false;
	}

	public void testApplyUpdate() throws Exception {
		// Create the update
		cu.create();

		// Move lastBinary to target/binary
		FileUtils.copyDirsRecursively(pub, testPub);

		// apply update to target/binary
		cu.applyUpdate(new File(updateDir, UpdateUtils
				.getUpdateFileName("1.0.0")), testPub);

		// compare folders target/binary and nextBinary
		compare(next, testPub);
	}

	public void testVersionCompare() throws Exception {
		UpdateDiscovery ui = new UpdateDiscovery(null);
		assertTrue(ui.compare("1.0.0", "1.0") == 0);
		assertTrue(ui.compare("1.0.0", "1.0.0.1") < 0);
		assertTrue(ui.compare("1.0.0", "1.0.0") == 0);
		assertTrue(ui.compare("1.0.0", "0.9") > 0);
	}

	public void testDiscover() throws Exception {
		// Create the update
		cu.create();

		// Discover new updates
		UpdateDiscovery ud = new UpdateDiscovery(this.getClass().getResource(
				"twoVersions"));
		UpdateInfo[] updates = ud.getAvailableUpdatesInfo("0.8");
		assertTrue(updates.length == 2);

		updates = ud.getAvailableUpdatesInfo("0.9.1");
		assertTrue(updates.length == 1);

		updates = ud.getAvailableUpdatesInfo("1");
		assertTrue(updates.length == 0);
	}

	public void testDownloadDiscovered() throws Exception {
		// Create the update
		cu.setUrlPrefix("file://"
				+ new File("target/updates").getAbsolutePath() + "/");
		cu.create();

		// Discover new updates
		UpdateDiscovery ud = new UpdateDiscovery(updateDir.toURI().toURL());
		UpdateInfo[] updates = ud.getAvailableUpdatesInfo("0.9.1");
		assertTrue(updates.length == 1);

		ud.download(updates[0]);

		// Download new updates with wrong checksum
		FileWriter pw = new FileWriter(new File(updateDir, UpdateUtils
				.getUpdateFileName(updates[0].getVersionNumber())), true);
		pw.write(0);
		pw.close();
		try {
			ud.download(updates[0]);
			assertTrue(false);
		} catch (IOException e) {
		}
	}

	private void compare(File dir1, File dir2) throws Exception {
		CreateUpdate cu = new CreateUpdate(dir1, dir2, null, new URL(
				"file:///foo"), "", "", "");
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
