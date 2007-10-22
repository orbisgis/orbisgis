package org.orbisgis.pluginManager;

import java.io.DataInputStream;
import java.io.File;
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

public class CommonClassLoader extends SecureClassLoader {

	private ArrayList<File> outputFolders = new ArrayList<File>();

	private URLClassLoader jarsClassLoader;

	private ArrayList<URL> jars = new ArrayList<URL>();

	public CommonClassLoader() {
		super();
	}

	public void addJars(URL[] jars) {
		for (int i = 0; i < jars.length; i++) {
			this.jars.add(jars[i]);
		}
	}

	public void addOutputFolders(File[] outputFolders) {
		for (int i = 0; i < outputFolders.length; i++) {
			this.outputFolders.add(outputFolders[i]);
		}
	}

	public void finished() {
		this.jarsClassLoader = new URLClassLoader(jars.toArray(new URL[0]));
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
				String classFileName = outputFolders.get(i).getAbsolutePath() + "/"
						+ name.replace('.', '/') + ".class";
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
			resource = jarsClassLoader.getResource(name);
		}
		if (resource == null) {
			// Look in the classes directory
			for (int i = 0; i < outputFolders.size(); i++) {
				String resourceName = outputFolders.get(i).getAbsolutePath() + "/"
						+ name;
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
