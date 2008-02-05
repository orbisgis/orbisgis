/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.views.sqlSemanticRepository;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.Function;
import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.ClassName;
import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.Menu;
import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.MenuItem;
import org.orbisgis.images.IconLoader;

public class ToolsMenuPanelTreeCellRenderer implements TreeCellRenderer {

	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private static final Icon FOLDER_ICON = IconLoader.getIcon("folder.png");

	private static final Icon FOLDER_MAGNIFY_ICON = IconLoader.getIcon("folder_magnify.png");

	private static final Icon FUNCTION_MAP_ICON = IconLoader.getIcon("functionMap.png");

	private static final Icon CUSTOMQUERY_MAP_ICON = IconLoader.getIcon("customQueryMap.png");

	private static final Icon SQLSCRIPT_MAP_ICON = IconLoader.getIcon("sqlScriptMap.png");

	private NodeJPanel nodeJPanel = null;

	public ToolsMenuPanelTreeCellRenderer() {
		nodeJPanel = new NodeJPanel();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (leaf) {
			try {
				nodeJPanel.setNodeCosmetic(tree, (MenuItem) value, selected,
						expanded, leaf, row, hasFocus);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			nodeJPanel.setNodeCosmetic(tree, (Menu) value, selected, expanded,
					leaf, row, hasFocus);
		}
		return nodeJPanel;
	}

	private class NodeJPanel extends JPanel {
		private JLabel iconAndLabel;

		public NodeJPanel() {
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			setLayout(fl);
			iconAndLabel = new JLabel();
			add(iconAndLabel);
		}

		public void setNodeCosmetic(JTree tree, Menu node, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {
			Icon icon = (expanded) ? FOLDER_ICON : FOLDER_MAGNIFY_ICON;

			iconAndLabel.setIcon(icon);
			iconAndLabel.setText(node.getLabel());
			iconAndLabel.setVisible(true);

			if (selected) {
				setBackground(SELECTED);
				iconAndLabel.setForeground(SELECTED_FONT);
			} else {
				setBackground(DESELECTED);
				iconAndLabel.setForeground(DESELECTED_FONT);
			}
		}

		public void setNodeCosmetic(JTree tree, MenuItem node,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) throws InstantiationException,
				IllegalAccessException, ClassNotFoundException {
			final ClassName className = node.getClassName();
			if (null == className) {
				iconAndLabel.setIcon(SQLSCRIPT_MAP_ICON);

			} else {
				final Object newInstance = Class.forName(
						className.getValue().trim()).newInstance();
				if (newInstance instanceof Function) {
					iconAndLabel.setIcon(FUNCTION_MAP_ICON);
				} else if (newInstance instanceof CustomQuery) {
					iconAndLabel.setIcon(CUSTOMQUERY_MAP_ICON);
				}
			}

			iconAndLabel.setText(node.getLabel());
			iconAndLabel.setVisible(true);

			if (selected) {
				setBackground(SELECTED);
				iconAndLabel.setForeground(SELECTED_FONT);
			} else {
				setBackground(DESELECTED);
				iconAndLabel.setForeground(DESELECTED_FONT);
			}
		}
	}
}