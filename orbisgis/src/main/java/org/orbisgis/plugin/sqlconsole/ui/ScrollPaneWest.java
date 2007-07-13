package org.orbisgis.plugin.sqlconsole.ui;

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

import org.orbisgis.plugin.view.ui.workbench.LayerTransferable;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.MyNode;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.MyNodeTransferable;

public class ScrollPaneWest extends JScrollPane implements DropTargetListener {

	public static JTextArea jTextArea;

	public ScrollPaneWest() {
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
		// TODO : maybe improve the code by improving DataFlavor stuff...

		try {

			DataFlavor favoriteDataFlavor = t.getTransferDataFlavors()[0];

			if (favoriteDataFlavor.equals(MyNodeTransferable.myNodeFlavor)) {
				if (t.getTransferDataFlavors()[0].getParameter("name").equals(
						(MyNodeTransferable.myNodeFlavor.getParameter("name")))) {
					MyNode myNode = (MyNode) t
							.getTransferData(MyNodeTransferable.myNodeFlavor);
					if (myNode.getType() == MyNode.sqlquery) {
						query = myNode.getQuery();
					} else {
						query = myNode.toString();
					}

				} else if (t.getTransferDataFlavors()[0].getParameter("name")
						.equals(
								(LayerTransferable.layerFlavor
										.getParameter("name")))) {
					query = (String) t.getTransferData(DataFlavor.stringFlavor);

				}
			}

			else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
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
