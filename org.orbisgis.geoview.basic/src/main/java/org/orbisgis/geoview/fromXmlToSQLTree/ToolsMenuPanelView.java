package org.orbisgis.geoview.fromXmlToSQLTree;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;

public class ToolsMenuPanelView implements IView {
	private ToolsMenuPanel toolsMenuPanel;

	public Component getComponent(GeoView2D geoview) {
		try {
			toolsMenuPanel = new ToolsMenuPanel(geoview);
			return toolsMenuPanel;
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

	public void addSubMenus(final URL xmlFileUrl) {
		// TODO
	}
}