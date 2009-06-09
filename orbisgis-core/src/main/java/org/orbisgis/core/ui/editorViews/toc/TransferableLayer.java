/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.editorViews.toc;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.ui.views.editor.TransferableEditableElement;

public class TransferableLayer implements Transferable {

	private static DataFlavor layerFlavor = new DataFlavor(ILayer.class,
			"Resource");

	private ILayer[] nodes = null;
	private EditableElement element;

	public TransferableLayer(EditableElement element, ILayer[] node) {
		this.element = element;
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
		} else if (flavor
				.equals(TransferableEditableElement.editableElementFlavor)) {
			EditableElement[] elems = new EditableElement[nodes.length];
			for (int i = 0; i < nodes.length; i++) {
				elems[i] = new EditableLayer(element, nodes[i]);
			}
			ret = elems;
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
		return (new DataFlavor[] { layerFlavor,
				TransferableEditableElement.editableElementFlavor,
				DataFlavor.stringFlavor });
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(TransferableEditableElement.editableElementFlavor)
				|| flavor.equals(getLayerFlavor())
				|| flavor.equals(DataFlavor.stringFlavor);
	}

	public static DataFlavor getLayerFlavor() {
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
