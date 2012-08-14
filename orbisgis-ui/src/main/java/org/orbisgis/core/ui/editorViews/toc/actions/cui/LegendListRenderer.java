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
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class LegendListRenderer implements ListCellRenderer {

	private LegendListRenderPanel ourJPanel = null;
	private LegendsPanel legendsPanel;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED = Color.red;

	public LegendListRenderer(LegendsPanel legendsPanel) {
		this.legendsPanel = legendsPanel;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		ourJPanel = new LegendListRenderPanel();
		ourJPanel.setNodeCosmetic(list, value, index, isSelected, cellHasFocus);
		return ourJPanel;

	}

	public class LegendListRenderPanel extends JPanel {

		private JCheckBox jCheckBox;
		private JLabel label;

		public LegendListRenderPanel() {

			jCheckBox = new JCheckBox();
			jCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			label = new JLabel();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(jCheckBox);
			add(label);
			this.setBackground(DESELECTED);

		}

		public void setNodeCosmetic(JList list, Object value, int legendIndex,
				boolean isSelected, boolean cellHasFocus) {
			jCheckBox.setVisible(true);

			if (legendsPanel.getLegends()[legendIndex].isVisible()) {
				jCheckBox.setSelected(true);
			} else {
				jCheckBox.setSelected(false);
			}

			if (isSelected) {
				label.setForeground(SELECTED);
				jCheckBox.setForeground(SELECTED);
			} else {
				jCheckBox.setBackground(DESELECTED);
				jCheckBox.setForeground(DESELECTED);
			}

			label.setText(value.toString());

		}

		public Rectangle getCheckBoxBounds() {
			return jCheckBox.getBounds();

		}
	}

	public Rectangle getCheckBoxBounds() {

		return ourJPanel.getCheckBoxBounds();

	}

}
