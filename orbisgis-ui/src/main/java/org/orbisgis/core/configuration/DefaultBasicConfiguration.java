package org.orbisgis.core.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.orbisgis.core.OrbisGISPersitenceConfig;
import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.utils.I18N;

public class DefaultBasicConfiguration implements BasicConfiguration {
	private Properties properties = new Properties();
	private File propertiesFile;

	@Override
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	@Override
	public void load() {
		Workspace workspace = Services.getService(Workspace.class);
		propertiesFile = workspace.getFile(OrbisGISPersitenceConfig.DEFAULT_BASIC_CONFIGURATION);
		if (propertiesFile.exists()) {
			try {
				properties.load(new FileInputStream(propertiesFile));
			} catch (FileNotFoundException e) {
				Services.getErrorManager().error(I18N.getString("orbisgis.org.orbisgis.defaultBasicConfiguration.bug"), e); //$NON-NLS-1$
			} catch (IOException e) {
				Services.getErrorManager().error(
						I18N.getString("orbisgis.org.orbisgis.defaultBasicConfiguration.cannotLoadConfigurationPreferences"), e); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void save() {
		try {
			properties.store(new FileOutputStream(propertiesFile), ""); //$NON-NLS-1$
		} catch (FileNotFoundException e) {
			Services.getErrorManager().error(I18N.getString("orbisgis.org.orbisgis.defaultBasicConfiguration.bug"), e); //$NON-NLS-1$
		} catch (IOException e) {
			Services.getErrorManager().error(
					I18N.getString("orbisgis.org.orbisgis.defaultBasicConfiguration.cannotSavedConfigurationPreferences"), e); //$NON-NLS-1$
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
