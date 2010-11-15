/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.plugins.views.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.properties.DockingWindowProperties;
import net.infonode.gui.panel.SimplePanel;

import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.edition.EditableElementListener;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.ui.editor.EditorDecorator;
import org.orbisgis.core.ui.editor.EditorListener;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.TableEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.OpenGeocognitionElementJob;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;
import org.orbisgis.progress.NullProgressMonitor;

public class EditorPanel extends Container {

	private static final Logger logger = Logger.getLogger(EditorPanel.class);
	private RootWindow root;
	private static ArrayList<EditorInfo> editorsInfo = new ArrayList<EditorInfo>();
	private EditorDecorator lastEditor = null;
	private ChangeNameListener changeNameListener = new ChangeNameListener();
	private ArrayList<EditorListener> listeners = new ArrayList<EditorListener>();

	public EditorPanel() {
		this.setLayout(new BorderLayout());
		root = new RootWindow(null);
		root.getRootWindowProperties().getSplitWindowProperties()
				.setContinuousLayoutEnabled(false);	
		root.getRootWindowProperties().getTabWindowProperties()
				.getCloseButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties()
				.getUndockButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties()
				.getRestoreButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties()
				.getMinimizeButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties()
				.getMaximizeButtonProperties().setVisible(false);
		root.getRootWindowProperties().getWindowAreaProperties().setInsets(
				new Insets(0, 0, 0, 0));
		root.getRootWindowProperties().getTabWindowProperties()
				.getTabProperties().getFocusedProperties()
				.getComponentProperties().setBackgroundColor(
						new Color(100, 140, 190));
		root.getRootWindowProperties().getWindowAreaProperties()
				.setBackgroundColor(new Color(238, 238, 238));
		
		root.getRootWindowProperties().getWindowAreaProperties().setBorder(
				BorderFactory.createEmptyBorder());

		// Disable undock
		DockingWindowProperties defaultWindowProperties = root
				.getRootWindowProperties().getDockingWindowProperties();
		defaultWindowProperties.setDockEnabled(false);
		defaultWindowProperties.setUndockEnabled(false);
		defaultWindowProperties.setUndockOnDropEnabled(false);
		


		this.add(root, BorderLayout.CENTER);

		// root = Services.getService(RootWindow.class);

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

		this.setDropTarget(new DropTarget(this, new DropTargetAdapter() {

			@Override
			public void drop(DropTargetDropEvent dtde) {
				BackgroundManager backgroundManager = (BackgroundManager) Services
						.getService(BackgroundManager.class);
				Transferable trans = dtde.getTransferable();
				if (trans
						.isDataFlavorSupported(TransferableEditableElement.editableElementFlavor)) {
					try {
						EditableElement[] elements = (EditableElement[]) trans
								.getTransferData(TransferableEditableElement.editableElementFlavor);
						backgroundManager
								.backgroundOperation(new OpenGeocognitionElementJob(
										elements));
					} catch (UnsupportedFlavorException e) {
						Services.getErrorManager().error(
								"Cannot open this type of element", e);
					} catch (IOException e) {
						Services.getErrorManager().error("Cannot open element",
								e);
					}
				}
			}

		}));
	}

	public EditableElement getCurrentDocument() {
		if (lastEditor != null) {
			return lastEditor.getElement();
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
	public boolean isBeingEdited(EditableElement doc,
			Class<? extends IEditor> editorClass) {
		return findViewWithEditor(root, doc, editorClass) != null;
	}

	/**
	 * Makes the editor visible
	 * 
	 * @param element
	 * @param editorClass
	 */
	public void showEditor(EditableElement document,
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

	public IEditor[] getEditor(EditableElement element) {
		ArrayList<IEditor> ret = new ArrayList<IEditor>();
		for (EditorInfo editorInfo : editorsInfo) {
			if (editorInfo.getDocument().equals(element)) {
				ret.add(editorInfo.getEditorDecorator().getEditor());
			}
		}

		return ret.toArray(new IEditor[ret.size()]);
	}

	private EditorInfo[] getEditorsByDocument(EditableElement document) {
		ArrayList<EditorInfo> ret = new ArrayList<EditorInfo>();
		for (EditorInfo editorInfo : editorsInfo) {
			if (editorInfo.getDocument().equals(document)) {
				ret.add(editorInfo);
			}
		}

		return ret.toArray(new EditorInfo[ret.size()]);
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
				.add(new EditorInfo(view, editor.getElement(), editor, comp));

		editor.getElement().addElementListener(changeNameListener);
		//inform that element was loaded	
		if(editor.getEditor() instanceof MapEditorPlugIn) 
			fireElementLoaded(editor.getEditor(),comp);	
	}

	private View findViewWithEditor(DockingWindow wnd, EditableElement doc,
			Class<? extends IEditor> editorClass) {
		for (int i = 0; i < wnd.getChildWindowCount(); i++) {
			DockingWindow child = wnd.getChildWindow(i);
			if (child instanceof View) {
				Component comp = ((View) child).getComponent();
				EditorDecorator existingEditor = getEditorByComponent(comp)
						.getEditorDecorator();
				if ((existingEditor.getElement().equals(doc))
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
		HashSet<EditableElement> done = new HashSet<EditableElement>();
		Iterator<EditorInfo> it = editorsInfo.iterator();
		while (it.hasNext()) {
			EditorInfo editorInfo = it.next();
			EditableElement document = editorInfo.getDocument();
			if (!done.contains(document)) {
				try {
					document.save();
				} catch (EditableElementException e) {
					Services.getService(ErrorManager.class).error(
							"Problem saving", e);
				}
				done.add(document);
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
				try {
					EditorDecorator editorDecorator = editorInfo
							.getEditorDecorator();
					if (fireEditorClosing(editorDecorator.getEditor(),
							editorDecorator.getId())) {
						if (editorInfo.element.isModified()) {
							int res = JOptionPane.showConfirmDialog(
									EditorPanel.this, "There are unsaved "
											+ "changes in "
											+ editorInfo.element.getId()
											+ ", save them before closing?",
									"Close editor",
									JOptionPane.YES_NO_CANCEL_OPTION);
							if (res == JOptionPane.CANCEL_OPTION) {
								throw new OperationAbortedException();
							} else if (res == JOptionPane.YES_OPTION) {
								editorInfo.element.save();
							}
						}
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
		}

		public void windowClosed(DockingWindow arg0) {
			if (arg0 instanceof View) {
				View closedView = (View) arg0;
				EditorInfo editorInfo = getEditorByComponent(closedView
						.getComponent());
				editorsInfo.remove(editorInfo);
				IEditor closedEditor = editorInfo.getEditorDecorator();

				// Remove document listener
				closedEditor.getElement().removeElementListener(
						changeNameListener);

				// Focus next editor
				if (nextFocus != null) {
					nextFocus.requestFocusInWindow();
					if (lastEditor != null) {
						IEditor previous = lastEditor.getEditor();

						lastEditor = null;
						fireActiveEditorChanged(previous, null);
					}
				} else {
					lastEditor = null;
					fireActiveEditorChanged(lastEditor, null);
				}

				fireEditorClosed(closedEditor, editorInfo.getEditorDecorator()
						.getId());
				freeView(closedView, editorInfo.editorDecorator);

				try {
					editorInfo.element.close(new NullProgressMonitor());
				} catch (UnsupportedOperationException e) {
					logger.debug("Cannot close editor", e);
				} catch (EditableElementException e) {
					logger.debug("Cannot close editor", e);
				}
			}
		}

		private void freeView(View v1, EditorDecorator editorDecorator) {
			root.removeView(v1);
			Component panel = v1.getComponent();
			SimplePanel simplePanel = (SimplePanel) panel.getParent();
			if (simplePanel != null) {
				simplePanel.remove(panel); // here we can call removeAll()
				simplePanel.setComponent(null); // very important
			}

			editorDecorator.delete();
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
					fireActiveEditorChanged(previous, lastEditor.getEditor());
					//inform element was loaded
					IEditor editor = lastEditor.getEditor();
					if(editor instanceof MapEditorPlugIn) 
						fireElementLoaded(editor,focusedView.getComponent());					
				}
			}
		}
	}

	private class ChangeNameListener implements EditableElementListener {

		@Override
		public void idChanged(EditableElement element) {
			setTitle(element);
		}

		private void setTitle(EditableElement element) {
			EditorInfo[] infos = getEditorsByDocument(element);
			for (EditorInfo editorInfo : infos) {
				View view = editorInfo.getView();
				EditorDecorator editor = editorInfo.getEditorDecorator();
				String title = editor.getTitle();
				if (editor.getElement().isModified()) {
					title = "*" + title;
				}
				view.getViewProperties().setTitle(title);
			}
		}

		@Override
		public void contentChanged(EditableElement element) {
			setTitle(element);
		}

		@Override
		public void saved(EditableElement element) {
			setTitle(element);
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
		private EditableElement element;
		private EditorDecorator editorDecorator;
		private Component editorComponent;

		public EditorInfo(View view, EditableElement element,
				EditorDecorator editorDecorator, Component editorComponent) {
			super();
			this.view = view;
			this.element = element;
			this.editorDecorator = editorDecorator;
			this.editorComponent = editorComponent;
		}

		public View getView() {
			return view;
		}

		public EditableElement getDocument() {
			return element;
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
			if (editor == editorInfo.getEditorDecorator().getEditor()) {
				return closeEditorView(editorInfo.getView());
			}
		}

		throw new IllegalArgumentException("The editor does not exist");
	}

	public IEditor[] getEditors() {
		IEditor[] ret = new IEditor[editorsInfo.size()];
		for (int i = 0; i < editorsInfo.size(); i++) {
			ret[i] = editorsInfo.get(i).getEditorDecorator().getEditor();
		}

		return ret;
	}

	public IEditor[] getEditors(String editorId, Object object) {
		ArrayList<IEditor> ret = new ArrayList<IEditor>();
		for (int i = 0; i < editorsInfo.size(); i++) {
			EditorDecorator editorDecorator = editorsInfo.get(i)
					.getEditorDecorator();
			if (editorDecorator.getId().equals(editorId)
					&& editorDecorator.getElement().getObject() == object) {
				ret.add(editorDecorator.getEditor());
			}
		}

		return ret.toArray(new IEditor[ret.size()]);
	}

	public IEditor[] getEditors(String editorId) {
		ArrayList<IEditor> ret = new ArrayList<IEditor>();
		for (int i = 0; i < editorsInfo.size(); i++) {
			EditorDecorator editorDecorator = editorsInfo.get(i)
					.getEditorDecorator();
			if (editorDecorator.getId().equals(editorId)) {
				ret.add(editorDecorator.getEditor());
			}
		}

		return ret.toArray(new IEditor[ret.size()]);
	}

	public String getEditorId(IEditor editor) {
		for (EditorInfo editorInfo : editorsInfo) {
			if (editor == editorInfo.getEditorDecorator().getEditor()) {
				return editorInfo.getEditorDecorator().getId();
			}
		}

		return null;
	}

	public void addEditorListener(EditorListener listener) {
		listeners.add(listener);
	}

	public void removeEditorListener(EditorListener listener) {
		listeners.remove(listener);
	}

	@SuppressWarnings("unchecked")
	private void fireActiveEditorChanged(IEditor previous, IEditor current) {
		ArrayList<EditorListener> l = (ArrayList<EditorListener>) listeners
				.clone();
		for (EditorListener listener : l) {
			listener.activeEditorChanged(previous, current);
		}
		WorkbenchContext wbContext =
			Services.getService(WorkbenchContext.class);
		wbContext.setLastAction("Editor changed");
	}

	@SuppressWarnings("unchecked")
	private void fireEditorClosed(IEditor editor, String editorId) {
		ArrayList<EditorListener> l = (ArrayList<EditorListener>) listeners
				.clone();
		for (EditorListener listener : l) {
			listener.activeEditorClosed(editor, editorId);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean fireEditorClosing(IEditor editor, String editorId) {
		ArrayList<EditorListener> l = (ArrayList<EditorListener>) listeners
				.clone();
		for (EditorListener listener : l) {
			if (!listener.activeEditorClosing(editor, editorId)) {
				return false;
			}
		}

		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void fireElementLoaded(IEditor editor, Component comp) {
		ArrayList<EditorListener> l = (ArrayList<EditorListener>) listeners	.clone();
		for (EditorListener listener : l) {
			listener.elementLoaded(editor, comp);
		}		
		WorkbenchContext wbContext =
			Services.getService(WorkbenchContext.class);
		wbContext.setLastAction("Editor changed");		
	}
	
	// Return an editor	
	public static EditorDecorator getFirstEditor(EditableElement element) {
		WorkbenchContext wbContext = Services
				.getService(WorkbenchContext.class);		
		PlugInContext plugInContext = wbContext.createPlugInContext();

		IEditor[] editors = new IEditor[2];
		editors[0] = new MapEditorPlugIn();
		editors[1] = new TableEditorPlugIn();
		IEditor editor = null;
		for (int i = 0; i < editors.length; i++) {
			if (editors[i].acceptElement(element.getTypeId())) {
				try {
					editor = editors[i].getClass().newInstance();
					editor.initialize(plugInContext);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (editor instanceof MapEditorPlugIn)
					return new EditorDecorator(editor, IconLoader
							.getIcon("map.png"), Names.EDITOR_MAP_ID);
				else if (editor instanceof TableEditorPlugIn) {
					return new EditorDecorator(editor, IconLoader
							.getIcon("openattributes.png"), Names.EDITOR_TABLE_ID);
				}
			}
		}
		return null;
	}
}
