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

	private static void register(String id, IWindow wnd,
			HashMap<String, File> infoFiles) {
		ArrayList<WindowDecorator> wndLlist = windowsById.get(id);
		if (wndLlist == null) {
			wndLlist = new ArrayList<WindowDecorator>();
		}
		wndLlist.add(new WindowDecorator(wnd, infoFiles));
		windowsById.put(id, wndLlist);
	}

	public static IWindow[] getWindows(String id) {
		ArrayList<WindowDecorator> ret = windowsById.get(id);
		if (ret == null) {
			return new IWindow[0];
		} else {
			WindowDecorator[] decs = ret.toArray(new WindowDecorator[0]);
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
				HashMap<String, File> filePaths = decorator.getFiles();
				PersistenceContext pc = new PersistenceContext(filePaths);
				try {
					window.save(pc);
					addFiles(wnd, pc);
					wnds.getWindow().add(wnd);
				} catch (PersistenceException e) {
					PluginManager.error("Cannot save the status of the window "
							+ wndId, e);
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

	private static void addFiles(Window wnd, PersistenceContext pc) {
		Iterator<String> it = pc.getFileNames();
		while (it.hasNext()) {
			String fileName = it.next();
			File file = pc.getFile(fileName);
			org.orbisgis.core.persistence.File f = new org.orbisgis.core.persistence.File();
			f.setFileName(fileName);
			f.setPath(file.getAbsolutePath());
			wnd.getFile().add(f);
		}
	}

	public static void loadStatus(Workspace workspace) {
		cleanWindows();
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
					try {
						IWindow iWindow = (IWindow) Class.forName(clazz)
								.newInstance();
						HashMap<String, File> files = getFileMapping(window);
						PersistenceContext pc = new PersistenceContext(files);
						iWindow.load(pc);
						iWindow.setPosition(position);
						register(id, iWindow, files);
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

	private static void cleanWindows() {
		Iterator<String> wndIds = windowsById.keySet().iterator();
		while (wndIds.hasNext()) {
			String id = wndIds.next();
			ArrayList<WindowDecorator> windowList = windowsById.get(id);
			for (WindowDecorator windowDecorator : windowList) {
				windowDecorator.getWindow().delete();
			}
		}
		windowsById = new HashMap<String, ArrayList<WindowDecorator>>();
	}

	private static HashMap<String, File> getFileMapping(Window window) {
		List<org.orbisgis.core.persistence.File> files = window.getFile();
		HashMap<String, File> ret = new HashMap<String, File>();
		for (org.orbisgis.core.persistence.File file : files) {
			ret.put(file.getFileName(), new File(file.getPath()));
		}

		return ret;
	}

}
