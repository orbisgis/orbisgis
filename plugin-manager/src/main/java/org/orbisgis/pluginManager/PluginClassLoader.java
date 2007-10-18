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

public class PluginClassLoader extends SecureClassLoader {

	private File[] outputFolders;

	private PluginClassLoader[] allPlugins;

	private URLClassLoader jarsClassLoader;

	public PluginClassLoader(URL[] jars, File[] outputFolders) {
		super();

		this.jarsClassLoader = new URLClassLoader(jars);
		this.outputFolders = outputFolders;
	}

	public void setAllPluginsClassLoader(PluginClassLoader[] loader) {
		this.allPlugins = loader;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		try {
			return super.loadClass(name);
		} catch (ClassNotFoundException e) {
			Class<?> c = getFromJars(name);

			if (c == null) {
				// Look in all plugins
				for (PluginClassLoader loader : allPlugins) {
					c = loader.getFromJars(name);
				}
			}

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
		for (int i = 0; i < outputFolders.length; i++) {
			try {
				String classFileName = outputFolders[i].getAbsolutePath() + "/"
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
			for (int i = 0; i < outputFolders.length; i++) {
				String resourceName = outputFolders[i].getAbsolutePath() + "/"
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
