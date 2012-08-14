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
				.getString("orbisgis.org.orbisgis.configuration.clickItemLeft")); //$NON-NLS-1$
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
		return I18N.getString("orbisgis.org.orbisgis.ui.frame.configurationPanel.configuration"); //$NON-NLS-1$
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
		return (ret == null || c == null) ? null : ret + " - " + c.getText(); //$NON-NLS-1$
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
