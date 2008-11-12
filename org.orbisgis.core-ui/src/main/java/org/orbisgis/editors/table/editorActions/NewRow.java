package org.orbisgis.editors.table.editorActions;

import java.text.ParseException;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.ui.sif.AskValidRow;
import org.sif.UIFactory;

public class NewRow implements IEditorAction {

	@Override
	public void actionPerformed(IEditor editor) {
		TableEditableElement element = (TableEditableElement) editor
				.getElement();
		DataSource ds = element.getDataSource();
		try {
			AskValidRow rowInput = new AskValidRow("Introduce row values", ds);
			if (UIFactory.showDialog(rowInput)) {
				ds.insertFilledRow(rowInput.getRow());
			}
		} catch (IllegalArgumentException e) {
			Services.getService(ErrorManager.class).error("Cannot add row", e);
		} catch (IncompatibleTypesException e) {
			Services.getService(ErrorManager.class).error(
					"Incompatible types at insertion", e);
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error("Data access error",
					e);
		} catch (ParseException e) {
			Services.getService(ErrorManager.class).error("Unrecognized input",
					e);
		}
	}

	@Override
	public boolean isEnabled(IEditor editor) {
		TableEditableElement element = (TableEditableElement) editor
				.getElement();
		return element.getDataSource().isEditable();
	}

	@Override
	public boolean isVisible(IEditor editor) {
		return true;
	}

}
