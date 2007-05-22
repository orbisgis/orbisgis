package org.orbisgis.plugin.view.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
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
import org.gdms.data.DataSourceFactory;
import org.gdms.spatial.NullCRS;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.referencing.CRS;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.OurReader;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;
import org.orbisgis.plugin.view.tools.TransitionException;
import org.orbisgis.plugin.view.tools.instances.PanTool;
import org.orbisgis.plugin.view.tools.instances.ZoomInTool;
import org.orbisgis.plugin.view.tools.instances.ZoomOutTool;
import org.orbisgis.plugin.view.ui.utility.style.UtilStyle;

public class ViewFrame extends JFrame {

	private static final String OPEN = "open";

	private static final String EXIT = "exit";

	private static final String ZOOM_FULL = "zoomFull";

	private static final String ZOOM_IN = "zoomIn";

	private static final String ZOOM_OUT = "zoomOut";

	private static final String PAN = "pan";

	private GeoView geoView;

	public static void printMem() {
		Runtime rt = Runtime.getRuntime();
		System.out.printf("===========> %d KB\n", (rt.totalMemory() - rt
				.freeMemory()) / 1024);
	}

	public ViewFrame(LayerCollection root) {
		geoView = new GeoView(root);

		Action openAction = new CustomAction("Add", "addDataSource.png", OPEN);
		Action exitAction = new CustomAction("Exit", "exit.png", EXIT);

		Action zoomFullAction = new CustomAction("Zoom full", "zoomFull.png",
				ZOOM_FULL);
		Action zoomInAction = new CustomAction("Zoom in", "zoomIn.png", ZOOM_IN);
		Action zoomOutAction = new CustomAction("Zoom out", "zoomOut.png",
				ZOOM_OUT);
		Action panAction = new CustomAction("Zoom in", "pan.png", PAN);

		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem addDS = new JMenuItem(openAction);
		file.add(addDS);
		file.addSeparator();
		JMenuItem exit = new JMenuItem(exitAction);
		file.add(exit);
		menuBar.add(file);

		JToolBar toolBar = new JToolBar();
		toolBar.add(openAction);
		toolBar.add(exitAction);

		toolBar.add(zoomFullAction);
		toolBar.add(zoomInAction);
		toolBar.add(zoomOutAction);
		toolBar.add(panAction);

		this.setJMenuBar(menuBar);
		this.setLayout(new BorderLayout());
		this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
		this.getContentPane().add(geoView, BorderLayout.CENTER);
		this.setTitle("OrbisGIS :: G e o V i e w 2D");
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

			} else if (EXIT.equals(e.getActionCommand())) {
				System.exit(0);
			} else if (ZOOM_FULL.equals(e.getActionCommand())) {
				// try {
				// geoView.getMapControl().setTool(new ZoomFullTool());
				// } catch (TransitionException e1) {
				// throw new RuntimeException(e1);
				// }
			} else if (ZOOM_IN.equals(e.getActionCommand())) {
				try {
					geoView.getMapControl().setTool(new ZoomInTool());
				} catch (TransitionException e1) {
					throw new RuntimeException(e1);
				}
			} else if (ZOOM_OUT.equals(e.getActionCommand())) {
				try {
					geoView.getMapControl().setTool(new ZoomOutTool());
				} catch (TransitionException e1) {
					throw new RuntimeException(e1);
				}
			} else if (PAN.equals(e.getActionCommand())) {
				try {
					geoView.getMapControl().setTool(new PanTool());
				} catch (TransitionException e1) {
					throw new RuntimeException(e1);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		LayerCollection root = new LayerCollection("my root");
		final boolean raster = false;

		if (raster) {
			CoordinateReferenceSystem crs = NullCRS.singleton;
			// CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
			LayerCollection lc = new LayerCollection("Raster data");
			String[] fileNameArray = new String[] { "440606", "440607",
					"440608", "440706", "440707", "440708", "440806", "440807",
					"440808" };
			for (String fileName : fileNameArray) {
				RasterLayer rl = new RasterLayer(fileName, crs);
				GridCoverage gc = new OurReader("../../datas2tests/geotif/"
						+ fileName + ".tif").getGc();
				rl.setGridCoverage(gc);
				lc.put(rl);
				ViewFrame.printMem();
			}
			root.put(lc);
		} else {
			// French EPSG code for Lambert 2 extended
			// CoordinateReferenceSystem crs = NullCRS.singleton;
			CoordinateReferenceSystem crs = CRS.decode("EPSG:27582");

			DataSourceFactory dsf = new DataSourceFactory();

			DataSource sds1 = dsf.getDataSource(new File(
					"../../datas2tests/shp/mediumshape2D/landcover2000.shp"));

			DataSource sds2 = dsf.getDataSource(new File(
					"../../datas2tests/shp/mediumshape2D/hedgerow.shp"));

			VectorLayer vl1 = new VectorLayer("Landcover", crs);
			vl1.setDataSource(new SpatialDataSourceDecorator(sds1));

			VectorLayer vl2 = new VectorLayer("Hedgerow", crs);
			vl2
					.set(
							new SpatialDataSourceDecorator(sds2),
							UtilStyle
									.loadStyleFromXml("../../datas2tests/sld/greenlinewithlabel.sld"));

			LayerCollection lc = new LayerCollection("other data");
			lc.put(vl1);
			lc.put(vl2);
			root.put(lc);
		}

		PropertyConfigurator.configure(ViewFrame.class
				.getResource("log4j.properties"));
		PatternLayout l = new PatternLayout("%p %t %C - %m%n");
		RollingFileAppender fa = new RollingFileAppender(l,
				System.getProperty("user.home") + File.separator + "orbisgis"
						+ File.separator + "orbisgis.log", false);
		fa.setMaxFileSize("512KB");
		fa.setMaxBackupIndex(3);
		Logger.getRootLogger().addAppender(fa);
		ViewFrame vf = new ViewFrame(root);
		vf.setDefaultCloseOperation(EXIT_ON_CLOSE);
		vf.pack();
		vf.setVisible(true);
	}
}