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
package org.orbisgis.core.ui.plugins.views.geocognition;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.ui.plugins.views.editor.TransferableEditableElement;

public class TransferableGeocognitionElement implements Transferable {

	public static final DataFlavor geocognitionFlavor = new DataFlavor(
			GeocognitionElement.class, "Geocognition element");

	private GeocognitionElement[] elements;

	public TransferableGeocognitionElement(GeocognitionElement[] elements) {
		this.elements = elements;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		Object ret = null;
		if (flavor.equals(geocognitionFlavor)) {
			ret = elements;
		} else if (flavor
				.equals(TransferableEditableElement.editableElementFlavor)) {
			ret = elements;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			String retString = "";
			String separator = "";
			for (GeocognitionElement element : elements) {
				retString = retString + separator + element.getId();
				separator = ", ";
			}
			ret = retString;
		}

		return ret;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return (new DataFlavor[] {
				TransferableEditableElement.editableElementFlavor,
				geocognitionFlavor, DataFlavor.stringFlavor });
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor
				.equals(TransferableEditableElement.editableElementFlavor)
				|| flavor.equals(geocognitionFlavor) || flavor
				.equals(DataFlavor.stringFlavor));
	}

}
