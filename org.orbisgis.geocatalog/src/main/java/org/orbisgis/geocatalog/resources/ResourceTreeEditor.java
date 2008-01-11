/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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
//SAM : COMPLETE
package org.orbisgis.geocatalog.resources;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;


public class ResourceTreeEditor implements TreeCellEditor {

	private class OurJPanel extends JPanel {

		private JLabel iconAndLabel;

		private JTextField textField;

		public OurJPanel() {
			setBackground(Color.white);
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			setLayout(fl);
			iconAndLabel = new JLabel();
			textField = new JTextField(14);
			textField.addKeyListener(new KeyAdapter() {

				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						tree.stopEditing();
					}
				}

			});
			add(iconAndLabel);
			add(textField);
		}

		public void setNodeCosmetic(JTree tree, IResource node,
				boolean isSelected, boolean expanded, boolean leaf, int row) {

			Icon icon = node.getIcon(expanded);
			if (null != icon) {
				iconAndLabel.setIcon(icon);
			} else {
				iconAndLabel.setIcon(null);
			}

			iconAndLabel.setVisible(true);
			textField.setText(node.getName());
			textField.selectAll();
		}
	}

	private OurJPanel ourJPanel = null;

	private JTree tree = null;

	public ResourceTreeEditor(JTree tree) {
		this.tree = tree;
		ourJPanel = new OurJPanel();
	}

	public void addCellEditorListener(CellEditorListener l) {
	}

	public void cancelCellEditing() {
	}

	public Object getCellEditorValue() {
		return null;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {

		ourJPanel.setNodeCosmetic(tree, (IResource) value, isSelected,
				expanded, leaf, row);
		return ourJPanel;

	}

	public boolean isCellEditable(EventObject anEvent) {
		boolean ok = false;
		if (anEvent instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) anEvent;
			if (me.getClickCount() == 2 && me.getButton() == 1) {
				TreePath selectionPath = tree.getSelectionPath();
				if (selectionPath == null) {
					ok = false;
				} else {
					IResource node = (IResource) selectionPath
							.getLastPathComponent();
					// The offset is used to determine if we clicked on the icon
					// or
					// on the string
					int offset = (node.getResourcePath().length - 2) * 20 + 30;
					if (me.getX() >= offset) {
						ok = true;
					}
				}
			}
		}
		return ok;
	}

	public void removeCellEditorListener(CellEditorListener l) {
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return false;
	}

	public boolean stopCellEditing() {
		boolean ok = false;
		if (ourJPanel.textField.getText().length() != 0) {
			IResource node = (IResource) tree.getEditingPath()
					.getLastPathComponent();
			try {
				node.setResourceName(ourJPanel.textField.getText());
			} catch (ResourceTypeException e) {
				ok = false;
			}
			ok = true;
		}
		return ok;
	}

}
