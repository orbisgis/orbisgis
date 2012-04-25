/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
/**
 *
 */
package org.gdms;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.driverManager.DriverManager;
import org.orbisgis.utils.FileUtils;

public class FileTestSource extends TestSource {

	private String fileName;
	private File originalFile;

	public FileTestSource(String name, String file) {
		super(name);
		this.fileName = new File(file).getName();
		this.originalFile = new File(file);
	}

        @Override
	public void backup() throws Exception {
		File backupFile = getBackupFile();
		String prefix = originalFile.getAbsolutePath();
		prefix = prefix.substring(0, prefix.length() - 4);
		copyGroup(new File(prefix), getDestDirectory());

		FileSourceDefinition def = new FileSourceDefinition(backupFile, DriverManager.DEFAULT_SINGLE_TABLE_NAME);
		TestBase.dsf.getSourceManager().register(name, def);
	}

	public File getBackupFile() {
		File dest = getDestDirectory();
		dest.mkdirs();
		return new File(dest, fileName);
	}

	private File getDestDirectory() {
		return new File(SourceTest.backupDir.getAbsolutePath() + "../"
				+ name);
	}

	public void copyGroup(final File prefix, File dir) throws IOException {
		File[] dbFiles = prefix.getParentFile().listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(prefix.getName());
			}
		});

		if (dbFiles == null) {
			throw new RuntimeException("Copying group " + prefix + " to " + dir);
		}

		for (int i = 0; i < dbFiles.length; i++) {
			FileUtils.copy(dbFiles[i],
					new File(dir, dbFiles[i].getName()));
		}
	}

}