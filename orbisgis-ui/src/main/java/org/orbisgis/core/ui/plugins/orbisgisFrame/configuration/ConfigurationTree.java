/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.orbisgis.core.ui.components.resourceTree.FilterTreeModelDecorator;
import org.orbisgis.core.ui.components.resourceTree.ReadOnlyCellEditor;
import org.orbisgis.core.ui.pluginSystem.menu.IMenu;

class ConfigurationTree extends JPanel {

	private BasicResourceTree treePanel;

	/**
	 * Creates a new configuration tree
	 * 
	 * @param root
	 *            the root menu of the configuration tree
	 * @param configs
	 *            the installed configurations
	 */
	ConfigurationTree(IMenu root, List<ConfigurationDecorator> configs) {
		treePanel = new BasicResourceTree();
		FilterTreeModelDecorator model = new FilterTreeModelDecorator(
				new ConfigurationTreeModel(root, configs, treePanel.getTree()),
				treePanel.getTree());
		ConfigurationTreeRenderer renderer = new ConfigurationTreeRenderer();

		treePanel.getTree().setModel(model);
		treePanel.getTree().setCellRenderer(renderer);
		treePanel.getTree().setCellEditor(new ReadOnlyCellEditor());
		treePanel.getTree().setRootVisible(false);
		treePanel.getTree().getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		expandAll(treePanel.getTree());
		ToolTipManager.sharedInstance().registerComponent(treePanel.getTree());

		JTextField field = new JTextField(10);
		field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				FilterTreeModelDecorator model = (FilterTreeModelDecorator) treePanel
						.getTree().getModel();
				model.filter(((JTextField) e.getSource()).getText());
			}
		});

		JPanel north = new JPanel(new BorderLayout());
		north.add(field, BorderLayout.CENTER);
		int textFieldMargin = 10;
		north.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0),
				textFieldMargin));
		Dimension min = field.getPreferredSize();
		min.width += textFieldMargin * 2;
		min.height += textFieldMargin * 2;
		north.setMinimumSize(min);
		JPanel aux = new JPanel(new BorderLayout());
		aux.setBorder(BorderFactory.createLineBorder(Color.black));
		aux.add(north, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(treePanel, BorderLayout.CENTER);
		add(aux, BorderLayout.NORTH);
	}

	/**
	 * Adds a new selection listener
	 * 
	 * @param l
	 *            the listener to add
	 */
	void addSelectionListener(TreeSelectionListener l) {
		treePanel.getTree().addTreeSelectionListener(l);
	}

	/**
	 * Gets the selected element
	 * 
	 * @return the selected element
	 */
	Object getSelectedElement() {
		TreePath selectionPath = treePanel.getTree().getSelectionPath();
		return (selectionPath == null) ? null : selectionPath
				.getLastPathComponent();
	}
	
	/**
	 * Expand configuration tree
	 * @param tree
	 */
	public void expandAll(JTree tree) {  
	    for (int row = 0; row < tree.getRowCount() ; row++) {
	      tree.expandRow(row);
	    }
	}
}
