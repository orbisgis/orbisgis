package org.orbisgis.core.ui.views.geocognition;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.ui.views.editor.TransferableEditableElement;

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
