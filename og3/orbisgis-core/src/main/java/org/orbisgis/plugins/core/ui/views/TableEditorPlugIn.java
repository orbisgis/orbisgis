package org.orbisgis.plugins.core.ui.views;

import java.awt.Component;
import java.util.Observable;

import org.orbisgis.plugins.core.edition.EditableElement;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.editorViews.toc.EditableLayer;
import org.orbisgis.plugins.core.ui.editors.table.TableComponent;
import org.orbisgis.plugins.core.ui.editors.table.TableEditableElement;
import org.orbisgis.plugins.core.ui.views.geocatalog.EditableSource;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;

public class TableEditorPlugIn extends ViewPlugIn implements IEditor {
	private TableEditableElement element;
	private TableComponent table;
	private WorkbenchContext wbContext;
	private String editors[];

	public TableComponent getPanel() {
		return table;
	}

	public TableEditorPlugIn() {
		table = new TableComponent(this);
	}

	public void initialize(PlugInContext context) {
		editors = new String[0];
		if (context.getWorkbenchContext().getWorkbench().getFrame()
				.getViewDecorator("Table") == null)
			context.getWorkbenchContext().getWorkbench().getFrame().getViews()
					.add(
							new ViewDecorator(this, "Table",
									getIcon("openattributes.png"), editors));
	}

	@Override
	public boolean acceptElement(String typeId) {
		return EditableSource.EDITABLE_RESOURCE_TYPE.equals(typeId)
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
	public void initialize(WorkbenchContext wbContext) {
		this.wbContext = wbContext;
	}

	public void moveSelectionUp() {
		table.moveSelectionUp();
	}

	public boolean execute(PlugInContext context) throws Exception {
		return false;
	}

	// Observer & PlugIn
	public void update(Observable arg0, Object arg1) {
	}

	public boolean isVisible() {
		return false;
	}

	public ViewPlugIn getView() {
		return this;
	}

}
