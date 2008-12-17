package org.orbisgis.views.geocognition;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;

import org.orbisgis.Services;
import org.orbisgis.action.IActionAdapter;
import org.orbisgis.action.IActionFactory;
import org.orbisgis.action.ISelectableActionAdapter;
import org.orbisgis.action.MenuTree;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.ui.resourceTree.ResourceTree;
import org.orbisgis.ui.resourceTree.ContextualActionExtensionPointHelper;
import org.orbisgis.views.geocognition.action.IGeocognitionAction;
import org.orbisgis.views.geocognition.action.IGeocognitionGroupAction;
import org.orbisgis.views.geocognition.filter.IGeocognitionFilter;
import org.orbisgis.views.geocognition.wizard.EPGeocognitionWizardHelper;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.views.geocognition.wizard.NewGeocognitionObject;

public class GeocognitionTree extends ResourceTree {

	private GeocognitionTreeModel treeModel;
	private GeocognitionRenderer geocognitionRenderer;

	public GeocognitionTree() {
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
		MenuTree menuTree = new MenuTree();
		GeocognitionActionFactory factory = new GeocognitionActionFactory();
		ContextualActionExtensionPointHelper.createPopup(menuTree, factory,
				"org.orbisgis.views.geocognition.Action");
		EPGeocognitionWizardHelper wh = new EPGeocognitionWizardHelper();
		wh.addWizardMenus(menuTree, new GeocognitionWizardActionFactory(),
				"org.orbisgis.views.geocognition.New");
		menuTree.removeEmptyMenus();
		JPopupMenu popup = new JPopupMenu();
		JComponent[] menus = menuTree.getJMenus();
		for (JComponent menu : menus) {
			popup.add(menu);
		}

		return popup;
	}

	private class GeocognitionWizardActionFactory implements IActionFactory {

		private class GeocognitionWizardActionDecorator implements
				IActionAdapter {

			private String wizardId;

			public GeocognitionWizardActionDecorator(String wizardId) {
				this.wizardId = wizardId;
			}

			public void actionPerformed() {
				TreePath[] parents = GeocognitionTree.this.getSelection();
				EPGeocognitionWizardHelper wh = new EPGeocognitionWizardHelper();
				NewGeocognitionObject[] objs = wh.runWizard(wizardId);

				if (objs != null) {
					if (parents.length == 0) {
						wh.addElements(objs, "");
					} else {
						GeocognitionElement parent = (GeocognitionElement) parents[0]
								.getLastPathComponent();
						String parentPath = parent.getIdPath();
						wh.addElements(objs, parentPath);
					}
				}
			}

			public boolean isEnabled() {
				return true;
			}

			public boolean isVisible() {
				GeocognitionElement[] elements = toElementArray(getSelection());
				if (elements.length == 0) {
					return true;
				} else {
					return (elements.length == 1) && (elements[0].isFolder());
				}
			}

		}

		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			return new GeocognitionWizardActionDecorator((String) action);
		}

		public ISelectableActionAdapter getSelectableAction(Object action,
				HashMap<String, String> attributes) {
			throw new RuntimeException(
					"Bug! No selection in geocognition wizards");
		}

	}

	private GeocognitionElement[] toElementArray(TreePath[] selectedResources) {
		GeocognitionElement[] elements = new GeocognitionElement[selectedResources.length];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = (GeocognitionElement) selectedResources[i]
					.getLastPathComponent();
		}
		return elements;
	}

	private class GeocognitionActionFactory implements IActionFactory {

		private class GeocognitionActionDecorator implements IActionAdapter {

			private IGeocognitionAction action;

			public GeocognitionActionDecorator(IGeocognitionAction action) {
				this.action = action;
			}

			public void actionPerformed() {
				Geocognition geocog = Services.getService(Geocognition.class);
				GeocognitionElement[] elements = toElementArray(getSelection());
				if (elements.length == 0) {
					action.execute(geocog, null);
				} else {
					for (GeocognitionElement element : elements) {
						action.execute(geocog, element);
					}
				}
			}

			public boolean isEnabled() {
				return true;
			}

			public boolean isVisible() {
				Geocognition geocog = Services.getService(Geocognition.class);
				TreePath[] res = getSelection();
				GeocognitionElement[] elements = toElementArray(res);
				boolean allAccepted = true;
				if (!action.acceptsSelectionCount(geocog, elements.length)) {
					allAccepted = false;
				} else {
					for (GeocognitionElement selectedElement : elements) {
						if (!action.accepts(geocog, selectedElement)) {
							allAccepted = false;
							break;
						}
					}
				}

				return allAccepted;
			}

		}

		private class GeocognitionGroupActionDecorator implements
				IActionAdapter {

			private IGeocognitionGroupAction action;

			public GeocognitionGroupActionDecorator(
					IGeocognitionGroupAction action) {
				this.action = action;
			}

			public void actionPerformed() {
				Geocognition geocog = Services.getService(Geocognition.class);
				GeocognitionElement[] elements = toElementArray(getSelection());
				action.execute(geocog, elements);
			}

			public boolean isEnabled() {
				return true;
			}

			public boolean isVisible() {
				Geocognition geocog = Services.getService(Geocognition.class);
				TreePath[] res = getSelection();
				GeocognitionElement[] elements = toElementArray(res);
				return action.accepts(geocog, elements);
			}

		}

		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			if (action instanceof IGeocognitionAction) {
				return new GeocognitionActionDecorator(
						(IGeocognitionAction) action);
			} else {
				return new GeocognitionGroupActionDecorator(
						(IGeocognitionGroupAction) action);
			}
		}

		public ISelectableActionAdapter getSelectableAction(Object action,
				HashMap<String, String> attributes) {
			return null;
		}

	}

	public void filter(String text, ArrayList<IGeocognitionFilter> filters) {
		treeModel.filter(text, filters);
	}

	public void setRenderers(ElementRenderer[] renderers) {
		geocognitionRenderer.setRenderers(renderers);
	}

}
