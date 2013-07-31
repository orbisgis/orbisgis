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
package org.orbisgis.view.toc.actions.cui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.*;

/**
 * As a {@code CellRenderer}, this class is used in {@code LegendList} to render
 * the elements of the list of {@code Legend}. Each time a change occur in the
 * list organization, it is called to perform the rendering.
 * @author Alexis Guéganno
 */
public class LegendListRenderer implements ListCellRenderer {

	private LegendListRenderPanel ourJPanel = null;
	private SimpleStyleEditor editor;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED = Color.red;

        /**
         * Build a new {@code ListCellRenderer} dedicated to the rendering of
         * {@code Legend} elements and affiliated.
         * @param editor
         */
	public LegendListRenderer(SimpleStyleEditor editor) {
		this.editor = editor;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		ourJPanel = new LegendListRenderPanel();
		ourJPanel.setNodeCosmetic(list, value, index, isSelected, cellHasFocus);
		return ourJPanel;

	}

        /**
         * The {@code CellRenderer} is used by its owning {@code LegendList} to
         * produce instances of {@code Component} each time it's needed. This
         * goal is achieved by {@code LegendListRenderer} by using this inner
         * class.
         */
	public class LegendListRenderPanel extends JPanel {

		private JCheckBox jCheckBox;
		private JLabel label;

                /**
                 * Prepare the {@code Component} that will be returned to the
                 * {@code LegendList}.
                 */
		public LegendListRenderPanel() {

			jCheckBox = new JCheckBox();
			jCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			label = new JLabel();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(jCheckBox);
			add(label);
			this.setBackground(DESELECTED);

		}

                /**
                 * Configure the {@code Component} that will be returned to the
                 * {@code LegendList}.
                 * @param list
                 * @param value
                 * @param legendIndex
                 * @param isSelected
                 * @param cellHasFocus
                 */
		public void setNodeCosmetic(JList list, Object value, int legendIndex,
				boolean isSelected, boolean cellHasFocus) {
			jCheckBox.setVisible(true);

//			if (editor.getLegends()[legendIndex].isVisible()) {
				jCheckBox.setSelected(true);
//			} else {
//				jCheckBox.setSelected(false);
//			}

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

        /**
         * Get the bounds of the last produced {@code Component}.
         * @return
         */
	public Rectangle getCheckBoxBounds() {
		return ourJPanel.getCheckBoxBounds();
	}

}
