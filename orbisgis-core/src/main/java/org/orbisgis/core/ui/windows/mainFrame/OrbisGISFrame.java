/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.windows.mainFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;

import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.ui.action.ActionControlsRegistry;
import org.orbisgis.core.ui.action.EPActionHelper;
import org.orbisgis.core.ui.action.EPBaseActionHelper;
import org.orbisgis.core.ui.action.IAction;
import org.orbisgis.core.ui.action.IActionAdapter;
import org.orbisgis.core.ui.action.IActionFactory;
import org.orbisgis.core.ui.action.IMenuActionControl;
import org.orbisgis.core.ui.action.ISelectableAction;
import org.orbisgis.core.ui.action.ISelectableActionAdapter;
import org.orbisgis.core.ui.action.JActionMenuBar;
import org.orbisgis.core.ui.action.JActionMenuItem;
import org.orbisgis.core.ui.action.JActionToolBar;
import org.orbisgis.core.ui.action.MenuTree;
import org.orbisgis.core.ui.action.ToolBarArray;
import org.orbisgis.core.ui.editor.EditorListener;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editor.IExtensionPointEditor;
import org.orbisgis.core.ui.editor.action.IEditorAction;
import org.orbisgis.core.ui.editor.action.ISelectableEditorAction;
import org.orbisgis.core.ui.view.EPViewHelper;
import org.orbisgis.core.ui.view.ViewDecorator;
import org.orbisgis.core.ui.view.ViewManager;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.core.ui.window.IWindow;
import org.orbisgis.images.IconLoader;
import org.orbisgis.core.workspace.Workspace;


public class OrbisGISFrame extends JFrame implements IWindow, ViewManager,
		UIManager {

	private static final Logger logger = Logger.getLogger(OrbisGISFrame.class);

	private static final String LAYOUT_PERSISTENCE_FILE = "org.orbisgis.core.ui.ViewLayout.obj";

	private JActionMenuBar menuBar;

	private JActionToolBar mainToolBar;

	private ArrayList<ViewDecorator> views = new ArrayList<ViewDecorator>();

	private RootWindow root;

	private MyViewSerializer viewSerializer = new MyViewSerializer();

	private boolean perspectiveLoaded = false;

	public OrbisGISFrame() {

		// Init mapcontrol and fixed ui components
		mainToolBar = new JActionToolBar("OrbisGIS main tools");
		mainToolBar.setFloatable(true);
		menuBar = new JActionMenuBar();
		this.setLayout(new BorderLayout());
		this.getContentPane().add(mainToolBar, BorderLayout.PAGE_START);

		// Initialize views
		root = new RootWindow(viewSerializer);

		root.getRootWindowProperties().getSplitWindowProperties()
				.setContinuousLayoutEnabled(false);

		root.getRootWindowProperties().getTabWindowProperties()
				.getTabProperties().getFocusedProperties()
				.getComponentProperties().setBackgroundColor(
						new Color(100, 140, 190));

		//Some options for window properties
		root.getRootWindowProperties().getTabWindowProperties()
		.getCloseButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties().getDockButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties().getMaximizeButtonProperties().setVisible(false);
		root.getRootWindowProperties().getTabWindowProperties().getUndockButtonProperties().setVisible(false);

		DockingWindowsTheme theme = new ShapedGradientDockingTheme();
		// Apply theme
		root.getRootWindowProperties().addSuperObject(
		  theme.getRootWindowProperties());


		this.getContentPane().add(root, BorderLayout.CENTER);

		// Prepare menu and toolbar
		this.setJMenuBar(menuBar);
		MenuTree menuTree = new MenuTree();
		ToolBarArray toolBarArray = new ToolBarArray();
		EPBaseActionHelper.configureParentMenusAndToolBars(new String[] {
				"org.orbisgis.core.ui.Action",
				"org.orbisgis.core.ui.editor.Action" }, menuTree, toolBarArray);

		// Read editors and install editors extensions
		ArrayList<ViewDecorator> editorViews = EPViewHelper.getViewsInfo(
				"org.orbisgis.core.ui.Editor", "editor");
		prepareEditorExtensionPoints(editorViews, menuTree, toolBarArray);

		// Install view and editor actions
		IActionFactory actionFactory = new GeoviewActionFactory();
		IActionFactory editorActionFactory = new EditorActionFactory();
		EPBaseActionHelper.configureMenuAndToolBar(
				"org.orbisgis.core.ui.Action", "action", actionFactory,
				menuTree, toolBarArray);
		EPBaseActionHelper.configureMenuAndToolBar(
				"org.orbisgis.core.ui.editor.Action", "editor-action",
				editorActionFactory, menuTree, toolBarArray);
		installEditorExtensionPoints(editorViews, menuTree, toolBarArray);

		// Read views and editorViews and install them on the view menu
		views = EPViewHelper.getViewsInfo("org.orbisgis.core.ui.View", "view");
		views.addAll(EPViewHelper.getViewsInfo(
				"org.orbisgis.core.ui.EditorView", "editor-view"));
		EPViewHelper
				.addViewMenu(menuTree, root, new ViewActionFactory(), views);

		// Initialize views and editors
		views.addAll(editorViews);
		initializeViews();

		// Install action controls
		JComponent[] menus = menuTree.getJMenus();
		for (int i = 0; i < menus.length; i++) {
			menuBar.add(menus[i]);
		}
		for (JToolBar toolbar : toolBarArray.getToolBars()) {
			toolbar.setFloatable(true);
			mainToolBar.add(toolbar);
		}

		this.setTitle("OrbisGIS Platform");
		this.setIconImage(IconLoader.getIcon("mini_orbisgis.png").getImage());
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		this.setSize((int) screenSize.width, (int) screenSize.height);
		this.setLocationRelativeTo(null);
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				EPActionHelper
						.executeAction("org.orbisgis.core.ui.actions.Exit");
			}

		});



		Services.registerService(ViewManager.class,
				"Open, close and gets access to the views", this);

		Services.registerService(UIManager.class,
				"Gets access to the main frame", this);

	}

	private void installEditorExtensionPoints(
			ArrayList<ViewDecorator> editorViews, MenuTree menuTree,
			ToolBarArray toolBarArray) {
		for (ViewDecorator viewDecorator : editorViews) {
			if (viewDecorator.getView() instanceof IExtensionPointEditor) {
				IExtensionPointEditor editor = (IExtensionPointEditor) viewDecorator
						.getView();
				editor.installExtensionPoint(menuTree, toolBarArray);
			}
		}
	}

	private void prepareEditorExtensionPoints(
			ArrayList<ViewDecorator> editorViews, MenuTree menuTree,
			ToolBarArray toolBarArray) {
		for (ViewDecorator viewDecorator : editorViews) {
			if (viewDecorator.getView() instanceof IExtensionPointEditor) {
				IExtensionPointEditor editor = (IExtensionPointEditor) viewDecorator
						.getView();
				editor.prepareMenus(menuTree, toolBarArray);
			}
		}
	}

	private void initializeViews() {
		for (ViewDecorator view : views) {
			try {
				view.getView().initialize();
			} catch (Exception e) {
				Services.getErrorManager().error(
						"Error initializating view " + view.getTitle(), e);
			}
		}

		final EditorManager em = Services.getService(EditorManager.class);
		if (em == null) {
			throw new RuntimeException(
					"A view must initialize the EditorManager service");
		}

		em.addEditorListener(new EditorListener() {

			public void activeEditorChanged(IEditor previous, IEditor current) {
				refreshUI();
				IEditor activeEditor = em.getActiveEditor();
				for (ViewDecorator view : views) {
					view.editorChanged(activeEditor, em
							.getEditorId(activeEditor));
				}
			}

			public void activeEditorClosed(IEditor editor, String editorId) {
				for (ViewDecorator view : views) {
					view.editorClosed(editorId);
				}
			}

			@Override
			public boolean activeEditorClosing(IEditor editor, String editorId) {
				return true;
			}

		});
	}

	public void showWindow() {
		if (!perspectiveLoaded) {
			// Load default perspective
			loadPerspective(OrbisGISFrame.class
					.getResourceAsStream(LAYOUT_PERSISTENCE_FILE));
		}
		this.setVisible(true);
	}

	public Component getView(String viewId) {
		ViewDecorator ret = getViewDecorator(viewId);
		if (ret != null) {
			if (!ret.isOpen()) {
				EditorManager em = Services.getService(EditorManager.class);
				IEditor activeEditor = em.getActiveEditor();
				ret.open(root, activeEditor, em.getEditorId(activeEditor));
			}
			return ret.getViewComponent();
		} else {
			return null;
		}
	}

	public void showView(String id) {
		ViewDecorator view = getViewDecorator(id);
		if (view != null) {
			EditorManager em = Services.getService(EditorManager.class);
			IEditor activeEditor = em.getActiveEditor();
			view.open(root, activeEditor, em.getEditorId(activeEditor));
		}
	}

	public void hideView(String id) {
		ViewDecorator view = getViewDecorator(id);
		if (view != null) {
			view.close();
		}
	}

	private ViewDecorator getViewDecorator(String id) {
		for (ViewDecorator view : views) {
			if (view.getId().equals(id)) {
				return view;
			}
		}

		return null;
	}

	public void refreshUI() {
		ActionControlsRegistry.refresh();
	}

	public JFrame getMainFrame() {
		return this;
	}

	private final class ViewActionFactory implements IActionFactory {

		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			throw new RuntimeException("bug");
		}

		public ISelectableActionAdapter getSelectableAction(Object action,
				HashMap<String, String> attributes) {
			return new ViewSelectableAction((String) action);
		}
	}

	private final class ViewSelectableAction implements IActionAdapter,
			ISelectableActionAdapter {

		private String id;
		private ViewDecorator viewDecorator;

		public ViewSelectableAction(String id) {
			this.id = id;
		}

		public void actionPerformed() {
			if (getViewDecorator().isOpen()) {
				getViewDecorator().close();
			} else {
				EditorManager em = Services.getService(EditorManager.class);
				IEditor activeEditor = em.getActiveEditor();
				getViewDecorator().open(root, activeEditor,
						em.getEditorId(activeEditor));
			}
		}

		public boolean isEnabled() {
			return true;
		}

		public boolean isVisible() {
			return true;
		}

		public boolean isSelected() {
			return getViewDecorator().isOpen();
		}

		private ViewDecorator getViewDecorator() {
			if (viewDecorator == null) {
				for (ViewDecorator view : views) {
					if (view.getId().equals(id)) {
						viewDecorator = view;
						break;
					}
				}
			}

			return viewDecorator;
		}
	}

	private final class EditorActionFactory implements IActionFactory {

		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			return new EditorActionDecorator((IEditorAction) action, attributes
					.get("editor-id"));
		}

		public ISelectableActionAdapter getSelectableAction(Object action,
				HashMap<String, String> attributes) {
			return new EditorActionDecorator((IEditorAction) action, attributes
					.get("editor-id"));
		}
	}

	private final class EditorActionDecorator implements IActionAdapter,
			ISelectableActionAdapter {

		private IEditorAction action;
		private String editorId;

		public EditorActionDecorator(IEditorAction action, String editorId) {
			this.action = action;
			this.editorId = editorId;
		}

		public boolean isVisible() {
			EditorManager em = Services.getService(EditorManager.class);
			IEditor activeEditor = em.getActiveEditor();
			if (activeEditor == null) {
				return false;
			} else {
				if (editorId.equals(em.getEditorId(activeEditor))) {
					return action.isVisible(activeEditor);
				} else {
					return false;
				}
			}
		}

		public boolean isEnabled() {
			EditorManager em = Services.getService(EditorManager.class);
			IEditor activeEditor = em.getActiveEditor();
			if (activeEditor == null) {
				return false;
			} else {
				if (editorId.equals(em.getEditorId(activeEditor))) {
					return action.isEnabled(activeEditor);
				} else {
					return false;
				}
			}
		}

		public void actionPerformed() {
			action.actionPerformed(Services.getService(EditorManager.class)
					.getActiveEditor());
		}

		public boolean isSelected() {
			EditorManager em = Services.getService(EditorManager.class);
			IEditor activeEditor = em.getActiveEditor();
			if (activeEditor == null) {
				return false;
			} else {
				if (editorId.equals(em.getEditorId(activeEditor))) {
					return ((ISelectableEditorAction) action)
							.isSelected(activeEditor);
				} else {
					return false;
				}
			}
		}
	}

	private final class GeoviewActionFactory implements IActionFactory {

		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			return new IGeoviewActionDecorator(action);
		}

		public ISelectableActionAdapter getSelectableAction(Object action,
				HashMap<String, String> attributes) {
			return new IGeoviewActionDecorator(action);
		}
	}

	private final class IGeoviewActionDecorator implements IActionAdapter,
			ISelectableActionAdapter {

		private IAction action;

		public IGeoviewActionDecorator(Object action) {
			this.action = (IAction) action;
		}

		public boolean isVisible() {
			return action.isVisible();
		}

		public boolean isEnabled() {
			return action.isEnabled();
		}

		public void actionPerformed() {
			action.actionPerformed();
		}

		public boolean isSelected() {
			return ((ISelectableAction) action).isSelected();
		}
	}

	public Rectangle getPosition() {
		return this.getBounds();
	}

	public boolean isOpened() {
		return this.isVisible();
	}

	public void load(Map<String, String> properties)
			throws PersistenceException {
		// we override the default layout
		this.getContentPane().remove(root);
		root = new RootWindow(viewSerializer);

		DockingWindowsTheme theme = new ShapedGradientDockingTheme();

		// Apply theme
		root.getRootWindowProperties().addSuperObject(
		  theme.getRootWindowProperties());

		this.getContentPane().add(root, BorderLayout.CENTER);

		Workspace ws = (Workspace) Services.getService(Workspace.class);
		FileInputStream layoutStream;
		try {
			layoutStream = new FileInputStream(ws
					.getFile(LAYOUT_PERSISTENCE_FILE));
			loadPerspective(layoutStream);
		} catch (FileNotFoundException e) {
			logger.error("Could not recover perspective, missing file", e);
		}
	}

	private void loadPerspective(InputStream layoutStream) {
		try {
			ObjectInputStream ois = new ObjectInputStream(layoutStream);
			root.read(ois);
			perspectiveLoaded = true;
			ois.close();
		} catch (Exception e) {
			Services.getErrorManager().error(
					"Cannot recover the layout of the window", e);
		}
	}

	public Map<String, String> save() throws PersistenceException {
		try {
			Workspace ws = (Workspace) Services.getService(Workspace.class);
			FileOutputStream fos = new FileOutputStream(ws
					.getFile(LAYOUT_PERSISTENCE_FILE));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			root.write(oos);
			oos.close();
		} catch (IOException e) {
			throw new PersistenceException(e);
		}

		return null;
	}

	public void setPosition(Rectangle position) {
		this.setBounds(position);
	}

	/**
	 * Writes the id of the view and then writes the status. Reads the id,
	 * obtains the data from the extension xml and reads the status
	 *
	 * @author Fernando Gonzalez Cortes
	 */
	private class MyViewSerializer implements ViewSerializer {

		public View readView(ObjectInputStream ois) throws IOException {
			String id = ois.readUTF();
			ViewDecorator vd = OrbisGISFrame.this.getViewDecorator(id);
			if (vd != null) {
				try {
					EditorManager em = Services.getService(EditorManager.class);
					IEditor activeEditor = em.getActiveEditor();
					vd.loadStatus(activeEditor, em.getEditorId(activeEditor));
					return vd.getDockingView();
				} catch (Throwable t) {
					Services.getErrorManager().error(
							"Cannot recover view " + id, t);
				}
			}

			return null;
		}

		public void writeView(View view, ObjectOutputStream oos)
				throws IOException {
			ViewDecorator vd = getViewDecorator(view);
			if (vd!=null){
			oos.writeUTF(vd.getId());
			try {
				vd.getView().saveStatus();
			} catch (Throwable e) {
				Services.getErrorManager().error(
						"Cannot save view " + vd.getId(), e);
			}
			}

		}

		private ViewDecorator getViewDecorator(View view) {
			for (ViewDecorator viewDecorator : views) {
				if (viewDecorator.getDockingView() == view) {
					return viewDecorator;
				}

			}

			return null;
		}

	}

	public void delete() {
		this.setVisible(false);
		this.dispose();
		for (ViewDecorator vd : views) {
			if (vd.getViewComponent() != null) {
				vd.getView().delete();
			}
		}
	}

	@Override
	public String[] getInstalledMenuGroups() {
		HashSet<String> ret = new HashSet<String>();
		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			HashSet<IMenuActionControl> actions = fillGroups(menuBar.getMenu(i));
			for (IMenuActionControl actionControl : actions) {
				String group = actionControl.getGroup();
				if (group != null) {
					ret.add(group);
				}
			}
		}

		return ret.toArray(new String[0]);
	}

	private HashSet<IMenuActionControl> fillGroups(Component menuItem) {
		HashSet<IMenuActionControl> ret = new HashSet<IMenuActionControl>();
		if (menuItem instanceof IMenuActionControl) {
			IMenuActionControl actionControl = (IMenuActionControl) menuItem;
			ret.add(actionControl);
		}
		if (menuItem instanceof JMenu) {
			JMenu c = (JMenu) menuItem;
			for (int i = 0; i < c.getMenuComponentCount(); i++) {
				HashSet<IMenuActionControl> aux = fillGroups(c
						.getMenuComponent(i));
				ret.addAll(aux);
			}
		}

		return ret;
	}

	@Override
	public String[] getMenuChildren(String parentMenuId) {
		HashSet<String> ret = new HashSet<String>();
		if (parentMenuId == null) {
			for (int i = 0; i < menuBar.getMenuCount(); i++) {
				JMenu menu = menuBar.getMenu(i);
				if (menu instanceof IMenuActionControl) {
					ret.add(((IMenuActionControl) menu).getId());
				}
			}
		} else {
			IMenuActionControl menu = getActionMenu(parentMenuId);
			if (menu != null && (menu instanceof JMenu)) {
				JMenu m = (JMenu) menu;
				for (int i = 0; i < m.getMenuComponentCount(); i++) {
					Component comp = m.getMenuComponent(i);
					if (comp instanceof IMenuActionControl) {
						ret.add(((IMenuActionControl) comp).getId());
					}
				}
			}
		}

		return ret.toArray(new String[0]);
	}

	@Override
	public String getMenuName(String menuId) {
		IMenuActionControl menu = getActionMenu(menuId);
		if (menu != null) {
			return menu.getText();
		} else {
			return null;
		}
	}

	private IMenuActionControl getActionMenu(String menuId) {
		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			JMenu menu = menuBar.getMenu(i);
			IMenuActionControl ret = getActionMenu(menu, menuId);
			if (ret != null) {
				return ret;
			}
		}

		return null;
	}

	private IMenuActionControl getActionMenu(Component menu, String menuId) {
		if (menu instanceof IMenuActionControl) {
			IMenuActionControl menuActionControl = (IMenuActionControl) menu;
			if (menuActionControl.getId().equals(menuId)) {
				return menuActionControl;
			} else if (menu instanceof JMenu) {
				JMenu m = (JMenu) menu;
				for (int i = 0; i < m.getMenuComponentCount(); i++) {
					Component comp = m.getMenuComponent(i);
					IMenuActionControl ret = getActionMenu(comp, menuId);
					if (ret != null) {
						return ret;
					}
				}
			}
		}

		return null;
	}

	@Override
	public void installMenu(String id, String text, String menuId,
			String group, IActionAdapter actionAdapter) {
		if (menuId == null) {
			throw new IllegalArgumentException("Null menuID");
		}
		IMenuActionControl parent = getActionMenu(menuId);
		if (parent == null) {
			throw new IllegalArgumentException("Menu: " + menuId + " not found");
		} else if (!(parent instanceof JMenu)) {
			throw new IllegalArgumentException("Invalid parent menu: " + menuId);
		} else {
			IMenuActionControl existingMenu = getActionMenu(id);
			if (existingMenu != null) {
				existingMenu.setActionAdapter(actionAdapter);
			} else {
				JActionMenuItem newMenu = new JActionMenuItem(text, group, id,
						actionAdapter);
				JMenu menu = (JMenu) parent;
				boolean inserted = false;
				for (int i = 0; i < menu.getMenuComponentCount(); i++) {
					Component comp = menu.getMenuComponent(i);
					if (comp instanceof IMenuActionControl) {
						IMenuActionControl actionChild = (IMenuActionControl) comp;
						String actionGroup = actionChild.getGroup();
						if ((actionGroup != null) && actionGroup.equals(group)) {
							menu.insert(newMenu, i);
							inserted = true;
							break;
						}
					}
				}
				if (!inserted) {
					menu.addSeparator();
					menu.add(newMenu);
				}
			}
		}
	}

	@Override
	public void uninstallMenu(String id) {
		IMenuActionControl action = getActionMenu(id);
		if (action instanceof JMenuItem) {
			JMenuItem menuItem = (JMenuItem) action;
			menuItem.getParent().remove(menuItem);
		}
	}

	public RootWindow getRoot() {
		return root;
	}
}