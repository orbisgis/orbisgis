package org.orbisgis;

import java.io.File;
import java.io.PrintWriter;

import org.orbisgis.pluginManager.ApplicationInfo;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.SystemListener;
import org.orbisgis.pluginManager.workspace.DefaultWorkspace;

import junit.framework.TestCase;

public class WorkspaceTest extends TestCase {

	private static final String NEW_WORKSPACE_VERSION_FILE_TEST = "src/test/resources/newWorkspaceVersionFileTest";
	private TestApplicationInfo applicationInfo;
	private File homeFile;

	@Override
	protected void setUp() throws Exception {
		homeFile = new File("src/test/resources/home");
		Services.registerService("org.orbisgis.PluginManager",
				PluginManager.class, "", new PluginManager() {

					public void stop() {
					}

					public void removeSystemListener(SystemListener listener) {
					}

					public String getLogFile() {
						return null;
					}

					public File getHomeFolder() {
						return homeFile;
					}

					public void addSystemListener(SystemListener listener) {

					}

				});

		applicationInfo = new TestApplicationInfo();
		Services.registerService("org.orbisgis.ApplicationInfo",
				ApplicationInfo.class, "", applicationInfo);

		File current = new File(homeFile, "currentWorkspace.txt");
		current.delete();

		File newWorkspace = new File(NEW_WORKSPACE_VERSION_FILE_TEST);
		deleteDir(newWorkspace);
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public void testInitEmpty() throws Exception {
		File file = new File("src/test/resources/home2");
		TestWorkspace tw = new TestWorkspace();
		tw.setWorkspaceFolderSelectionInDialog(file);
		applicationInfo.wsVersion = 1;
		tw.init(false);
		assertTrue(tw.getFile("..").getCanonicalPath().equals(
				file.getCanonicalPath()));
	}

	public void testGoodVersionNoVersionNumber() throws Exception {
		File file = new File("src/test/resources/wsNoVersion");
		TestWorkspace tw = new TestWorkspace();
		tw.setWorkspaceFolderSelectionInDialog(file);
		applicationInfo.wsVersion = 1;
		tw.init(false);
		assertTrue(tw.getFile("..").getCanonicalPath().equals(
				file.getCanonicalPath()));
	}

	public void testGoodVersion1() throws Exception {
		File file = new File("src/test/resources/wsVersion1");
		TestWorkspace tw = new TestWorkspace();
		tw.setWorkspaceFolderSelectionInDialog(file);
		applicationInfo.wsVersion = 1;
		tw.init(false);
		assertTrue(tw.getFile("..").getCanonicalPath().equals(
				file.getCanonicalPath()));
	}

	public void testBadVersionGoodVersion() throws Exception {
		File file1 = new File("src/test/resources/wsVersion1");
		File file2 = new File("src/test/resources/wsVersion2");
		TestWorkspace tw = new TestWorkspace();
		tw.setWorkspaceFoldersSelectionInDialog(file1, file2);
		applicationInfo.wsVersion = 2;
		tw.init(false);
		assertTrue(tw.allFoldersAsked());
	}

	public void testDontAskIfGood() throws Exception {
		File current = new File(homeFile, "currentWorkspace.txt");
		PrintWriter pw = new PrintWriter(current);
		pw.println("src/test/resources/wsVersion1");
		pw.close();
		TestWorkspace tw = new TestWorkspace();
		applicationInfo.wsVersion = 1;
		tw.init(false);
		assertTrue(tw.allFoldersAsked());
	}

	public void testVersionWrittenAtMetadataDirCreation() throws Exception {
		File file = new File(NEW_WORKSPACE_VERSION_FILE_TEST);
		file.mkdirs();
		TestWorkspace tw = new TestWorkspace();
		tw.setWorkspaceFoldersSelectionInDialog(file);
		applicationInfo.wsVersion = 1;
		tw.init(false);
		assertTrue(tw.allFoldersAsked());
		assertTrue(new File(NEW_WORKSPACE_VERSION_FILE_TEST,
				".metadata/org.orbisgis.version.txt").exists());
	}

	private final class TestApplicationInfo implements ApplicationInfo {
		private int wsVersion;

		public int getWsVersion() {
			return wsVersion;
		}

		public String getVersion() {
			return null;
		}

		public String getOrganization() {
			return null;
		}

		public String getName() {
			return null;
		}

		public void setWsVersion(int wsVersion) {
			this.wsVersion = wsVersion;
		}
	}

	private class TestWorkspace extends DefaultWorkspace {

		private File[] files = new File[0];
		private int index = 0;

		@Override
		protected File askWorkspace() {
			File file = files[index];
			index++;
			file.mkdirs();
			return file;
		}

		public void setWorkspaceFoldersSelectionInDialog(File... files) {
			this.files = files;
		}

		public void setWorkspaceFolderSelectionInDialog(File file) {
			this.files = new File[] { file };
		}

		public boolean allFoldersAsked() {
			return index == files.length;
		}
	}
}
