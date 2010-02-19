package org.orbisgis.plugins.core.ui.views;

import java.util.Observable;

import javax.swing.JMenuItem;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.edition.EditableElement;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.editor.EditorListener;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.editorViews.toc.AbstractTableEditableElement;
import org.orbisgis.plugins.core.ui.editorViews.toc.Toc;
import org.orbisgis.plugins.core.ui.editors.table.TableEditableElement;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.workbench.Names;

public class TocViewPlugIn extends ViewPlugIn {

	private Toc panel;
	private String editors[];
	private JMenuItem menuItem;
	private EditorListener closingListener = null;

	public Toc getPanel() {
		return panel;
	}

	public void initialize(PlugInContext context) throws Exception {
		editors = new String[2];
		editors[0] = "Map";
		editors[1] = "Table";

		panel = new Toc();
		// setComponent(panel,"Memory", getIcon("utilities-system-monitor.png"),
		// context);
		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.TOC, true,
				getIcon(Names.TOC_ICON), editors, panel, null, null,
				context.getWorkbenchContext());
	}

	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
		return true;
	}

	public boolean setEditor(IEditor editor) {
		listenEditorClosing();
		EditableElement element = editor.getElement();
		if (editor instanceof MapEditorPlugIn) {
			panel.setMapContext(element, (MapEditorPlugIn) editor);
			return true;
		} else if (editor instanceof TableEditorPlugIn) {
			TableEditableElement tableElement = (TableEditableElement) element;
			MapContext mapContext = tableElement.getMapContext();
			// Get MapContext element
			EditorManager em = Services.getService(EditorManager.class);
			IEditor[] mapEditors = em.getEditors("Map", mapContext);
			if (mapEditors.length > 0) {
				panel.setMapContext(mapEditors[0].getElement());
				return true;
			} else {
				editorViewDisabled();
				return false;
			}
		}

		return false;
	}

	public void listenEditorClosing() {
		if (closingListener == null) {
			closingListener = new EditorListener() {

				@Override
				public boolean activeEditorClosing(IEditor editor,
						String editorId) {
					if (editorId.equals("Map")) {
						MapContext mc = (MapContext) editor.getElement()
								.getObject();
						EditorManager em = Services
								.getService(EditorManager.class);
						IEditor[] editors = em.getEditors("Table");
						for (IEditor openEditor : editors) {
							AbstractTableEditableElement el = (AbstractTableEditableElement) openEditor
									.getElement();
							if (el.getMapContext() == mc) {
								if (!em.closeEditor(openEditor)) {
									return false;
								}
							}
						}
					}

					return true;
				}

				@Override
				public void activeEditorClosed(IEditor editor, String editorId) {
				}

				@Override
				public void activeEditorChanged(IEditor previous,
						IEditor current) {
				}
			};
			EditorManager em = Services.getService(EditorManager.class);
			em.addEditorListener(closingListener);
		}
	}

	public void editorViewDisabled() {
		// TODO (pyf): TOC is not available after one editor is closed. Except
		// that if it stays only one editor in EditorPanel.
		// In this case Toc context is updated. But upper 2 editors the previous
		// editor is not updated
		panel.setMapContext(null);
		panel.setMapContext(null, null);
	}

	public void update(Observable o, Object arg) {
		setSelected();
	}

	public void setSelected() {
		menuItem.setSelected(isVisible());
	}

	public boolean isVisible() {
		return getUpdateFactory().viewIsOpen(getId());
	}
}
