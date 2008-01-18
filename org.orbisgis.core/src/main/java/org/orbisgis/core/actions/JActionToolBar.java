package org.orbisgis.core.actions;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JToolBar;

public class JActionToolBar extends JToolBar implements IActionControl {

	public JActionToolBar(String text) {
		super(text);
		ActionControlsRegistry.addActionControl(this);
	}

	public void refresh() {
		for (int i = 0; i < getComponentCount(); i++) {
			checkVisibility((JComponent) getComponent(i));
		}
	}

	private void checkVisibility(JComponent toolBar) {
		boolean visible = false;
		for (int i = 0; i < toolBar.getComponentCount(); i++) {
			Component comp = toolBar.getComponent(i);
			if (comp instanceof JToolBar) {
				checkVisibility((JToolBar) comp);
			} else if (comp instanceof IActionControl) {
				((IActionControl) comp).refresh();
				if (comp.isVisible()) {
					visible = true;
				}
			}
		}

		toolBar.setVisible(visible);
	}

}
