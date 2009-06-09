package org.orbisgis.core.ui.editorViews.toc;

import org.gdms.data.DataSource;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.edition.EditableElementListener;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerListener;
import org.orbisgis.core.layerModel.LayerListenerAdapter;
import org.orbisgis.core.layerModel.LayerListenerEvent;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.table.Selection;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.progress.IProgressMonitor;

public class EditableLayer extends AbstractTableEditableElement implements
		TableEditableElement {

	public static final String EDITABLE_LAYER_TYPE = "org.orbisgis.mapContext.EditableLayer";

	private ILayer layer;
	private EditableElement element;
	private MapContext mapContext;

	private IdChangeListener listener;

	public EditableLayer(EditableElement element, ILayer layer) {
		this.layer = layer;
		this.element = element;
		this.mapContext = (MapContext) element.getObject();

		listener = new IdChangeListener();
	}

	@Override
	public String getId() {
		return element.getId() + ":" + layer.getName();
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

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		super.open(progressMonitor);
		element.addElementListener(listener);
		layer.addLayerListener(listener);
	}

	@Override
	public void close(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		super.close(progressMonitor);
		element.removeElementListener(listener);
		layer.removeLayerListener(listener);
	}
	
	private class IdChangeListener extends LayerListenerAdapter implements EditableElementListener, LayerListener {

		@Override
		public void contentChanged(EditableElement element) {
		}

		@Override
		public void idChanged(EditableElement element) {
			fireIdChanged();
		}

		@Override
		public void saved(EditableElement element) {
		}

		@Override
		public void nameChanged(LayerListenerEvent e) {
			fireIdChanged();
		}

	}
}
