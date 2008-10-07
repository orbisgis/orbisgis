package org.orbisgis;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal installer
 * @author Fernando Gonzalez Cortes
 */
public class Installers extends AbstractReleaseMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			buildStructure();
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot uncompress jdks", e);
		}

		if (lzPackHome == null) {
			throw new MojoExecutionException("lzPackHome configuation missing");
		} else if (versionNumber == null) {
			throw new MojoExecutionException("version configuation missing");
		} else {
			try {
				ExecutionUtils.executeThread(new String[] {
						lzPackHome + "/bin/compile", "install-linux.xml", "-b",
						".", "-o",
						appName + "-linux-installer-" + versionNumber + ".jar",
						"-h", lzPackHome }, null);
				ExecutionUtils.executeThread(new String[] {
						lzPackHome + "/bin/compile",
						"install-windows.xml",
						"-b",
						".",
						"-o",
						appName + "-windows-installer-" + versionNumber
								+ ".jar", "-h", lzPackHome }, null);
			} catch (IOException e) {
				throw new MojoExecutionException("Cannot execute lzPack", e);
			} catch (ExecutionException e) {
				throw new MojoExecutionException("Cannot execute lzPack", e);
			}
		}
	}

	private void buildStructure() throws MojoExecutionException, IOException {
		File linuxFolder = new File("linux");
		File windowsFolder = new File("windows");
		if (!linuxFolder.exists() || !windowsFolder.exists()) {
			throw new MojoExecutionException("This plugin can only be "
					+ "used in the platform-installers project");
		} else {
			Utils.buildStructure(binaryDir);
		}
	}

}
