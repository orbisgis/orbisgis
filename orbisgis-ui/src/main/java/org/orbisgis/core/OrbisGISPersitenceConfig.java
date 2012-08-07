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
package org.orbisgis.core;

/**
 * 
 * List of files used for the persistence.
 * 
 */
public class OrbisGISPersitenceConfig {

	public static final String LAYOUT_PERSISTENCE_FILE = "org.orbisgis.core.ui.ViewLayout.obj";

	public static final String WINDOW_PERSISTENCE_FILE = "org.orbisgis.core.ui.window.persistence";
	public static final String WINDOW_CREATED_FILE = "windows.xml";

	public static final String GEOCATALOG_PERSISTENCE_FILE = "org.orbisgis.core.ui.plugins.views.geocatalog.persistence";

	public static final String GEOCOGNITION_CREATED_FILE = "org.orbisgis.core.ui.Geocognition.xml";
	public static final String GEOCOGNITION_PERSISTENCE_FILE = "org.orbisgis.core.geocognition.persistence";

	public static final String DEFAULT_BASIC_CONFIGURATION = "org.orbisgis.core.configuration.properties";

	public static final String GEOCOGNITION_MAPCONTEXT_FACTORY_ID = "org.orbisgis.core.geocognition.MapContext";

	public static final String GEOCONGITION_CUSTOMQUERY_FACTORY_ID = "org.orbisgis.core.geocognition.BuiltInCustomQuery";

	public static final String GEOCOGNITION_FUNCTION_FACTORY_ID = "org.orbisgis.core.geocognition.BuiltInFunction";

	public static final String GEOCOGNITION_SYMBOL_FACTORY_ID = "org.orbisgis.core.geocognition.Symbol";

	public static final String GEOCOGNITION_FOLDER_ELEMENT_ID = "org.orbisgis.core.geocognition.Folder";
}
