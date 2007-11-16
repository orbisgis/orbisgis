package org.orbisgis.geocatalog.resources;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

public class TransferableResource implements Transferable {

	private static DataFlavor resourceFlavor = new DataFlavor(IResource.class,
			"Resource");

	private IResource[] nodes = null;

	public TransferableResource(IResource[] node) {

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
			IResource[] subtree = nodes.get(i).getResourcesRecursively();
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
				IResource[] children = resource.getResources();
				for (IResource child : children) {
					removeContained(nodes, child);
				}
			}
		}
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		Object ret = null;
		if (flavor.equals(resourceFlavor)) {
			ret = nodes;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			String retString = "";
			String separator = "";
			for (IResource node : nodes) {
				retString = retString + separator + node.getName();
				separator = ", ";
			}
			ret = retString;
		}

		return ret;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return (new DataFlavor[] { resourceFlavor, DataFlavor.stringFlavor });
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.equals(getResourceFlavor()) || flavor
				.equals(DataFlavor.stringFlavor));
	}

	public static DataFlavor getResourceFlavor() {
		return resourceFlavor;
	}
}
