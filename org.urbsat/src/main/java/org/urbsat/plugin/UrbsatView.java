package org.urbsat.plugin;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBException;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;
import org.urbsat.plugin.ui.UrbSATPanel;

public class UrbsatView implements IView {
	public Component getComponent(GeoView2D geoview) {
		try {
			return new UrbSATPanel(geoview);
		} catch (JAXBException e) {
			// throw new WarningException(e);
			throw new RuntimeException(e);
		}
	}

	public void loadStatus(InputStream ois) {

	}

	public void saveStatus(OutputStream oos) {

	}
}