package org.orbisgis.core.resourceTree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

public class TransferableResource implements Transferable {

	private final String MIME = DataFlavor.javaJVMLocalObjectMimeType
			+ ";name=MyNode";

	public static DataFlavor myNodeFlavor = null;

	private IResource[] nodes = null;

	private String sourceExtensionPoint;

	public TransferableResource(String sourceExtensionPoint, IResource[] node) {
		this.sourceExtensionPoint = sourceExtensionPoint;
		try {
			myNodeFlavor = new DataFlavor(MIME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Delete the nodes contained by other nodes

		ArrayList<IResource> nodes = new ArrayList<IResource>();
		for (int i = 0; i < node.length; i++) {
			if (!contains(nodes, node[i])) {
				removeContained(nodes, node[i]);
				nodes.add(node[i]);
			}
		}
		this.nodes = nodes.toArray(new IResource[0]);
	}

	private boolean contains(ArrayList<IResource> nodes, IResource resource) {
		for (int i = 0; i < nodes.size(); i++) {
			ArrayList<IResource> subtree = nodes.get(i).depthChildList();
			for (IResource descendant : subtree) {
				if (descendant == resource) {
					return true;
				}
			}
		}

		return false;
	}

	private void removeContained(ArrayList<IResource> nodes, IResource resource) {
		for (int i = 0; i < nodes.size(); i++) {
			if (resource == nodes.get(i)) {
				nodes.remove(i);
				i--;
			} else {
				IResource[] children = resource.getChildren();
				for (IResource child : children) {
					removeContained(nodes, child);
				}
			}
		}
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		Object ret = null;
		if (flavor.equals(myNodeFlavor)) {
			ret = new Data(nodes, sourceExtensionPoint);
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

	class Data {
		public IResource[] resources;
		public String sourceExtensionPoint;

		public Data(IResource[] resources, String sourceExtensionPoint) {
			super();
			this.resources = resources;
			this.sourceExtensionPoint = sourceExtensionPoint;
		}
	}

}
