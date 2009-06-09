package org.orbisgis.core.ui.actions;

import java.io.IOException;
import java.io.ObjectInputStream;

import net.infonode.docking.RootWindow;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.action.IAction;
import org.orbisgis.core.ui.windows.mainFrame.OrbisGISFrame;
import org.orbisgis.core.ui.windows.mainFrame.UIManager;

public class DefaultPerspective implements IAction {

	private static final String LAYOUT_PERSISTENCE_FILE = "org.orbisgis.core.ui.ViewLayout.obj";


	@Override
	public void actionPerformed() {
		UIManager ui = Services.getService(UIManager.class);

		RootWindow rootWindow = ui.getRoot();

		// Load the layout from a byte array
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(OrbisGISFrame.class
					.getResourceAsStream(LAYOUT_PERSISTENCE_FILE));

			rootWindow.read(in, true);
	        in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

}
