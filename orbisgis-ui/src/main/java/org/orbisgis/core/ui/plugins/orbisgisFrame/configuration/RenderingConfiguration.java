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

import java.util.Properties;

import javax.swing.JComponent;

import org.orbisgis.core.Services;
import org.orbisgis.core.configuration.BasicConfiguration;


public class RenderingConfiguration implements IConfiguration {


	private static final String ANTIALIASING_PROPERTY = "org.orbisgis.core.ui.configuration.antialiasing";
	private static final String COMPOSITE_PROPERTY = "org.orbisgis.core.ui.configuration.composite";;
	private static final String COMPOSITE_VALUE_PROPERTY = "org.orbisgis.core.ui.configuration.compositeLevel";
	private RenderingConfigurationPanel rcp;
	private String antialiasing;
	private String composite;
	private String composite_value = RenderingConfigurationConstants.items1;

	@Override
	public void applyUserInput() {

		if (rcp.getAntialiasingCheck().isSelected()) {
			antialiasing = "true";
		} else {
			antialiasing = "false";
		}

		if (rcp.getCompositeCheck().isSelected()) {
			composite = "true";
			composite_value = (String) rcp.getCompositeCb().getSelectedItem();
		} else {
			composite = "false";
			composite_value = RenderingConfigurationConstants.items1;
		}

		apply(antialiasing, composite,composite_value);

	}

	private void apply(String antialiasing, String composite, String composite_value) {

		Properties systemSettings = System.getProperties();
		if (antialiasing != null && composite != null ) {

			systemSettings.put(RenderingConfigurationConstants.SYSTEM_ANTIALIASING_STATUS, antialiasing);
			systemSettings.put(RenderingConfigurationConstants.SYSTEM_COMPOSITE_STATUS, composite);
			systemSettings.put(RenderingConfigurationConstants.SYSTEM_COMPOSITE_VALUE, composite_value);

		} else if (composite != null) {
			systemSettings.put(RenderingConfigurationConstants.SYSTEM_COMPOSITE_STATUS, composite);
			systemSettings.put(RenderingConfigurationConstants.SYSTEM_COMPOSITE_VALUE, composite_value);
		} else if (antialiasing != null) {
			systemSettings.put(RenderingConfigurationConstants.SYSTEM_ANTIALIASING_STATUS, antialiasing);
		} else {
			systemSettings.remove(RenderingConfigurationConstants.SYSTEM_ANTIALIASING_STATUS);
			systemSettings.remove(RenderingConfigurationConstants.SYSTEM_COMPOSITE_STATUS);
			systemSettings.remove(RenderingConfigurationConstants.SYSTEM_COMPOSITE_VALUE);

		}

	}

	@Override
	public JComponent getComponent() {
		rcp = new RenderingConfigurationPanel(new Boolean(antialiasing),
				new Boolean(composite), composite_value);

		rcp.init();

		return rcp;
	}

	@Override
	public void loadAndApply() {
		BasicConfiguration bc = Services.getService(BasicConfiguration.class);
		antialiasing = bc.getProperty(ANTIALIASING_PROPERTY);
		composite = bc.getProperty(COMPOSITE_PROPERTY);
		composite_value = bc.getProperty(COMPOSITE_VALUE_PROPERTY);

		apply(antialiasing, composite, composite_value);

	}

	@Override
	public void saveApplied() {

		Properties systemSettings = System.getProperties();
		antialiasing = systemSettings.getProperty(RenderingConfigurationConstants.SYSTEM_ANTIALIASING_STATUS);
		composite = systemSettings.getProperty(RenderingConfigurationConstants.SYSTEM_COMPOSITE_STATUS);
		composite_value = systemSettings.getProperty(RenderingConfigurationConstants.SYSTEM_COMPOSITE_VALUE);

		BasicConfiguration bc = Services.getService(BasicConfiguration.class);
		if (antialiasing != null && composite != null) {
			bc.setProperty(ANTIALIASING_PROPERTY, antialiasing);
			bc.setProperty(COMPOSITE_PROPERTY, composite);
			bc.setProperty(COMPOSITE_VALUE_PROPERTY, composite_value);
		} else if (composite != null) {
			bc.setProperty(COMPOSITE_PROPERTY, composite);
			bc.setProperty(COMPOSITE_VALUE_PROPERTY, composite_value);
		} else if (antialiasing != null) {
			bc.setProperty(ANTIALIASING_PROPERTY, antialiasing);
		} else {
			bc.removeProperty(ANTIALIASING_PROPERTY);
			bc.removeProperty(COMPOSITE_PROPERTY);
			bc.removeProperty(COMPOSITE_VALUE_PROPERTY);

		}

	}

	@Override
	public String validateInput() {
		return null;
	}

}
