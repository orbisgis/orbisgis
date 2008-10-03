package org.orbisgis.configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.orbisgis.action.EPBaseActionHelper;
import org.orbisgis.action.IMenu;
import org.orbisgis.action.MenuTree;
import org.orbisgis.action.ToolBarArray;
import org.sif.AbstractUIPanel;

public class ConfigurationPanel extends AbstractUIPanel {

	private JSplitPane pane;
	private ArrayList<ConfigurationDecorator> configs;
	private ConfigTree configTree;

	/**
	 * Creates a new configuration dialog
	 */
	public ConfigurationPanel() {
		MenuTree menuTree = new MenuTree();
		ToolBarArray foo = new ToolBarArray();
		EPBaseActionHelper.configureParentMenusAndToolBars(
				new String[] { "org.orbisgis.Configuration" }, "menu",
				menuTree, foo);
		IMenu root = menuTree.getRoot();
		// TODO create tree model linking IMenu's with Configurations
		configs = EPConfigHelper.getConfigurations();
		configTree = new ConfigTree(configs);
		configTree.addSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				for (ConfigurationDecorator config : configs) {
					if (config == configTree.getSelectedElement()) {
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
		return "Preferences";
	}

	@Override
	public String validateInput() {
		return null;
	}

	/**
	 * Saves all the preferences of the dialog
	 */
	public void savePreferences() {
		for (ConfigurationDecorator config : configs) {
			config.save();
		}
	}

	/**
	 * Configuration tree to show all the installed configurations
	 * 
	 * @author victorzinho
	 */
	private class ConfigTree extends JPanel {
		private JList list;

		/**
		 * Creates a new configuration tree with the specified configurations
		 * 
		 * @param decs
		 *            the configurations to show
		 */
		private ConfigTree(ArrayList<ConfigurationDecorator> decs) {
			list = new JList(decs.toArray());
			setLayout(new BorderLayout());
			add(list, BorderLayout.CENTER);
		}

		/**
		 * Adds a new selection listener
		 * 
		 * @param l
		 *            the listener to add
		 */
		private void addSelectionListener(ListSelectionListener l) {
			list.addListSelectionListener(l);
		}

		/**
		 * Gets the selected element
		 * 
		 * @return the selected element
		 */
		private ConfigurationDecorator getSelectedElement() {
			return (ConfigurationDecorator) list.getSelectedValue();
		}
	}

}
