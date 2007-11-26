package org.orbisgis.geoview;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import net.infonode.docking.RootWindow;
import net.infonode.docking.View;

import org.orbisgis.core.IWindow;
import org.orbisgis.core.actions.ActionControlsRegistry;
import org.orbisgis.core.actions.EPActionHelper;
import org.orbisgis.core.actions.IAction;
import org.orbisgis.core.actions.IActionFactory;
import org.orbisgis.core.actions.ISelectableAction;
import org.orbisgis.core.actions.MenuTree;
import org.orbisgis.core.actions.ToolBarArray;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.Automaton;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;

public class GeoView2D extends JFrame implements IWindow {

	private MapControl map;

	private ViewContext viewContext;

	private JMenuBar menuBar;

	private JToolBar mainToolBar;

	private ViewDecorator[] views;

	private RootWindow root;

	public GeoView2D() {
		// Init mapcontrol and fixed ui components
		mainToolBar = new JToolBar();
		menuBar = new JMenuBar();
		this.setLayout(new BorderLayout());
		this.getContentPane().add(mainToolBar, BorderLayout.PAGE_START);
		map = new MapControl();
		viewContext = new GeoViewContext(this);
		map.setEditionContext(viewContext);
		OGMapControlModel mapModel = new OGMapControlModel(viewContext
				.getRootLayer());
		mapModel.setMapControl((MapControl) map);
		((MapControl) map).setMapControlModel(mapModel);

		View mapControlView = new View("Map", null, map);

		// Initialize views
		root = new RootWindow(null);
		root.getRootWindowProperties().getSplitWindowProperties()
				.setContinuousLayoutEnabled(false);
		root.setWindow(mapControlView);
		this.getContentPane().add(root, BorderLayout.CENTER);

		this.setJMenuBar(menuBar);
		MenuTree menuTree = new MenuTree();
		ToolBarArray toolBarArray = new ToolBarArray();
		EPActionHelper.configureParentMenusAndToolBars(new String[] {
				"org.orbisgis.geoview.Action", "org.orbisgis.geoview.Tool" },
				menuTree, toolBarArray);
		IActionFactory actionFactory = new GeoviewActionFactory();
		IActionFactory toolFactory = new GeoviewToolFactory();
		EPActionHelper.configureMenuAndToolBar("org.orbisgis.geoview.Action",
				actionFactory, menuTree, toolBarArray);
		EPActionHelper.configureMenuAndToolBar("org.orbisgis.geoview.Tool",
				toolFactory, menuTree, toolBarArray);
		views = EPViewHelper.getViewsInfo(this);

		// Initialize actions
		EPViewHelper.addViewMenu(menuTree, root, this, views);
		JComponent[] menus = menuTree.getJMenus();
		for (int i = 0; i < menus.length; i++) {
			menuBar.add(menus[i]);
		}
		for (JToolBar toolbar : toolBarArray.getToolBars()) {
			mainToolBar.add(toolbar);
		}
		this.setTitle("OrbisGIS :: G e o V i e w 2D");
		java.net.URL url = this.getClass().getResource("mini_orbisgis.png");
		this.setIconImage(new ImageIcon(url).getImage());

	}

	public ViewContext getViewContext() {
		return viewContext;
	}

	public MapControl getMap() {
		return map;
	}

	public void showWindow() {
		this.setLocationRelativeTo(null);
		this.setSize(800, 700);
		this.setVisible(true);
	}

	public Component getView(String viewId) {
		ViewDecorator ret = getViewDecorator(viewId);
		if (ret != null) {
			return ret.getViewComponent();
		} else {
			return null;
		}
	}

	public void showView(String id) {
		ViewDecorator view = getViewDecorator(id);
		if (view != null) {
			view.open(root);
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

	public void enableControls() {
		ActionControlsRegistry.refresh();
	}

	private final class GeoviewToolFactory implements IActionFactory {

		public IAction getAction(Object action) {
			return new IGeoviewToolDecorator(action);
		}

		public ISelectableAction getSelectableAction(Object action) {
			return new IGeoviewToolDecorator(action);
		}
	}

	private final class IGeoviewToolDecorator implements IAction,
			ISelectableAction {

		private Automaton action;

		public IGeoviewToolDecorator(Object action) {
			this.action = (Automaton) action;
		}

		public boolean isVisible() {
			return action.isVisible(viewContext, viewContext.getToolManager());
		}

		public boolean isEnabled() {
			return action.isEnabled(viewContext, viewContext.getToolManager());
		}

		public void actionPerformed() {
			try {
				map.setTool(action);
			} catch (TransitionException e) {
				PluginManager.error("Cannot use tool", e);
			}
		}

		public boolean isSelected() {
			return viewContext.getToolManager().getTool().getClass().equals(
					action.getClass());
		}
	}

	private final class GeoviewActionFactory implements IActionFactory {

		public IAction getAction(Object action) {
			return new IGeoviewActionDecorator(action);
		}

		public ISelectableAction getSelectableAction(Object action) {
			return new IGeoviewActionDecorator(action);
		}
	}

	private final class IGeoviewActionDecorator implements IAction,
			ISelectableAction {

		private IGeoviewAction action;

		public IGeoviewActionDecorator(Object action) {
			this.action = (IGeoviewAction) action;
		}

		public boolean isVisible() {
			return action.isVisible(GeoView2D.this);
		}

		public boolean isEnabled() {
			return action.isEnabled(GeoView2D.this);
		}

		public void actionPerformed() {
			action.actionPerformed(GeoView2D.this);
		}

		public boolean isSelected() {
			return ((IGeoviewSelectableAction) action)
					.isSelected(GeoView2D.this);
		}
	}
}