/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
