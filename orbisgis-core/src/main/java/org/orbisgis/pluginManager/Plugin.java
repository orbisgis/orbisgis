/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.pluginManager;


import java.io.IOException;

import org.orbisgis.core.Services;

import org.orbisgis.utils.FileUtils;

public abstract class Plugin implements PluginInfo {

	private PluginActivator resolvedActivator;
	private String activator;
	private ClassLoader loader;

	public String getActivator() {
		return activator;
	}

	public void setResolvedActivator(PluginActivator resolvedActivator) {
		this.resolvedActivator = resolvedActivator;
	}

	public void setActivator(String activator) {
		this.activator = activator;
	}

	public void start() throws Exception {
		PluginActivator resolvedActivator = getResolvedActivator();
		if (resolvedActivator != null) {
			resolvedActivator.start();
		}
	}

	private PluginActivator getResolvedActivator()
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		if (activator == null) {
			return null;
		} else {
			if (resolvedActivator == null) {
				resolvedActivator = (PluginActivator) loader.loadClass(
						activator).newInstance();
			}

			return resolvedActivator;
		}
	}

	public void stop() throws Exception {
		try {
			PluginActivator resolvedActivator = getResolvedActivator();
			if (resolvedActivator != null) {
				resolvedActivator.stop();
			}
		} catch (InstantiationException e) {
			throw new RuntimeException("start worked but not stop: bug!");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("start worked but not stop: bug!");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("start worked but not stop: bug!");
		}
	}

	public boolean allowStop() {
		try {
			PluginActivator resolvedActivator = getResolvedActivator();
			if (resolvedActivator != null) {
				return resolvedActivator.allowStop();
			}
		} catch (InstantiationException e) {
			// Ignore the invalid activators
		} catch (IllegalAccessException e) {
			// Ignore the invalid activators
		} catch (ClassNotFoundException e) {
			// Ignore the invalid activators
		} catch (Exception e) {
			Services.getErrorManager().error("Error while shuting down", e);
		}
		return true;
	}

	@Override
	public void setActivatorClassName(String activatorClassName) {
		this.activator = activatorClassName;
	}

	@Override
	public void setPluginClassLoader(ClassLoader pluginClassLoader) {
		this.loader = pluginClassLoader;
	}

	@Override
	public ClassLoader getPluginClassLoader() {
		return loader;
	}

	@Override
	public String getAbsoluteSchema(String schemaAbsolutePath)
			throws IOException {
		return new String(FileUtils.getContent(loader
				.getResourceAsStream(schemaAbsolutePath)));
	}

}
