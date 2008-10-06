package org.orbisgis.views.europeGame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.DataManager;
import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.editors.map.MapControl;
import org.orbisgis.editors.map.tool.Automaton;
import org.orbisgis.editors.map.tool.DrawingException;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.editors.map.tools.AbstractPointTool;
import org.orbisgis.editors.map.tools.PanTool;
import org.orbisgis.editors.map.tools.ZoomInTool;
import org.orbisgis.editors.map.tools.ZoomOutTool;
import org.orbisgis.layerModel.DefaultMapContext;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.renderer.legend.carto.DefaultUniqueSymbolLegend;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;
import org.orbisgis.view.IView;
import org.orbisgis.workspace.DefaultOGWorkspace;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class EuropeGame implements IView {
	private static final Symbol MAP_SYMBOL_LEGEND = SymbolFactory
			.createPolygonSymbol(Color.black, 1, new Color(180, 150, 0));
	private static final Symbol SELECTED_SYMBOL = SymbolFactory
			.createPolygonSymbol(Color.blue, 2, new Color(180, 150, 0));

	private static final String MAP_RESOURCE = "../../maps/europe";

	// Countries with area lower than this value will not be asked
	// Some area values:
	// Andorra - 0.032454992187467724
	// Man, Isle of - 0.04017836093044025
	// Jan Mayen - 0.08229839242994785
	// Liechtenstein - 0.019155851157847792
	// Vatican - 2.8567238292157016E-5
	private static final double MINIMUM_AREA = 0.01;

	private String country;
	private ArrayList<String> countries;

	private MapContext mapContext;
	private ILayer mapLayer;
	private MapControl mapControl;

	private Automaton selection, zoomIn, zoomOut, pan;

	private EuropePanel europePanel;

	private File shp, shx, dbf;

	private Random rnd;

	@Override
	public void loadStatus() throws PersistenceException {
	}

	@Override
	public void saveStatus() throws PersistenceException {
	}

	public void delete() {
		shp.delete();
		shx.delete();
		dbf.delete();
	}

	/**
	 * Copies the given file from the classpath to the temp folder of the OG
	 * workspace
	 * 
	 * @param filename
	 *            the name of the file to copy
	 * @return the file copied in the workspace
	 * @throws IOException
	 *             if the file cannot be copied
	 */
	private File copyFiles(String filename) throws IOException {
		DefaultOGWorkspace workspace = new DefaultOGWorkspace();

		InputStream in = EuropeGame.class.getResourceAsStream(filename);

		File outFile = new File(workspace.getTempFolder() + File.separator
				+ filename.substring(filename.lastIndexOf('/')));
		FileOutputStream out = new FileOutputStream(outFile);

		byte[] buffer = new byte[1024];
		while (in.read(buffer) != -1) {
			out.write(buffer);
		}

		in.close();
		out.close();

		return outFile;
	}

	@Override
	public Component getComponent() {
		return europePanel;
	}

	@Override
	public void initialize() {
		try {
			rnd = new Random();

			// Copy map files to temp folder
			shp = copyFiles(MAP_RESOURCE + ".shp");
			shx = copyFiles(MAP_RESOURCE + ".shx");
			dbf = copyFiles(MAP_RESOURCE + ".dbf");

			// Create map
			DataManager dm = (DataManager) Services
					.getService(DataManager.class);
			mapLayer = dm.createLayer(shp);
			mapContext = new DefaultMapContext();
			mapContext.open(null);
			mapContext.getLayerModel().addLayer(mapLayer);
			DefaultUniqueSymbolLegend legend = new DefaultUniqueSymbolLegend();
			legend.setSymbol(MAP_SYMBOL_LEGEND);
			mapLayer.setLegend(legend);

			// Create tools
			selection = new CountrySelectionTool();
			zoomIn = new ZoomInTool();
			zoomOut = new ZoomOutTool();
			pan = new PanTool();
			mapControl = new MapControl(mapContext, selection);

			// Get countries
			SpatialDataSourceDecorator ds = mapLayer.getDataSource();
			countries = new ArrayList<String>();
			for (long i = 0; i < ds.getRowCount(); i++) {
				if (ds.getGeometry(i).getArea() >= MINIMUM_AREA) {
					countries.add(ds.getString(i, "CNTRY_NAME"));
				}
			}

			country = getRandomCountry();

			// Create panel
			europePanel = new EuropePanel(this);
			europePanel.setModel(mapControl, country);
		} catch (LayerException e) {
			Services.getErrorManager().error(
					"The europe map cannot be created", e);
		} catch (DriverException e) {
			Services.getErrorManager().error("The europe map cannot be readed",
					e);
		} catch (TransitionException e) {
			Services.getErrorManager().error("bug!", e);
		} catch (IOException e) {
			Services.getErrorManager().error("The europe map cannot be loaded",
					e);
		}
	}

	/**
	 * Determines if the specified tool of the map control is active
	 * 
	 * @param name
	 *            the name of the tool. See EuropePanel constants
	 * @return true if the tool is active, false otherwise
	 */
	public boolean isActiveTool(String toolName) {
		if (toolName.equalsIgnoreCase(EuropePanel.SELECTION_TOOL_NAME)) {
			return mapControl.getTool() == selection;
		} else if (toolName.equalsIgnoreCase(EuropePanel.ZOOM_IN_TOOL_NAME)) {
			return mapControl.getTool() == zoomIn;
		} else if (toolName.equalsIgnoreCase(EuropePanel.ZOOM_OUT_TOOL_NAME)) {
			return mapControl.getTool() == zoomOut;
		} else if (toolName.equalsIgnoreCase(EuropePanel.PAN_TOOL_NAME)) {
			return mapControl.getTool() == pan;
		} else {
			return false;
		}
	}

	/**
	 * Activates the specified tool
	 * 
	 * @param toolName
	 *            the name of the tool to activate. See EuropePanel constants
	 */
	public void activateTool(String toolName) {
		try {
			if (toolName.equalsIgnoreCase(EuropePanel.SELECTION_TOOL_NAME)) {
				mapControl.setTool(selection);
			} else if (toolName.equalsIgnoreCase(EuropePanel.ZOOM_IN_TOOL_NAME)) {
				mapControl.setTool(zoomIn);
			} else if (toolName
					.equalsIgnoreCase(EuropePanel.ZOOM_OUT_TOOL_NAME)) {
				mapControl.setTool(zoomOut);
			} else if (toolName.equalsIgnoreCase(EuropePanel.PAN_TOOL_NAME)) {
				mapControl.setTool(pan);
			}
		} catch (TransitionException e) {
			Services.getErrorManager().error(
					"An error has occurred and the tool is unavailable", e);
		}
	}

	/**
	 * Sets the extent of the map to the full map
	 */
	public void fullExtent() {

		mapControl.getMapTransform().setExtent(
				mapContext.getLayerModel().getEnvelope());
	}

	/**
	 * Gets a random country from the 'countries' ArrayList
	 * 
	 * @return a random country
	 */
	private String getRandomCountry() {
		return countries.get(rnd.nextInt(countries.size()));
	}

	/**
	 * Country selection tool. Highlights the country under the mouse and checks
	 * if the clicked country is the right one
	 * 
	 * @author Victorzinho
	 */
	private class CountrySelectionTool extends AbstractPointTool {
		@Override
		public boolean isVisible(MapContext vc, ToolManager tm) {
			return true;
		}

		@Override
		public boolean isEnabled(MapContext vc, ToolManager tm) {
			return true;
		}

		@Override
		protected void pointDone(Point point, MapContext vc, ToolManager tm)
				throws TransitionException {
			try {
				SpatialDataSourceDecorator ds = mapLayer.getDataSource();
				for (long i = 0; i < ds.getRowCount(); i++) {
					if (ds.getGeometry(i).contains(point)) {
						String clickedCountry = ds.getString(i, "CNTRY_NAME");
						if (clickedCountry.equalsIgnoreCase(country)) {
							JOptionPane.showMessageDialog(null, "Well done!",
									"", JOptionPane.INFORMATION_MESSAGE);
							country = getRandomCountry();
							europePanel.updateCountry(country);
						} else {
							JOptionPane.showMessageDialog(null,
									"You can do it better", "",
									JOptionPane.INFORMATION_MESSAGE);
						}

						break;
					}
				}
			} catch (DriverException exc) {
				Services.getErrorManager().error(
						"An error has occurred and the "
								+ "clicked point cannot be checked", exc);
			}
		}

		@Override
		public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm)
				throws DrawingException {
			try {
				GeometryFactory gf = new GeometryFactory();
				SpatialDataSourceDecorator ds = mapLayer.getDataSource();
				for (long i = 0; i < ds.getRowCount(); i++) {
					Geometry geom = ds.getGeometry(i);
					Point2D mouse = tm.getLastRealMousePosition();
					Point point = gf.createPoint(new Coordinate(mouse.getX(),
							mouse.getY()));
					if (geom.contains(point)) {
						tm.addGeomToDraw(geom, SELECTED_SYMBOL);
						return;
					}
				}
			} catch (DriverException e) {
				Services.getErrorManager().error(
						"An error has occurred and "
								+ "the map cannot be readed", e);
			}
		}
	}
}
