/**
 * 
 */
package org.orbisgis.plugin.view.ui.workbench;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

class PopupListener extends MouseAdapter {
	private JPopupMenu popup;

	PopupListener(JPopupMenu popupMenu) {
		popup = popupMenu;
	}

	public void mousePressed(MouseEvent e) {
		ShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		ShowPopup(e);
	}

	private void ShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}