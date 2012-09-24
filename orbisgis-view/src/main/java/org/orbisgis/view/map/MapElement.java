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
package org.orbisgis.view.map;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerCollectionEvent;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.LayerListenerAdapter;
import org.orbisgis.core.layerModel.LayerListenerEvent;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.MapContextListener;
import org.orbisgis.core.layerModel.SelectionEvent;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.toc.Toc;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * MapElement is an editable document that contains a Map Context.
 * @note The source code, functionality is mainly provided by GeocognitionMapContext
 */
public final class MapElement extends EditableElement {
        public static final String EDITABLE_TYPE = "MapContext";
        private static final Logger LOGGER = Logger.getLogger("gui."+MapElement.class);
	private static final I18n I18N = I18nFactory.getI18n(MapElement.class);
        
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

        public File getMapContextFile() {
                return mapContextFile;
        }       
        
        /**
         * Set this element as modified
         */
        public void setModified() {
                setModified(true);
        }
        
        /**
         * Update the modified state
         * @param modified 
         */
        public void setModified(Boolean modified) {
            this.modified = modified;
        }
        
	@Override
	public void save() throws UnsupportedOperationException {
                try {
                        //Create folders if needed
                        File parentFolder = mapContextFile.getParentFile();
                        if(!parentFolder.exists()) {
                                parentFolder.mkdirs();
                        }
                        mapContext.write(new FileOutputStream(mapContextFile));
                } catch (FileNotFoundException ex) {
                        throw new UnsupportedOperationException(ex);
                }
                setModified(false);
	}

	@Override
	public void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException {
                setModified(false);
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
        /**
         * Return the edited map context
         * @return 
         */
        public MapContext getMapContext()  {
                return mapContext;
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
		return I18N.tr("MapContext - {0}",FilenameUtils.getBaseName(mapContextFile.getName()));
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
                setModified(true);
            }

            @Override
            public void visibilityChanged(LayerListenerEvent e) {
                setModified(true);
            }

            @Override
            public void styleChanged(LayerListenerEvent e) {      
                setModified(true);
            }

            @Override
            public void layerAdded(LayerCollectionEvent e) {
                for (final ILayer layer : e.getAffected()) {
                        layer.addLayerListenerRecursively(this);
                }
                setModified(true);
            }

            @Override
            public void layerRemoved(LayerCollectionEvent e) {
                for (final ILayer layer : e.getAffected()) {
                        layer.removeLayerListenerRecursively(this);
                }
                setModified(true);
            }

            @Override
            public void layerMoved(LayerCollectionEvent e) {
                setModified(true);
            }

            @Override
            public void selectionChanged(SelectionEvent e) {
                setModified(true);
            }
            
        }
}
