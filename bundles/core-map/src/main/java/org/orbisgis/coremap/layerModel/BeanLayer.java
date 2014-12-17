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
package org.orbisgis.coremap.layerModel;

import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.opengis.ows_context.LayerType;
import org.orbisgis.corejdbc.common.IntegerUnion;
import org.orbisgis.corejdbc.common.LongUnion;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.coremap.renderer.se.common.Description;


/**
 * This class provide bean access to properties and jaxb (de)serialisation
 */
public abstract class BeanLayer extends AbstractLayer {

        //Listener container
        protected transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

        //bean properties
        private Description description;
        protected List<Style> styleList = new ArrayList<Style>();
        protected LongUnion selection = new LongUnion();
        private boolean visible = true;
        private PropertyChangeListener styleListener = EventHandler.create(PropertyChangeListener.class,this,"onStyleChanged","");

        public BeanLayer(String name) {
                super();
                description = new Description();
                description.addTitle(Locale.getDefault(), name);
        }
        
	public BeanLayer(LayerType layerType) {
                super();
                try {
                        description = new Description(layerType);
                } catch (InvalidStyle ex) {
                        LOGGER.error(ex.getLocalizedMessage(), ex);
                }
	}

        /**
         * Get the value of visible
         *
         * @return the value of visible
         */
        @Override
        public boolean isVisible() {
                return visible;
        }

        /**
         * Set the value of visible
         *
         * @param visible new value of visible
         */
        @Override
        public void setVisible(boolean visible) throws LayerException  {
                boolean oldVisible = this.visible;
                this.visible = visible;
                propertyChangeSupport.firePropertyChange(PROP_VISIBLE, oldVisible, visible);
                //Deprecated listener
                fireVisibilityChanged();
        }

        /**
         * Get the value of description
         *
         * @return the value of description
         */
        @Override
        public Description getDescription() {
                return description;
        }

        /**
         * Set the value of description
         *
         * @param description new value of description
         */
        @Override
        public void setDescription(Description description) {
                Description oldDescription = this.description;
                this.description = description;
                propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION, oldDescription, description);
        }
    
        /**
        * Add a property-change listener for all properties.
        * The listener is called for all properties.
        * @param listener The PropertyChangeListener instance
        * Use EventHandler.create to build the PropertyChangeListener instance
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
        * Use EventHandler.create to build the PropertyChangeListener instance
        */
        @Override
        public void addPropertyChangeListener(String prop,PropertyChangeListener listener) {
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
        public void removePropertyChangeListener(String prop,PropertyChangeListener listener) {
                propertyChangeSupport.removePropertyChangeListener(prop,listener);
        }
        
	/**
	 * 
	 * @see org.orbisgis.coremap.layerModel.ILayer#getName()
	 */
        @Override
	public String getName() {
                String ret = description.getTitle(Locale.getDefault());
		return ret == null ? description.getDefaultTitle() : ret;
	}

	/**
	 * 
	 * @throws LayerException
	 * @see org.orbisgis.coremap.layerModel.ILayer#setName(java.lang.String)
	 */
        @Override
	public void setName(final String name) throws LayerException {
                if(name!=null && !name.equals(getName())) {
                        //Set the localised title of this layer
                        final Set<String> allLayersNames = getRoot().getAllLayersNames();
                        allLayersNames.remove(getName());
                        String unUsedName = provideNewLayerName(name, allLayersNames);                
                        description.addTitle(Locale.getDefault(), unUsedName);
                        setDescription(description);
                        fireNameChanged();
                }
	}
        
    @Override
    public List<Style> getStyles() {
        return Collections.unmodifiableList(styleList);
    }

    @Override
    public void setStyles(List<Style> fts) {
        List<Style> oldStyles = this.styleList;
        for(Style style : styleList) {
                removeStyleListener(style);
        }
        styleList = new ArrayList<Style>(fts);
        for(Style style : styleList) {
                addStyleListener(style);
        }
        propertyChangeSupport.firePropertyChange(PROP_STYLES, oldStyles, styleList);
        super.setStyles(styleList);
    }

    @Override
    public Style getStyle(int i){
            return styleList.get(i);
    }

    protected void removeStyleListener(Style s){
            s.removePropertyChangeListener(styleListener);
    }
    protected void addStyleListener(Style s) {
            s.addPropertyChangeListener(styleListener);
    }
    
    /***
     * The specified property has been updated
     * Called by the private style property change listener
     * @param evt 
     */
    public void onStyleChanged(PropertyChangeEvent evt) {
            fireStyleChanged();
            if(styleList!=null) {
                int index =styleList.indexOf(evt.getSource());
                if(index!=-1) {
                        propertyChangeSupport.fireIndexedPropertyChange(PROP_STYLES, index, evt.getSource(), evt.getSource());
                }
            }
    }
    
    @Override
    public void setStyle(int i, Style s){
        if (styleList == null){
            styleList = new ArrayList<Style>(); //out of bound exception instead of null pointer exception
        }
        Style oldStyle = styleList.get(i);
        removeStyleListener(oldStyle);
        styleList.set(i, s);
        addStyleListener(s);
        propertyChangeSupport.fireIndexedPropertyChange(PROP_STYLES, i, oldStyle, s);
        this.fireStyleChanged();
    }

    @Override
    public void addStyle(Style s){
        if (styleList == null){
            styleList = new ArrayList<Style>();
        }
        addStyle(styleList.size(),s);
    }

    @Override
    public void addStyle(int i, Style s){
        if (styleList == null){
            styleList = new ArrayList<Style>();
        }
        addStyleListener(s);
        styleList.add(i, s);
        propertyChangeSupport.fireIndexedPropertyChange(PROP_STYLES, i, null, s);
        this.fireStyleChanged();
    }

    @Override
    public void removeStyle(Style s){
            removeStyleListener(s);
            styleList.remove(s);
            propertyChangeSupport.firePropertyChange(PROP_STYLES, s, null);
            this.fireStyleChanged();
    }

    @Override
        public int indexOf(Style s) {
                return styleList == null ? -1 : styleList.indexOf(s);
        }

        @Override
        public Set<Long> getSelection() {
                return selection;
        }

        @Override
        public void setSelection(Set<Long> newSelection) {
                LongUnion oldSelection = selection;
                selection = new LongUnion(newSelection);
                propertyChangeSupport.firePropertyChange(PROP_SELECTION, oldSelection, selection);
        }
}
