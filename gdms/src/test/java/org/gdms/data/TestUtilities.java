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
package org.gdms.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class TestUtilities {
	/**
	 * DOCUMENT ME!
	 *
	 * @param zipFile
	 *            DOCUMENT ME!
	 *
	 * @throws IOException
	 */
	public static void unzip(File zipFile) throws IOException {
		int BUFFER = 10240;
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(zipFile);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;

		while ((entry = zis.getNextEntry()) != null) {
			int count;
			byte[] data = new byte[BUFFER];

			// write the files to the disk
			FileOutputStream fos = new FileOutputStream(entry.getName());
			dest = new BufferedOutputStream(fos, BUFFER);

			while ((count = zis.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, count);
			}

			dest.flush();
			dest.close();
		}

		zis.close();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param str
	 *            DOCUMENT ME!
	 * @param f
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public static boolean equals(String str, String f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		byte[] correcto = new byte[str.getBytes().length];
		fis.read(correcto);
		fis.close();

		return str.equals(new String(correcto));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param str
	 *            DOCUMENT ME!
	 * @param f
	 *            DOCUMENT ME!
	 *
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public static void writeTestResult(String str, String f) throws IOException {
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(str.getBytes());
		fos.close();
	}

	public static void printFreeMemory() {
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		// System.out.println("free memory: " + freeMemory / 1024);
		// System.out.println("allocated memory: " + allocatedMemory / 1024);
		// System.out.println("max memory: " + maxMemory / 1024);
		System.out.println("total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024);
	}

}
