package org.orbisgis.core.ui.plugins.editors.tableEditor;

import java.awt.event.MouseEvent;
import java.util.Observable;

import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableComponent;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.TableEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.progress.IProgressMonitor;

public class SetNullPlugIn extends AbstractPlugIn {

	private MouseEvent event;
	private boolean isVisible;

	public boolean execute(final PlugInContext context) throws Exception {
		
		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public void run(IProgressMonitor pm) {
		
				IEditor editor = context.getActiveEditor();
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
			}

			@Override
			public String getTaskName() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbContext.getWorkbench()
				.getFrame().getTableEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_SETNULL_PATH1 },
				Names.POPUP_TABLE_SETNULL_GROUP, false,
				getIcon(IconNames.POPUP_TABLE_SETNULL_ICON), wbContext);
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
		IEditor editor = null;
		if((editor=getPlugInContext().getTableEditor()) != null){
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
