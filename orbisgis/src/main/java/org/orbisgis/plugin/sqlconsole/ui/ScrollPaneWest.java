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

import org.orbisgis.plugin.view.ui.workbench.geocatalog.MyNode;
import org.orbisgis.plugin.view.ui.workbench.geocatalog.MyNodeTransferable;

public class ScrollPaneWest extends JScrollPane implements DropTargetListener{


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
			jTextArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			jTextArea.setDropTarget(new DropTarget(this, this));

		}
		return jTextArea;
	}
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub

	}
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}
	public void drop(DropTargetDropEvent dtde) {
		
        Transferable t = dtde.getTransferable();
        String query = null;
		
		try {
			
			if (t.isDataFlavorSupported(MyNodeTransferable.myNodeFlavor)) {
				MyNode myNode = (MyNode) t.getTransferData(MyNodeTransferable.myNodeFlavor);
				if (myNode.getType()==MyNode.sqlquery) {
					query = myNode.getQuery();
				} else {
					query = myNode.toString();
				}
			
			} else  if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            	dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String s = (String)t.getTransferData(DataFlavor.stringFlavor);
                dtde.getDropTargetContext().dropComplete(true);         
                query = SQLConsolePanel.getQuery(s);
            }
        } catch (IOException e) {
        	dtde.rejectDrop();
        } catch (UnsupportedFlavorException e) {
        	dtde.rejectDrop();
        }
        
        if (query !=null) {
        	//Cursor position
        	int position = jTextArea.getCaretPosition();
        	jTextArea.insert(query, position);
			//Replace the cursor at end line
            jTextArea.requestFocus();
        }
        dtde.rejectDrop();

	}
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}






}
