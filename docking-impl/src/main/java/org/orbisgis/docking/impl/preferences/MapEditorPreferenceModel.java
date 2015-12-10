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
package org.orbisgis.docking.impl.preferences;

import bibliothek.extension.gui.dock.preference.DefaultPreference;
import bibliothek.extension.gui.dock.preference.DefaultPreferenceModel;
import bibliothek.extension.gui.dock.preference.Preference;
import bibliothek.extension.gui.dock.preference.PreferenceListener;
import bibliothek.extension.gui.dock.preference.preferences.DockPropertyPreference;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.util.Path;
import java.awt.Color;
import java.beans.EventHandler;
import org.orbisgis.docking.impl.preferences.editors.UserInformationEditor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * A panel to set some preferences for the MapEditor
 * 
 * @author Erwan Bocher
 */


public class MapEditorPreferenceModel extends DefaultPreferenceModel{
    
    private static interface StringPreferenceListener extends PreferenceListener<String>{};

    private static final I18n I18N = I18nFactory.getI18n(MapEditorPreferenceModel.class);
    private final String DEFAULT_BACKGROUNDCOLOR = "#FFFFFF";
     private String oldBackgroundColor = DEFAULT_BACKGROUNDCOLOR;
    
    private static final String MAPEDITOR_BACKGROUNDCOLOR_KEY = "map.editor.color.background";
    private static final String USE_VALUE_ANTIALIAS_KEY = "map.editor.renderer.value_antialias_on";
    private static final String MAPEDITOR_LABEL_KEY = "map.editor.mapeditorlabel";
    private DefaultPreference<String> mapEditorInfo;
    private DockPropertyPreference<Boolean> useAntialiasOn;
    
    //Background color
    public static final PropertyKey<String> MAPEDITOR_BACKGROUNDCOLOR = 
        new PropertyKey<String>( MAPEDITOR_BACKGROUNDCOLOR_KEY,
        		new ConstantPropertyFactory<String>("#FFFFFF"), true );  
    private final DockPropertyPreference<String> backgroundColor;
    
    
    public static final PropertyKey<Boolean> VALUE_ANTIALIAS_ON = 
        new PropertyKey<Boolean>( USE_VALUE_ANTIALIAS_KEY,
        		new ConstantPropertyFactory<Boolean>( true ), true );
    
    
    private boolean skipEvent = false; //Skip event while update values
    
    public MapEditorPreferenceModel(DockController controller) {
        super(controller);        
        
        //Message Label
        mapEditorInfo = new UnsavedPreference<String>("", UserInformationEditor.TYPE_USER_INFO, new Path(MAPEDITOR_LABEL_KEY));
        mapEditorInfo.setDefaultValue("");
        mapEditorInfo.setLabel("");
        this.add(mapEditorInfo);
        
        //Background Color
        backgroundColor = new DockPropertyPreference<String>(controller.getProperties(),MAPEDITOR_BACKGROUNDCOLOR, Path.TYPE_STRING_PATH, new Path(MAPEDITOR_BACKGROUNDCOLOR_KEY));
        backgroundColor.setLabel(I18N.tr("Background color"));
        backgroundColor.setDefaultValue(DEFAULT_BACKGROUNDCOLOR);
        this.add(backgroundColor);
        
        //Use antialiasing 
        useAntialiasOn = new DockPropertyPreference<Boolean>(controller.getProperties(),VALUE_ANTIALIAS_ON, Path.TYPE_BOOLEAN_PATH, new Path(USE_VALUE_ANTIALIAS_KEY));
        useAntialiasOn.setLabel(I18N.tr("Geometry antialiasing"));
        useAntialiasOn.setDefaultValue(Boolean.TRUE);
        this.add(useAntialiasOn);        
    }
    
    
    /**
     * Init listeners
     * @return 
     */
    public MapEditorPreferenceModel initListeners() {        
        backgroundColor.addPreferenceListener(EventHandler.create(StringPreferenceListener.class, this,"onUserSetColorChange",""));        
        useAntialiasOn.addPreferenceListener(EventHandler.create(PreferenceListener.class, this,"onUseAntialias","")); 
        return this;
    }
    
    
    /**
     * User update the background color and renderer value
     * @param preference Updated preference
     */
    public void onUserSetColorChange(Preference<String> preference) {
        if(skipEvent) {
            return;
        }
        try {
            Color color = Color.decode(preference.getValue().trim());            
            if (color==null) {
                skipEvent = true;
                mapEditorInfo.setValue(I18N.tr("The background colour is invalid"));
                preference.setValue(oldBackgroundColor);
            }
            
        } catch (NumberFormatException e) {
            skipEvent = true;
            mapEditorInfo.setValue(I18N.tr("The background colour must be specified in HTML notation  : #aabbcc"));
            preference.setValue(oldBackgroundColor);
            return;
        }
        finally { 
            skipEvent = false;
        }
        mapEditorInfo.setValue("");
        oldBackgroundColor = preference.getValue();
        System.setProperty(MAPEDITOR_BACKGROUNDCOLOR_KEY, oldBackgroundColor);
        preference.setValue(oldBackgroundColor);
     }
    
    /**
     * Update the system properties
     *
     * @param preference
     */
    public void onUseAntialias(Preference<Boolean> preference) {
        System.setProperty(USE_VALUE_ANTIALIAS_KEY, String.valueOf(useAntialiasOn.getValue()));
    }
    
    
    
}
