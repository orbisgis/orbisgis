/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.map;

import com.vividsolutions.jts.geom.Envelope;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.map.TransformListener;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.edition.AbstractEditableElement;

/**
 * MapElement is an editable document that contain a Map Context
 */
public final class MapElement extends AbstractEditableElement implements
		TransformListener{
        public final static String EDITABLE_TYPE = "MapContext";
        private final static Logger LOGGER = Logger.getLogger("gui."+MapElement.class);
	private Boolean modified = false;
	private MapContext mapContext;
        Object jAXBObject; //The saved state of the MapContext
        private String mapId;
        
	public MapElement(MapContext mapContext) {
		this.mapContext = mapContext;
                mapId = String.valueOf(mapContext.getIdTime());
	}

	@Override
	public boolean isModified() {
            return modified;
	}

	@Override
	public void save() throws UnsupportedOperationException {
            jAXBObject = mapContext.getJAXBObject();
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
                    mapContext.addMapContextListener(null);
                } catch (LayerException ex) {
                    LOGGER.error(ex);
                } catch (IllegalStateException ex) {
                    LOGGER.error(ex);
                }
	}

	@Override
	public void close(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException {
		mapContext.close(progressMonitor);
                //TODO Remove listeners
                
	}

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

	@Override
	public void extentChanged(Envelope oldExtent, MapTransform mapTransform) {		
		if(oldExtent!=null) {
                        if(!mapTransform.getExtent().equals(oldExtent)){		
				//Extent update change the Map content
                                LOGGER.debug("Map move !"+mapTransform.getExtent());
				modified = true;
			}
		}
	}

	@Override
	public void imageSizeChanged(int oldWidth, int oldHeight,
			MapTransform mapTransform) {		
	}
}
