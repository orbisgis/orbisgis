package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class MyNodeTransferable implements Transferable {

	private final String MIME = DataFlavor.javaJVMLocalObjectMimeType
			+ ";name=MyNode";

	public static DataFlavor myNodeFlavor = null;

	private MyNode node = null;

	public MyNodeTransferable(MyNode node) {
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
		if (myNodeFlavor.equals(flavor)) {
			ret = node;
		} else if (myNodeFlavor.equals(DataFlavor.stringFlavor)) {
			ret = node.toString();
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
