package org.orbisgis.views.geocatalog;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.edition.EditableElementException;
import org.orbisgis.editorViews.toc.AbstractTableEditableElement;
import org.orbisgis.editors.table.Selection;
import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.progress.IProgressMonitor;

public class EditableResource extends AbstractTableEditableElement implements
		TableEditableElement {

	public static final String EDITABLE_RESOURCE_TYPE = "org.orbisgis.geocatalog.EditableResource";

	private String sourceName;
	private DataSource ds;
	private ResourceSelection resourceSelection = new ResourceSelection();

	public EditableResource(String sourceName) {
		this.sourceName = sourceName;
	}

	@Override
	public String getId() {
		return sourceName;
	}

	@Override
	public void close(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		super.close(progressMonitor);
		try {
			ds.close();
			ds = null;
		} catch (AlreadyClosedException e) {
			throw new EditableElementException("Cannot close the table", e);
		} catch (DriverException e) {
			throw new EditableElementException("Cannot close the table", e);
		}
	}

	@Override
	public String getTypeId() {
		return EDITABLE_RESOURCE_TYPE;
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		try {
			if (ds == null) {
				DataSourceFactory dsf = Services.getService(DataManager.class)
						.getDSF();
				ds = dsf.getDataSource(sourceName);
			}
			ds.open();
			super.open(progressMonitor);
		} catch (DriverException e) {
			throw new EditableElementException("Cannot open the source", e);
		} catch (DriverLoadException e) {
			throw new EditableElementException("Cannot open the source", e);
		} catch (NoSuchTableException e) {
			throw new EditableElementException("Cannot open the source", e);
		} catch (DataSourceCreationException e) {
			throw new EditableElementException("Cannot open the source", e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EditableResource) {
			EditableResource er = (EditableResource) obj;
			return sourceName.equals(er.sourceName);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return sourceName.hashCode();
	}

	@Override
	public DataSource getDataSource() {
		return ds;
	}

	@Override
	public Selection getSelection() {
		return resourceSelection;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public MapContext getMapContext() {
		return null;
	}
}
