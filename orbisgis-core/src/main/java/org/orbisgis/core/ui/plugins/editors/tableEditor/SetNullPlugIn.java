package org.orbisgis.core.ui.plugins.editors.tableEditor;

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
import org.orbisgis.progress.IProgressMonitor;

public class SetNullPlugIn extends AbstractPlugIn {

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
									.getTable().rowAtPoint(getEvent().getPoint()),
							((TableComponent) editor.getView().getComponent())
									.getTable().columnAtPoint(getEvent().getPoint()),
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

	public boolean isEnabled() {
		boolean isEnabled = false;
		IEditor editor = null;
		int row = -1;
		int column = -1;
		if((editor=getPlugInContext().getTableEditor()) != null
				&& getSelectedColumn() ==-1 && getEvent()!=null ){
			
			row = ((TableComponent) editor.getView().getComponent()).
								getTable().rowAtPoint(getEvent().getPoint());
			column = ((TableComponent) editor.getView().getComponent())
								.getTable().columnAtPoint(getEvent().getPoint());
			if(row!=-1 && column!=-1) {			
				editor = (TableEditorPlugIn) editor;
				TableEditableElement element = (TableEditableElement) editor
						.getElement();
				try {
					isEnabled = element.isEditable()
							&& !element.getDataSource().isNull( row,column );
				} catch (DriverException e) {
					Services.getService(ErrorManager.class).error(
							"Cannot set null row", e);
					return false;
				}
			}
		}
		return isEnabled;
	}
}
