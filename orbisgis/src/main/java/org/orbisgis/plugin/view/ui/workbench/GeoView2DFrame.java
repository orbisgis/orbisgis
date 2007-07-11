package org.orbisgis.plugin.view.ui.workbench;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.driver.DriverException;
import org.gdms.spatial.NullCRS;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.CRSException;
import org.orbisgis.plugin.view.layerModel.GridCoverageReader;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view.tools.TransitionException;
import org.orbisgis.plugin.view.tools.instances.PanTool;
import org.orbisgis.plugin.view.tools.instances.ZoomInTool;
import org.orbisgis.plugin.view.tools.instances.ZoomOutTool;
import org.orbisgis.plugin.view.utilities.file.FileUtility;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Envelope;

public class GeoView2DFrame extends JFrame {

	private static final String OPEN = "open";

	private static final String EXIT = "exit";

	private static final String ZOOM_FULL = "zoomFull";

	private static final String ZOOM_IN = "zoomIn";

	private static final String ZOOM_OUT = "zoomOut";

	private static final String PAN = "pan";

	private static final String FEATUREINFO = "featureInfo";

	private String OPENATTRIBUTES = "openattributes";

	private GeoView2DPanel geoView2D;

	public GeoView2DPanel getGeoView2D() {
		return geoView2D;
	}

	public static void printMem() {
		Runtime rt = Runtime.getRuntime();
		System.out.printf("===========> %d KB\n", (rt.totalMemory() - rt
				.freeMemory()) / 1024);
	}

	public GeoView2DFrame(LayerCollection root) {
		geoView2D = new GeoView2DPanel(root);

		// Action on the buton in the navigationToolBar

		Action openAction = new CustomAction("Add", "home.png", OPEN);
		Action exitAction = new CustomAction("Exit", "exit.png", EXIT);
		Action zoomFullAction = new CustomAction("Zoom full", "zoomFull.png",
				ZOOM_FULL);
		Action zoomInAction = new CustomAction("Zoom in", "zoomIn.png", ZOOM_IN);
		Action zoomOutAction = new CustomAction("Zoom out", "zoomOut.png",
				ZOOM_OUT);
		Action panAction = new CustomAction("Zoom in", "pan.png", PAN);
		Action featureInfo = new CustomAction("Feature info",
				"featureInfo.png", FEATUREINFO);

		Action openAttributes = new CustomAction("Open attributes",
				"openattributes.png", OPENATTRIBUTES);

		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem addSpatialDS = new JMenuItem(openAction);
		file.add(addSpatialDS);
		file.addSeparator();
		JMenuItem exit = new JMenuItem(exitAction);
		file.add(exit);
		menuBar.add(file);
		
		
		
		JMenu help = new JMenu("Help");
		JMenuItem about = new JMenuItem("About");
		about.setIcon(new ImageIcon(this.getClass().getResource("about.png")));
		help.add(about);
		menuBar.add(help);
		
		
		JToolBar navigationToolBar = new JToolBar("Navigation ToolBar");
		navigationToolBar.add(openAction);
		navigationToolBar.add(exitAction);

		navigationToolBar.add(zoomFullAction);
		navigationToolBar.add(zoomInAction);
		navigationToolBar.add(zoomOutAction);
		navigationToolBar.add(panAction);
		navigationToolBar.add(featureInfo);
		navigationToolBar.add(openAttributes);

		this.setJMenuBar(menuBar);	
		this.setLayout(new BorderLayout());
		this.getContentPane().add(navigationToolBar, BorderLayout.PAGE_START);
		this.getContentPane().add(geoView2D, BorderLayout.CENTER);
		this.setTitle("OrbisGIS :: G e o V i e w 2D");
		java.net.URL url = this.getClass().getResource("mini_orbisgis.png");
		Image image = Toolkit.getDefaultToolkit().getImage(url); 
		this.setIconImage(new ImageIcon(url).getImage());
		
		
	}

	/**
	 * refresh the tree useful after removing DataSources
	 * 
	 */
	public void refresh() {
		geoView2D.getTOC().updateUI();
	}

	private class CustomAction extends AbstractAction {

		public CustomAction(String name, String icon, String actionCommand) {
			putValue(NAME, name);
			putValue(SMALL_ICON, new ImageIcon(this.getClass()
					.getResource(icon)));
			putValue(ACTION_COMMAND_KEY, actionCommand);
		}

		public void actionPerformed(ActionEvent e) {
			if (OPEN.equals(e.getActionCommand())) {
				TempPluginServices.geoCatalog.show();
				
			} else if (EXIT.equals(e.getActionCommand())) {
				System.exit(0);
			} else if (ZOOM_FULL.equals(e.getActionCommand())) {
				LayerAction la = new LayerAction();
				LayerCollection.processLayersLeaves(TempPluginServices.lc, la);
				Envelope globalEnv = la.getGlobalEnvelope();

				geoView2D.getMapControl().setExtent(
						(null == globalEnv) ? null : new Rectangle2D.Double(
								globalEnv.getMinX(), globalEnv.getMinY(),
								globalEnv.getWidth(), globalEnv.getHeight()));
				
			} else if (ZOOM_IN.equals(e.getActionCommand())) {
				try {
					geoView2D.getMapControl().setTool(new ZoomInTool());
				} catch (TransitionException e1) {
					throw new RuntimeException(e1);
				}
			} else if (ZOOM_OUT.equals(e.getActionCommand())) {
				try {
					geoView2D.getMapControl().setTool(new ZoomOutTool());
				} catch (TransitionException e1) {
					throw new RuntimeException(e1);
				}
			} else if (PAN.equals(e.getActionCommand())) {
				try {
					geoView2D.getMapControl().setTool(new PanTool());
				} catch (TransitionException e1) {
					throw new RuntimeException(e1);
				}
			} else if (FEATUREINFO.equals(e.getActionCommand())) {
			}
			else if (OPENATTRIBUTES.equals(e.getActionCommand())){
				
				
				try {
					TempPluginServices.dsf.executeSQL("call show('select * from "+ TOC.selectedLayer.getName() + "','" + TOC.selectedLayer.getName() +"');");
				} catch (SyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DriverLoadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NoSuchTableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}
	}

	public static void main(String[] args) throws Exception {

		Class.forName(org.urbsat.Register.class.getName());

		LayerCollection root = new LayerCollection("my root");
		TempPluginServices.lc = root;
		final boolean raster = true;
		CoordinateReferenceSystem crs = NullCRS.singleton;
		
		 GridCoverage gcEsri = new
		GridCoverageReader("../../datas2tests/grid/mnt.asc").getGc();
		 RasterLayer esriGrid = new RasterLayer("DEM", crs);
		esriGrid.setGridCoverage(gcEsri);//,//UtilStyle.loadStyleFromXml("../../datas2tests/sld/rasterclassification2.sld"))
		
		
		TempPluginServices.dsf = new DataSourceFactory();
	
		 root.put(esriGrid);
	
		PropertyConfigurator.configure(GeoView2DFrame.class
				.getResource("log4j.properties"));
		PatternLayout l = new PatternLayout("%p %t %C - %m%n");
		RollingFileAppender fa = new RollingFileAppender(l,
				System.getProperty("user.home") + File.separator + "orbisgis"
						+ File.separator + "orbisgis.log", false);
		fa.setMaxFileSize("512KB");
		fa.setMaxBackupIndex(3);
		Logger.getRootLogger().addAppender(fa);
		TempPluginServices.vf = new GeoView2DFrame(root);
		TempPluginServices.vf.setDefaultCloseOperation(EXIT_ON_CLOSE);
		TempPluginServices.vf.pack();
		TempPluginServices.vf.setVisible(true);
	}
}