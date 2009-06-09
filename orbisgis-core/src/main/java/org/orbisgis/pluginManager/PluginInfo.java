/*
 * GELAT is a Geographic information system focused in geoprocessing.
 * It's able to manipulate and create vector and raster spatial information. GELAT
 * is distributed under GPL 3 license.
 *
 * Copyright (C) 2009 Fernando GONZALEZ CORTES, Victor GONZALEZ CORTES
 *
 * This file is part of GELAT.
 *
 * GELAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GELAT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GELAT. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://gelat.forge.osor.eu/>
 *
 * or contact directly fergonco _at_ gmail.com

	Some part has been imported from GearScape a fork of orbisgis
 */


/**
 *
 */
package org.orbisgis.pluginManager;

import java.io.File;
import java.io.IOException;

public interface PluginInfo {

	byte[] getDescriptorStream() throws IOException;

	File[] getOutputFolders();

	File[] getJars();

	/**
	 * Gets the specified schema.
	 *
	 * @param schemaRelativePath
	 *            Path relative to the plugin configuration path or folder
	 * @return
	 * @throws IOException
	 */
	String getRelativeSchema(String schemaRelativePath) throws IOException;

	/**
	 * @param schemaAbsolutePath
	 *            Absolute path of the resource containing the schema in the
	 *            class loader
	 * @return
	 * @throws IOException
	 */
	String getAbsoluteSchema(String schemaAbsolutePath) throws IOException;

	void setActivatorClassName(String activatorClassName);

	void setPluginClassLoader(ClassLoader pluginClassLoader);

	void start() throws Exception;

	boolean allowStop();

	void stop() throws Exception;

	ClassLoader getPluginClassLoader();

}