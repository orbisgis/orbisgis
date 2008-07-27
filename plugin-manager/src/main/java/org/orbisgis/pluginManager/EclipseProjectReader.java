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
package org.orbisgis.pluginManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class EclipseProjectReader implements PluginClassPathReader {

	private HashMap<File, File[]> pluginDirJars = new HashMap<File, File[]>();
	private HashMap<File, File[]> pluginDirOutputFolders = new HashMap<File, File[]>();

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

	public File[] getJars(File pluginDir) {
		File[] files = pluginDirJars.get(pluginDir);
		if (files == null) {
			File classpathFile = new File(pluginDir, ".classpath");
			ArrayList<File> ret = new ArrayList<File>();
			try {
				VTD vtd = new VTD(classpathFile);
				int n = vtd.evalToInt("count(" + CLASSPATH_CLASSPATHENTRY
						+ "[(@kind='var') or (@kind='lib')])");
				for (int i = 0; i < n; i++) {
					String attribute = vtd.getAttribute(
							CLASSPATH_CLASSPATHENTRY
									+ "[(@kind='var') or (@kind='lib')]["
									+ (i + 1) + "]", "path");
					if (attribute.contains("M2_REPO")) {
						String mavenRepo = System.getProperty("user.home")
								.replaceAll("\\Q\\\\E", "/")
								+ "/.m2/repository/";
						if (!new File(mavenRepo).exists()) {
							throw new RuntimeException(
									"Cannot work with a different M2_REPO than default");
						}
						attribute = attribute.replaceAll("\\QM2_REPO\\E",
								mavenRepo);
					}
					ret.add(new File(attribute));
				}
				n = vtd.evalToInt("count(" + CLASSPATH_CLASSPATHENTRY
						+ "[@kind='src'])");
				for (int i = 0; i < n; i++) {
					String attribute = vtd.getAttribute(
							CLASSPATH_CLASSPATHENTRY + "[@kind='src']["
									+ (i + 1) + "]", "path");
					if (attribute.startsWith("/")) {
						// remove the '/' from the beginning
						attribute = attribute.substring(attribute
								.lastIndexOf('/'));
						File linkedProject = new File(
								pluginDir.getParentFile(), attribute);
						if (!linkedProject.exists()) {
							throw new RuntimeException(
									"Cannot find project related to "
											+ pluginDir.getAbsolutePath()
											+ ": "
											+ attribute
											+ ". They have to be under the same directory.");
						}
						File[] jars = getJars(linkedProject);
						for (File url : jars) {
							ret.add(url);
						}
					}
				}

				files = ret.toArray(new File[0]);
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

		pluginDirJars.put(pluginDir, files);
		return files;
	}

	public File[] getOutputFolders(File projectDir) {
		File[] files = pluginDirOutputFolders.get(projectDir);
		if (files == null) {
			File classpathFile = new File(projectDir, ".classpath");
			ArrayList<File> ret = new ArrayList<File>();
			try {
				VTD vtd = new VTD(classpathFile);
				int n = vtd.evalToInt("count(" + CLASSPATH_CLASSPATHENTRY
						+ "[@kind='output'])");
				for (int i = 0; i < n; i++) {
					String attribute = vtd.getAttribute(
							CLASSPATH_CLASSPATHENTRY + "[@kind='output']["
									+ (i + 1) + "]", "path");
					ret.add(new File(projectDir, attribute));
				}

				n = vtd.evalToInt("count(" + CLASSPATH_CLASSPATHENTRY
						+ "[@kind='src'])");
				for (int i = 0; i < n; i++) {
					String attribute = vtd.getAttribute(
							CLASSPATH_CLASSPATHENTRY + "[@kind='src']["
									+ (i + 1) + "]", "path");
					if (attribute.startsWith("/")) {
						// remove the '/' from the beginning
						attribute = attribute.substring(attribute
								.lastIndexOf('/'));
						File linkedProject = new File(projectDir
								.getParentFile(), attribute);
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

				files = ret.toArray(new File[0]);
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

		pluginDirOutputFolders.put(projectDir, files);
		return files;
	}
}
