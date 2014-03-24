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
package org.orbisgis.view.docking.preferences.editors;

import bibliothek.extension.gui.dock.preference.PreferenceEditor;
import bibliothek.extension.gui.dock.preference.PreferenceEditorCallback;
import bibliothek.extension.gui.dock.preference.PreferenceEditorFactory;
import bibliothek.extension.gui.dock.preference.PreferenceOperation;
import bibliothek.util.Path;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;

/**
 * An editor that show error to the user.
 */
public class UserInformationEditor extends JLabel implements PreferenceEditor<String>{
    public static final Path TYPE_USER_INFO = new Path( "orbisgis.infolabel" );
    /**
     * A factory creating new {@link UserInformationEditor}s.
     */
    public static final PreferenceEditorFactory<String> FACTORY = new PreferenceEditorFactory<String>(){
        public PreferenceEditor<String> create() {
            return new UserInformationEditor();
        }
    };
    
    private String value;

    public UserInformationEditor() {
        this.setForeground(Color.red);
    }
    
    public void doOperation( PreferenceOperation operation ) {
        // does not declare any operations
    }

    public Component getComponent() {
        return this;
    }
    
    public void setCallback( PreferenceEditorCallback<String> callback ) {
        // The user cannot update the editor
    }

    public void setValue( String value ) {
        this.value = value;
        setText( String.valueOf( value ) );
    }

    public void setValueInfo( Object information ) {
        // ignore
    }
    
    public String getValue() {
        return value;
    }

}
