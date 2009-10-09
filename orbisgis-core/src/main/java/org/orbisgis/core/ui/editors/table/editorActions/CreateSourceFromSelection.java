package org.orbisgis.core.ui.editors.table.editorActions;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editor.action.IEditorAction;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.errorManager.ErrorManager;

public class CreateSourceFromSelection implements IEditorAction {

	@Override
	public void actionPerformed(IEditor editor) {
		TableEditableElement element = (TableEditableElement) editor
				.getElement();
		DataSource original = element.getDataSource();
		int[] selectedRows = element.getSelection().getSelectedRows();
		createSourceFromSelection(original, selectedRows);
	}

	public static void createSourceFromSelection(DataSource original,
			int[] selectedRows) {
		try {
			DataManager dm = Services.getService(DataManager.class);

			// Create the new source
			DataSourceFactory dsf = dm.getDSF();
			File file = dsf.getResultFile();
			DataSourceCreation dsc = new FileSourceCreation(file, original
					.getMetadata());
			dsf.createDataSource(dsc);
			FileSourceDefinition dsd = new FileSourceDefinition(file);

			// Find an unique name to register
			SourceManager sm = dm.getSourceManager();
			int index = -1;
			String newName;
			do {
				index++;
				newName = original.getName() + "_selection_" + index;
			} while (sm.getSource(newName) != null);
			sm.register(newName, dsd);

			// Populate the new source
			DataSource newds = dsf.getDataSource(newName);
			newds.open();
			for (int i = 0; i < selectedRows.length; i++) {
				newds.insertFilledRow(original.getRow(selectedRows[i]));
			}
			newds.commit();
			newds.close();
		} catch (SourceAlreadyExistsException e) {
			Services.getService(ErrorManager.class).error("Bug", e);
		} catch (DriverLoadException e) {
			Services.getService(ErrorManager.class).error("Bug", e);
		} catch (NoSuchTableException e) {
			Services.getService(ErrorManager.class).error("Bug", e);
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot create source", e);
		} catch (DataSourceCreationException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot create source", e);
		} catch (NonEditableDataSourceException e) {
			Services.getService(ErrorManager.class).error("Bug", e);
		}
	}

	@Override
	public boolean isEnabled(IEditor editor) {
		TableEditableElement element = (TableEditableElement) editor
				.getElement();
		return element.getSelection().getSelectedRows().length > 0;
	}

	@Override
	public boolean isVisible(IEditor editor) {
		return true;
	}

}
