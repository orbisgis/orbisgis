package org.orbisgis.pluginManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class EclipseProjectReader implements PluginClassPathReader {

	private static final String CLASSPATH_CLASSPATHENTRY = "/classpath/classpathentry";

	public boolean accepts(File pluginDir) {
		File projectFile = new File(pluginDir, ".project");
		if (projectFile.exists()) {
			File classpathFile = new File(pluginDir, ".classpath");
			if (classpathFile.exists()) {
				return true;
			}
		}

		return false;
	}

	public URL[] getJars(File pluginDir) {
		File classpathFile = new File(pluginDir, ".classpath");
		ArrayList<URL> ret = new ArrayList<URL>();
		try {
			VTD vtd = new VTD(classpathFile);
			int n = vtd.evalToInt("count(" + CLASSPATH_CLASSPATHENTRY
					+ "[(@kind='var') or (@kind='lib')])");
			for (int i = 0; i < n; i++) {
				String attribute = vtd.getAttribute(CLASSPATH_CLASSPATHENTRY
						+ "[(@kind='var') or (@kind='lib')][" + (i + 1) + "]", "path");
				String mavenRepo = System.getProperty("user.home").replaceAll("\\Q\\\\E", "/")
						+ "/.m2/repository/";
				if (!new File(mavenRepo).exists()) {
					throw new RuntimeException(
							"Cannot work with a different M2_REPO than default");
				}
				attribute = attribute.replaceAll("\\QM2_REPO\\E", mavenRepo);
				ret.add(new File(attribute).toURI().toURL());
			}
			n = vtd.evalToInt("count(" + CLASSPATH_CLASSPATHENTRY
					+ "[@kind='src'])");
			for (int i = 0; i < n; i++) {
				String attribute = vtd.getAttribute(CLASSPATH_CLASSPATHENTRY
						+ "[@kind='src'][" + (i + 1) + "]", "path");
				if (attribute.startsWith("/")) {
					// remove the '/' from the beginning
					attribute = attribute.substring(attribute.lastIndexOf('/'));
					File linkedProject = new File(pluginDir.getParentFile(),
							attribute);
					if (!linkedProject.exists()) {
						throw new RuntimeException(
								"Cannot find project related to "
										+ pluginDir.getAbsolutePath()
										+ ": "
										+ attribute
										+ ". They have to be under the same directory.");
					}
					URL[] jars = getJars(linkedProject);
					for (URL url : jars) {
						ret.add(url);
					}
				}
			}

			return ret.toArray(new URL[0]);
		} catch (EncodingException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ pluginDir.getAbsolutePath(), e);
		} catch (EOFException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ pluginDir.getAbsolutePath(), e);
		} catch (EntityException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ pluginDir.getAbsolutePath(), e);
		} catch (ParseException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ pluginDir.getAbsolutePath(), e);
		} catch (IOException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ pluginDir.getAbsolutePath(), e);
		} catch (XPathParseException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ pluginDir.getAbsolutePath(), e);
		} catch (XPathEvalException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ pluginDir.getAbsolutePath(), e);
		} catch (NavException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ pluginDir.getAbsolutePath(), e);
		}
	}

	public File[] getOutputFolders(File projectDir) {
		File classpathFile = new File(projectDir, ".classpath");
		ArrayList<File> ret = new ArrayList<File>();
		try {
			VTD vtd = new VTD(classpathFile);
			int n = vtd.evalToInt("count(" + CLASSPATH_CLASSPATHENTRY
					+ "[@kind='output'])");
			for (int i = 0; i < n; i++) {
				String attribute = vtd.getAttribute(CLASSPATH_CLASSPATHENTRY
						+ "[@kind='output'][" + (i + 1) + "]", "path");
				ret.add(new File(projectDir, attribute));
			}

			n = vtd.evalToInt("count(" + CLASSPATH_CLASSPATHENTRY
					+ "[@kind='src'])");
			for (int i = 0; i < n; i++) {
				String attribute = vtd.getAttribute(CLASSPATH_CLASSPATHENTRY
						+ "[@kind='src'][" + (i + 1) + "]", "path");
				if (attribute.startsWith("/")) {
					// remove the '/' from the beginning
					attribute = attribute.substring(attribute.lastIndexOf('/'));
					File linkedProject = new File(projectDir.getParentFile(),
							attribute);
					if (!linkedProject.exists()) {
						throw new RuntimeException(
								"Cannot find project related to "
										+ projectDir.getAbsolutePath()
										+ ": "
										+ attribute
										+ ". They have to be under the same directory.");
					}
					File[] outputDirs = getOutputFolders(linkedProject);
					for (File file : outputDirs) {
						ret.add(file);
					}
				}
			}

			return ret.toArray(new File[0]);
		} catch (EncodingException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ projectDir.getAbsolutePath(), e);
		} catch (EOFException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ projectDir.getAbsolutePath(), e);
		} catch (EntityException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ projectDir.getAbsolutePath(), e);
		} catch (ParseException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ projectDir.getAbsolutePath(), e);
		} catch (IOException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ projectDir.getAbsolutePath(), e);
		} catch (XPathParseException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ projectDir.getAbsolutePath(), e);
		} catch (XPathEvalException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ projectDir.getAbsolutePath(), e);
		} catch (NavException e) {
			throw new RuntimeException(
					"Cannot understand plugin of type 'eclipse':"
							+ projectDir.getAbsolutePath(), e);
		}
	}
}
