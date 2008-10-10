package org.orbisgis;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.ximpleware.AutoPilot;
import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

/**
 * @goal binary
 * @aggregator
 * 
 * @author Fernando Gonzalez Cortes
 */
public class BinaryPack extends AbstractReleaseMojo {

	private File bin;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		bin = new File("target/" + appName);
		if (!bin.exists() && !bin.mkdirs()) {
			throw new MojoExecutionException("Cannot create binary directory: "
					+ bin);
		}

		getLog().info("Building binary in " + bin.getAbsolutePath());

		try {
			String[] plugins = readPluginList();
			File[] pluginsDir = new File[plugins.length];
			for (int i = 0; i < pluginsDir.length; i++) {
				pluginsDir[i] = new File(new File(pluginList).getParentFile(),
						plugins[i]);
			}

			createPluginStructure(pluginsDir);

			copyJars(pluginsDir);

			generateShellFiles();

			zipBin();
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot read plugin-list.xml", e);
		} catch (EncodingException e) {
			throw new MojoExecutionException("Cannot read plugin-list.xml", e);
		} catch (EOFException e) {
			throw new MojoExecutionException("Cannot read plugin-list.xml", e);
		} catch (EntityException e) {
			throw new MojoExecutionException("Cannot read plugin-list.xml", e);
		} catch (ParseException e) {
			throw new MojoExecutionException("Cannot read plugin-list.xml", e);
		} catch (XPathParseException e) {
			throw new MojoExecutionException("Cannot read plugin-list.xml", e);
		} catch (XPathEvalException e) {
			throw new MojoExecutionException("Cannot read plugin-list.xml", e);
		} catch (NavException e) {
			throw new MojoExecutionException("Cannot read plugin-list.xml", e);
		}
	}

	private void zipBin() throws MojoExecutionException, IOException {
		Utils.executeZipBinary(getFileName(false), bin.getAbsolutePath());
	}

	private void generateShellFiles() throws FileNotFoundException {
		String shellName = appName.toLowerCase();
		createLinuxShells("", new File(bin, shellName + ".sh"));
		createLinuxShells("../jdk/bin/", new File(bin, shellName
				+ ".sh.installer"));
		createWindowShells("", new File(bin, shellName + ".bat"));
		createWindowShells("..\\jdk\\bin\\", new File(bin, shellName
				+ ".bat.installer"));
	}

	private void createWindowShells(String javaBinPath, File windowsShell)
			throws FileNotFoundException {
		PrintWriter pw;
		pw = new PrintWriter(windowsShell);
		pw.println("echo off");
		pw.println("if exist bin2. (");
		pw.println("  rmdir /q /s bin");
		pw.println("  move /y bin2 bin");
		pw.println(")");
		pw.println("start " + javaBinPath + "javaw -Xmx512M -cp "
				+ getClassPath(";") + " " + mainClass + " %1");
		pw.close();
	}

	private void createLinuxShells(String javaBinPath, File linuxShell)
			throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(linuxShell);
		pw.println("#!/bin/bash");
		pw.println("if [ bin2 ]; then");
		pw.println("  rm -fr bin;");
		pw.println("  mv bin2 bin;");
		pw.println("fi");
		pw.println(javaBinPath + "java -Xmx512M -cp " + getClassPath(":") + " "
				+ mainClass + " ${@}");
		pw.close();
		linuxShell.setExecutable(true);
	}

	private String getClassPath(String separator) {
		File lib = new File(bin, "lib");
		File[] libs = lib.listFiles();
		String ret = "";
		String sep = "";
		for (File jar : libs) {
			ret = ret + sep + "lib/" + jar.getName();
			sep = separator;
		}

		return ret;
	}

	private void copyJars(File[] pluginsDir) throws MojoExecutionException,
			IOException {
		// Copy dependencies
		File lib = new File(bin, "lib");
		if (!lib.exists() && !lib.mkdirs()) {
			throw new IOException("Cannot create lib directory");
		}
		Utils.executeCopyJars("dependencies", lib.getAbsolutePath());

		// Copy plugin jars
		for (File pluginDir : pluginsDir) {
			Utils.executeCopyJars(pluginDir + "/target", lib.getAbsolutePath());
		}
	}

	private void createPluginStructure(File[] pluginsDir) throws IOException,
			MojoExecutionException {
		PrintWriter pw = new PrintWriter(new File(bin, "plugin-list.xml"));
		try {
			pw.println("<plugins>");
			for (File pluginDir : pluginsDir) {
				File binPlugins = new File(bin, "plugins");
				File binaryPluginDir = new File(binPlugins, pluginDir.getName());
				if (!binaryPluginDir.exists() && !binaryPluginDir.mkdirs()) {
					throw new IOException("Cannot create plugin dir: "
							+ binaryPluginDir);
				} else {
					Utils.executeCopyPlugin(pluginDir.getAbsolutePath(),
							binaryPluginDir.getAbsolutePath());
					pw.println("  <plugin dir=\"plugins/" + pluginDir.getName()
							+ "\"/>");
				}
			}
			pw.println("</plugins>");
		} finally {
			pw.close();
		}

	}

	private String[] readPluginList() throws IOException, EncodingException,
			EOFException, EntityException, ParseException, XPathParseException,
			XPathEvalException, NavException {
		if ((pluginList == null) || !new File(pluginList).exists()) {
			throw new IOException("Cannot find pluginList: " + pluginList);
		} else {
			DataInputStream dis = new DataInputStream(new FileInputStream(
					new File(pluginList)));
			byte[] buffer = new byte[dis.available()];
			dis.readFully(buffer);
			dis.close();

			VTDGen gen = new VTDGen();
			gen.setDoc(buffer);
			gen.parse(false);
			VTDNav vn = gen.getNav();
			AutoPilot ap = new AutoPilot(vn);
			ap.selectXPath("/plugins/plugin/@dir");
			ArrayList<String> plugins = new ArrayList<String>();
			while (ap.evalXPath() != -1) {
				int attrIndex = vn.getAttrVal("dir");
				if (attrIndex != -1) {
					plugins.add(vn.toNormalizedString(attrIndex));
				}
			}

			return plugins.toArray(new String[0]);
		}
	}
}
