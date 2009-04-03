/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.views.sqlRepository;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.Function;
import org.orbisgis.images.IconLoader;
import org.orbisgis.views.sqlRepository.persistence.Category;
import org.orbisgis.views.sqlRepository.persistence.SqlInstruction;
import org.orbisgis.views.sqlRepository.persistence.SqlScript;

public class SQLRepositoryTreeCellRenderer implements TreeCellRenderer {

	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private static final Icon FOLDER_ICON = IconLoader.getIcon("folder.png");

	private static final Icon FOLDER_MAGNIFY_ICON = IconLoader
			.getIcon("folder_magnify.png");

	private static final Icon FUNCTION_MAP_ICON = IconLoader
			.getIcon("functionmap.png");

	private static final Icon CUSTOMQUERY_MAP_ICON = IconLoader
			.getIcon("customquerymap.png");

	private static final Icon SQLSCRIPT_MAP_ICON = IconLoader
			.getIcon("sqlscriptmap.png");

	private NodeJPanel nodeJPanel = null;

	public SQLRepositoryTreeCellRenderer() {
		nodeJPanel = new NodeJPanel();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (leaf) {
			try {
				nodeJPanel.setNodeCosmetic(tree, value, selected, expanded,
						leaf, row, hasFocus);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			nodeJPanel.setNodeCosmetic(tree, (Category) value, selected,
					expanded, leaf, row, hasFocus);
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

		public void setNodeCosmetic(JTree tree, Category node,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
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

		public void setNodeCosmetic(JTree tree, Object node, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus)
				throws InstantiationException, IllegalAccessException,
				ClassNotFoundException {
			if (node instanceof SqlScript) {
				SqlScript script = (SqlScript) node;
				iconAndLabel.setIcon(SQLSCRIPT_MAP_ICON);
				iconAndLabel.setText(script.getId());
			} else if (node instanceof SqlInstruction) {
				SqlInstruction instruction = (SqlInstruction) node;
				final Object newInstance = Class
						.forName(instruction.getClazz()).newInstance();
				if (newInstance instanceof Function) {
					iconAndLabel.setIcon(FUNCTION_MAP_ICON);
					iconAndLabel.setText(((Function) newInstance).getName());
				} else if (newInstance instanceof CustomQuery) {
					iconAndLabel.setIcon(CUSTOMQUERY_MAP_ICON);
					iconAndLabel.setText(((CustomQuery) newInstance).getName());
				}
			} else {
				throw new RuntimeException("bug!");
			}

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