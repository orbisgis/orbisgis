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
package org.orbisgis.view.toc;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.view.edition.TransferableEditableElement;
import org.orbisgis.viewapi.edition.EditableElement;

/**
 * Transfer of a collection of ILayer.
 */
public class TransferableLayer implements Transferable {

	public final static DataFlavor LAYER_FLAVOR = new DataFlavor(EditableLayer.class,
			"EditableLayer");

	private EditableLayer[] nodes;
	private EditableElement element;

	public TransferableLayer(MapElement element, List<EditableLayer> nodeLst) {
		this.element = element;
                //Transfer only parents elements,
                //childs will be automatically moved with their parents
		ArrayList<EditableLayer> nodesList = new ArrayList<EditableLayer>();
		for (EditableLayer editableNode : nodeLst) {
                        ILayer node = editableNode.getLayer();
			if (!contains(nodesList, node)) {
				removeContained(nodesList, node);
				nodesList.add(editableNode);
			}
		}
		this.nodes = nodesList.toArray(new EditableLayer[nodesList.size()]);
	}

        @Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		Object ret = null;
		if (flavor.equals(LAYER_FLAVOR)) {
			ret = nodes;
		} else if (flavor
				.equals(TransferableEditableElement.editableElementFlavor)) {
                    ret = nodes;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
                        StringBuilder retString = new StringBuilder();
			for (EditableLayer node : nodes) {
                                if(retString.length()!=0) {
                                    retString.append(", ");
                                }
				retString.append(node.getLayer().getName());
			}
			ret = retString.toString();
		}

		return ret;
	}

        @Override
	public DataFlavor[] getTransferDataFlavors() {
		return (new DataFlavor[] { LAYER_FLAVOR,
				TransferableEditableElement.editableElementFlavor,
				DataFlavor.stringFlavor });
	}

        @Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(TransferableEditableElement.editableElementFlavor)
				|| flavor.equals(getLayerFlavor())
				|| flavor.equals(DataFlavor.stringFlavor);
	}

	public static DataFlavor getLayerFlavor() {
		return LAYER_FLAVOR;
	}

	private boolean contains(ArrayList<EditableLayer> nodes, ILayer resource) {
		for (int i = 0; i < nodes.size(); i++) {
			ILayer[] subtree = nodes.get(i).getLayer().getLayersRecursively();
			for (ILayer descendant : subtree) {
				if (descendant == resource) {
					return true;
				}
			}
		}

		return false;
	}

	private void removeContained(ArrayList<EditableLayer> nodes, ILayer resource) {
		for (int i = 0; i < nodes.size(); i++) {
			if (resource == nodes.get(i).getLayer()) {
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
