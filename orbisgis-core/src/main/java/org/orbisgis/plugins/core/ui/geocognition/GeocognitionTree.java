package org.orbisgis.plugins.core.ui.geocognition;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.geocognition.Geocognition;
import org.orbisgis.plugins.core.geocognition.GeocognitionElement;
import org.orbisgis.plugins.core.ui.components.resourceTree.ResourceTree;
import org.orbisgis.plugins.core.ui.views.geocognition.filters.IGeocognitionFilter;
import org.orbisgis.plugins.core.ui.views.geocognition.wizard.ElementRenderer;

public class GeocognitionTree extends ResourceTree {

	private GeocognitionTreeModel treeModel;
	private GeocognitionRenderer geocognitionRenderer;

	private org.orbisgis.plugins.core.ui.menu.MenuTree menuTree;

	public org.orbisgis.plugins.core.ui.menu.MenuTree getMenuTree() {
		return menuTree;
	}

	public GeocognitionTree() {
		menuTree = new org.orbisgis.plugins.core.ui.menu.MenuTree();
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
		// MenuTree menuTree = new MenuTree();
		/*
		 * GeocognitionActionFactory factory = new GeocognitionActionFactory();
		 * ContextualActionExtensionPointHelper.createPopup(menuTree, factory,
		 * "org.orbisgis.core.ui.views.geocognition.Action");
		 * EPGeocognitionWizardHelper wh = new EPGeocognitionWizardHelper();
		 * wh.addWizardMenus(menuTree, new GeocognitionWizardActionFactory(),
		 * "org.orbisgis.core.ui.views.geocognition.New");
		 * menuTree.removeEmptyMenus();
		 */
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
