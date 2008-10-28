package org.orbisgis.editors.table;

import java.awt.Component;

import org.gdms.data.DataSource;
import org.orbisgis.PersistenceException;
import org.orbisgis.edition.EditableElement;
import org.orbisgis.editor.IEditor;
import org.orbisgis.views.geocatalog.EditableResource;

public class TableEditor implements IEditor {

	private EditableElement element;

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
	}

	@Override
	public void delete() {
	}

	@Override
	public Component getComponent() {
		TableComponent table = new TableComponent();
		table.setDataSource((DataSource) this.element.getObject());
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
