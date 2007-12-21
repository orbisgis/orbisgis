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