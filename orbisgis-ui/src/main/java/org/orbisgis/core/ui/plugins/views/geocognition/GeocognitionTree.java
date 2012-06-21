/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.views.geocognition;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;

import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.ui.components.resourceTree.ResourceTree;
import org.orbisgis.core.ui.plugins.views.geocognition.filters.IGeocognitionFilter;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.ElementRenderer;

public class GeocognitionTree extends ResourceTree {

	private GeocognitionTreeModel treeModel;
	private GeocognitionRenderer geocognitionRenderer;

	private org.orbisgis.core.ui.pluginSystem.menu.MenuTree menuTree;

	public org.orbisgis.core.ui.pluginSystem.menu.MenuTree getMenuTree() {
		return menuTree;
	}

	public GeocognitionTree() {
		menuTree = new org.orbisgis.core.ui.pluginSystem.menu.MenuTree();
		setGeocognitionModel();
		geocognitionRenderer = new GeocognitionRenderer();
		getTree().setCellRenderer(geocognitionRenderer);
		getTree().setCellEditor(new GeocognitionEditor(getTree()));
		ToolTipManager.sharedInstance().registerComponent(getTree());
	}

	void setGeocognitionModel() {
		treeModel = new GeocognitionTreeModel(getTree());
		this.setModel(treeModel);
	}

	@Override
	protected boolean isDroppable(TreePath path) {
		return true;
	}

	@Override
	protected boolean doDrop(Transferable trans, Object element) {

		// Get the node where we drop
		GeocognitionElement dropElement = (GeocognitionElement) element;

		// By default drop on rootNode
		if (dropElement == null) {
			Geocognition geocognition = Services.getService(Geocognition.class);
			dropElement = geocognition.getRoot();
		}

		/** *** DRAG STUFF **** */
		if (trans
				.isDataFlavorSupported(TransferableGeocognitionElement.geocognitionFlavor)) {
			try {
				GeocognitionElement[] elements = (GeocognitionElement[]) trans
						.getTransferData(TransferableGeocognitionElement.geocognitionFlavor);

				if (isValidDragAndDrop(elements, dropElement)) {
					Geocognition geocognition = Services
							.getService(Geocognition.class);
					for (GeocognitionElement elem : elements) {
						geocognition.move(elem.getIdPath(), dropElement
								.getIdPath());
					}
				} else {
					return false;
				}

			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}

		return true;
	}

	private boolean isValidDragAndDrop(GeocognitionElement[] elements,
			GeocognitionElement dropElement) {
		for (GeocognitionElement node : elements) {
			if (dropElement.getIdPath().indexOf(node.getIdPath()) != -1) {
				// Cannot drop in child
				return false;
			} else if (node.getParent() == dropElement) {
				// Cannot drop in parent
				return false;
			}
		}

		return true;
	}

	@Override
	protected Transferable getDragData(DragGestureEvent dge) {
		GeocognitionElement[] selected = toElementArray(getSelection());
		if (selected.length > 0) {
			return new TransferableGeocognitionElement(selected);
		} else {
			return null;
		}
	}

	@Override
	public JPopupMenu getPopup() {		
		JPopupMenu popup = new JPopupMenu();
		JComponent[] menus = menuTree.getJMenus();
		for (JComponent menu : menus) {
			popup.add(menu);
		}
		return popup;
	}

	public static GeocognitionElement[] toElementArray(
			TreePath[] selectedResources) {
		GeocognitionElement[] elements = new GeocognitionElement[selectedResources.length];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = (GeocognitionElement) selectedResources[i]
					.getLastPathComponent();
		}
		return elements;
	}

	public void filter(String text, ArrayList<IGeocognitionFilter> filters) {
		treeModel.filter(text, filters);
	}

	public void setRenderers(ElementRenderer[] renderers) {
		geocognitionRenderer.setRenderers(renderers);
	}

}
