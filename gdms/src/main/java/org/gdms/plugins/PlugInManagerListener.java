/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.plugins;

/**
 * Listener for plug-in loading/unloading.
 * @author Antoine Gourlay
 * @since 2.0
 */
public interface PlugInManagerListener {
        
        /**
         * Called when an instance of a plug-in class is about to be created.
         * @param name the name of the plug-in class
         * @return false if the plug-in must not be loaded, true otherwise
         */
        boolean pluginLoading(String name);
        
        /**
         * Called when a plug-in has been loaded.
         * @param p the plug-in instance
         */
        void pluginLoaded(GdmsPlugIn p);
        
        /**
         * Called when a plug-in is about to be unloaded.
         * @param p the plug-in instance
         */
        void pluginUnloading(GdmsPlugIn p);
}
