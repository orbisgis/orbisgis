package org.orbisgis.plugin.view.ui.workbench;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.orbisgis.plugin.view.layerModel.ILayer;

public class LayerTransferable implements Transferable {

	private final String MIME = DataFlavor.javaJVMLocalObjectMimeType
			+ ";name=Layer";

	public static DataFlavor layerFlavor = null;

	private ILayer layer = null;

	public LayerTransferable(ILayer layer) {
		try {
			layerFlavor = new DataFlavor(MIME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		this.layer = layer;
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		Object ret = null;
		if (flavor.equals(layerFlavor)) {
			ret = layer;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			ret = layer.getName();
		}
		return ret;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return (new DataFlavor[] { layerFlavor, DataFlavor.stringFlavor });
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.equals(layerFlavor) | flavor
				.equals(DataFlavor.stringFlavor));
	}

}
