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
package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import javax.swing.JComponent;

public class ConfigurationDecorator implements IConfiguration {
	private IConfiguration config;
	private String id, text, parentId;
	private boolean isComponentCreated;

	/**
	 * Creates a new configuration decorator for the specified class with the
	 * given id
	 * 
	 * @param className
	 *            the name of the decorated class
	 * @param id
	 *            the id of the configuration
	 */
	public ConfigurationDecorator(IConfiguration config, String id,
			String text, String parentId) {
		this.id = id;
		this.text = text;
		this.parentId = parentId;
		this.config = config;
		isComponentCreated = false;
	}

	@Override
	public JComponent getComponent() {
		isComponentCreated = true;
		return config.getComponent();
	}

	@Override
	public void loadAndApply() {
		config.loadAndApply();
	}

	@Override
	public String validateInput() {
		if (isComponentCreated) {
			return config.validateInput();
		} else {
			return null;
		}
	}

	@Override
	public void applyUserInput() {
		if (isComponentCreated) {
			config.applyUserInput();
		}
	}

	@Override
	public void saveApplied() {
		config.saveApplied();
	}

	/**
	 * Gets the id of this configuration
	 * 
	 * @return the id of this configuration
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the text to show of this configuration
	 * 
	 * @return the text to show of this configuration
	 */
	public String getText() {
		return text;
	}

	/**
	 * Gets the parent id of this configuration
	 * 
	 * @return the parent id of this configuration
	 */
	public String getParentId() {
		return parentId;
	}

	@Override
	public String toString() {
		return text;
	}
}
