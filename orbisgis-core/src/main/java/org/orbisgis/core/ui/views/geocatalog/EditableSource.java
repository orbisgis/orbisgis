package org.orbisgis.core.ui.views.geocatalog;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceRemovalEvent;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editorViews.toc.AbstractTableEditableElement;
import org.orbisgis.core.ui.editors.table.Selection;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.progress.IProgressMonitor;

public class EditableSource extends AbstractTableEditableElement implements
		TableEditableElement {

	public static final String EDITABLE_RESOURCE_TYPE = "org.orbisgis.core.ui.geocatalog.EditableResource";

	private String sourceName;
	private DataSource ds;
	private SourceSelection resourceSelection = new SourceSelection();

	private NameChangeSourceListener listener = new NameChangeSourceListener();

	public EditableSource(String sourceName) {
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
		Services.getService(DataManager.class).getSourceManager()
				.removeSourceListener(listener);
	}

	@Override
	public String getTypeId() {
		return EDITABLE_RESOURCE_TYPE;
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		try {
			DataManager dataManager = Services.getService(DataManager.class);
			if (ds == null) {
				DataSourceFactory dsf = dataManager.getDSF();
				ds = dsf.getDataSource(sourceName);
			}
			ds.open();
			super.open(progressMonitor);

			dataManager.getSourceManager().addSourceListener(listener);
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
		if (obj instanceof EditableSource) {
			EditableSource er = (EditableSource) obj;
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
		return ds.isEditable();
	}

	@Override
	public MapContext getMapContext() {
		return null;
	}

	private class NameChangeSourceListener implements SourceListener {

		@Override
		public void sourceAdded(SourceEvent e) {
		}

		@Override
		public void sourceNameChanged(SourceEvent e) {
			sourceName = e.getNewName();
			fireIdChanged();
		}

		@Override
		public void sourceRemoved(SourceRemovalEvent e) {
		}

	}
}
