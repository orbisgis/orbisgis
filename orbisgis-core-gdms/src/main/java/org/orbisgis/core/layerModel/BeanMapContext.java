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
package org.orbisgis.core.layerModel;

import com.vividsolutions.jts.geom.Envelope;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.common.Description;

/**
 * Define Map Context properties as Java Beans, add the ability to
 * listen for property change.
 */
public abstract class BeanMapContext implements MapContext {

        //Listener container
        protected transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
        //Properties
        protected Envelope boundingBox = null;
        protected ILayer[] selectedLayers = new ILayer[]{};
        protected Style[] selectedStyles = new Style[]{};
        protected ILayer activeLayer = null;
        protected ILayer layerModel;
        protected int epsg_code = -1;
        protected Description description = new Description();
        
        @Override
        public Description getDescription() {
                return description;
        }
        
        @Override
        public void setDescription(Description description) {
                Description oldDescription = this.description;
                this.description = description;
                propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION, oldDescription, description);
        }
        
        @Override
        public String getTitle() {
                return description.getDefaultTitle();
        }

        /**
         * Get the value of the EPSG code
         *
         * @return the value of the EPSG code
         */
        public int getCoordinateReferenceSystem() {
                return epsg_code;
        }

        /**
         * Set the value of the EPSG code
         *
         * @param epsg new value of the EPSG code
         */
        public void setCoordinateReferenceSystem(int epsg) {
                int oldEPSG_code = this.epsg_code;
                this.epsg_code = oldEPSG_code;
                propertyChangeSupport.firePropertyChange(PROP_COORDINATEREFERENCESYSTEM, oldEPSG_code, epsg);
        }
       
        
        /**
         * Get the value of layerModel
         *
         * @return the value of layerModel
         */
        @Override
        public ILayer getLayerModel() {
                return layerModel;
        }

        /**
         * Set the value of layerModel
         *
         * @param layerModel new value of layerModel
         */
        protected void setLayerModel(ILayer layerModel) {
                ILayer oldLayerModel = this.layerModel;
                this.layerModel = layerModel;
                propertyChangeSupport.firePropertyChange(PROP_LAYERMODEL, oldLayerModel, layerModel);
        }

        /**
         * Get the value of activeLayer
         *
         * @return the value of activeLayer
         */
        @Override
        public ILayer getActiveLayer() {
                return activeLayer;
        }

        /**
         * Set the value of activeLayer
         *
         * @param activeLayer new value of activeLayer
         */
        @Override
        public void setActiveLayer(ILayer activeLayer) {
                ILayer oldActiveLayer = this.activeLayer;
                this.activeLayer = activeLayer;
                propertyChangeSupport.firePropertyChange(PROP_ACTIVELAYER, oldActiveLayer, activeLayer);
        }

        /**
         * sourceManager.addSourceListener(listener); Get the value of
         * selectedStyles
         *
         * @return the value of selectedStyles
         */
        @Override
        public Style[] getSelectedStyles() {
                return selectedStyles;
        }

        /**
         * Set the value of selectedStyles
         *
         * @param selectedStyles new value of selectedStyles
         */
        @Override
        public void setSelectedStyles(Style[] selectedStyles) {
                Style[] oldSelectedStyles = this.selectedStyles;
                this.selectedStyles = selectedStyles;
                propertyChangeSupport.firePropertyChange(PROP_SELECTEDSTYLES, oldSelectedStyles, selectedStyles);
        }

        /**
         * Get the value of selectedLayers
         *
         * @return the value of selectedLayers
         */
        @Override
        public ILayer[] getSelectedLayers() {
                return selectedLayers;
        }

        /**
         * Set the value of selectedLayers
         *
         * @param selectedLayers new value of selectedLayers
         */
        @Override
        public void setSelectedLayers(ILayer[] selectedLayers) {
                ILayer[] oldSelectedLayers = this.selectedLayers;
                this.selectedLayers = selectedLayers;
                propertyChangeSupport.firePropertyChange(PROP_SELECTEDLAYERS, oldSelectedLayers, selectedLayers);
        }

        /**
         * Get the value of boundingBox
         *
         * @return the value of boundingBox
         */
        @Override
        public Envelope getBoundingBox() {
                return boundingBox;
        }

        /**
         * Set the value of boundingBox
         *
         * @param bBox new value of boundingBox
         */
        @Override
        public void setBoundingBox(Envelope bBox) {
                if ((bBox == null && boundingBox != null)
                                || (bBox != null && !bBox.equals(this.boundingBox))) {
                        Envelope oldBoundingBox = this.boundingBox;
                        this.boundingBox = bBox;
                        propertyChangeSupport.firePropertyChange(PROP_BOUNDINGBOX, oldBoundingBox, bBox);
                }
        }

        /**
         * Add a property-change listener for all properties.
         * The listener is called for all properties.
         * @param listener The PropertyChangeListener instance
         * @note Use EventHandler.create to build the PropertyChangeListener instance
         */
        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
                propertyChangeSupport.addPropertyChangeListener(listener);
        }

        /**
         * Add a property-change listener for a specific property.
         * The listener is called only when there is a change to
         * the specified property.
         * @param prop The static property name PROP_..
         * @param listener The PropertyChangeListener instance
         * @note Use EventHandler.create to build the PropertyChangeListener instance
         */
        @Override
        public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
                propertyChangeSupport.addPropertyChangeListener(prop, listener);
        }

        /**
         * Remove the specified listener from the list
         * @param listener The listener instance
         */
        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
                propertyChangeSupport.removePropertyChangeListener(listener);
        }

        /**
         * Remove the specified listener for a specified property from the list
         * @param prop The static property name PROP_..
         * @param listener The listener instance
         */
        @Override
        public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
                propertyChangeSupport.removePropertyChangeListener(prop, listener);
        }

}
