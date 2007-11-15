package org.orbisgis.geoview.toc;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import org.orbisgis.geoview.layerModel.ILayer;

public class TransferableLayer implements Transferable {

	private final static String MIME = DataFlavor.javaJVMLocalObjectMimeType
			+ ";name=org.orbisgis.ILayer";

	private static DataFlavor layerFlavor;

	private ILayer[] nodes = null;

	public TransferableLayer(ILayer[] node) {
		ArrayList<ILayer> nodes = new ArrayList<ILayer>();
		for (int i = 0; i < node.length; i++) {

			if (!contains(nodes, node[i])) {
				removeContained(nodes, node[i]);
				nodes.add(node[i]);
			}
		}
		this.nodes = nodes.toArray(new ILayer[0]);
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		Object ret = null;
		if (flavor.equals(layerFlavor)) {
			ret = nodes;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			String retString = "";
			String separator = "";
			for (ILayer node : nodes) {
				retString = retString + separator + node.getName();
				separator = ", ";
			}
			ret = retString;
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

	public static DataFlavor getLayerFlavor() {
		if (layerFlavor == null) {
			try {
				layerFlavor = new DataFlavor(MIME);
			} catch (ClassNotFoundException e) {
			}
		}

		return layerFlavor;
	}

	public ILayer[] getNodes() {
		return nodes;
	}

	private boolean contains(ArrayList<ILayer> nodes, ILayer resource) {
		for (int i = 0; i < nodes.size(); i++) {
			ILayer[] subtree = nodes.get(i).getLayersRecursively();
			for (ILayer descendant : subtree) {
				if (descendant == resource) {
					return true;
				}
			}
		}

		return false;
	}

	private void removeContained(ArrayList<ILayer> nodes, ILayer resource) {
		for (int i = 0; i < nodes.size(); i++) {
			if (resource == nodes.get(i)) {
				nodes.remove(i);
				i--;
			} else {
				ILayer[] children = resource.getChildren();
				for (ILayer child : children) {
					removeContained(nodes, child);
				}
			}
		}
	}

}
