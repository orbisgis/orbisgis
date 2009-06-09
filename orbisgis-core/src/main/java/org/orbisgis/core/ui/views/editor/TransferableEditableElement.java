package org.orbisgis.core.ui.views.editor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.orbisgis.core.edition.EditableElement;

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
			String retString = "";
			String separator = "";
			for (EditableElement element : elements) {
				retString = retString + separator + element.getId();
				separator = ", ";
			}
			ret = retString;
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
