package org.orbisgis.views.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BorderFactory;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;

import org.orbisgis.Services;
import org.orbisgis.editor.EditorDecorator;
import org.orbisgis.editor.IEditor;
import org.orbisgis.views.documentCatalog.DocumentException;
import org.orbisgis.views.documentCatalog.IDocument;

public class EditorPanel extends Container {

	private RootWindow root;
	private HashMap<Component, EditorDecorator> componentEditor = new HashMap<Component, EditorDecorator>();
	private EditorDecorator lastEditor = null;
	private EditorView editorView;

	public EditorPanel(EditorView editorView) {
		this.setLayout(new BorderLayout());
		root = new RootWindow(null);
		root.getRootWindowProperties().getSplitWindowProperties()
				.setContinuousLayoutEnabled(false);
		root.getRootWindowProperties().getTabWindowProperties()
				.getTabProperties().getNormalButtonProperties()
				.getCloseButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties()
				.getTabProperties().getNormalButtonProperties()
				.getUndockButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties()
				.getTabProperties().getNormalButtonProperties()
				.getRestoreButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties()
				.getTabProperties().getNormalButtonProperties()
				.getMinimizeButtonProperties().setVisible(false);
		root.getRootWindowProperties().getWindowAreaProperties().setBorder(
				BorderFactory.createEmptyBorder());
		root.getRootWindowProperties().getTabWindowProperties()
				.getTabProperties().getFocusedProperties()
				.getComponentProperties().setBackgroundColor(
						new Color(100, 140, 190));
		root.getRootWindowProperties().getWindowAreaProperties()
				.setBackgroundColor(new Color(238, 238, 238));

		this.add(root, BorderLayout.CENTER);

		this.editorView = editorView;
	}

	public IDocument getCurrentDocument() {
		if (lastEditor != null) {
			return lastEditor.getDocument();
		} else {
			return null;
		}
	}

	/**
	 * @param doc
	 * @param editorClass
	 * @return True if the specified document is currently being edited by an
	 *         editor of the specified class
	 */
	public boolean isBeingEdited(IDocument doc,
			Class<? extends IEditor> editorClass) {
		return findViewWithEditor(root, doc, editorClass) != null;
	}

	/**
	 * Makes the editor visible
	 *
	 * @param document
	 * @param editorClass
	 */
	public void showEditor(IDocument document,
			Class<? extends IEditor> editorClass) {
		View existingView = findViewWithEditor(root, document, editorClass);
		if (existingView != null) {
			existingView.makeVisible();
			existingView.requestFocus();
		}
	}

	/**
	 * Adds and shows a new editor
	 *
	 * @param editor
	 */
	public void addEditor(EditorDecorator editor) {
		Component comp = editor.getComponent();
		View view = new View(editor.getTitle(), editor.getIcon(), comp);
		view.addListener(new DockingWindowAdapter() {

			private View nextFocus;

			@Override
			public void windowClosing(DockingWindow arg0)
					throws OperationAbortedException {
				// Focus another view
				DockingWindow parent = arg0.getWindowParent();
				HashSet<DockingWindow> visited = new HashSet<DockingWindow>();
				visited.add(arg0);
				nextFocus = getNextFocus(parent, visited);
			}

			public void windowClosed(DockingWindow arg0) {
				if (arg0 instanceof View) {
					View closedView = (View) arg0;
					IEditor closedEditor = componentEditor.remove(closedView
							.getComponent());
					if (nextFocus != null) {
						nextFocus.requestFocus();
						nextFocus.requestFocusInWindow();
						lastEditor = componentEditor.get(nextFocus
								.getComponent());
					} else {
						lastEditor = null;
						editorView.fireActiveEditorChanged(lastEditor, null);
					}

					editorView.fireEditorClosed(closedEditor);
				}
			}

			private View getNextFocus(DockingWindow parent,
					HashSet<DockingWindow> visited) {
				// Find a view at the same level
				for (int i = 0; i < parent.getChildWindowCount(); i++) {
					DockingWindow child = parent.getChildWindow(i);
					if (visited.contains(child)) {
						continue;
					} else {
						visited.add(child);
						// If it's a view return it
						if (child instanceof View) {
							return (View) child;
						} else {
							// Otherwise go deeper
							View ret = getNextFocus(child, visited);
							if (ret != null) {
								return ret;
							}
						}
					}
				}

				// Search in the upper level
				if ((parent.getWindowParent() != null)
						&& (parent.getWindowParent() != parent)) {
					return getNextFocus(parent.getWindowParent(), visited);
				} else {
					return null;
				}
			}

			@Override
			public void viewFocusChanged(View arg0, View arg1) {
				if (arg1 != null) {
					IEditor previous = null;
					if (lastEditor != null) {
						previous = lastEditor.getEditor();
					}
					lastEditor = componentEditor.get(arg1.getComponent());
					editorView.fireActiveEditorChanged(previous, lastEditor
							.getEditor());
				}
			}
		});
		DockingWindowUtil.addNewView(root, view);
		view.requestFocus();
		componentEditor.put(comp, editor);
	}

	private View findViewWithEditor(DockingWindow wnd, IDocument doc,
			Class<? extends IEditor> editorClass) {
		for (int i = 0; i < wnd.getChildWindowCount(); i++) {
			DockingWindow child = wnd.getChildWindow(i);
			if (child instanceof View) {
				Component comp = ((View) child).getComponent();
				EditorDecorator existingEditor = componentEditor.get(comp);
				if ((existingEditor.getDocument() == doc)
						&& (existingEditor.getEditor().getClass() == editorClass)) {
					return (View) child;
				}
			} else {
				View ret = findViewWithEditor(child, doc, editorClass);
				if (ret != null) {
					return ret;
				}
			}
		}

		return null;
	}

	public EditorDecorator getCurrentEditor() {
		return lastEditor;
	}

	public void saveAllDocuments() {
		HashSet<IDocument> done = new HashSet<IDocument>();
		Iterator<EditorDecorator> it = componentEditor.values().iterator();
		while (it.hasNext()) {
			EditorDecorator editor = it.next();
			IDocument document = editor.getDocument();
			try {
				if (!done.contains(document)) {
					document.saveDocument();
					done.add(document);
				}
			} catch (DocumentException e) {
				Services.getErrorManager().error(
						"Cannot save document " + document.getName());
			} catch (Exception e) {
				Services.getErrorManager().error(
						"Bug saving document: " + document.getName());
			}
		}
	}
}
