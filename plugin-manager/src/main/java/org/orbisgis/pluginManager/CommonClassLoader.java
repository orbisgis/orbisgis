/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.pluginManager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class CommonClassLoader extends SecureClassLoader {

	private ArrayList<File> outputFolders = new ArrayList<File>();

	private HashMap<String, File> resourcesFile = new HashMap<String, File>();

	private TreeSet<File> jars;

	public CommonClassLoader() throws IOException {
		super();
		jars = new TreeSet<File>(new Comparator<File>() {

			public int compare(File u1, File u2) {
				String o1 = u1.getAbsolutePath();
				String o2 = u2.getAbsolutePath();
				return compare2Strings(o1, o2);
			}
		});
		File lib = new File("lib");
		if (lib.exists()) {
			File[] jars = lib.listFiles(new FileFilter() {

				public boolean accept(File pathname) {
					String name = pathname.getName().toLowerCase();
					return name.endsWith(".jar") || name.endsWith(".zip");
				}

			});

			addJars(jars);
		}
	}

	private int compare2Strings(String o1, String o2) {
		if (o1.equals(o2)) {
			return 0;
		} else {
			if (o1.length() != o2.length()) {
				return o1.length() - o2.length();
			} else {
				for (int i = 0; i < o1.length(); i++) {
					if (o1.charAt(i) < o2.charAt(i)) {
						return -1;
					} else if (o1.charAt(i) > o2.charAt(i)) {
						return 1;
					}
				}
			}
		}
		throw new RuntimeException("Should never arrive here");
	}

	public void addJars(File[] jars) throws IOException {
		for (int i = 0; i < jars.length; i++) {
			if (!this.jars.contains(jars[i])) {
				ZipFile jar = null;
				try {
					jar = new ZipFile(jars[i].getPath());
				} catch (ZipException e) {
					throw new IOException("Cannot open " + jars[i] + ": "
							+ e.getMessage(), e);
				}

				Enumeration<? extends ZipEntry> entries = jar.entries();

				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					String entryName = entry.getName();

					if (entryName.toLowerCase().endsWith(".class")) {
						entryName = entryName.substring(0,
								entryName.length() - 6).replace('/', '.');
						File fileForClass = resourcesFile.get(entryName);
						if (fileForClass != null) {
							throw new RuntimeException(
									"There are two classes/resources with the same name in "
											+ fileForClass.getAbsolutePath()
											+ " and in "
											+ jars[i].getAbsolutePath() + ": "
											+ entryName);
						}
					}

					resourcesFile.put(entryName, jars[i]);
				}
				this.jars.add(jars[i]);
			}
		}
	}

	public void addOutputFolders(File[] outputFolders) {
		for (int i = 0; i < outputFolders.length; i++) {
			if (!this.outputFolders.contains(outputFolders[i])) {
				this.outputFolders.add(outputFolders[i]);
			}
		}
	}

	public void finished() {
		jars = null;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> c = findLoadedClass(name);
		if (c == null) {
			try {
				c = getSystemClassLoader().loadClass(name);
			} catch (ClassNotFoundException e) {
				try {
					c = getFromJars(name);
				} catch (ZipException e1) {
					throw new ClassNotFoundException(name, e1);
				} catch (IOException e1) {
					throw new ClassNotFoundException(name, e1);
				}
			}
		}

		if (c == null) {
			throw new ClassNotFoundException(name);
		} else {
			return c;
		}
	}

	private Class<?> getFromJars(String name) throws ZipException, IOException {

		Class<?> c = null;
		// Look in the jars
		File file = resourcesFile.get(name);
		if (file != null) {
			ZipFile zf = new ZipFile(file);
			ZipEntry entry = zf.getEntry(name.replace('.', '/') + ".class");
			byte[] bytes = loadClassData(zf.getInputStream(entry));
			c = defineClass(name, bytes, 0, bytes.length);
		}

		if (c != null) {
			return c;
		}

		// Look in the classes directory
		for (int i = 0; i < outputFolders.size(); i++) {
			try {
				String classFileName = outputFolders.get(i).getAbsolutePath()
						+ "/" + name.replace('.', '/') + ".class";
				File f = new File(classFileName);
				if (f.exists()) {
					byte[] data = loadClassData(f);
					c = defineClass(name, data, 0, data.length);
					break;
				}
			} catch (IOException e) {
				throw new NoClassDefFoundError("Cannot read .class file:"
						+ e.getMessage());
			}
		}

		return c;
	}

	/**
	 * Gets the bytes of a File
	 *
	 * @param file
	 *            File
	 *
	 * @return bytes of file
	 *
	 * @throws IOException
	 *             If the operation fails
	 */
	private byte[] loadClassData(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		return loadClassData(fis);
	}

	private byte[] loadClassData(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(is);

		byte[] bytes = new byte[is.available()];
		dis.readFully(bytes);

		return bytes;
	}

	@Override
	public URL findResource(String name) {
		// Look in the jars
		File file = resourcesFile.get(name);
		if (file != null) {
			String url = null;
			try {
				url = "jar:file:" + file.getAbsolutePath() + "!/" + name;
				return new URL(url);
			} catch (MalformedURLException e) {
				throw new RuntimeException("loading " + name + " with " + url,
						e);
			}
		}

		// Look in the classes directory
		for (int i = 0; i < outputFolders.size(); i++) {
			String resourceName = outputFolders.get(i).getAbsolutePath() + "/"
					+ name;
			File f = new File(resourceName);
			if (f.exists()) {
				try {
					return f.toURI().toURL();
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return super.findResource(name);
	}

	@Override
	protected PermissionCollection getPermissions(CodeSource codesource) {
		PermissionCollection perms = super.getPermissions(codesource);
		perms.add(new AllPermission());

		return perms;
	}
}
