/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
import org.orbisgis.progress.ProgressMonitor;

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
	public void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		super.open(progressMonitor);
		element.addElementListener(listener);
		layer.addLayerListener(listener);
	}

	@Override
	public void close(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		super.close(progressMonitor);
		element.removeElementListener(listener);
		layer.removeLayerListener(listener);
	}

	private class IdChangeListener extends LayerListenerAdapter implements
			EditableElementListener, LayerListener {

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
