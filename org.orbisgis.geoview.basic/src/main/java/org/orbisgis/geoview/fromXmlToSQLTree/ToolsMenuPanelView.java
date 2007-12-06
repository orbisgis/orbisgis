package org.orbisgis.geoview.fromXmlToSQLTree;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBException;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class ToolsMenuPanelView implements IView {
	public Component getComponent(GeoView2D geoview) {
		try {
			return new ToolsMenuPanel(geoview);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public void loadStatus(InputStream ois) {

	}

	public void saveStatus(OutputStream oos) {

	}

	public void delete() {

	}

	public void initialize(GeoView2D geoView2D) {

	}
}