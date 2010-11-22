/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Antoine GOURLAY, Maxence LAURENT
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.gdms.source.util;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.SQLSourceDefinition;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.gdms.GdmsDriver;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.gdms.source.ExtendedSource;
import org.orbisgis.utils.FileUtils;

/**
 * 
 * This class is used to disconnect a source and delete it on disk or database.
 * In the future must be moved to the driver like open, close. TODO : Change to
 * put it in driver classes. As driver.delete().
 * 
 */
public class SourcesCleaner {

	/**
	 * Remove a source and delete its physic storage based on datasource
	 * definition So it can work with all gdms datasources.
	 * 
	 * @param def
	 * @return
	 * @throws DriverException
	 */
	public static void delete(ExtendedSource source)
			throws IllegalStateException {

		DataSourceDefinition def = source.getDef();
		if (def instanceof FileSourceDefinition) {

			File file = ((FileSourceDefinition) def).getFile();

			ReadOnlyDriver driver = def.getDriver();

			if (driver instanceof ShapefileDriver) {
				deleteSHPFiles(file);
			} else if (driver instanceof GdmsDriver) {
				deleteFile(file);
			}

		} else if (def instanceof SQLSourceDefinition) {
			try {
				deleteFile(((SQLSourceDefinition) def).getFile());
			} catch (DriverLoadException e) {
				throw new DriverLoadException("Cannot purge "
						+ source.getName(), e);
			}

		} else if (def instanceof DBTableSourceDefinition) {
			throw new IllegalStateException("Not yet implemented");
		} else {
			throw new IllegalStateException("Not yet implemented");
		}
	}

	/**
	 * A simple method to delete all shape files : dbf, shp, shx, prj.
	 * 
	 * @param fileShp
	 * @throws IOException
	 * @throws DriverException
	 */
	public static void deleteSHPFiles(File fileShp) {

		try {
			File fileShx = FileUtils.getFileWithExtension(fileShp, "shx");
			File fileDbf = FileUtils.getFileWithExtension(fileShp, "dbf");
			File filePrj = FileUtils.getFileWithExtension(fileShp, "prj");

			deleteFile(fileShp);
			deleteFile(fileShx);
			deleteFile(fileDbf);
			deleteFile(filePrj);

		} catch (IOException e) {
			throw new DriverLoadException("Cannot purge "
					+ fileShp.getAbsolutePath(), e);
		}

	}

	/**
	 * Delete a file
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file != null && file.exists()) {
			file.delete();
		}
	}
}
