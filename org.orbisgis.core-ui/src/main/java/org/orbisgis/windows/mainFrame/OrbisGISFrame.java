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
package org.orbisgis.windows.mainFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.action.ActionControlsRegistry;
import org.orbisgis.action.EPActionHelper;
import org.orbisgis.action.EPBaseActionHelper;
import org.orbisgis.action.IAction;
import org.orbisgis.action.IActionAdapter;
import org.orbisgis.action.IActionFactory;
import org.orbisgis.action.ISelectableAction;
import org.orbisgis.action.ISelectableActionAdapter;
import org.orbisgis.action.JActionMenuBar;
import org.orbisgis.action.JActionToolBar;
import org.orbisgis.action.MenuTree;
import org.orbisgis.action.ToolBarArray;
import org.orbisgis.actions.about.HtmlViewer;
import org.orbisgis.editor.EditorListener;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.IExtensionPointEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.editor.action.IEditorSelectableAction;
import org.orbisgis.images.IconLoader;
import org.orbisgis.pluginManager.workspace.Workspace;
import org.orbisgis.view.EPViewHelper;
import org.orbisgis.view.IEditorsView;
import org.orbisgis.view.ViewDecorator;
import org.orbisgis.view.ViewManager;
import org.orbisgis.window.IWindow;

public class OrbisGISFrame extends JFrame implements IWindow, ViewManager,
		UIManager {

	private static final String LAYOUT_PERSISTENCE_FILE = "org.orbisgis.ViewLayout.obj";

	private JActionMenuBar menuBar;

	private JActionToolBar mainToolBar;

	private ArrayList<ViewDecorator> views = new ArrayList<ViewDecorator>();

	private RootWindow root;

	private MyViewSerializer viewSerializer = new MyViewSerializer();

	private Component welcomeComponent;

	private IEditorsView editorsView;

	public OrbisGISFrame() {
		// Init mapcontrol and fixed ui components
		mainToolBar = new JActionToolBar("OrbisGIS");
		mainToolBar.setFloatable(false);
		menuBar = new JActionMenuBar();
		this.setLayout(new BorderLayout());
		this.getContentPane().add(mainToolBar, BorderLayout.PAGE_START);

		// Initialize views
		root = new RootWindow(viewSerializer);
		root.getRootWindowProperties().getSplitWindowProperties()
				.setContinuousLayoutEnabled(false);

		welcomeComponent = new HtmlViewer(getClass()
				.getResource("welcome.html"));
		View welcome = new View("OrbisGIS", null, welcomeComponent);
		root.getRootWindowProperties().getTabWindowProperties()
				.getTabProperties().getFocusedProperties()
				.getComponentProperties().setBackgroundColor(
						new Color(100, 140, 190));
		root.setWindow(welcome);

		this.getContentPane().add(root, BorderLayout.CENTER);

		// Prepare menu and toolbar
		this.setJMenuBar(menuBar);
		MenuTree menuTree = new MenuTree();
		ToolBarArray toolBarArray = new ToolBarArray();
		EPBaseActionHelper.configureParentMenusAndToolBars(new String[] {
				"org.orbisgis.Action", "org.orbisgis.editor.Action" },
				menuTree, toolBarArray);

		// Read editors and install editors extensions
		ArrayList<ViewDecorator> editorViews = EPViewHelper.getViewsInfo(
				"org.orbisgis.Editor", "editor");
		prepareEditorExtensionPoints(editorViews, menuTree, toolBarArray);

		// Install view and editor actions
		IActionFactory actionFactory = new GeoviewActionFactory();
		IActionFactory editorActionFactory = new EditorActionFactory();
		EPBaseActionHelper.configureMenuAndToolBar("org.orbisgis.Action",
				"action", actionFactory, menuTree, toolBarArray);
		EPBaseActionHelper.configureMenuAndToolBar("org.orbisgis.editor.Action",
				"editor-action", editorActionFactory, menuTree, toolBarArray);
		installEditorExtensionPoints(editorViews, menuTree, toolBarArray);

		// Read views and editorViews and install them on the view menu
		views = EPViewHelper.getViewsInfo("org.orbisgis.View", "view");
		views.addAll(EPViewHelper.getViewsInfo("org.orbisgis.EditorView",
				"editor-view"));
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
			toolbar.setFloatable(false);
			mainToolBar.add(toolbar);
		}
		this.setTitle("OrbisGIS :: G e o V i e w 2D");
		this.setIconImage(IconLoader.getIcon("mini_orbisgis.png").getImage());
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		this.setSize((int) (screenSize.width / 1.5),
				(int) (screenSize.height / 1.5));
		this.setLocationRelativeTo(null);

		// TODO remove when the window management is implemented
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				EPActionHelper.executeAction("org.orbisgis.actions.Exit");
			}

		});

		Services.registerService("org.orbisgis.ViewManager", ViewManager.class,
				"Open, close and gets access to the views", this);

		Services.registerService("org.orbisgis.UIManager", UIManager.class,
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
		ViewDecorator editor = null;
		for (ViewDecorator view : views) {
			if (view.isEditor()) {
				if (editor == null) {
					editor = view;
				} else {
					throw new RuntimeException(
							"No more than one editor is allowed. "
									+ editor.getId() + " and " + view.getId()
									+ " found.");
				}
			}
			view.getView().initialize();
		}

		this.editorsView = (IEditorsView) editor.getView();
		this.editorsView.addEditorListener(new EditorListener() {

			public void activeEditorChanged(IEditor previous, IEditor current) {
				refreshUI();
				for (ViewDecorator view : views) {
					view.editorChanged(OrbisGISFrame.this.editorsView
							.getActiveEditor());
				}
			}

			public void activeEditorClosed(IEditor editor) {

			}

		});
	}

	public void showWindow() {
		this.setVisible(true);
	}

	public Component getView(String viewId) {
		ViewDecorator ret = getViewDecorator(viewId);
		if (ret != null) {
			if (!ret.isOpen()) {
				ret.open(root, editorsView.getActiveEditor());
			}
			return ret.getViewComponent();
		} else {
			return null;
		}
	}

	public void showView(String id) {
		ViewDecorator view = getViewDecorator(id);
		if (view != null) {
			view.open(root, editorsView.getActiveEditor());
		}
	}

	public void hideView(String id) {
		ViewDecorator view = getViewDecorator(id);
		if (view != null) {
			view.close();
		}
	}

	public IEditorsView getEditorsView() {
		return editorsView;
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
				getViewDecorator().open(root, editorsView.getActiveEditor());
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
			if (editorsView.getActiveEditor() == null) {
				return false;
			} else {
				if (editorId.equals(editorsView.getActiveEditor().getId())) {
					return action.isVisible(editorsView.getActiveEditor()
							.getEditor());
				} else {
					return false;
				}
			}
		}

		public boolean isEnabled() {
			if (editorsView.getActiveEditor() == null) {
				return false;
			} else {
				if (editorId.equals(editorsView.getActiveEditor().getId())) {
					return action.isEnabled(editorsView.getActiveEditor()
							.getEditor());
				} else {
					return false;
				}
			}
		}

		public void actionPerformed() {
			action.actionPerformed(editorsView.getActiveEditor().getEditor());
		}

		public boolean isSelected() {
			return ((IEditorSelectableAction) action).isSelected(editorsView
					.getActiveEditor().getEditor());
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
			return ((ISelectableAction) action)
					.isSelected();
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
		this.getContentPane().add(root, BorderLayout.CENTER);

		try {
			Workspace ws = (Workspace) Services
					.getService("org.orbisgis.Workspace");
			FileInputStream fis = new FileInputStream(ws
					.getFile(LAYOUT_PERSISTENCE_FILE));
			ObjectInputStream ois = new ObjectInputStream(fis);
			root.read(ois);
			ois.close();
		} catch (Exception e) {
			Services.getErrorManager().error("Cannot recover the layout of the window", e);
		}
	}

	public Map<String, String> save() throws PersistenceException {
		try {
			Workspace ws = (Workspace) Services
					.getService("org.orbisgis.Workspace");
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
			if (id.equals("welcome")) {
				return new View("OrbisGIS", null, welcomeComponent);
			} else {
				ViewDecorator vd = OrbisGISFrame.this.getViewDecorator(id);
				if (vd != null) {
					try {
						vd.loadStatus(editorsView.getActiveEditor());
						return vd.getDockingView();
					} catch (Throwable t) {
						Services.getErrorManager().error("Cannot recover view " + id, t);
					}
				}
			}

			return null;
		}

		public void writeView(View view, ObjectOutputStream oos)
				throws IOException {
			ViewDecorator vd = getViewDecorator(view);
			if (vd != null) {
				oos.writeUTF(vd.getId());
				try {
					vd.getView().saveStatus();
				} catch (Throwable e) {
					Services.getErrorManager().error("Cannot save view " + vd.getId(), e);
				}
			} else if (view.getComponent() == welcomeComponent) {
				oos.writeUTF("welcome");
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
}