package org.orbisgis.plugin.view.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenuItem;

public class GeoCatalog implements Runnable {
//	private final static String swixmlSrc = "resources/xml/GcMenuBar.xml";

	private JFrame gcFrame = null;

	private JMenuItem mi_new2d = null;

	private JMenuItem mi_new3d = null;

	private JMenuItem mi_openDS = null;

	private JMenuItem mi_exit = null;

	private JMenuItem mi_about = null;

	// internal class for event management...
	private class MultipleActionEventManager extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			System.err.println(cmd);
			if (cmd.equals("ac_new2d"))
				try {
		///			new Thread(new GeoView()).start();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		}
	}

	public GeoCatalog() throws Exception {
		// load the menuBar and toolBar...
	//	File menuAndToolBar = new File(swixmlSrc);
//		gcFrame = (JFrame) new SwingEngine(this).render(menuAndToolBar);

		// load the root and docking windows...
//		ViewMap viewMap = new ViewMap();
//		viewMap.addView(0, new View("Search", null, new GeoCatalogSearch()));
//		viewMap.addView(1, new View("Catalog", null, new GeoCatalogTree()));
//		RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, true);
//		rootWindow.setWindow(new SplitWindow(false, 0.3f, viewMap.getView(0),
//				viewMap.getView(1)));
//		gcFrame.add(rootWindow);

		// events management
		ActionListener eventMgt = new MultipleActionEventManager();
		mi_new2d.addActionListener(eventMgt);
		mi_new3d.addActionListener(eventMgt);
		mi_openDS.addActionListener(eventMgt);
		mi_exit.addActionListener(eventMgt);
		mi_about.addActionListener(eventMgt);
	}

	public void run() {
		// display the frame...
		gcFrame.setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		System.err.println("Working dir : " + System.getProperty("user.dir"));
		new Thread(new GeoCatalog()).start();
	}
}