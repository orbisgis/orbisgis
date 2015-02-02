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

import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.extension.gui.dock.preference.model.BubbleThemePreferenceModel;
import bibliothek.extension.gui.dock.preference.model.ButtonContentPreferenceModel;
import bibliothek.extension.gui.dock.preference.model.EclipseThemePreferenceModel;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CPreferenceModel;
import bibliothek.gui.dock.common.preference.CKeyStrokePreferenceModel;
import bibliothek.gui.dock.common.preference.CLayoutPreferenceModel;
import bibliothek.util.Path;
import bibliothek.util.PathCombiner;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Specific OrbisGIS preferences.
 */
public class OrbisGISPreferenceTreeModel extends PreferenceTreeModel {
    private static final I18n I18N = I18nFactory.getI18n(OrbisGISPreferenceTreeModel.class);
        /**
     * Creates a new model. This constructor sets the behavior of how to
     * create paths for preferences to {@link PathCombiner#SECOND}. This
     * behavior allows reordering of models and preferences in future releases,
     * however forces any preference to have a truly unique path in a global
     * scale.
     * @param control the control whose settings can be changed by this model
     */
    public OrbisGISPreferenceTreeModel( CControl control ){
        this( control, PathCombiner.APPEND );
        
    }
    
    /**
     * Creates a new model.
     * @param control the control whose settings can be changed by this model
     * @param combiner how to combine paths of models and of preferences
     * @see CPreferenceModel for the built-in preference model
     */
    public OrbisGISPreferenceTreeModel( CControl control, PathCombiner combiner ){
        super( combiner, control.getController() );
        DockController controller = control.intern().getController();
        //Linked, we use the DockingFrames I18N
        //Taken from CPreferenceModel, but with a specific root node
        putNode(new Path( "windows"),I18N.tr("Windows"));
        putLinked( new Path( "windows.shortcuts" ), "preference.shortcuts", new CKeyStrokePreferenceModel( controller.getProperties() ) );
        putLinked( new Path( "windows.buttonContent" ), "preference.buttonContent", new ButtonContentPreferenceModel( controller ) );
        putLinked( new Path( "windows.layout" ), "preference.layout", new CLayoutPreferenceModel( control ));
        putLinked( new Path( "windows.layout.BubbleTheme" ), "theme.bubble", new BubbleThemePreferenceModel( controller.getProperties() ));
        putLinked( new Path( "windows.layout.EclipseTheme" ), "theme.eclipse", new EclipseThemePreferenceModel( controller.getProperties() ));
        //Custom properties
        putNode(new Path( "web"),I18N.tr("Web configuration"));
        put(new Path( "web.proxy" ),I18N.tr("Proxy"),new ProxyPreferenceModel(controller).initListeners());
        
    }
}
