package org.orbisgis.core.ui.plugins.views;

import java.awt.Component;
import java.util.Observable;

import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editorViews.toc.EditableLayer;
import org.orbisgis.core.ui.editors.table.TableComponent;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.geocatalog.EditableSource;

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
		setPlugInContext(context);
		if (context.getWorkbenchContext().getWorkbench().getFrame()
				.getViewDecorator(Names.EDITOR_TABLE_ID) == null)
			context.getWorkbenchContext().getWorkbench().getFrame().getViews()
					.add(
							new ViewDecorator(this, Names.EDITOR_TABLE_ID,
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

	//View plugin is updated by EditorViewPlugIn
	public boolean isEnabled() {		
		return true;
	}
	
	public boolean isSelected() {
		return true;
	}

	public ViewPlugIn getView() {
		return this;
	}
	
	public String getName() {		
		return "Table editor view";
	}

}
