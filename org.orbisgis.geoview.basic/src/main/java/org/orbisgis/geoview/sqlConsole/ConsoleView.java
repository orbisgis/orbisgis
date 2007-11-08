package org.orbisgis.geoview.sqlConsole;

import java.awt.Component;

import javax.swing.JLabel;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;
import org.orbisgis.geoview.sqlConsole.ui.SQLConsolePanel;

public class ConsoleView implements IView {

	public Component getComponent(GeoView2D geoview) {
		return new SQLConsolePanel(geoview);
	}

}
