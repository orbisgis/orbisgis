/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *  
 *  Lead Erwan BOCHER, scientific researcher, 
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer. 
 *  
 *  User support lead : Gwendall Petit, geomatic engineer. 
 * 
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * This file is part of OrbisGIS.
 * 
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 * 
 * or contact directly: 
 * erwan.bocher _at_ ec-nantes.fr 
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.plugins.views;

import java.awt.Component;

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

	public boolean acceptElement(String typeId) {
		return EditableSource.EDITABLE_RESOURCE_TYPE.equals(typeId)
				|| EditableLayer.EDITABLE_LAYER_TYPE.equals(typeId);
	}

	public EditableElement getElement() {
		return element;
	}

	public String getTitle() {
		return element.getId();
	}

	public void setElement(EditableElement element) {
		this.element = (TableEditableElement) element;
		table.setElement(this.element);
	}

	public void delete() {
		table.setElement(null);
	}

	public Component getComponent() {
		return table;
	}

	public void initialize(WorkbenchContext wbContext) {
		this.wbContext = wbContext;
	}

	public void moveSelectionUp() {
		table.moveSelectionUp();
	}

	public boolean execute(PlugInContext context) throws Exception {
		return false;
	}

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
