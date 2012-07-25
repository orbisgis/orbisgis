/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
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
                if (ds.isEditable()) {
                        ds.removeEditionListener(modificationListener);
                        ds.removeMetadataEditionListener(modificationListener);
                }
	}

	@Override
	public void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		DataSource ds = getDataSource();
                if (ds.isEditable()) {
                        ds.addEditionListener(modificationListener);
                        ds.addMetadataEditionListener(modificationListener);
                }
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
