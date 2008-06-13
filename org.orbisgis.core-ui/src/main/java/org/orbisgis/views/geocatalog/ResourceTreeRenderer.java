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
//SAM : COMPLETE
package org.orbisgis.views.geocatalog;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.gdms.data.NoSuchTableException;
import org.gdms.source.SourceManager;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.resource.IResource;

public class ResourceTreeRenderer extends DefaultTreeCellRenderer {

	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private OurJPanel ourJPanel = null;

	public ResourceTreeRenderer() {
		ourJPanel = new OurJPanel();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		ourJPanel.setNodeCosmetic(tree, (IResource) value, sel, expanded, leaf,
				row, hasFocus);
		return ourJPanel;
	}

	private class OurJPanel extends JPanel {

		private JLabel iconAndLabel;

		public OurJPanel() {
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			setLayout(fl);
			iconAndLabel = new JLabel();
			add(iconAndLabel);
		}

		public void setNodeCosmetic(JTree tree, IResource node,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Icon icon = node.getIcon(expanded);
			if (null != icon) {
				iconAndLabel.setIcon(icon);
			} else {
				iconAndLabel.setIcon(null);
			}
			DataManager dataManager = (DataManager) Services
					.getService("org.orbisgis.DataManager");
			SourceManager sourceManager = dataManager.getSourceManager();
			String text = node.getName();
			try {
				text +=  " (" + sourceManager.getSourceTypeName(node.getName())+ ")";
			} catch (NoSuchTableException e) {
			}
			iconAndLabel.setText(text);
			iconAndLabel.setVisible(true);

			if (selected) {
				this.setBackground(SELECTED);
				iconAndLabel.setForeground(SELECTED_FONT);
			} else {
				this.setBackground(DESELECTED);
				iconAndLabel.setForeground(DESELECTED_FONT);
			}
		}
	}

}
