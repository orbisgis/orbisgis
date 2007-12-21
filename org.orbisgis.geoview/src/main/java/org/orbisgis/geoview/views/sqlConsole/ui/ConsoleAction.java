package org.orbisgis.geoview.views.sqlConsole.ui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class ConsoleAction {
	public final static int EXECUTE = 110;
	public final static int CLEAR = 111;
	public final static int STOP = 112;
	public final static int PREVIOUS = 113;
	public final static int NEXT = 114;
	public final static int OPEN = 115;
	public final static int SAVE = 116;

	private static class InternalConsoleAction {
		ImageIcon icon;
		String toolTipText;

		InternalConsoleAction(final String icon, final String toolTipText) {
			this.icon = new ImageIcon(getClass().getResource(icon));
			this.toolTipText = toolTipText;
		}
	}

	private static Map<Integer, InternalConsoleAction> mapOfActions;

	static {
		mapOfActions = new HashMap<Integer, InternalConsoleAction>(7);

		mapOfActions.put(EXECUTE, new InternalConsoleAction("Execute.png",
				"Click to execute query"));
		mapOfActions.put(CLEAR, new InternalConsoleAction("Erase.png",
				"Clear console"));
		mapOfActions.put(STOP, new InternalConsoleAction("Stop.png",
				"Stop the query"));
		mapOfActions.put(PREVIOUS, new InternalConsoleAction("go-previous.png",
				"Previous query"));
		mapOfActions.put(NEXT, new InternalConsoleAction("go-next.png",
				"Next query"));
		mapOfActions.put(OPEN, new InternalConsoleAction("Open.png",
				"Open an already saved SQL script"));
		mapOfActions.put(SAVE, new InternalConsoleAction("Save.png",
				"Save current console"));
	}

	public static ImageIcon getImageIcon(final int type) {
		return mapOfActions.get(type).icon;
	}

	public static String getToolTipText(final int type) {
		return mapOfActions.get(type).toolTipText;
	}
}