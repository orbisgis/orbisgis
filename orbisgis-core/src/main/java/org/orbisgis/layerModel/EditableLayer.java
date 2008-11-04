package org.orbisgis.layerModel;

import org.gdms.data.NonEditableDataSourceException;
import org.gdms.driver.DriverException;
import org.orbisgis.edition.AbstractEditableElement;
import org.orbisgis.edition.EditableElement;
import org.orbisgis.edition.EditableElementException;
import org.orbisgis.progress.IProgressMonitor;

public class EditableLayer extends AbstractEditableElement implements
		EditableElement {

	public static final String EDITABLE_LAYER_TYPE = "org.orbisgis.mapContext.EditableLayer";

	private ILayer layer;
	private String prefix;

	public EditableLayer(String prefix, ILayer layer) {
		this.prefix = prefix;
		this.layer = layer;
	}

	@Override
	public String getId() {
		return prefix + ":" + layer.getName();
	}

	@Override
	public boolean isModified() {
		return layer.getDataSource().isModified();
	}

	@Override
	public void close(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		// Nothing. Layers are closed when the map is closed
	}

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return layer.getDataSource();
	}

	@Override
	public String getTypeId() {
		return EDITABLE_LAYER_TYPE;
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		// Nothing. Layers are closed when the map is closed
	}

	@Override
	public void save() throws UnsupportedOperationException,
			EditableElementException {
		try {
			layer.getDataSource().commit();
		} catch (DriverException e) {
			throw new EditableElementException("Could not save", e);
		} catch (NonEditableDataSourceException e) {
			throw new EditableElementException("Non editable element", e);
		}
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

}
