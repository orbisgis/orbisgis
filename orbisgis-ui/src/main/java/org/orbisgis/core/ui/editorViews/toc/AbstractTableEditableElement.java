package org.orbisgis.core.ui.editorViews.toc;

import org.gdms.data.DataSource;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.FieldEditionEvent;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.driver.DriverException;
import org.orbisgis.core.edition.AbstractEditableElement;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

public abstract class AbstractTableEditableElement extends
		AbstractEditableElement implements TableEditableElement {

	private ModificationListener modificationListener = new ModificationListener();

	@Override
	public boolean isModified() {
                if (!getDataSource().isOpen()) {
                        return false;
                }
                return getDataSource().isModified();
        }

	@Override
	public void close(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		DataSource ds = getDataSource();
		ds.removeEditionListener(modificationListener);
		ds.removeMetadataEditionListener(modificationListener);
	}

	@Override
	public void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		DataSource ds = getDataSource();
		ds.addEditionListener(modificationListener);
		ds.addMetadataEditionListener(modificationListener);
	}

	@Override
	public void save() throws UnsupportedOperationException,
			EditableElementException {
		try {
			if (isEditable())
			getDataSource().commit();
		} catch (DriverException e) {
			throw new EditableElementException(I18N.getString("orbisgis.org.orbisgis.ui.toc.abstractTableEditableElement.cannotSave"), e); //$NON-NLS-1$
		} catch (NonEditableDataSourceException e) {
			throw new EditableElementException(I18N.getString("orbisgis.org.orbisgis.ui.toc.abstractTableEditableElement.nonEditableElement"), e); //$NON-NLS-1$
		}
	}

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return getDataSource();
	}

	private class ModificationListener implements EditionListener,
			MetadataEditionListener {

		@Override
		public void multipleModification(MultipleEditionEvent e) {
			fireContentChanged();
		}

		@Override
		public void singleModification(EditionEvent e) {
			fireContentChanged();
		}

		@Override
		public void fieldAdded(FieldEditionEvent event) {
			fireContentChanged();
		}

		@Override
		public void fieldModified(FieldEditionEvent event) {
			fireContentChanged();
		}

		@Override
		public void fieldRemoved(FieldEditionEvent event) {
			fireContentChanged();
		}

	}

}
