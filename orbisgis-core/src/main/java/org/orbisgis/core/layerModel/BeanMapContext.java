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
package org.orbisgis.core.layerModel;

import com.vividsolutions.jts.geom.Envelope;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.orbisgis.core.renderer.se.Style;

/**
 * Define Map Context properties as Java Beans, add the ability to
 * listen for property change.
 */
public abstract class BeanMapContext implements MapContext {

    //Listener container
    protected transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    //Properties
    protected Envelope boundingBox = null;
    protected ILayer[] selectedLayers = new ILayer[] {};
    protected Style[] selectedStyles = new Style[] {};
    protected ILayer activeLayer = null;
    protected ILayer layerModel;

    /**
     * Get the value of layerModel
     *
     * @return the value of layerModel
     */
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
    public ILayer getActiveLayer() {
        return activeLayer;
    }

    /**
     * Set the value of activeLayer
     *
     * @param activeLayer new value of activeLayer
     */
    public void setActiveLayer(ILayer activeLayer) {
        ILayer oldActiveLayer = this.activeLayer;
        this.activeLayer = activeLayer;
        propertyChangeSupport.firePropertyChange(PROP_ACTIVELAYER, oldActiveLayer, activeLayer);
    }

    /**
        sourceManager.addSourceListener(listener);
     * Get the value of selectedStyles
     *
     * @return the value of selectedStyles
     */
    public Style[] getSelectedStyles() {
        return selectedStyles;
    }

    /**
     * Set the value of selectedStyles
     *
     * @param selectedStyles new value of selectedStyles
     */
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
    public ILayer[] getSelectedLayers() {
        return selectedLayers;
    }

    /**
     * Set the value of selectedLayers
     *
     * @param selectedLayers new value of selectedLayers
     */
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
    public Envelope getBoundingBox() {
        return boundingBox;
    }

    /**
     * Set the value of boundingBox
     *
     * @param boundingBox new value of boundingBox
     */
    @Override
    public void setBoundingBox(Envelope boundingBox) {
        if(!boundingBox.equals(this.boundingBox)) {
                Envelope oldBoundingBox = this.boundingBox;
                this.boundingBox = boundingBox;
                propertyChangeSupport.firePropertyChange(PROP_BOUNDINGBOX, oldBoundingBox, boundingBox);
        }
    }

    
    /**
    * Add a property-change listener for all properties.
    * The listener is called for all properties.
    * @param listener The PropertyChangeListener instance
    * @note Use EventHandler.create to build the PropertyChangeListener instance
    */
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
    public void addPropertyChangeListener(String prop,PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(prop, listener);
    }
    /**
    * Remove the specified listener from the list
    * @param listener The listener instance
    */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
    * Remove the specified listener for a specified property from the list
    * @param prop The static property name PROP_..
    * @param listener The listener instance
    */
    public void removePropertyChangeListener(String prop,PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(prop,listener);
    }
    
}
