/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.mapeditorapi;

import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.LayerCollectionEvent;
import org.orbisgis.coremap.layerModel.LayerException;
import org.orbisgis.coremap.layerModel.LayerListenerAdapter;
import org.orbisgis.coremap.layerModel.LayerListenerEvent;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.layerModel.OwsMapContext;
import org.orbisgis.coremap.layerModel.SelectionEvent;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.sif.edition.AbstractEditableElement;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * MapElement is an editable document that contains a Map Context.
 * The source code, functionality is mainly provided by GeocognitionMapContext
 */
public final class MapElement extends AbstractEditableElement {
        public static final String EDITABLE_TYPE = "MapContext";
        private static final Logger LOGGER = LoggerFactory.getLogger("gui." + MapElement.class);
	private static final I18n I18N = I18nFactory.getI18n(MapElement.class);

	// The following events does not change the MapElement modification state.
    private Set<String> ignoredModificationEvents = new HashSet<String>(Arrays.asList(
            new String[]{MapContext.PROP_ACTIVELAYER, MapContext.PROP_SELECTEDLAYERS, MapContext.PROP_SELECTEDSTYLES}));
        
	private MapContext mapContext;
        private String mapId;
        private PropertyChangeListener mapContextPropertyUpdateListener =
                EventHandler.create(PropertyChangeListener.class, this , "onMapUpdate","");
        private LayerUpdateListener layerUpdateListener = new LayerUpdateListener();
        private File mapContextFile;
        
	public MapElement(MapContext mapContext,File mapContextFile) {
                if(mapContext == null) {
                    throw new IllegalArgumentException("MapContext argument cannot be null");
                }
		        this.mapContext = mapContext;
                this.mapContextFile = mapContextFile;
                mapId = String.valueOf(mapContext.getIdTime());
	}

    /**
     * Constructor that read the provided map context file
     * @param mapContextFile Xml file
     * @param manager Where to register MapContext URI
     */
    public MapElement(File mapContextFile, DataManager manager) {
        mapContext = new OwsMapContext(manager);
        try {
            mapContext.read(new FileInputStream(mapContextFile));
            mapContext.setLocation(mapContextFile.toURI());
        } catch (FileNotFoundException | IllegalArgumentException ex) {
            LOGGER.error(I18N.tr("The saved map context cannot be read, starting with an empty map context."), ex);
        }
        this.mapContextFile = mapContextFile;
        mapId = String.valueOf(mapContext.getIdTime());
    }
        /**
         * Use the EditorManager service and search for the first available editable map.
         * @return The map context or null if it is not found.
         */
        public static MapElement fetchFirstMapElement(EditorManager editorManager) {
                for (EditableElement editable : editorManager.getEditableElements()) {
                        if (editable instanceof MapElement) {
                                return (MapElement)editable;
                        }
                }
                return null;
        }
        
        public File getMapContextFile() {
                return mapContextFile;
        }       
        
        /**
         * Set this element as modified
         */
        public void onMapUpdate(PropertyChangeEvent evt) {
                if(!ignoredModificationEvents.contains(evt.getPropertyName())) {
                    setModified(true);
                }
        }
        
        /**
         * Update the modified state
         * @param modified 
         */
        public void setModified(Boolean modified) {
            this.modified = modified;
        }        
        private boolean hasTemporaryTables() {
                try {
                    try (Connection connection = mapContext.getDataManager().getDataSource().getConnection()) {
                        for (ILayer layer : mapContext.getLayers()) {
                            String table = layer.getTableReference();
                            if (table != null && !table.isEmpty()) {
                                if (JDBCUtilities.isTemporaryTable(connection, table)) {
                                    return true;
                                }
                            }
                        }
                    }
                } catch (SQLException ex) {
                    LOGGER.error(I18N.tr("Error while checking temporary table"));
                }
                return false;
        }
        
	@Override
	public void save() throws UnsupportedOperationException {
                // If a layer hold a not well known source then alert the user
                boolean doSave = true;
                if(hasTemporaryTables()) {
                        int response = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(),
                                I18N.tr("Some layers use temporary table, are you sure to save this map and loose layers with temporary tables ?"),
                                I18N.tr("Temporary layers data source"),
                                JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                        if(response == JOptionPane.NO_OPTION) {
                                doSave = false;
                        }
                }
                if(doSave) {
                        // Save MapContext
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
                        if(doSave) {
                            setModified(false);
                        }
                }
	}

	@Override
	public void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException {
                setModified(false);
                try {
                    mapContext.open(progressMonitor);
                    mapContext.addPropertyChangeListener(mapContextPropertyUpdateListener);
                    setListeners(mapContext.getLayerModel());
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
        mapContext.removePropertyChangeListener(mapContextPropertyUpdateListener);
        removeListeners(mapContext.getLayerModel());
	}
    private void setListeners(ILayer layer) {
        if(layer==null) {
            return;
        }
        layer.addLayerListener(layerUpdateListener);
        ILayer[] layers = layer.getLayersRecursively();
        if(layers!=null) {
            for(ILayer subLayer : layers) {
                setListeners(subLayer);
            }
        }
    }
    private void removeListeners(ILayer layer) {
        if(layer==null) {
            return;
        }
        layer.removeLayerListener(layerUpdateListener);
        ILayer[] layers = layer.getLayersRecursively();
        if(layers!=null) {
            for(ILayer subLayer : layers) {
                removeListeners(subLayer);
            }
        }
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
                        setListeners(layer);
                }
                setModified(true);
            }

            @Override
            public void layerRemoved(LayerCollectionEvent e) {
                for (final ILayer layer : e.getAffected()) {
                        removeListeners(layer);
                }
                setModified(true);
            }

            @Override
            public void layerMoved(LayerCollectionEvent e) {
                setModified(true);
            }

            @Override
            public void selectionChanged(SelectionEvent e) {
                // row selection is not serialised in MapContext
            }
            
        }
}
