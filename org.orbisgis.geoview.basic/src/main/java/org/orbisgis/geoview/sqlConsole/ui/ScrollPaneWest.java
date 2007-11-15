package org.orbisgis.geoview.sqlConsole.ui;

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

import org.orbisgis.core.resourceTree.TransferableResource;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.toc.TransferableLayer;

public class ScrollPaneWest extends JScrollPane implements DropTargetListener {

	public static JTextArea jTextArea;
	public static GeoView2D geoview;

	public ScrollPaneWest(GeoView2D geoview) {
		ScrollPaneWest.geoview = geoview;
		setViewportView(getJTextArea());
	}

	/**
	 * This method initializes jTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setLineWrap(true);
			jTextArea.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			jTextArea.setDropTarget(new DropTarget(this, this));

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

		Transferable t = dtde.getTransferable();
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
				query = SQLConsolePanel.getQuery(s);
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
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

}
