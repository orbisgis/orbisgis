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
package org.orbisgis.view.map;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.*;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.edition.AbstractEditableElement;
import org.orbisgis.view.toc.Toc;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * MapElement is an editable document that contains a Map Context.
 * @note The source code, functionality is mainly provided by GeocognitionMapContext
 */
public final class MapElement extends AbstractEditableElement {
        public static final String EDITABLE_TYPE = "MapContext";
        private static final Logger LOGGER = Logger.getLogger("gui."+MapElement.class);
	private static final I18n I18N = I18nFactory.getI18n(MapElement.class);
        
        private Boolean modified = false;
	private MapContext mapContext;
        private MapEditor editor;
        private String mapId;
        private MapContextListener updateListener = EventHandler.create(MapContextListener.class, this , "setModified");
        private PropertyChangeListener mapContextPropertyUpdateListener =
                EventHandler.create(PropertyChangeListener.class, this , "setModified");
        private LayerUpdateListener layerUpdateListener = new LayerUpdateListener();
        private File mapContextFile;
        
	public MapElement(MapContext mapContext,File mapContextFile) {
		this.mapContext = mapContext;
                this.mapContextFile = mapContextFile;
                mapId = String.valueOf(mapContext.getIdTime());
	}

        /**
         * Update the modified state
         * @param modified 
         */
        public void setModified(Boolean modified) {
            this.modified = modified;
        }

        /**
         * Mark this MapContext as modified
         */
        public void setModified() {
            setModified(true);
        }
	@Override
	public boolean isModified() {
            return modified;
	}

	@Override
	public void save() throws UnsupportedOperationException {
                try {
                        mapContext.write(new FileOutputStream(mapContextFile));
                } catch (FileNotFoundException ex) {
                        throw new UnsupportedOperationException(ex);
                }
                modified = false;
                fireSave();
	}

	@Override
	public void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException {
                //TODO add Edition Listener
		modified = false;
                try {
                    mapContext.open(progressMonitor);
                    mapContext.addMapContextListener(updateListener);
                    mapContext.addPropertyChangeListener(mapContextPropertyUpdateListener);
                    mapContext.getLayerModel().addLayerListenerRecursively(layerUpdateListener);
                } catch (LayerException ex) {
                    LOGGER.error(I18N.tr("Unable to load the map context"),ex);
                } catch (IllegalStateException ex) {
                    LOGGER.error(I18N.tr("Unable to load the map context"),ex);
                }
	}

	@Override
	public void close(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException {
		mapContext.close(progressMonitor);
                mapContext.removeMapContextListener(updateListener);
                mapContext.removePropertyChangeListener(mapContextPropertyUpdateListener);
                mapContext.getLayerModel().removeLayerListenerRecursively(layerUpdateListener);
                
	}

        @Override
        public String getId() {
            return mapId;
        }

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return mapContext;
	}

	@Override
	public String getTypeId() {
		return EDITABLE_TYPE;
	}

	@Override
	public String toString() {
		return getId();
	}

        /**
         * Gets the editor linked to this {@code MapElement}. This is needed
         * in order to be able to retrieve all the informations about the map
         * from the {@link Toc}
         * @return
         */
        public MapEditor getMapEditor(){
                return editor;
        }

        /**
         * Sets the editor linked to this {@code MapElement}. This is needed
         * in order to be able to retrieve all the informations about the map
         * from the {@link Toc}
         * @param edit
         */
        public void setMapEditor(MapEditor edit){
                editor = edit;
        }

        /**
         * Set the editable map as modified when the layer model change
         */
        private class LayerUpdateListener extends LayerListenerAdapter {

            @Override
            public void nameChanged(LayerListenerEvent e) {
                setModified();
            }

            @Override
            public void visibilityChanged(LayerListenerEvent e) {
                setModified();
            }

            @Override
            public void styleChanged(LayerListenerEvent e) {                
                
                setModified();
            }

            @Override
            public void layerAdded(LayerCollectionEvent e) {
                for (final ILayer layer : e.getAffected()) {
                        layer.addLayerListenerRecursively(this);
                }
                setModified();
            }

            @Override
            public void layerRemoved(LayerCollectionEvent e) {
                for (final ILayer layer : e.getAffected()) {
                        layer.removeLayerListenerRecursively(this);
                }
                setModified();
            }

            @Override
            public void layerMoved(LayerCollectionEvent e) {
                setModified();
            }

            @Override
            public void selectionChanged(SelectionEvent e) {
                setModified();
            }
            
        }
}
