package org.orbisgis.geocatalog.resources;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableResource implements Transferable {

	private final String MIME = DataFlavor.javaJVMLocalObjectMimeType
			+ ";name=MyNode";

	public static DataFlavor myNodeFlavor = null;

	private IResource node = null;

	public TransferableResource(IResource node) {
		try {
			myNodeFlavor = new DataFlavor(MIME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		this.node = node;
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		Object ret = null;
		if (flavor.equals(myNodeFlavor)) {
			ret = node;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			ret = node.getName();
		}
		return ret;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return (new DataFlavor[] { myNodeFlavor, DataFlavor.stringFlavor });
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.equals(myNodeFlavor) | flavor
				.equals(DataFlavor.stringFlavor));
	}

}
