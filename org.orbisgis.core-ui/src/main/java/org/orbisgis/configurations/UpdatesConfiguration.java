package org.orbisgis.configurations;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.orbisgis.Services;
import org.orbisgis.configuration.BasicConfiguration;
import org.orbisgis.configuration.IConfiguration;
import org.orbisgis.configurations.ui.ConfigUnitPanel;
import org.orbisgis.updates.UpdateManager;
import org.sif.CRFlowLayout;
import org.sif.multiInputPanel.InputType;
import org.sif.multiInputPanel.StringType;

public class UpdatesConfiguration implements IConfiguration {
	// Property constants
	private static final String UPDATE_SEARCH_PROPERTY = "org.orbisgis.configuration.updateSearchAtStartup";
	private static final String UPDATE_URL_PROPERTY = "org.orbisgis.configuration.updatesURL";

	// Attributes
	private JPanel panel;
	private StringType url;
	private JCheckBox checkbox;
	private URL updatesURL;

	@Override
	public JComponent getComponent() {
		if (panel == null) {
			CRFlowLayout layout = new CRFlowLayout();
			layout.setVgap(40);
			panel = new JPanel(layout);

			url = new StringType(30);
			checkbox = new JCheckBox();

			UpdateManager update = Services.getService(UpdateManager.class);
			updatesURL = update.getUpdateSiteURL();
			url.setValue(updatesURL.toString());

			checkbox.setSelected(update.isSearchAtStartup());
			url.setEditable(updatesURL != null);

			String[] labels = { "URL: " };
			InputType[] inputs = { url };
			JPanel urlPanel = new ConfigUnitPanel("Updates",
					checkbox, "Enable update search at startup", labels, inputs);

			panel.add(urlPanel);
		}

		return panel;
	}

	@Override
	public void loadAndApply() {
		BasicConfiguration bc = Services.getService(BasicConfiguration.class);
		String urlString = bc.getProperty(UPDATE_URL_PROPERTY);
		String updateSearchAtStartup = bc.getProperty(UPDATE_SEARCH_PROPERTY);
		apply(urlString, Boolean.parseBoolean(updateSearchAtStartup));
	}

	/**
	 * Applies the specified values in the UpdateManager registered in the
	 * services
	 * 
	 * @param updatesURLString
	 *            the url to apply in the update manager
	 * @param updateSearchAtStartup
	 */
	private void apply(String updatesURLString, Boolean updateSearchAtStartup) {
		try {
			UpdateManager update = Services.getService(UpdateManager.class);
			if (updatesURLString != null) {
				update.setUpdateSiteURL(new URL(updatesURLString));
			}

			if (updateSearchAtStartup != null) {
				update.setSearchAtStartup(updateSearchAtStartup);
			}
		} catch (MalformedURLException e) {
			Services.getErrorManager().error("bug!", e);
		}
	}

	@Override
	public String validateInput() {
		try {
			new URL(url.getValue());
			return null;
		} catch (MalformedURLException e) {
			return "You must specify a correct URL: " + e.getMessage();
		}
	}

	@Override
	public void applyUserInput() {
		apply(url.getValue(), checkbox.isSelected());
	}

	@Override
	public void saveApplied() {
		UpdateManager update = Services.getService(UpdateManager.class);
		BasicConfiguration bc = Services.getService(BasicConfiguration.class);

		String updatesURL = update.getUpdateSiteURL().toString();
		Boolean updateSearchAtStartup = update.isSearchAtStartup();
		if (updatesURL != null) {
			bc.setProperty(UPDATE_URL_PROPERTY, updatesURL);
		} else {
			bc.removeProperty(UPDATE_URL_PROPERTY);
		}
		if (updatesURL != null) {
			bc.setProperty(UPDATE_SEARCH_PROPERTY, updateSearchAtStartup
					.toString());
		} else {
			bc.removeProperty(UPDATE_SEARCH_PROPERTY);
		}
	}
}
