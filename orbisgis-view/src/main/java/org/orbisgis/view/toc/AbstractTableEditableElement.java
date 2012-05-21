package org.orbisgis.view.toc;

import org.gdms.data.DataSource;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.edition.*;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.edition.AbstractEditableElement;
import org.orbisgis.view.edition.EditableElementException;
import org.orbisgis.view.table.TableEditableElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public abstract class AbstractTableEditableElement extends
		AbstractEditableElement implements TableEditableElement {

	private ModificationListener modificationListener = new ModificationListener();
        protected final static I18n I18N = I18nFactory.getI18n(AbstractTableEditableElement.class);
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
			throw new EditableElementException(I18N.tr("The table cannot be saved"), e); //$NON-NLS-1$
		} catch (NonEditableDataSourceException e) {
			throw new EditableElementException(I18N.tr("This element cannot be saved"), e); //$NON-NLS-1$
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
