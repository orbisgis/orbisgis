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
package org.orbisgis.core.ui.editorViews.toc;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;

import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.sif.CRFlowLayout;

public class TocEditor extends TocAbstractRenderer implements TreeCellEditor {
	private EditorPanel editorPanel;

	private JTree tree;

	public class EditorPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private JCheckBox check;

		private JLabel iconAndLabel;

		private JTextField textField;

		public EditorPanel() {
			FlowLayout fl = new FlowLayout(CRFlowLayout.LEADING);
			fl.setHgap(0);
			setLayout(fl);
			check = new JCheckBox();
			iconAndLabel = new JLabel();
			textField = new JTextField(14);
			textField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						tree.stopEditing();
					}
				}

			});
			add(check);
			add(iconAndLabel);
			add(textField);
		}

		public void setNodeCosmetic(JTree tree, ILayer node,
				boolean isSelected, boolean expanded, boolean leaf, int row) {
			check.setVisible(true);
			check.setSelected(node.isVisible());

			Icon icon = null;
			try {
				icon = getLayerIcon(node);
			} catch (DriverException e) {
			} catch (IOException e) {
			}
			if (null != icon) {
				iconAndLabel.setIcon(icon);
			}
			iconAndLabel.setVisible(true);

			textField.setText(node.getName());
			textField.selectAll();
		}

	}

	public TocEditor(JTree toc) {
		editorPanel = new EditorPanel();
		this.tree = toc;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		editorPanel.setNodeCosmetic(tree, (ILayer) value, isSelected, expanded,
				leaf, row);
		return editorPanel;
	}

	public void addCellEditorListener(CellEditorListener l) {

	}

	public void cancelCellEditing() {

	}

	public Object getCellEditorValue() {
		return editorPanel.textField.getText();
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
		return false;
	}

	public boolean stopCellEditing() {
		if (editorPanel.textField.getText().length() == 0) {
			return false;
		} else {
			ILayer l = (ILayer) tree.getEditingPath().getLastPathComponent();
			try {
				l.setName(editorPanel.textField.getText());
			} catch (LayerException e) {
				return false;
			}
			return true;
		}
	}
}