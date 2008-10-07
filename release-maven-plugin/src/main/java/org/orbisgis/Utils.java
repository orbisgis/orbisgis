package org.orbisgis;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class Utils {

	public static void buildStructure(String binaryDir)
			throws MojoExecutionException, IOException {
		executeAnt("build-installer-structure", "", "", "", "", "", "",
				binaryDir);
	}

	public static void executeZipBinary(String binFileName, String binFolder)
			throws MojoExecutionException, IOException {
		executeAnt("zip-binary", "", "", "", "", "", binFileName, binFolder);
	}

	public static void executeCopyJars(String jarSource, String libDir)
			throws MojoExecutionException, IOException {
		executeAnt("copy-jars", "", "", "", jarSource, libDir, "", "");
	}

	public static void executeCopyPlugin(String pluginDir,
			String pluginBinaryDir) throws MojoExecutionException, IOException {
		executeAnt("copy-plugin", "", pluginDir, pluginBinaryDir, "", "", "",
				"");
	}

	public static void executeSource(String sourceFileName)
			throws MojoExecutionException, IOException {
		executeAnt("source", sourceFileName, "", "", "", "", "", "");
	}

	private static void executeAnt(String task, String sourceFileName,
			String pluginDir, String pluginBinaryDir, String jarSource,
			String libDir, String binFileName, String binFolder)
			throws IOException, FileNotFoundException, MojoExecutionException {
		File ant = File.createTempFile("build", ".xml");
		DataInputStream dis = new DataInputStream(Utils.class
				.getResourceAsStream("/build.xml"));
		byte[] buffer = new byte[dis.available()];
		dis.readFully(buffer);
		dis.close();
		String script = new String(buffer);
		script = script.replaceAll("\\QSOURCE_FILE_NAME\\E", sourceFileName);
		script = script.replaceAll("\\QDESTINATION\\E", pluginBinaryDir);
		script = script.replaceAll("\\QPLUGIN_DIR\\E", pluginDir);
		script = script.replaceAll("\\QLIB_DIR\\E", libDir);
		script = script.replaceAll("\\QJAR_SOURCE\\E", jarSource);
		script = script.replaceAll("\\QBIN_FILE_NAME\\E", binFileName);
		script = script.replaceAll("\\QBIN_FOLDER\\E", binFolder);
		script = script.replaceAll("\\QBIN_FOLDER\\E", binFolder);
		script = script.replaceAll("\\QBASEDIR\\E", new File(".")
				.getAbsolutePath());
		FileOutputStream antos = new FileOutputStream(ant);
		antos.write(script.getBytes());
		antos.close();

		Project p = new Project();
		p.setUserProperty("ant.file", ant.getAbsolutePath());
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);

		try {
			p.fireBuildStarted();
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, ant);
			p.executeTarget(task);
			p.fireBuildFinished(null);
		} catch (BuildException e) {
			p.fireBuildFinished(e);
			throw new MojoExecutionException("Cannot execute ant", e);
		}
	}

}
