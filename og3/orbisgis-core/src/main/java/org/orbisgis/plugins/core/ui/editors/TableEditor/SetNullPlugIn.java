package org.orbisgis.plugins.core.ui.editors.TableEditor;

import java.awt.event.MouseEvent;
import java.util.Observable;

import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.editors.table.TableComponent;
import org.orbisgis.plugins.core.ui.editors.table.TableEditableElement;
import org.orbisgis.plugins.core.ui.views.TableEditorPlugIn;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;
import org.orbisgis.plugins.errorManager.ErrorManager;

public class SetNullPlugIn extends AbstractPlugIn {

	private MouseEvent event;
	private boolean isVisible;

	public boolean execute(PlugInContext context) throws Exception {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		TableEditableElement element = (TableEditableElement) editor
				.getElement();
		try {
			element.getDataSource().setFieldValue(
					((TableComponent) editor.getView().getComponent())
							.getTable().rowAtPoint(event.getPoint()),
					((TableComponent) editor.getView().getComponent())
							.getTable().columnAtPoint(event.getPoint()),
					ValueFactory.createNullValue());
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error("Cannot set null", e);
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbContext.getWorkbench()
				.getFrame().getTableEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_SETNULL_PATH1 },
				Names.POPUP_TABLE_SETNULL_GROUP, false,
				getIcon(Names.POPUP_TABLE_SETNULL_ICON), wbContext);
	}

	public void update(Observable o, Object arg) {
		isVisible(arg);
	}

	public boolean isEnabled(Object arg) {
		return true;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public boolean isVisible(Object arg) {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if ("Table".equals(em.getEditorId(editor)) && editor != null) {
			try {
				event = (MouseEvent) arg;
			} catch (Exception e) {
				return isVisible = false;
			}
			editor = (TableEditorPlugIn) editor;
			TableEditableElement element = (TableEditableElement) editor
					.getElement();
			try {
				return isVisible = element.isEditable()
						&& !element.getDataSource().isNull(
								((TableComponent) editor.getView()
										.getComponent()).getTable().rowAtPoint(
										event.getPoint()),
								((TableComponent) editor.getView()
										.getComponent()).getTable()
										.columnAtPoint(event.getPoint()));
			} catch (DriverException e) {
				Services.getService(ErrorManager.class).error(
						"Cannot set null row", e);
				return false;
			}
		}
		return isVisible = false;
	}
}
