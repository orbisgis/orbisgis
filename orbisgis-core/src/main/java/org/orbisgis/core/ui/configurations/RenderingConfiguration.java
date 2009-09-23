package org.orbisgis.core.ui.configurations;

import java.util.Properties;

import javax.swing.JComponent;

import org.orbisgis.core.Services;
import org.orbisgis.core.configuration.BasicConfiguration;
import org.orbisgis.core.ui.configuration.IConfiguration;


public class RenderingConfiguration implements IConfiguration {


	public static String items1 = "1.0";
	public static String items2 = "0.75";
	public static String items3 = "0.5";
	public static String items4 = "0.25";



	private static final String ANTIALIASING_PROPERTY = "org.orbisgis.core.ui.configuration.antialiasing";
	private static final String COMPOSITE_PROPERTY = "org.orbisgis.core.ui.configuration.composite";;
	private static final String COMPOSITE_VALUE_PROPERTY = "org.orbisgis.core.ui.configuration.compositeLevel";
	public static final String SYSTEM_ANTIALIASING_STATUS = "antialiasing";
	public static final String SYSTEM_COMPOSITE_STATUS = "composite";
	public static final String SYSTEM_COMPOSITE_VALUE = "composite_level";
	private RenderingConfigurationPanel rcp;
	private String antialiasing;
	private String composite;
	private String composite_value = items1;

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
		}

		apply(antialiasing, composite,composite_value);

	}

	private void apply(String antialiasing, String composite, String composite_value) {

		Properties systemSettings = System.getProperties();
		if (antialiasing != null && composite != null && composite_value!=null) {

			systemSettings.put(SYSTEM_ANTIALIASING_STATUS, antialiasing);
			systemSettings.put(SYSTEM_COMPOSITE_STATUS, composite);
			systemSettings.put(SYSTEM_COMPOSITE_VALUE, composite_value);

		} else if (composite != null) {
			systemSettings.put(SYSTEM_COMPOSITE_STATUS, composite);
			systemSettings.put(SYSTEM_COMPOSITE_VALUE, composite_value);
		} else if (antialiasing != null) {
			systemSettings.put(SYSTEM_ANTIALIASING_STATUS, antialiasing);
		} else {
			systemSettings.remove(SYSTEM_ANTIALIASING_STATUS);
			systemSettings.remove(SYSTEM_COMPOSITE_STATUS);
			systemSettings.remove(SYSTEM_COMPOSITE_VALUE);

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
		antialiasing = systemSettings.getProperty(SYSTEM_ANTIALIASING_STATUS);
		composite = systemSettings.getProperty(SYSTEM_COMPOSITE_STATUS);
		composite_value = systemSettings.getProperty(SYSTEM_COMPOSITE_VALUE);

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
