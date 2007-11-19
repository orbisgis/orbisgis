package org.orbisgis.core.errorListener;

import javax.swing.JTextPane;

public class NoWrapTextPane extends JTextPane {

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

}
