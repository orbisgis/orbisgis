package org.urbsat;

import java.net.URL;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.core.windows.EPWindowHelper;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.pluginManager.PluginActivator;
import org.orbisgis.toolsMenuPanel.ToolsMenuPanel;
import org.urbsat.custom.BalancedBuildVolume;
import org.urbsat.custom.FrontalDensity;
import org.urbsat.custom.LateralDensity;
import org.urbsat.kmeans.KMeans;
import org.urbsat.landcoverIndicators.custom.Density;
import org.urbsat.landcoverIndicators.function.Compacity;
import org.urbsat.landcoverIndicators.function.MeanSpacingBetweenBuildingsInACell;
import org.urbsat.utilities.CreateGrid;
import org.urbsat.utilities.CropRaster;
import org.urbsat.utilities.GetZDEM;
import org.urbsat.utilities.RasterToPoints;

public class Register implements PluginActivator {
	private final static URL xmlFileUrl = Register.class
			.getResource("urbsat.xml");

	public void start() throws Exception {
		QueryManager.registerQuery(new CreateGrid());
		QueryManager.registerQuery(new Density());
		// QueryManager.registerQuery(new BuildNumber());
		// QueryManager.registerQuery(new BuildLenght());
		// QueryManager.registerQuery(new OldAverageBuildHeight());
		// QueryManager.registerQuery(new AverageBuildHeight());
		QueryManager.registerQuery(new LateralDensity());
		QueryManager.registerQuery(new FrontalDensity());
		// QueryManager.registerQuery(new BuildVolume());
		// QueryManager.registerQuery(new BuildArea());
		QueryManager.registerQuery(new BalancedBuildVolume());
		// QueryManager.registerQuery(new StandardDeviationBuildBalanced());
		// QueryManager.registerQuery(new StandardDeviationBuildHeight());

		QueryManager.registerQuery(new GetZDEM());

		FunctionManager.addFunction(new MeanSpacingBetweenBuildingsInACell());
		FunctionManager.addFunction(new Compacity());

		QueryManager.registerQuery(new KMeans());
		QueryManager.registerQuery(new CropRaster());
		QueryManager.registerQuery(new RasterToPoints());

		// import the content of the XML file into the ToolsMenuPanelView :
		final GeoView2D geoview = (GeoView2D) EPWindowHelper
				.getWindows("org.orbisgis.geoview.Window")[0];
		final ToolsMenuPanel toolsMenuPanel = (ToolsMenuPanel) (geoview
				.getView("org.orbisgis.toolsMenuPanel.ToolsMenuPanelView"));
		toolsMenuPanel.getFunctionsPanel().addSubMenus(xmlFileUrl);
	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}