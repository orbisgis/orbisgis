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
package org.orbisgis.geoview.views.sqlConsole.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import org.orbisgis.geocatalog.resources.TransferableResource;
import org.orbisgis.geoview.views.sqlConsole.actions.ActionsListener;
import org.orbisgis.geoview.views.toc.TransferableLayer;

public class ScrollPaneWest extends JScrollPane implements DropTargetListener {
	private JTextArea jTextArea;
	private ActionsListener actionAndKeyListener;

	public ScrollPaneWest(final ActionsListener actionAndKeyListener) {
		this.actionAndKeyListener = actionAndKeyListener;
		setViewportView(getJTextArea());
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setLineWrap(true);
			jTextArea.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			jTextArea.setDropTarget(new DropTarget(this, this));
			jTextArea.addKeyListener(actionAndKeyListener);
		}
		return jTextArea;
	}

	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
	}

	public void drop(DropTargetDropEvent dtde) {
		final Transferable t = dtde.getTransferable();
		String query = null;

		try {
			if ((t.isDataFlavorSupported(TransferableResource
					.getResourceFlavor()))
					|| (t.isDataFlavorSupported(TransferableLayer
							.getLayerFlavor()))) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				String s = (String) t.getTransferData(DataFlavor.stringFlavor);
				dtde.getDropTargetContext().dropComplete(true);
				query = s;
			} else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				String s = (String) t.getTransferData(DataFlavor.stringFlavor);
				dtde.getDropTargetContext().dropComplete(true);
				query = s;
			}
		} catch (IOException e) {
			dtde.rejectDrop();
		} catch (UnsupportedFlavorException e) {
			dtde.rejectDrop();
		}

		if (query != null) {
			// Cursor position
			int position = jTextArea.getCaretPosition();
			jTextArea.insert(query, position);
			// Replace the cursor at end line
			jTextArea.requestFocus();
		}
		dtde.rejectDrop();
		
		actionAndKeyListener.setButtonsStatus();
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void setText(String text) {
		jTextArea.setText(text);
	}
}