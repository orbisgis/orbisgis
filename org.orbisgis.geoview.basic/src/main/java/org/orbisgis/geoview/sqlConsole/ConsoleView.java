package org.orbisgis.geoview.sqlConsole;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;
import org.orbisgis.geoview.sqlConsole.ui.SQLConsolePanel;

public class ConsoleView implements IView {

	public Component getComponent(GeoView2D geoview) {
		return new SQLConsolePanel(geoview);
	}

	public void loadStatus(InputStream ois) {
	}

	public void saveStatus(OutputStream oos) {
	}

	public void delete() {

	}

}
