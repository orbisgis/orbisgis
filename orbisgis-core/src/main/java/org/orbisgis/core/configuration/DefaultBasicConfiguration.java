package org.orbisgis.core.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.Workspace;

public class DefaultBasicConfiguration implements BasicConfiguration {
	private static final String FILE = "org.orbisgis.core.configuration.properties";

	private Properties properties = new Properties();
	private File propertiesFile;

	@Override
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	@Override
	public void load() {
		Workspace workspace = Services.getService(Workspace.class);
		propertiesFile = workspace.getFile(FILE);
		if (propertiesFile.exists()) {
			try {
				properties.load(new FileInputStream(propertiesFile));
			} catch (FileNotFoundException e) {
				Services.getErrorManager().error("bug!", e);
			} catch (IOException e) {
				Services.getErrorManager().error(
						"The configuration preferences cannot be loaded", e);
			}
		}
	}

	@Override
	public void save() {
		try {
			properties.store(new FileOutputStream(propertiesFile), "");
		} catch (FileNotFoundException e) {
			Services.getErrorManager().error("bug!", e);
		} catch (IOException e) {
			Services.getErrorManager().error(
					"The configuration preferences cannot be stored", e);
		}
	}

	@Override
	public String setProperty(String key, String value) {
		Object ret = properties.setProperty(key, value);
		return ret == null ? null : ret.toString();
	}

	@Override
	public String removeProperty(String key) {
		Object ret = properties.remove(key);
		return ret == null ? null : ret.toString();
	}
}
