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
package org.orbisgis.plugin.sqlconsole.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * A simple file filter implementation that allow to specify one or more file
 * extensions
 *
 * @author wolf
 */
public class SimpleFileFilter extends FileFilter implements java.io.FileFilter {
    String[] extensions;
    String description;

    public SimpleFileFilter(String extension, String description) {
        this.extensions = new String[] { extension };
        this.description = description;
    }

    public SimpleFileFilter(String[] extensions, String description) {
        this.extensions = extensions;
        this.description = description;
    }

    /**
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = FileUtility.getFileExtension(f);

        for (int i = 0; i < extensions.length; i++) {
            if (extension.equals(extensions[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return description;
    }
}
