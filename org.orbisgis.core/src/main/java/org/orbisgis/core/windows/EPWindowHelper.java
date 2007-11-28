package org.orbisgis.core.windows;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.core.persistence.PersistenceException;
import org.orbisgis.core.persistence.Window;
import org.orbisgis.core.persistence.Windows;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.workspace.Workspace;

public class EPWindowHelper {

	private static final String BASE_CONF = "/extension/window";
	private static final String EXTENSION_ID = "org.orbisgis.Window";
	private static HashMap<String, ArrayList<WindowDecorator>> windowsById = new HashMap<String, ArrayList<WindowDecorator>>();

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
		register(id, wnd, null);
		return wnd;
	}

	private static void register(String id, IWindow wnd, File infoFile) {
		ArrayList<WindowDecorator> wndLlist = windowsById.get(id);
		if (wndLlist == null) {
			wndLlist = new ArrayList<WindowDecorator>();
		}
		wndLlist.add(new WindowDecorator(wnd, infoFile));
		windowsById.put(id, wndLlist);
	}

	public static IWindow[] getWindows(String id) {
		ArrayList<WindowDecorator> ret = windowsById.get(id);
		if (ret == null) {
			return new IWindow[0];
		} else {
			WindowDecorator[] decs = new WindowDecorator[0];
			IWindow[] wnds = new IWindow[decs.length];
			for (int i = 0; i < wnds.length; i++) {
				wnds[i] = decs[i].getWindow();
			}

			return wnds;
		}
	}

	public static IWindow createWindow(String id) {
		return instantiate(id);
	}

	public static void saveStatus(Workspace workspace) {
		Windows wnds = new Windows();
		Iterator<String> it = windowsById.keySet().iterator();
		while (it.hasNext()) {
			String wndId = it.next();
			ArrayList<WindowDecorator> wndList = windowsById.get(wndId);
			for (WindowDecorator decorator : wndList) {
				IWindow window = decorator.getWindow();
				Window wnd = new Window();
				wnd.setClazz(window.getClass().getCanonicalName());
				wnd.setId(wndId);
				Rectangle position = window.getPosition();
				wnd.setX(Integer.toString(position.x));
				wnd.setY(Integer.toString(position.y));
				wnd.setWidth(Integer.toString(position.width));
				wnd.setHeight(Integer.toString(position.height));
				wnd.setOpen(Boolean.toString(window.isOpened()));
				File filePath = decorator.getFile();
				if (filePath == null) {
					filePath = workspace.createNewFile("window", ".xml");
				}
				try {
					window.save(filePath);
					wnd.setInfoFile(filePath.getPath());
					wnds.getWindow().add(wnd);
				} catch (PersistenceException e) {
					PluginManager.error(
							"Cannot save the status of the window " + wndId, e);
				}
			}
		}

		try {
			JAXBContext jc = JAXBContext.newInstance(
					"org.orbisgis.core.persistence", EPWindowHelper.class
							.getClassLoader());
			File file = workspace.getFile("windows.xml");

			jc.createMarshaller().marshal(wnds, new PrintWriter(file));
		} catch (JAXBException e) {
			PluginManager.error("Bug! cannot serialize xml", e);
		} catch (FileNotFoundException e) {
			PluginManager.error("Cannot write in the workspace directory", e);
		}

	}

	public static void loadStatus(Workspace workspace) {
		File file = workspace.getFile("windows.xml");
		if (file.exists()) {
			try {
				JAXBContext jc = JAXBContext.newInstance(
						"org.orbisgis.core.persistence", EPWindowHelper.class
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
					String infoFile = window.getInfoFile();
					try {
						IWindow iWindow = (IWindow) Class.forName(clazz)
								.newInstance();
						iWindow.load(new File(infoFile));
						iWindow.setPosition(position);
						register(id, iWindow, new File(infoFile));
						if (open) {
							iWindow.showWindow();
						}
					} catch (InstantiationException e) {
						PluginManager.error("Cannot recover window. id = " + id
								+ " class = " + clazz, e);
					} catch (IllegalAccessException e) {
						PluginManager.error("Cannot recover window. id = " + id
								+ " class = " + clazz, e);
					} catch (ClassNotFoundException e) {
						PluginManager.error("Cannot recover window. id = " + id
								+ " class = " + clazz, e);
					} catch (PersistenceException e) {
						PluginManager.error("Cannot recover window. id = " + id
								+ " class = " + clazz, e);
					}
				}
			} catch (JAXBException e) {
				PluginManager.error("Cannot read the xml file:" + file, e);
			}
		} else {
			showInitial();
		}
	}
}
