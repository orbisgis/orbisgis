package org.orbisgis.geoview;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.core.windows.EPWindowHelper;
import org.orbisgis.core.windows.IWindow;
import org.orbisgis.geoview.basic.persistence.Menu;
import org.orbisgis.geoview.fromXmlToSQLTree.ToolsMenuPanel;
import org.orbisgis.pluginManager.PluginActivator;

public class Register implements PluginActivator {
	private final static Menu menu = new Menu();
	private final static URL XML_FILE_URL = Register.class
			.getResource("OGCSQLQueries.xml");

	static {
		try {
			addSubMenu(XML_FILE_URL);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void start() throws Exception {
		// menu = EPFromXmlToSqlTreeHelper.install();//TODO not static
	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}

	public static Menu getMenu() {
		return menu;
	}

	public static void addSubMenu(final URL xmlFileUrl) throws JAXBException {
		final Menu subMenu = (Menu) JAXBContext.newInstance(
				"org.orbisgis.geoview.basic.persistence",
				Register.class.getClassLoader()).createUnmarshaller()
				.unmarshal(xmlFileUrl);
		menu.getMenuOrMenuItem().add(subMenu);

		final IWindow[] iWindows = EPWindowHelper
				.getWindows("org.orbisgis.geoview.Window");
		if ((null != iWindows) && (0 < iWindows.length)) {
			final GeoView2D geoview = (GeoView2D) iWindows[0];
			if (null != geoview) {
				final ToolsMenuPanel toolsMenuPanel = (ToolsMenuPanel) geoview
						.getView("org.orbisgis.geoview.fromXmlToSQLTree.ToolsMenuPanelView");
				if (null != toolsMenuPanel) {
					toolsMenuPanel.refresh();
				}
			}
		}
	}
}