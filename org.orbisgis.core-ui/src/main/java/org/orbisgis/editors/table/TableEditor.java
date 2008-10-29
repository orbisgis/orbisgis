package org.orbisgis.editors.table;

import java.awt.Component;

import org.gdms.data.DataSource;
import org.orbisgis.PersistenceException;
import org.orbisgis.edition.EditableElement;
import org.orbisgis.editor.IEditor;
import org.orbisgis.views.geocatalog.EditableResource;

public class TableEditor implements IEditor {

	private EditableElement element;
	private TableComponent table;

	public TableEditor() {
		table = new TableComponent();
	}

	@Override
	public boolean acceptElement(String typeId) {
		return EditableResource.EDITABLE_RESOURCE_TYPE.equals(typeId);
	}

	@Override
	public EditableElement getElement() {
		return element;
	}

	@Override
	public String getTitle() {
		return element.getId();
	}

	@Override
	public void setElement(EditableElement element) {
		this.element = element;
		if (element instanceof EditableResource) {
			table.setDataSource((DataSource) this.element.getObject(), null);
		}
	}

	@Override
	public void delete() {
		table.setDataSource(null, null);
	}

	@Override
	public Component getComponent() {
		return table;
	}

	@Override
	public void initialize() {
	}

	@Override
	public void loadStatus() throws PersistenceException {
	}

	@Override
	public void saveStatus() throws PersistenceException {
	}

}
