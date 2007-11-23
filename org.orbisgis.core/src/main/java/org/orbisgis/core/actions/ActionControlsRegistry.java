package org.orbisgis.core.actions;

import java.util.ArrayList;

public class ActionControlsRegistry {

	private static ArrayList<IActionControl> controls = new ArrayList<IActionControl>();

	public static void addActionControl(IActionControl actionControl) {
		controls.add(actionControl);
	}

	public static void refresh() {
		for (IActionControl control : controls) {
			control.refresh();
		}
	}
}
