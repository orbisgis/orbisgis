package org.orbisgis.pluginManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class EclipseProjectReader implements PluginClassPathReader {

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

	public PluginClassLoader getClassLoader(File pluginDir) {
		ClassPathEntry[] entries = getJars(pluginDir);
		ClassPathEntry[] output = getBinaryDir(pluginDir);

		URL[] jars = new URL[entries.length];
		for (int i = 0; i < jars.length; i++) {
			try {
				jars[i] = new File(entries[i].path).toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

		File[] dirs = new File[output.length];
		for (int i = 0; i < dirs.length; i++) {
			dirs[i] = new File(output[i].path);
		}

		return new PluginClassLoader(jars, dirs);
	}

	private ClassPathEntry[] getJars(File pluginDir) {
		File classpathFile = new File(pluginDir, ".classpath");
		TreeSet<ClassPathEntry> ret = new TreeSet<ClassPathEntry>(new Comparator<ClassPathEntry>() {

			public int compare(ClassPathEntry o1, ClassPathEntry o2) {
				if (o1.path.equals(o2.path)) {
					return 0;
				} else {
					if (o1.path.length() != o2.path.length()) {
						return o1.path.length() - o2.path.length();
					} else {
						for (int i = 0; i < o1.path.length(); i++) {
							if (o1.path.charAt(i) < o2.path.charAt(i)) {
								return -1;
							} else if (o1.path.charAt(i) > o2.path.charAt(i)) {
								return 1;
							}
						}
					}
				}

				throw new RuntimeException("Should never arrive here");
			}

		});
		try {
			VTD vtd = new VTD(classpathFile);
			int n = vtd
					.evalToInt("count(/classpath/classpathentry[@kind='var'])");
			for (int i = 0; i < n; i++) {
				String attribute = vtd.getAttribute(
						"/classpath/classpathentry[@kind='var'][" + (i + 1)
								+ "]", "path");
				String mavenRepo = System.getProperty("user.home")
						+ "/.m2/repository/";
				if (!new File(mavenRepo).exists()) {
					throw new RuntimeException(
							"Cannot work with a different M2_REPO than default");
				}
				attribute = attribute.replaceAll("\\QM2_REPO\\E", mavenRepo);
				ret.add(new ClassPathEntry(attribute));
			}
			n = vtd.evalToInt("count(/classpath/classpathentry[@kind='src'])");
			for (int i = 0; i < n; i++) {
				String attribute = vtd.getAttribute(
						"/classpath/classpathentry[@kind='src'][" + (i + 1)
								+ "]", "path");
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
					ClassPathEntry[] jars = getJars(linkedProject);
					for (ClassPathEntry classPathEntry : jars) {
						ret.add(classPathEntry);
					}
				}
			}

			return ret.toArray(new ClassPathEntry[0]);
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

	private ClassPathEntry[] getBinaryDir(File projectDir) {
		File classpathFile = new File(projectDir, ".classpath");
		ArrayList<ClassPathEntry> ret = new ArrayList<ClassPathEntry>();
		try {
			VTD vtd = new VTD(classpathFile);
			int n = vtd
					.evalToInt("count(/classpath/classpathentry[@kind='output'])");
			for (int i = 0; i < n; i++) {
				String attribute = vtd.getAttribute(
						"/classpath/classpathentry[@kind='output'][" + (i + 1)
								+ "]", "path");
				ret.add(new ClassPathEntry(new File(projectDir, attribute)
						.getAbsolutePath()));
			}

			n = vtd.evalToInt("count(/classpath/classpathentry[@kind='src'])");
			for (int i = 0; i < n; i++) {
				String attribute = vtd.getAttribute(
						"/classpath/classpathentry[@kind='src'][" + (i + 1)
								+ "]", "path");
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
					ClassPathEntry[] outputDirs = getBinaryDir(linkedProject);
					for (ClassPathEntry classPathEntry : outputDirs) {
						ret.add(classPathEntry);
					}
				}
			}

			return ret.toArray(new ClassPathEntry[0]);
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

	private class ClassPathEntry {
		private String path;

		public ClassPathEntry(String path) {
			this.path = path;
		}
	}

}
