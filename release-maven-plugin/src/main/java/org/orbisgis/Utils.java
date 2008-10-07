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

	public static void unzip(File zipFile, File outFolder) throws IOException,
			MojoExecutionException {
		String script = getAntScript();
		script = script.replaceAll("\\QZIP_FILE\\E", zipFile.getAbsolutePath());
		script = script.replaceAll("\\QOUT_FOLDER\\E", outFolder.getAbsolutePath());
		executeAnt("unzip", script);
	}

	public static void buildStructure(String binaryDir)
			throws MojoExecutionException, IOException {
		String script = getAntScript();
		script = script.replaceAll("\\QBIN_FOLDER\\E", binaryDir);
		executeAnt("build-installer-structure", script);
	}

	public static void executeZipBinary(String binFileName, String binFolder)
			throws MojoExecutionException, IOException {
		String script = getAntScript();
		script = script.replaceAll("\\QBIN_FILE_NAME\\E", binFileName);
		script = script.replaceAll("\\QBIN_FOLDER\\E", binFolder);
		executeAnt("zip-binary", script);
	}

	public static void executeCopyJars(String jarSource, String libDir)
			throws MojoExecutionException, IOException {
		String script = getAntScript();
		script = script.replaceAll("\\QLIB_DIR\\E", libDir);
		script = script.replaceAll("\\QJAR_SOURCE\\E", jarSource);
		executeAnt("copy-jars", script);
	}

	public static void executeCopyPlugin(String pluginDir,
			String pluginBinaryDir) throws MojoExecutionException, IOException {
		String script = getAntScript();
		script = script.replaceAll("\\QDESTINATION\\E", pluginBinaryDir);
		script = script.replaceAll("\\QPLUGIN_DIR\\E", pluginDir);
		executeAnt("copy-plugin", script);
	}

	public static void executeSource(String sourceFileName)
			throws MojoExecutionException, IOException {
		String script = getAntScript();
		script = script.replaceAll("\\QSOURCE_FILE_NAME\\E", sourceFileName);
		executeAnt("source", script);
	}

	private static void executeAnt(String task, String script)
			throws IOException, FileNotFoundException, MojoExecutionException {
		script = script.replaceAll("\\QBASEDIR\\E", new File(".")
				.getAbsolutePath());
		File ant = File.createTempFile("build", ".xml");
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

	private static String getAntScript() throws IOException {
		DataInputStream dis = new DataInputStream(Utils.class
				.getResourceAsStream("/build.xml"));
		byte[] buffer = new byte[dis.available()];
		dis.readFully(buffer);
		dis.close();
		String script = new String(buffer);
		return script;
	}

}
