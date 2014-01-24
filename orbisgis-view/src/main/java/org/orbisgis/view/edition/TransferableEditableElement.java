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
package org.orbisgis.view.edition;

import org.orbisgis.viewapi.edition.EditableElement;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * For OS Drag&Drop.
 * Transfer of multiple editable elements
 */
public class TransferableEditableElement implements Transferable {

	public static final DataFlavor editableElementFlavor = new DataFlavor(
			EditableElement.class, "Editable element");

	private EditableElement[] elements;

	public TransferableEditableElement(EditableElement[] elements) {
		this.elements = elements;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		Object ret = null;
		if (flavor.equals(editableElementFlavor)) {
			ret = elements;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			StringBuilder buff = new StringBuilder();
			for (int elid = 0;elid<elements.length;elid++) {
                                if(elid>0) {
                                    buff.append(", ");
                                }
                                buff.append(elements[elid].getId());
			}
			ret = buff.toString();
		}
		return ret;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return (new DataFlavor[] { editableElementFlavor,
				DataFlavor.stringFlavor });
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.equals(editableElementFlavor) || flavor
				.equals(DataFlavor.stringFlavor));
	}
}
