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
package org.orbisgis.geoview.views.toc;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.orbisgis.geoview.layerModel.ILayer;

public class TocRenderer implements TreeCellRenderer {
	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private RenderPanel ourJPanel;

	public TocRenderer() {
		ourJPanel = new RenderPanel();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		ourJPanel.setNodeCosmetic(tree, (ILayer) value,
				selected, expanded, leaf, row, hasFocus);
		return ourJPanel;
	}

	public Rectangle getCheckBoxBounds() {
		return ourJPanel.getCheckBoxBounds();
	}

	public class RenderPanel extends JPanel {
		private JCheckBox check;

		private JLabel iconAndLabel;

		public RenderPanel() {
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			setLayout(fl);
			check = new JCheckBox();
			iconAndLabel = new JLabel();
			add(check);
			add(iconAndLabel);
		}

		public void setNodeCosmetic(JTree tree, ILayer node, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {
			check.setVisible(true);
			check.setSelected(node.isVisible());

			Icon icon = node.getIcon();
			if (null != icon) {
				iconAndLabel.setIcon(icon);
			}
			iconAndLabel.setText(node.getName());
			iconAndLabel.setVisible(true);

			if (selected) {
				this.setBackground(SELECTED);
				check.setBackground(SELECTED);
				iconAndLabel.setForeground(SELECTED_FONT);
			} else {
				this.setBackground(DESELECTED);
				check.setBackground(DESELECTED);
				iconAndLabel.setForeground(DESELECTED_FONT);
			}
		}

		private Rectangle getCheckBoxBounds() {
			return check.getBounds();
		}
	}

}
