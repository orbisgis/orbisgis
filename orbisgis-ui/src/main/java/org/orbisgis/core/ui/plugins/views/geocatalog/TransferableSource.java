/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.views.geocatalog;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.ui.plugins.views.editor.TransferableEditableElement;

public class TransferableSource implements Transferable {

	private static DataFlavor sourceFlavor = new DataFlavor(String[].class,
			"Source");

	private String[] sources = null;

	public TransferableSource(String[] sources) {
		this.sources = sources;
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		Object ret = null;
		if (flavor.equals(sourceFlavor)) {
			ret = sources;
		} else if (flavor
				.equals(TransferableEditableElement.editableElementFlavor)) {
			EditableElement[] elems = new EditableElement[sources.length];
			for (int i = 0; i < sources.length; i++) {
				elems[i] = new EditableSource(sources[i]);
			}
			ret = elems;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			String retString = "";
			String separator = "";
			for (String node : sources) {
				retString = retString + separator + node;
				separator = ", ";
			}
			ret = retString;
		}

		return ret;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return (new DataFlavor[] { sourceFlavor,
				TransferableEditableElement.editableElementFlavor,
				DataFlavor.stringFlavor });
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor
				.equals(TransferableEditableElement.editableElementFlavor)
				|| flavor.equals(getResourceFlavor()) || flavor
				.equals(DataFlavor.stringFlavor));
	}

	public static DataFlavor getResourceFlavor() {
		return sourceFlavor;
	}
}
