/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ib��ez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package org.orbisgis.geoview.sqlConsole.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class that contains some useful function related to file management
 * 
 * @author wolf
 */
public class FileUtility {
	private FileUtility() {
	}

	public static String getFileExtension(File f) {
		String fileName = f.getName();
		int extensionStart = fileName.lastIndexOf('.');
		String extension = "";

		if (extensionStart >= 0) {
			extension = fileName.substring(extensionStart + 1);
		}

		return extension;
	}

	public static String getFileWithoutExtension(File f) {
		String fileName = f.getAbsolutePath();
		int extensionStart = fileName.lastIndexOf('.');
		if (extensionStart != -1)
			return fileName.substring(0, extensionStart);
		else
			return fileName;
	}

	public static String getJustFileNameWithoutExtension(File f) {
		String fileName = f.getName();
		int extensionStart = fileName.lastIndexOf('.');
		if (extensionStart != -1)
			return fileName.substring(0, extensionStart);
		else
			return fileName;
	}

	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[10240];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

}
