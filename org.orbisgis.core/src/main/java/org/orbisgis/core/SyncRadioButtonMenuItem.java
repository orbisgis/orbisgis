package org.orbisgis.core;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SyncRadioButtonMenuItem extends JRadioButtonMenuItem {

	private boolean ignoreUpdate = false;

	public SyncRadioButtonMenuItem(String text, final JToggleButton btn) {
		super(text);
		btn.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (!ignoreUpdate) {
					setSelected(btn.isSelected());
				}
			}

		});

		this.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				ignoreUpdate = true;
				btn.setSelected(isSelected());
				ignoreUpdate = false;
			}

		});
	}
}
