package org.orbisgis.pluginManager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class CommonClassLoader extends SecureClassLoader {

	private static Logger logger = Logger.getLogger(CommonClassLoader.class);

	private ArrayList<File> outputFolders = new ArrayList<File>();

	private URLClassLoader jarsClassLoader;

	private TreeSet<URL> jars;

	public CommonClassLoader() {
		super();
		jars = new TreeSet<URL>(new Comparator<URL>() {

			public int compare(URL u1, URL u2) {
				String o1 = u1.toExternalForm();
				String o2 = u2.toExternalForm();
				return compare2Strings(o1, o2);
			}

		});
		File lib = new File("lib");
		File[] jars = lib.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				String name = pathname.getName().toLowerCase();
				return name.endsWith(".jar") || name.endsWith(".zip");
			}

		});

		for (File file : jars) {
			try {
				this.jars.add(file.toURI().toURL());
			} catch (MalformedURLException e) {
				logger
						.error("Cannot add the jar: " + file.getAbsolutePath(),
								e);
			}
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

	public void addJars(URL[] jars) {
		for (int i = 0; i < jars.length; i++) {
			if (!this.jars.contains(jars[i])) {
				this.jars.add(jars[i]);
			}
		}
		this.jarsClassLoader = new URLClassLoader(this.jars.toArray(new URL[0])) {

			@Override
			public URL findResource(String name) {
				URL resource = super.findResource(name);
				if (resource == null) {
					return CommonClassLoader.this.findResource(name);
				} else {
					return resource;
				}
			}

		};
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
		try {
			return super.loadClass(name);
		} catch (ClassNotFoundException e) {
			Class<?> c = getFromJars(name);

			if (c == null) {
				throw new ClassNotFoundException(name);
			} else {
				return c;
			}
		}
	}

	public Class<?> getFromJars(String name) {

		Class<?> c = null;
		// Look in the jars
		try {
			c = jarsClassLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
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
		DataInputStream dis = new DataInputStream(fis);

		byte[] bytes = new byte[(int) fis.getChannel().size()];
		dis.readFully(bytes);

		return bytes;
	}

	@Override
	public URL findResource(String name) {
		URL resource = super.findResource(name);
		if (resource == null) {
			// Look in the classes directory
			for (int i = 0; i < outputFolders.size(); i++) {
				String resourceName = outputFolders.get(i).getAbsolutePath()
						+ "/" + name;
				File f = new File(resourceName);
				if (f.exists()) {
					try {
						resource = f.toURI().toURL();
					} catch (MalformedURLException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		return resource;
	}

	@Override
	protected PermissionCollection getPermissions(CodeSource codesource) {
		PermissionCollection perms = super.getPermissions(codesource);
		perms.add(new AllPermission());

		return perms;
	}
}
