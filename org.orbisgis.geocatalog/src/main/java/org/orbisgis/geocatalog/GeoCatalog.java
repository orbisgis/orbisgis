package org.orbisgis.geocatalog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.core.actions.EPActionHelper;
import org.orbisgis.core.actions.IAction;
import org.orbisgis.core.actions.IActionFactory;
import org.orbisgis.core.actions.ISelectableAction;
import org.orbisgis.core.actions.MenuTree;
import org.orbisgis.core.actions.ToolBarArray;
import org.orbisgis.core.persistence.PersistenceException;
import org.orbisgis.core.windows.EPWindowHelper;
import org.orbisgis.core.windows.IWindow;
import org.orbisgis.core.windows.PersistenceContext;
import org.orbisgis.geocatalog.persistence.Resource;
import org.orbisgis.geocatalog.resources.EPResourceWizardHelper;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.IResourceType;
import org.orbisgis.geocatalog.resources.ResourceFactory;
import org.orbisgis.geocatalog.resources.ResourceTreeModel;
import org.orbisgis.geocatalog.resources.ResourceTypeException;
import org.orbisgis.pluginManager.PluginManager;

/**
 * Graphical interface for the Geo Catalog This file mainly contains user
 * interface stuff
 *
 * @author Samuel Chemla
 * @version beta1
 */

public class GeoCatalog implements IWindow {

	/**
	 * The frame is made of a vertical BoxLayout, which contains : 1-a menu bar
	 * 2-a tool bar 3-a scroll pane with a grid layout inside with a tree inside
	 */

	// Let you set the size of the frame
	private final Dimension FrameSize = new Dimension(250, 640);

	// The frame containing everything.
	private JFrame jFrame = null;

	private static Catalog myCatalog = null; // See Catalog.java

	public GeoCatalog() {

		jFrame = new JFrame();

		// be instantied now or the listener won't work...
		jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jFrame.setSize(FrameSize);
		jFrame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				EPGeocatalogActionHelper.executeAction(myCatalog,
						"org.orbisgis.geocatalog.Exit");
			}

		});

		java.net.URL url = this.getClass().getResource("mini_orbisgis.png");
		jFrame.setIconImage(new ImageIcon(url).getImage());

		jFrame.setTitle("OrbisGIS : GeoCatalog");
		JMenuBar menuBar = new JMenuBar();
		JToolBar toolBar = new JToolBar();
		myCatalog = new Catalog();
		MenuTree menuTree = new MenuTree();
		ToolBarArray toolBarArray = new ToolBarArray();
		GeocatalogActionFactory actionFactory = new GeocatalogActionFactory();
		EPActionHelper.configureParentMenusAndToolBars(
				"org.orbisgis.geocatalog.Action", menuTree, toolBarArray);
		EPActionHelper.configureMenuAndToolBar(
				"org.orbisgis.geocatalog.Action", actionFactory, menuTree,
				toolBarArray);
		EPResourceWizardHelper.addWizardMenus(menuTree,
				new ResourceWizardActionFactory(myCatalog));
		JComponent[] menus = menuTree.getJMenus();
		for (int i = 0; i < menus.length; i++) {
			menuBar.add(menus[i]);
		}
		for (JToolBar toolbar : toolBarArray.getToolBars()) {
			toolBar.add(toolbar);
		}

		JToolBar errorToolBar = new JToolBar("Errors");
		
		ErrorButton errorButton = new ErrorButton("");
		errorButton.setIcon(new ImageIcon(getClass().getResource("error.png")));
		errorButton.setToolTipText("Press the button to show the message");
		errorToolBar.add(errorButton);
		toolBar.add(errorToolBar);

		jFrame.setJMenuBar(menuBar); // Add the menu bar
		jFrame.getContentPane().setLayout(new BorderLayout());
		jFrame.getContentPane().add(toolBar, BorderLayout.PAGE_START);

		jFrame.getContentPane().add(myCatalog, BorderLayout.CENTER);

		jFrame.setExtendedState(JFrame.NORMAL);
		jFrame.toFront();

	}

	/** Restore and show the GeoCatalog */
	public void show() {
		jFrame.setVisible(true);
	}

	public void showWindow() {
		show();
	}

	public Catalog getCatalog() {
		return myCatalog;
	}

	private final class GeocatalogActionFactory implements IActionFactory {

		public IAction getAction(Object action) {
			return new IGeocatalogActionDecorator(action);
		}

		public ISelectableAction getSelectableAction(Object action) {
			return new IGeocatalogActionDecorator(action);
		}
	}

	private final class IGeocatalogActionDecorator implements IAction,
			ISelectableAction {

		private IGeocatalogAction action;

		public IGeocatalogActionDecorator(Object action) {
			this.action = (IGeocatalogAction) action;
		}

		public boolean isVisible() {
			return action.isVisible(GeoCatalog.this);
		}

		public boolean isEnabled() {
			return action.isEnabled(GeoCatalog.this);
		}

		public void actionPerformed() {
			action.actionPerformed(myCatalog);
		}

		public boolean isSelected() {
			return ((IGeocatalogSelectableAction) action).isSelected(myCatalog);
		}
	}

	public Rectangle getPosition() {
		return jFrame.getBounds();
	}

	public boolean isOpened() {
		return jFrame.isVisible();
	}

	public void load(PersistenceContext pc) throws PersistenceException {
		try {
			JAXBContext jc = JAXBContext.newInstance(
					"org.orbisgis.geocatalog.persistence", EPWindowHelper.class
							.getClassLoader());
			org.orbisgis.geocatalog.persistence.Catalog cat = (org.orbisgis.geocatalog.persistence.Catalog) jc
					.createUnmarshaller().unmarshal(pc.getFile("catalog"));
			ResourceTreeModel treeModel = getCatalog().getTreeModel();
			IResource newRoot = populate(cat.getResource().get(0), treeModel);
			treeModel.setRootNode(newRoot);
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot load geocatalog", e);
		} catch (InstantiationException e) {
			throw new PersistenceException("Cannot load geocatalog", e);
		} catch (IllegalAccessException e) {
			throw new PersistenceException("Cannot load geocatalog", e);
		} catch (ClassNotFoundException e) {
			throw new PersistenceException("Cannot load geocatalog", e);
		}
	}

	private IResource populate(Resource xmlNode, ResourceTreeModel treeModel)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		String resourceTypeClassName = xmlNode.getTypeClass();
		IResourceType resourceType = (IResourceType) Class.forName(
				resourceTypeClassName).newInstance();
		String xmlName = xmlNode.getName();
		IResource newResource = ResourceFactory.createResource(xmlName,
				resourceType, treeModel);
		List<Resource> xmlChildren = xmlNode.getResource();
		for (int i = 0; i < xmlChildren.size(); i++) {
			try {
				newResource
						.addResource(populate(xmlChildren.get(i), treeModel));
			} catch (ResourceTypeException e) {
				PluginManager.error("Cannot recover resource: " + xmlName, e);
			} catch (InstantiationException e) {
				PluginManager.error("Cannot recover resource: " + xmlName, e);
			} catch (IllegalAccessException e) {
				PluginManager.error("Cannot recover resource: " + xmlName, e);
			} catch (ClassNotFoundException e) {
				PluginManager.error("Cannot recover resource: " + xmlName, e);
			}
		}

		return newResource;
	}

	public void save(PersistenceContext pc) throws PersistenceException {
		org.orbisgis.geocatalog.persistence.Catalog catalog = new org.orbisgis.geocatalog.persistence.Catalog();
		Resource root = new Resource();
		populate(root, getCatalog().getTreeModel().getRoot());
		catalog.getResource().add(root);
		File file = pc.getFile("catalog", "catalog", ".xml");
		try {
			JAXBContext jc = JAXBContext.newInstance(
					"org.orbisgis.geocatalog.persistence", EPWindowHelper.class
							.getClassLoader());
			jc.createMarshaller().marshal(catalog, new PrintWriter(file));
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot save geocatalog", e);
		} catch (FileNotFoundException e) {
			throw new PersistenceException("Cannot write the file: " + file);
		}
	}

	private void populate(Resource xmlNode, IResource node) {
		xmlNode.setName(node.getName());
		xmlNode.setTypeClass(node.getResourceType().getClass()
				.getCanonicalName());
		for (int i = 0; i < node.getChildCount(); i++) {
			Resource xmlChild = new Resource();
			populate(xmlChild, node.getResourceAt(i));
			xmlNode.getResource().add(xmlChild);
		}
	}

	public void setPosition(Rectangle position) {
		jFrame.setBounds(position);
	}

	public void delete() {
		jFrame.setVisible(false);
		jFrame.dispose();
		myCatalog.delete();
	}
}