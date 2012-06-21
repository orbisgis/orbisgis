/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PackageReflection {

	private Node root = new Node("", "");

	public PackageReflection(File[] buildPath) throws LinkageError {
		addURLs(buildPath);
		File sys = new File(System.getProperty("java.home") + File.separator
				+ "lib" + File.separator + "rt.jar");
		addURLs(new File[] { sys });
	}

	private void addURLs(File[] files) throws LinkageError {
		for (int i = 0; i < files.length; i++) {
			try {
				File f = files[i];
				if (f.isFile()) {
					ZipFile zip = new ZipFile(f);
					Enumeration<? extends ZipEntry> entries = zip.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						populate(entry.getName());
					}
				} else {
					ArrayList<File> pending = new ArrayList<File>();
					pending.add(f);
					while (!pending.isEmpty()) {
						File file = pending.remove(0);
						if (file.isFile()) {
							String relativePath = file
									.getAbsolutePath()
									.substring(f.getAbsolutePath().length() + 1);
							populate(relativePath);
						} else if (file.isDirectory()) {
							File[] children = file.listFiles();
							for (File child : children) {
								pending.add(child);
							}
						}
					}
				}
			} catch (IllegalArgumentException iae) {
				throw new LinkageError("malformed class path url:\n "
						+ files[i]);
			} catch (IOException ioe) {
				throw new LinkageError("invalid class path url:\n " + files[i]);
			}
		}
	}

	private void populate(String name) {
		if (name.toLowerCase().endsWith(".class")) {
			String qName = name.replace('/', '.');
			qName = qName.substring(0, qName.length() - 6);
			String[] parts = qName.split("\\Q.\\E");
			Node current = root;
			qName = "";
			String separator = "";
			for (int i = 0; i < parts.length; i++) {
				qName += separator + parts[i];
				separator = ".";
				Node parent = current;
				current = current.getNode(parts[i]);
				if (current == null) {
					String nodeQName = qName;
					for (int j = i; j < parts.length; j++) {
						Node newNode = new Node(parts[j], nodeQName);
						parent.addNode(newNode);
						parent = newNode;
						if (j < parts.length - 1) {
							nodeQName += separator + parts[j + 1];
						}
					}
					break;
				}
			}
		}
	}

	public ArrayList<String> getClasses(String pkg) {
		return getClasses(pkg, false);
	}

	public ArrayList<String> getClasses(String pkg, boolean fullyQualified) {
		ArrayList<String> ret = new ArrayList<String>();
		String[] parts = pkg.split("\\Q.\\E");
		Node current = root;
		for (int i = 0; i < parts.length; i++) {
			current = current.getNode(parts[i]);
		}

		Iterator<String> it = current.getEntries();
		while (it.hasNext()) {
			String elem = it.next();
			if (isValidClass(current, elem)) {
				if (fullyQualified) {
					elem = pkg + "." + elem;
				}
				ret.add(elem);
			}
		}

		return ret;
	}

	private boolean isValidClass(Node currentPackage, String className) {
		try {
			Class<?> clazz = Class.forName(currentPackage.getQName() + "."
					+ className);
			return isValidClass(clazz);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	private boolean isValidClass(Node classNode) {
		try {
			Class<?> clazz = Class.forName(classNode.getQName());
			return isValidClass(clazz);
		} catch (ClassNotFoundException e) {
			return false;
		} catch (NoClassDefFoundError e) {
			return false;
		} catch (UnsatisfiedLinkError e) {
			return false;
		}
	}

	private boolean isValidClass(Class<?> clazz) {
		return !clazz.isAnnotation() && !clazz.isAnonymousClass()
				&& !clazz.isMemberClass()
				&& (clazz.getModifiers() & Modifier.PUBLIC) > 0;
	}

	private class Node {

		private String name;
		private String qName;
		private HashMap<String, Node> nodes = new HashMap<String, Node>();
		private boolean isClass = true;

		private Node(String name, String qName) {
			this.name = name;
			this.qName = qName;
		}

		public String getName() {
			return name;
		}

		public boolean isClass() {
			return isClass;
		}

		public boolean isPackage() {
			return !isClass;
		}

		public void addNode(Node node) {
			isClass = false;
			nodes.put(node.getName(), node);
		}

		public Node getNode(String nodeName) {
			return nodes.get(nodeName);
		}

		public Iterator<String> getEntries() {
			return nodes.keySet().iterator();
		}

		public String getQName() {
			return qName;
		}
	}

	/**
	 * Get the qualified name of all the classes in the node structure which
	 * unqualified name starts with classNameStart
	 * 
	 * @param classNameStart
	 * @return
	 */
	public ArrayList<String> getClassStartingBy(String classNameStart) {
		return getPossibleImports(root, classNameStart);
	}

	private ArrayList<String> getPossibleImports(Node node, String part) {
		ArrayList<String> ret = new ArrayList<String>();
		if (node.isClass()) {
			if (node.getName().toLowerCase().startsWith(part.toLowerCase())) {
				if (isValidClass(node)) {
					ret.add(node.getQName());
				}
			}
		} else {
			Iterator<String> it = node.nodes.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				ret.addAll(getPossibleImports(node.nodes.get(name), part));
			}
			return ret;
		}

		return ret;
	}

	/**
	 * Gets the classes starting with classNameStart in the specified package
	 * 
	 * @param pack
	 * @param classNameStart
	 */
	public String[] getClassNamesStartingBy(String pack, String classNameStart) {
		ArrayList<String> path = new ArrayList<String>();
		if (pack != null) {
			String[] packageComponents = pack.split("\\Q.\\E");
			for (String packageComponent : packageComponents) {
				path.add(packageComponent);
			}
		}

		ArrayList<String> ret = new ArrayList<String>();
		Node n = getNode(root, path);
		HashMap<String, Node> children = n.nodes;
		Iterator<Node> it = children.values().iterator();
		while (it.hasNext()) {
			Node node = it.next();
			if (node.name.toLowerCase()
					.startsWith(classNameStart.toLowerCase())) {
				if (node.isClass) {
					if (isValidClass(n, node.getName())) {
						ret.add(node.name);
					}
				} else {
					ret.add(node.name);
				}
			}
		}

		return ret.toArray(new String[ret.size()]);
	}

	private Node getNode(Node startingNode, ArrayList<String> path) {
		if (path.size() == 0) {
			return startingNode;
		} else {
			HashMap<String, Node> children = startingNode.nodes;
			Iterator<Node> it = children.values().iterator();
			while (it.hasNext()) {
				Node node = it.next();
				if (node.name.equals(path.get(0))) {
					path.remove(0);
					return getNode(node, path);
				}
			}
			return null;
		}
	}
}
