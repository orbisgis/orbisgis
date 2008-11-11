package org.orbisgis.editors.table;

import java.awt.Component;

import org.orbisgis.PersistenceException;
import org.orbisgis.edition.EditableElement;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editorViews.toc.EditableLayer;
import org.orbisgis.views.geocatalog.EditableResource;

public class TableEditor implements IEditor {

	private TableEditableElement element;
	private TableComponent table;

	public TableEditor() {
		table = new TableComponent(this);
	}

	@Override
	public boolean acceptElement(String typeId) {
		return EditableResource.EDITABLE_RESOURCE_TYPE.equals(typeId)
				|| EditableLayer.EDITABLE_LAYER_TYPE.equals(typeId);
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
		this.element = (TableEditableElement) element;
		table.setElement(this.element);
	}

	@Override
	public void delete() {
		table.setElement(null);
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

	public void moveSelectionUp() {
		table.moveSelectionUp();
	}

}
