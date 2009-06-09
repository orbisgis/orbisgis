package org.orbisgis.core.ui.editors.table.editorActions;

import java.util.Arrays;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editor.action.IEditorAction;
import org.orbisgis.core.ui.editors.table.TableEditableElement;

public class DeleteSelection implements IEditorAction {

	@Override
	public void actionPerformed(IEditor editor) {
		TableEditableElement element = (TableEditableElement) editor
				.getElement();
		removeSelection(element);
	}

	public static void removeSelection(TableEditableElement element) {
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
		TableEditableElement element = (TableEditableElement) editor
				.getElement();

		if (element.isEditable()){
			return true;
		}
		else if (element.getMapContext() == null){
			return isEnabled(element);
		}
		return false;
	}

	@Override
	public boolean isVisible(IEditor editor) {
		return true;
	}

	public static boolean isEnabled(TableEditableElement element) {

		return element.getDataSource().isEditable();
	}

}
