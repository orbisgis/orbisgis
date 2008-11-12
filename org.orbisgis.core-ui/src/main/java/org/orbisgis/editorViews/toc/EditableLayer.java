package org.orbisgis.editorViews.toc;

import org.gdms.data.DataSource;
import org.orbisgis.editors.table.Selection;
import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

public class EditableLayer extends AbstractTableEditableElement implements
		TableEditableElement {

	public static final String EDITABLE_LAYER_TYPE = "org.orbisgis.mapContext.EditableLayer";

	private ILayer layer;
	private String prefix;
	private MapContext mapContext;

	public EditableLayer(String prefix, MapContext mapContext, ILayer layer) {
		this.prefix = prefix;
		this.layer = layer;
		this.mapContext = mapContext;
	}

	@Override
	public String getId() {
		return prefix + ":" + layer.getName();
	}

	@Override
	public String getTypeId() {
		return EDITABLE_LAYER_TYPE;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EditableLayer) {
			EditableLayer er = (EditableLayer) obj;
			return getId().equals(er.getId());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public DataSource getDataSource() {
		return layer.getDataSource();
	}

	@Override
	public Selection getSelection() {
		return new LayerSelection(layer);
	}

	@Override
	public boolean isEditable() {
		return mapContext.getActiveLayer() == layer;
	}

	@Override
	public MapContext getMapContext() {
		return mapContext;
	}

}
