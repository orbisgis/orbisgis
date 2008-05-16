package org.orbisgis.views.documentCatalog;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.orbisgis.Services;
import org.orbisgis.action.IActionAdapter;
import org.orbisgis.action.IActionFactory;
import org.orbisgis.action.ISelectableActionAdapter;
import org.orbisgis.action.MenuTree;
import org.orbisgis.editor.EditorListener;
import org.orbisgis.editor.IEditor;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.ui.resourceTree.ResourceTree;
import org.orbisgis.ui.resourceTree.ResourceTreeActionExtensionPointHelper;
import org.orbisgis.view.IEditorsView;
import org.orbisgis.view.ViewManager;

public class DocumentCatalog extends ResourceTree {

	private IDocument root;
	private DocumentTreeModel model;

	public DocumentCatalog(IDocument root) {
		setRootDocument(root);
		DocumentRenderer renderer = new DocumentRenderer();
		this.setTreeCellRenderer(renderer);
		this.setTreeCellEditor(new DocumentEditor(getTree()));

		ViewManager vm = (ViewManager) Services
				.getService("org.orbisgis.ViewManager");
		IEditorsView editorsView = vm.getEditorsView();
		editorsView.addEditorListener(new EditorListener() {

			public void activeEditorClosed(IEditor editor) {
				try {
					editor.getDocument().closeDocument(
							new NullProgressMonitor());
				} catch (DocumentException e) {
					Services.getErrorManager().error(
							"Document was not properly closed", e);
				}
			}

			public void activeEditorChanged(IEditor previous, IEditor current) {
			}

		});
	}

	public void setRootDocument(IDocument root) {
		this.root = root;
		model = new DocumentTreeModel(this.getTree(), root);
		this.setModel(model);
	}

	@Override
	protected boolean doDrop(Transferable trans, Object node) {
		return false;
	}

	@Override
	protected Transferable getDragData(DragGestureEvent dge) {
		return null;
	}

	@Override
	public JPopupMenu getPopup() {
		MenuTree menuTree = new MenuTree();
		DocumentActionFactory factory = new DocumentActionFactory();
		ResourceTreeActionExtensionPointHelper.createPopup(menuTree, factory,
				this, "org.orbisgis.views.documentCatalog.Action");
		EPDocumentWizardHelper wh = new EPDocumentWizardHelper();
		wh.addWizardMenus(menuTree, new DocumentWizardActionFactory(),
				"org.orbisgis.views.documentCatalog.New");
		menuTree.removeEmptyMenus();
		JPopupMenu popup = new JPopupMenu();
		JComponent[] menus = menuTree.getJMenus();
		for (JComponent menu : menus) {
			popup.add(menu);
		}

		return popup;
	}

	private class DocumentWizardActionFactory implements IActionFactory {

		private class DocumentWizardActionDecorator implements IActionAdapter {

			private String wizardId;

			public DocumentWizardActionDecorator(String wizardId) {
				this.wizardId = wizardId;
			}

			public void actionPerformed() {
				TreePath[] parents = DocumentCatalog.this.getSelection();
				EPDocumentWizardHelper wh = new EPDocumentWizardHelper();
				IDocument[] docs = wh.runWizard(wizardId);
				if (docs != null) {
					if (parents.length == 0) {
						for (IDocument document : docs) {
							root.addDocument(document);
							model.refresh();
						}
					} else {
						IDocument parent = (IDocument) parents[0]
								.getLastPathComponent();
						for (IDocument document : docs) {
							parent.addDocument(document);
						}
					}
				}
			}

			public boolean isEnabled() {
				return true;
			}

			public boolean isVisible() {
				IDocument[] docs = toDocumentArray(getSelection());
				boolean allTrue = true;
				for (IDocument document : docs) {
					if (!document.allowsChildren()) {
						allTrue = false;
						break;
					}
				}
				return allTrue;
			}

		}

		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			return new DocumentWizardActionDecorator((String) action);
		}

		public ISelectableActionAdapter getSelectableAction(Object action,
				HashMap<String, String> attributes) {
			throw new RuntimeException("Bug! No selection in document wizards");
		}

	}

	private IDocument[] toDocumentArray(TreePath[] selectedResources) {
		IDocument[] documents = new IDocument[selectedResources.length];
		for (int i = 0; i < documents.length; i++) {
			documents[i] = (IDocument) selectedResources[i]
					.getLastPathComponent();
		}
		return documents;
	}

	private class DocumentActionFactory implements IActionFactory {

		private class DocumentActionDecorator implements IActionAdapter {

			private IDocumentAction action;

			public DocumentActionDecorator(IDocumentAction action) {
				this.action = action;
			}

			public void actionPerformed() {
				IDocument[] documents = toDocumentArray(getSelection());
				if (documents.length == 0) {
					action.execute(DocumentCatalog.this, null);
				} else {
					for (IDocument document : documents) {
						action.execute(DocumentCatalog.this, document);
					}
				}
			}

			public boolean isEnabled() {
				return true;
			}

			public boolean isVisible() {
				TreePath[] res = getSelection();
				IDocument[] documents = toDocumentArray(res);
				boolean allAccepted = true;
				if (!action.acceptsSelectionCount(DocumentCatalog.this,
						documents.length)) {
					allAccepted = false;
				} else {
					for (IDocument selectedDocument : documents) {
						if (!action.accepts(DocumentCatalog.this,
								selectedDocument)) {
							allAccepted = false;
							break;
						}
					}
				}

				return allAccepted;
			}

		}

		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			return new DocumentActionDecorator((IDocumentAction) action);
		}

		public ISelectableActionAdapter getSelectableAction(Object action,
				HashMap<String, String> attributes) {
			return null;
		}

	}

	public IDocument getDocumentRoot() {
		return root;
	}

	public void refresh() {
		model.refresh();
	}

	public void removeDocument(IDocument document) {
		IDocument parent = findParent(root, document);
		parent.removeDocument(document);
		model.refresh();
	}

	private IDocument findParent(IDocument parent, IDocument document) {
		if (contains(parent, document)) {
			return parent;
		} else {
			for (int i = 0; i < parent.getDocumentCount(); i++) {
				IDocument found = findParent(parent.getDocument(i), document);
				if (found != null) {
					return found;
				}
			}
		}

		return null;
	}

	private boolean contains(IDocument parent, IDocument document) {
		for (int i = 0; i < parent.getDocumentCount(); i++) {
			if (parent.getDocument(i).equals(document)) {
				return true;
			}
		}

		return false;
	}

}
