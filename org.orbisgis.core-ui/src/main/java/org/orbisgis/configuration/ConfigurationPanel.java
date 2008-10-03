package org.orbisgis.configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.orbisgis.action.IMenu;
import org.sif.AbstractUIPanel;

public class ConfigurationPanel extends AbstractUIPanel {

	private JSplitPane pane;
	private ArrayList<ConfigurationDecorator> configs;
	private ConfigurationTree configTree;
	private ConfigurationDecorator currentConfiguration;

	/**
	 * Creates a new configuration dialog
	 */
	public ConfigurationPanel() {
		IMenu root = EPConfigHelper.getConfigurationMenu();
		configs = EPConfigHelper.getConfigurations();

		configTree = new ConfigurationTree(root, configs);
		configTree.addSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				for (ConfigurationDecorator config : configs) {
					if (config == configTree.getSelectedElement()) {
						currentConfiguration = config;
						pane.setRightComponent(config.getComponent());
						return;
					}
				}
			}
		});

		pane = new JSplitPane();

		// Panel shown at the opening of the dialog
		JLabel label = new JLabel("Click on a configuration item on the left");
		label.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel init = new JPanel();
		init.setPreferredSize(new Dimension(500, 500));
		init.setLayout(new BorderLayout());
		init.add(label, BorderLayout.CENTER);

		pane.setRightComponent(init);
		pane.setLeftComponent(configTree);

		pane.addPropertyChangeListener(
				JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY,
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						configTree.setPreferredSize(configTree.getSize());
					}
				});
	}

	@Override
	public Component getComponent() {
		return pane;
	}

	@Override
	public String getTitle() {
		return "Configuration";
	}

	@Override
	public String validateInput() {
		return (currentConfiguration == null) ? null : currentConfiguration
				.validateInput();
	}

	/**
	 * Saves all the preferences of the dialog
	 */
	public void savePreferences() {
		for (ConfigurationDecorator config : configs) {
			config.save();
		}
	}
}
