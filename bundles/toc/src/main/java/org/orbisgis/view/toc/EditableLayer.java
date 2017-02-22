/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.view.toc;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sif.edition.AbstractEditableElement;
import org.orbisgis.sif.edition.EditableElementException;

/**
 * This editable correspond to a Layer on the {@link Toc}.
 */
public class EditableLayer extends AbstractEditableElement {

	public static final String EDITABLE_LAYER_TYPE = "EditableLayer";

	private ILayer layer;
	private MapElement element;
	private MapContext mapContext;

	private PropertyChangeListener layerIdListener = EventHandler.create(PropertyChangeListener.class,this,"updateId");

	public EditableLayer(MapElement element, ILayer layer) {
		this.layer = layer;
		this.element = element;
		this.mapContext = (MapContext) element.getObject();
                updateId();
	}

        public ILayer getLayer() {
            return layer;
        }
        
        private void updateId() {
                setId(element.getId() + ":" + layer.getName());                
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

	public MapContext getMapContext() {
		return mapContext;
	}

	@Override
	public void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		layer.addPropertyChangeListener(ILayer.PROP_DESCRIPTION,layerIdListener);
	}

	@Override
	public void close(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		layer.removePropertyChangeListener(layerIdListener);
	}

        @Override
        public void save() throws UnsupportedOperationException, EditableElementException {
                element.save();
        }

        @Override
        public Object getObject() throws UnsupportedOperationException {
                return layer;
        }
}
