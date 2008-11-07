package org.orbisgis.editors.table.actions;

import java.util.Arrays;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.editors.table.TableEditableElement;

public class DeleteSelection implements IEditorAction {

	@Override
	public void actionPerformed(IEditor editor) {
		TableEditableElement element = (TableEditableElement) editor.getElement();
		int[] sel = element.getSelection().getSelectedRows().clone();
		Arrays.sort(sel);
		DataSource dataSource = element.getDataSource();
		try {
			dataSource.setDispatchingMode(DataSource.STORE);
			for (int i = sel.length - 1; i >= 0; i--) {
				dataSource.deleteRow(sel[i]);
			}
			dataSource.setDispatchingMode(DataSource.DISPATCH);
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot delete selected features",
					e);
		}
	}

	@Override
	public boolean isEnabled(IEditor editor) {
		TableEditableElement element = (TableEditableElement) editor.getElement();
		return element.getSelection().getSelectedRows().length > 0;
	}

	@Override
	public boolean isVisible(IEditor editor) {
		return true;
	}

}
