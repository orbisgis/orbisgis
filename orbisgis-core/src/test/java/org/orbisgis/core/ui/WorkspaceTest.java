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
package org.orbisgis.core.ui;

import java.io.File;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.workspace.DefaultSwingWorkspace;
import org.orbisgis.utils.FileUtils;

public class WorkspaceTest extends TestCase {

	private static final String NEW_WORKSPACE_VERSION_FILE_TEST = "target/newWorkspaceVersionFileTest";
	private File homeFile;

	@Override
	protected void setUp() throws Exception {
		homeFile = new File("target/home");
		Services.registerService(ApplicationInfo.class, "", new ApplicationInfo() {

			@Override
			public File getHomeFolder() {
				return homeFile;
			}

			@Override
			public String getLogFile() {
				return null;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public String getOrganization() {
				return null;
			}

			@Override
			public String getVersionName() {
				return null;
			}

			@Override
			public String getVersionNumber() {
				return null;
			}

		});

		File current = new File(homeFile, "currentWorkspace.txt");
		current.delete();

		File newWorkspace = new File(NEW_WORKSPACE_VERSION_FILE_TEST);
		FileUtils.deleteDir(newWorkspace);
	}

	public void testInitEmpty() throws Exception {
		File file = new File("target/home2");
		FileUtils.deleteDir(file);
		TestWorkspace tw = new TestWorkspace();
		tw.setWorkspaceFolderSelectionInDialog(file);
		tw.wsVersion = 1;
		tw.init(false);
		assertTrue(tw.getFile("..").getCanonicalPath().equals(
				file.getCanonicalPath()));
	}

	public void testInitEmptyCancelWorkspaceSelection() throws Exception {
		TestWorkspace tw = new TestWorkspace();
		tw.setWorkspaceFolderSelectionInDialog(null);
		tw.wsVersion = 1;
		try {
			tw.init(false);
			assertTrue(false);
		} catch (RuntimeException e) {
		}
	}

	public void testGoodVersionNoVersionNumber() throws Exception {
		File file = new File(
				"src/test/resources/org/orbisgis/workspace/wsNoVersion");
		TestWorkspace tw = new TestWorkspace();
		tw.setWorkspaceFolderSelectionInDialog(file);
		tw.wsVersion = 1;
		tw.init(false);
		assertTrue(tw.getFile("..").getCanonicalPath().equals(
				file.getCanonicalPath()));
	}

	public void testGoodVersion1() throws Exception {
		File file = new File(
				"src/test/resources/org/orbisgis/workspace/wsVersion1");
		TestWorkspace tw = new TestWorkspace();
		tw.setWorkspaceFolderSelectionInDialog(file);
		tw.wsVersion = 1;
		tw.init(false);
		assertTrue(tw.getFile("..").getCanonicalPath().equals(
				file.getCanonicalPath()));
	}

	public void testBadVersionGoodVersion() throws Exception {
		File file1 = new File(
				"src/test/resources/org/orbisgis/workspace/wsVersion1");
		File file2 = new File(
				"src/test/resources/org/orbisgis/workspace/wsVersion2");
		TestWorkspace tw = new TestWorkspace();
		tw.setWorkspaceFoldersSelectionInDialog(file1, file2);
		tw.wsVersion = 2;
		tw.init(false);
		assertTrue(tw.allFoldersAsked());
	}

	public void testDontAskIfGood() throws Exception {
		File current = new File(homeFile, "currentWorkspace.txt");
		PrintWriter pw = new PrintWriter(current);
		pw.println("src/test/resources/org/orbisgis/workspace/wsVersion1");
		pw.close();
		TestWorkspace tw = new TestWorkspace();
		tw.wsVersion = 1;
		tw.init(false);
		assertTrue(tw.allFoldersAsked());
	}

	public void testVersionWrittenAtMetadataDirCreation() throws Exception {
		File file = new File(NEW_WORKSPACE_VERSION_FILE_TEST);
		file.mkdirs();
		TestWorkspace tw = new TestWorkspace();
		tw.setWorkspaceFoldersSelectionInDialog(file);
		tw.wsVersion = 1;
		tw.init(false);
		assertTrue(tw.allFoldersAsked());
		assertTrue(new File(NEW_WORKSPACE_VERSION_FILE_TEST,
				".metadata/org.orbisgis.version.txt").exists());
	}

	private class TestWorkspace extends DefaultSwingWorkspace {

		private File[] files = new File[0];
		private int index = 0;
		private int wsVersion;

		@Override
		protected File askWorkspace() {
			File file = files[index];
			if (file != null) {
				index++;
				file.mkdirs();
				return file;
			} else {
				return null;
			}
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

		public int getWsVersion() {
			return wsVersion;
		}

		public void setWsVersion(int wsVersion) {
			this.wsVersion = wsVersion;
		}
	}
}
