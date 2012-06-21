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
package org.orbisgis.core.ui.plugins.views.geocognition;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;

import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.geocognition.GeocognitionElement;

public class GeocognitionEditor implements TreeCellEditor {

	private JTextField jtextField;
	private JTree tree;

	public GeocognitionEditor(final JTree tree) {
		this.tree = tree;
		jtextField = new JTextField();
		jtextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					tree.stopEditing();
				}
			}

		});

	}

	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		String text = ((GeocognitionElement) value).getId();
		jtextField.setText(text);
		Component renderComponent = tree.getCellRenderer()
				.getTreeCellRendererComponent(tree, value, isSelected,
						expanded, leaf, row, false);
		int height = renderComponent.getPreferredSize().height;
		jtextField.setPreferredSize(new Dimension(
				tree.getPreferredSize().width, height));

		return jtextField;
	}

	public void addCellEditorListener(CellEditorListener l) {
	}

	public void cancelCellEditing() {

	}

	public Object getCellEditorValue() {
		return jtextField.getText();
	}

	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) anEvent;
			if (me.getClickCount() >= 2) {
				return true;
			}
		}
		return false;
	}

	public void removeCellEditorListener(CellEditorListener l) {
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	public boolean stopCellEditing() {
		GeocognitionElement doc = (GeocognitionElement) tree.getSelectionPath()
				.getLastPathComponent();
		try {
			doc.setId(getCellEditorValue().toString());
			return true;
		} catch (IllegalArgumentException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot change element id", e);
			return false;
		}
	}

}
