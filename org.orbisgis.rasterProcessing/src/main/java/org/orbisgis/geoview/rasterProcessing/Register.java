package org.orbisgis.geoview.rasterProcessing;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.geoview.rasterProcessing.sql.customQuery.D8StrahlerStreamOrder;
import org.orbisgis.geoview.rasterProcessing.sql.customQuery.D8ThresholdedWatershed;
import org.orbisgis.geoview.rasterProcessing.sql.customQuery.PointsToXYZ;
import org.orbisgis.geoview.rasterProcessing.sql.customQuery.RasterToPoints;
import org.orbisgis.geoview.rasterProcessing.sql.customQuery.RasterToPolygons;
import org.orbisgis.geoview.rasterProcessing.sql.customQuery.RasterToXYZ;
import org.orbisgis.geoview.rasterProcessing.sql.function.CropRaster;
import org.orbisgis.geoview.rasterProcessing.sql.function.D8Accumulations;
import org.orbisgis.geoview.rasterProcessing.sql.function.D8AllOutlets;
import org.orbisgis.geoview.rasterProcessing.sql.function.D8AllWatersheds;
import org.orbisgis.geoview.rasterProcessing.sql.function.D8Directions;
import org.orbisgis.geoview.rasterProcessing.sql.function.D8SlopesInPercent;
import org.orbisgis.geoview.rasterProcessing.sql.function.D8WatershedFromOutletIndex;
import org.orbisgis.geoview.rasterProcessing.sql.function.RowEnvelope;
import org.orbisgis.geoview.rasterProcessing.tin.Generate2DMesh;
import org.orbisgis.pluginManager.PluginActivator;

public class Register implements PluginActivator {
	public void start() throws Exception {
		QueryManager.registerQuery(new Generate2DMesh());

		QueryManager.registerQuery(new RasterToPoints());
		QueryManager.registerQuery(new RasterToPolygons());
		QueryManager.registerQuery(new RasterToXYZ());
		QueryManager.registerQuery(new PointsToXYZ());

		FunctionManager.addFunction(D8SlopesInPercent.class);
		FunctionManager.addFunction(D8Directions.class);
		FunctionManager.addFunction(D8Accumulations.class);
		FunctionManager.addFunction(D8AllOutlets.class);
		FunctionManager.addFunction(D8WatershedFromOutletIndex.class);
		FunctionManager.addFunction(D8AllWatersheds.class);
		QueryManager.registerQuery(new D8StrahlerStreamOrder());
		QueryManager.registerQuery(new D8ThresholdedWatershed());

		FunctionManager.addFunction(CropRaster.class);
		FunctionManager.addFunction(RowEnvelope.class);
	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}