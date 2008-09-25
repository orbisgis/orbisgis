/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/**
 *
 */
package org.gdms;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.gdms.data.file.FileSourceDefinition;
import org.orbisgis.utils.FileUtils;

public class FileTestSource extends TestSource {

	private String fileName;
	private File originalFile;

	public FileTestSource(String name, String file) {
		super(name);
		this.fileName = new File(file).getName();
		this.originalFile = new File(file);
	}

	public void backup() throws Exception {
		File backupFile = getBackupFile();
		String prefix = originalFile.getAbsolutePath();
		prefix = prefix.substring(0, prefix.length() - 4);
		copyGroup(new File(prefix), getDestDirectory());

		FileSourceDefinition def = new FileSourceDefinition(backupFile);
		SourceTest.dsf.getSourceManager().register(name, def);
	}

	public File getBackupFile() {
		File dest = getDestDirectory();
		dest.mkdirs();
		return new File(dest, fileName);
	}

	private File getDestDirectory() {
		return new File(SourceTest.backupDir.getAbsolutePath() + "/"
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