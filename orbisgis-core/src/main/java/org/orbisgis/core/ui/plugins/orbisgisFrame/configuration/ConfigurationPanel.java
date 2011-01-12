package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.orbisgis.core.sif.AbstractUIPanel;
import org.orbisgis.core.ui.pluginSystem.menu.IMenu;
import org.orbisgis.utils.I18N;

public class ConfigurationPanel extends AbstractUIPanel {

	private JSplitPane splitPane;
	private ArrayList<ConfigurationDecorator> configs;
	private ConfigurationTree configTree;
	private JPanel init, rightPanel;
	private JScrollPane scrollPane;

	public ConfigurationPanel() {
		// Create config tree
		IMenu root = EPConfigHelper.getConfigurationMenu();
		configs = EPConfigHelper.getConfigurations();
		configTree = new ConfigurationTree(root, configs);

		// Panel shown at the opening of the dialog
		JLabel label = new JLabel(I18N
				.getText("orbisgis.org.orbisgis.configuration.clickItemLeft"));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		init = new JPanel();
		init.setLayout(new BorderLayout());
		init.add(label, BorderLayout.CENTER);

		// Create configurations panel
		rightPanel = new JPanel(new BorderLayout());
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		size.height = size.height * 4 / 10;
		size.width = size.width * 4 / 10;
		rightPanel.setPreferredSize(size);
		scrollPane = new JScrollPane(init);
		rightPanel.add(scrollPane, BorderLayout.CENTER);

		// Put all together in a split pane
		splitPane = new JSplitPane();
		splitPane.setRightComponent(rightPanel);
		splitPane.setLeftComponent(configTree);

		// Add listener
		configTree.addSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				// Get the component to show
				Object selected = configTree.getSelectedElement();
				JComponent comp = null;
				if (selected == null || selected instanceof IMenu) {
					comp = init;
				} else {
					for (ConfigurationDecorator config : configs) {
						if (config == selected) {
							comp = config.getComponent();
							break;
						}
					}
				}

				// Show the component
				if (comp != null) {
					scrollPane.setViewportView(comp);
					rightPanel.setMinimumSize(comp.getMinimumSize());
					// If the new component is wider than the shown region
					if (rightPanel.getSize().width < comp.getMinimumSize().width) {
						int location = splitPane.getSize().width
								- comp.getMinimumSize().width
								- splitPane.getDividerSize();
						splitPane.setDividerLocation(location);
					}

					splitPane.repaint();
				}
			}
		});
	}

	@Override
	public Component getComponent() {
		return splitPane;
	}

	@Override
	public String getTitle() {
		return "Configuration";
	}

	@Override
	public String validateInput() {
		String ret = null;
		ConfigurationDecorator c = null;
		for (ConfigurationDecorator config : configs) {
			ret = config.validateInput();
			if (ret != null) {
				c = config;
				break;
			}
		}
		return (ret == null || c == null) ? null : ret + " - " + c.getText();
	}

	/**
	 * Applies all the configurations of the dialog
	 */
	public void applyConfigurations() {
		for (ConfigurationDecorator config : configs) {
			config.applyUserInput();
		}
	}

	public void loadConfigurations() {
		for (ConfigurationDecorator config : configs) {
			config.loadAndApply();
		}
	}
}
