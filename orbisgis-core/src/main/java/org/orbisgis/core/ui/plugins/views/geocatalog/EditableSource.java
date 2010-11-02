/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */

package org.orbisgis.core.ui.plugins.views.geocatalog;

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

	private boolean isSystemTable;

	public EditableSource(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getId() {
		return sourceName;
	}

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

	public String getTypeId() {
		return EDITABLE_RESOURCE_TYPE;
	}

	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		try {
			DataManager dataManager = Services.getService(DataManager.class);
			if (ds == null) {
				DataSourceFactory dsf = dataManager.getDataSourceFactory();
				isSystemTable = dsf.getSourceManager().getSource(sourceName)
						.isSystemTableSource();
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

	public boolean equals(Object obj) {
		if (obj instanceof EditableSource) {
			EditableSource er = (EditableSource) obj;
			return sourceName.equals(er.sourceName);
		} else {
			return false;
		}
	}

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
		if (isSystemTable) {
			return false;
		}
		return ds.isEditable();
	}

	public MapContext getMapContext() {
		return null;
	}

	private class NameChangeSourceListener implements SourceListener {

		public void sourceAdded(SourceEvent e) {
		}

		public void sourceNameChanged(SourceEvent e) {
			sourceName = e.getNewName();
			fireIdChanged();
		}

		public void sourceRemoved(SourceRemovalEvent e) {
		}
	}
}
