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
package org.orbisgis.view.geocatalog;


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
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditableElementException;
import org.orbisgis.view.table.Selection;

public class EditableSource extends EditableElement {

	public static final String EDITABLE_RESOURCE_TYPE = "EditableSource";

	private String sourceName;
        private boolean editing = false;
	private DataSource ds;
	private SourceSelection resourceSelection = new SourceSelection();

	private NameChangeSourceListener listener = new NameChangeSourceListener();

	public EditableSource(String sourceName) {
                id = sourceName;
                setId(sourceName);
	}

        @Override
	public void close(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
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
	public void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		try {
			DataManager dataManager = Services.getService(DataManager.class);
			if (ds == null) {
				DataSourceFactory dsf = dataManager.getDataSourceFactory();
				ds = dsf.getDataSource(sourceName);
			}
			ds.open();

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

	public DataSource getDataSource() {
		return ds;
	}

	public Selection getSelection() {
		return resourceSelection;
	}

	public boolean isEditable() {
		if (ds.getSource().isSystemTableSource() || ds.getSource().isLiveSource()) {
			return false;
		}
		return editing && ds.isEditable();
	}

	public MapContext getMapContext() {
		return null;
	}

        /**
         * @return the Editing
         */
        public boolean isEditing() {
                return editing;
        }

        /**
         * @param Editing the Editing to set
         */
        public void setEditing(boolean Editing) {
                this.editing = Editing;
        }

        @Override
        public void save() throws UnsupportedOperationException, EditableElementException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object getObject() throws UnsupportedOperationException {
                return ds;
        }

	private class NameChangeSourceListener implements SourceListener {

                @Override
		public void sourceAdded(SourceEvent e) {
		}

                @Override
		public void sourceNameChanged(SourceEvent e) {
			sourceName = e.getNewName();
                        setId(sourceName);
		}

                @Override
		public void sourceRemoved(SourceRemovalEvent e) {
		}
	}
}