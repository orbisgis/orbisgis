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
package org.orbisgis.views.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BorderFactory;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.gui.panel.SimplePanel;

import org.apache.log4j.Logger;
import org.orbisgis.Services;
import org.orbisgis.editor.EditorDecorator;
import org.orbisgis.editor.IEditor;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.views.documentCatalog.AbstractDocumentListener;
import org.orbisgis.views.documentCatalog.DocumentCatalogListener;
import org.orbisgis.views.documentCatalog.DocumentCatalogManager;
import org.orbisgis.views.documentCatalog.DocumentEvent;
import org.orbisgis.views.documentCatalog.DocumentException;
import org.orbisgis.views.documentCatalog.IDocument;

public class EditorPanel extends Container {

	private static final Logger logger = Logger.getLogger(EditorPanel.class);
	private RootWindow root;
	private ArrayList<EditorInfo> editorsInfo = new ArrayList<EditorInfo>();
	private EditorDecorator lastEditor = null;
	private EditorView editorView;
	private ChangeNameListener changeNameListener = new ChangeNameListener();
	private DocumentRemovalListener removalListener;

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

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				if ((editorsInfo.size() > 0) && (lastEditor == null)) {
					EditorInfo ei = editorsInfo.get(0);
					ei.getView().requestFocus();
					ei.getView().makeVisible();
				}
			}

		});
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

	public EditorInfo getEditorByComponent(Component component) {
		for (EditorInfo editorInfo : editorsInfo) {
			if (editorInfo.getEditorComponent() == component) {
				return editorInfo;
			}
		}

		return null;
	}

	private EditorInfo[] getEditorsByDocument(IDocument document) {
		ArrayList<EditorInfo> ret = new ArrayList<EditorInfo>();
		for (EditorInfo editorInfo : editorsInfo) {
			if (editorInfo.getDocument() == document) {
				ret.add(editorInfo);
			}
		}

		return ret.toArray(new EditorInfo[0]);
	}

	/**
	 * Adds and shows a new editor
	 *
	 * @param editor
	 */
	public void addEditor(EditorDecorator editor) {
		Component comp = editor.getComponent();
		View view = new View(editor.getTitle(), editor.getIcon(), comp);
		view.addListener(new ClosingListener());

		DockingWindowUtil.addNewView(root, view);
		view.requestFocus();
		editorsInfo
				.add(new EditorInfo(view, editor.getDocument(), editor, comp));

		editor.getDocument().addDocumentListener(changeNameListener);

		if (removalListener == null) {
			removalListener = new DocumentRemovalListener();
			DocumentCatalogManager dcm = (DocumentCatalogManager) Services
					.getService("org.orbisgis.DocumentCatalogManager");
			dcm.addDocumentCatalogListener(removalListener);
		}
	}

	private View findViewWithEditor(DockingWindow wnd, IDocument doc,
			Class<? extends IEditor> editorClass) {
		for (int i = 0; i < wnd.getChildWindowCount(); i++) {
			DockingWindow child = wnd.getChildWindow(i);
			if (child instanceof View) {
				Component comp = ((View) child).getComponent();
				EditorDecorator existingEditor = getEditorByComponent(comp)
						.getEditorDecorator();
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
		Iterator<EditorInfo> it = editorsInfo.iterator();
		while (it.hasNext()) {
			EditorInfo editorInfo = it.next();
			IDocument document = editorInfo.getDocument();
			try {
				if (!done.contains(document)) {
					document.saveDocument(new NullProgressMonitor());
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

	private final class ClosingListener extends DockingWindowAdapter {
		private View nextFocus;

		@Override
		public void windowClosing(DockingWindow arg0)
				throws OperationAbortedException {
			if (arg0 instanceof View) {
				View closedView = (View) arg0;
				EditorInfo editorInfo = getEditorByComponent(closedView
						.getComponent());
				IEditor closedEditor = editorInfo.getEditorDecorator();
				try {
					if (!closedEditor.closingEditor()) {
						throw new OperationAbortedException();
					}
				} catch (OperationAbortedException e) {
					throw e;
				} catch (Exception e) {
					logger.error("Problem closing editor", e);
				}
			}

			// Focus another view
			DockingWindow parent = arg0.getWindowParent();
			HashSet<DockingWindow> visited = new HashSet<DockingWindow>();
			visited.add(arg0);
			nextFocus = getNextFocus(parent, visited);
			if (nextFocus == null) {
				// We have undocked windows
				for (EditorInfo editorInfo : editorsInfo) {
					if (editorInfo.view != arg0) {
						nextFocus = editorInfo.view;
						break;
					}
				}
			}
		}

		public void windowClosed(DockingWindow arg0) {
			if (arg0 instanceof View) {
				View closedView = (View) arg0;
				EditorInfo editorInfo = getEditorByComponent(closedView
						.getComponent());
				editorsInfo.remove(editorInfo);
				IEditor closedEditor = editorInfo.getEditorDecorator();

				// Remove document listener
				closedEditor.getDocument().removeDocumentListener(
						changeNameListener);

				// Focus next editor
				if (nextFocus != null) {
					nextFocus.requestFocus();
					nextFocus.requestFocusInWindow();
					root.restoreFocus();
					lastEditor = null;
				} else {
					lastEditor = null;
					editorView.fireActiveEditorChanged(lastEditor, null);
				}

				editorView.fireEditorClosed(closedEditor, editorInfo
						.getEditorDecorator().getId());
				freeView(closedView);
			}
		}

		private void freeView(View v1) {
			root.removeView(v1);
			Component panel = v1.getComponent();
			SimplePanel simplePanel = (SimplePanel) panel.getParent();
			if (simplePanel != null) {
				simplePanel.remove(panel); // here we can call removeAll()
				simplePanel.setComponent(null); // very important
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
			View focusedView = root.getFocusedView();
			if (focusedView != null) {
				EditorDecorator nextEditor = getEditorByComponent(
						focusedView.getComponent()).getEditorDecorator();
				if (nextEditor != lastEditor) {
					IEditor previous = null;
					if (lastEditor != null) {
						previous = lastEditor.getEditor();
					}
					lastEditor = nextEditor;
					editorView.fireActiveEditorChanged(previous, lastEditor
							.getEditor());
				}
			}
		}
	}

	private class ChangeNameListener extends AbstractDocumentListener {

		@Override
		public void nameChanged(DocumentEvent documentEvent) {
			EditorInfo[] infos = getEditorsByDocument(documentEvent
					.getDocument());
			for (EditorInfo editorInfo : infos) {
				View view = editorInfo.getView();
				view.getViewProperties().setTitle(
						documentEvent.getDocument().getName());
			}
		}

	}

	private class DocumentRemovalListener implements DocumentCatalogListener {

		public void documentAdded(IDocument parent, IDocument document) {
		}

		public void documentRemoved(IDocument parent, IDocument document) {
		}

		public boolean documentRemoving(IDocument parent, IDocument document) {
			EditorInfo[] infos = getEditorsByDocument(document);
			for (EditorInfo editorInfo : infos) {
				View view = editorInfo.getView();
				return closeEditorView(view);
			}

			return true;
		}
	}

	private boolean closeEditorView(View view) {
		try {
			view.closeWithAbort();
			return true;
		} catch (OperationAbortedException e) {
			return false;
		} catch (Exception e) {
			Services.getErrorManager().error("Cannot close editor", e);
			return false;
		}
	}

	private class EditorInfo {
		private View view;
		private IDocument document;
		private EditorDecorator editorDecorator;
		private Component editorComponent;

		public EditorInfo(View view, IDocument document,
				EditorDecorator editorDecorator, Component editorComponent) {
			super();
			this.view = view;
			this.document = document;
			this.editorDecorator = editorDecorator;
			this.editorComponent = editorComponent;
		}

		public View getView() {
			return view;
		}

		public IDocument getDocument() {
			return document;
		}

		public EditorDecorator getEditorDecorator() {
			return editorDecorator;
		}

		public Component getEditorComponent() {
			return editorComponent;
		}
	}

	public boolean closeEditor(IEditor editor) {
		for (EditorInfo editorInfo : editorsInfo) {
			if (editor == editorInfo.getEditorDecorator()) {
				return closeEditorView(editorInfo.getView());
			}
		}

		throw new IllegalArgumentException("The editor does not exist");
	}

	public IEditor[] getEditors() {
		IEditor[] ret = new IEditor[editorsInfo.size()];
		for (int i = 0; i < editorsInfo.size(); i++) {
			ret[i] = editorsInfo.get(i).getEditorDecorator();
		}

		return ret;
	}
}
