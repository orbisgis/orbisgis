/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.window;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;
import org.orbisgis.pluginManager.workspace.Workspace;
import org.orbisgis.window.persistence.Property;
import org.orbisgis.window.persistence.Window;
import org.orbisgis.window.persistence.Windows;

public class EPWindowHelper {

	private static final String BASE_CONF = "/extension/window";
	private static final String EXTENSION_ID = "org.orbisgis.Window";
	private static HashMap<String, ArrayList<IWindow>> windowsById = new HashMap<String, ArrayList<IWindow>>();

	public static void showInitial() {
		ExtensionPointManager<IWindow> epm = new ExtensionPointManager<IWindow>(
				EXTENSION_ID);
		ArrayList<ItemAttributes<IWindow>> itemAttributes = epm
				.getItemAttributes(BASE_CONF);
		for (ItemAttributes<IWindow> attrs : itemAttributes) {
			String id = attrs.getAttribute("id");
			String newOnStartup = attrs.getAttribute("newOnStartup");
			if (newOnStartup != null) {
				if (newOnStartup.equals("true")) {
					instantiate(id).showWindow();
				}
			}
		}
	}

	public static IWindow newWindow(String id) {
		return instantiate(id);
	}

	private static IWindow instantiate(String id) {
		ExtensionPointManager<IWindow> epm = new ExtensionPointManager<IWindow>(
				EXTENSION_ID);
		ArrayList<ItemAttributes<IWindow>> itemAttributes = epm
				.getItemAttributes(BASE_CONF + "[@id='" + id + "']");
		IWindow wnd = itemAttributes.get(0).getInstance("class");
		register(id, wnd);
		return wnd;
	}

	private static void register(String id, IWindow wnd) {
		ArrayList<IWindow> wndLlist = windowsById.get(id);
		if (wndLlist == null) {
			wndLlist = new ArrayList<IWindow>();
		}
		wndLlist.add(wnd);
		windowsById.put(id, wndLlist);
	}

	public static IWindow[] getWindows(String id) {
		ArrayList<IWindow> ret = windowsById.get(id);
		if (ret == null) {
			return new IWindow[0];
		} else {
			return ret.toArray(new IWindow[0]);
		}
	}

	public static IWindow createWindow(String id) {
		return instantiate(id);
	}

	public static void saveStatus() {
		Windows wnds = new Windows();
		Iterator<String> it = windowsById.keySet().iterator();
		while (it.hasNext()) {
			String wndId = it.next();
			ArrayList<IWindow> wndList = windowsById.get(wndId);
			for (IWindow window : wndList) {
				Window wnd = new Window();
				wnd.setClazz(window.getClass().getCanonicalName());
				wnd.setId(wndId);
				Rectangle position = window.getPosition();
				wnd.setX(Integer.toString(position.x));
				wnd.setY(Integer.toString(position.y));
				wnd.setWidth(Integer.toString(position.width));
				wnd.setHeight(Integer.toString(position.height));
				wnd.setOpen(Boolean.toString(window.isOpened()));
				try {
					Map<String, String> properties = window.save();
					if (properties != null) {
						addProperties(wnd, properties);
					}
					wnds.getWindow().add(wnd);
				} catch (PersistenceException e) {
					Services.getErrorManager().error("Cannot save the status of the window "
							+ wndId, e);
				}
			}
		}

		try {
			JAXBContext jc = JAXBContext.newInstance(
					"org.orbisgis.window.persistence", EPWindowHelper.class
							.getClassLoader());
			Workspace ws = (Workspace) Services
					.getService("org.orbisgis.Workspace");
			File file = ws.getFile("windows.xml");

			jc.createMarshaller().marshal(wnds, new PrintWriter(file));
		} catch (JAXBException e) {
			Services.getErrorManager().error("Bug! cannot serialize xml", e);
		} catch (FileNotFoundException e) {
			Services.getErrorManager().error("Cannot write in the workspace directory", e);
		}

	}

	private static void addProperties(Window wnd, Map<String, String> properties) {
		Iterator<String> it = properties.keySet().iterator();
		while (it.hasNext()) {
			String propertyName = it.next();
			String propertyValue = properties.get(propertyName);
			Property property = new Property();
			property.setName(propertyName);
			property.setValue(propertyValue);
			wnd.getProperty().add(property);
		}
	}

	public static void loadStatus() {
		cleanWindows();
		Workspace ws = (Workspace) Services
				.getService("org.orbisgis.Workspace");
		File file = ws.getFile("windows.xml");
		if (file.exists()) {
			try {
				JAXBContext jc = JAXBContext.newInstance(
						"org.orbisgis.window.persistence", EPWindowHelper.class
								.getClassLoader());
				Windows wnds = (Windows) jc.createUnmarshaller()
						.unmarshal(file);
				List<Window> windowList = wnds.getWindow();
				for (Window window : windowList) {
					String id = window.getId();
					String clazz = window.getClazz();
					Rectangle position = new Rectangle(Integer.parseInt(window
							.getX()), Integer.parseInt(window.getY()), Integer
							.parseInt(window.getWidth()), Integer
							.parseInt(window.getHeight()));
					boolean open = Boolean.parseBoolean(window.getOpen());
					try {
						IWindow iWindow = (IWindow) Class.forName(clazz)
								.newInstance();
						Map<String, String> properties = getProperties(window);
						iWindow.load(properties);
						iWindow.setPosition(position);
						register(id, iWindow);
						if (open) {
							iWindow.showWindow();
						}
					} catch (Exception e) {
						Services.getErrorManager().error("Cannot recover window. id = " + id
								+ " class = " + clazz, e);
					}
				}
			} catch (JAXBException e) {
				Services.getErrorManager().error("Cannot read the xml file:" + file, e);
			}
		} else {
			showInitial();
		}
	}

	private static void cleanWindows() {
		Iterator<String> wndIds = windowsById.keySet().iterator();
		while (wndIds.hasNext()) {
			String id = wndIds.next();
			ArrayList<IWindow> windowList = windowsById.get(id);
			for (IWindow window : windowList) {
				window.delete();
			}
		}
		windowsById = new HashMap<String, ArrayList<IWindow>>();
	}

	private static Map<String, String> getProperties(Window window) {
		List<Property> properties = window.getProperty();
		HashMap<String, String> ret = new HashMap<String, String>();
		for (Property prop : properties) {
			ret.put(prop.getName(), prop.getValue());
		}

		return ret;
	}

}
