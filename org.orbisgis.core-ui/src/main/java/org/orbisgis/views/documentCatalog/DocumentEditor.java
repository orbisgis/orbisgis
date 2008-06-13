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
package org.orbisgis.views.documentCatalog;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;

public class DocumentEditor implements TreeCellEditor {

	private JTextField editorPanel;
	private JTree tree;

	public DocumentEditor(final JTree tree) {
		this.tree = tree;
		editorPanel = new JTextField();
		editorPanel.addKeyListener(new KeyAdapter() {

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
		String text = ((IDocument) value).getName();
		editorPanel.setText(text);
		editorPanel.setPreferredSize(tree.getPreferredSize());

		return editorPanel;
	}

	public void addCellEditorListener(CellEditorListener l) {
	}

	public void cancelCellEditing() {

	}

	public Object getCellEditorValue() {
		return editorPanel.getText();
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
		IDocument doc = (IDocument) tree.getSelectionPath()
				.getLastPathComponent();
		doc.setName(getCellEditorValue().toString());
		return true;
	}

}
