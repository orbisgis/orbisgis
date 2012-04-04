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
package org.orbisgis.core.ui.pluginSystem;

import java.util.Observer;

/**
 * From OpenJump Project
 * 
 * <p> Plug-ins are code modules that can be easily added to or
 * removed from OrbisGIS Workbench. For example, each menu item in the
 * OrbisGIS Workbench is a PlugIn. Typically plug-ins are executed with a
 * menu item -- FeatureInstaller has methods for adding plug-ins as
 * menu items. Alternatively, a plug-in need not be associated with a
 * menu-item; it might, for example, simply run some code when the
 * Workbench starts up.
 * </p>
 *
 * <p>
 * "Built-in" plug-ins are configured in a Setup class. Third-party plug-ins reside
 * in a JAR file that also contains an Extension class that configures them.
 * During development, third-party plug-ins may be specified in the
 * workbench-properties.xml file, to avoid having to build a JAR file.
 * </p>
 * 
 * @see org.orbisgis.core.ui.pluginSystem.workbench.Setup
 * @see Extension
 * @see PlugInManager
 */

public interface PlugIn extends Observer {
	
	public void initialize(PlugInContext context) throws Exception;

	boolean execute(PlugInContext context) throws Exception;

	boolean isEnabled() throws Exception;
	
	boolean isSelected();
	
	String getName();
}
