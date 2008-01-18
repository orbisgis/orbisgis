package org.orbisgis.core.actions;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class JActionMenuBar extends JMenuBar implements IActionControl {

	public JActionMenuBar() {
		ActionControlsRegistry.addActionControl(this);
	}

	public void refresh() {
		for (int i = 0; i < getMenuCount(); i++) {
			checkVisibility(getMenu(i));
		}
	}

	private void checkVisibility(JMenu menu) {
		boolean visible = false;
		for (int i = 0; i < menu.getMenuComponentCount(); i++) {
			Component menuComp = menu.getMenuComponent(i);
			if (menuComp instanceof JMenu) {
				checkVisibility((JMenu) menuComp);
			} else if (menuComp instanceof IActionControl) {
				((IActionControl) menuComp).refresh();
				if (menuComp.isVisible()) {
					visible = true;
				}
			}
		}

		menu.setVisible(visible);
	}

}
