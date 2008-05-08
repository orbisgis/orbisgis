package org.gdms;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.spatial.geometry.convert.PointsToXYZ;
import org.gdms.sql.customQuery.spatial.geometry.jgrapht.ShortestPath;
import org.gdms.sql.customQuery.spatial.geometry.tin.BuildTIN;
import org.gdms.sql.customQuery.spatial.geometry.topology.ToLineNoder;
import org.gdms.sql.customQuery.spatial.raster.convert.RasterToPoints;
import org.gdms.sql.customQuery.spatial.raster.convert.RasterToPolygons;
import org.gdms.sql.customQuery.spatial.raster.convert.RasterToXYZ;
import org.gdms.sql.customQuery.spatial.raster.hydrology.D8StrahlerStreamOrder;
import org.gdms.sql.customQuery.spatial.raster.hydrology.D8ThresholdedWatershed;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.spatial.geometry.extract.ToMultiSegments;
import org.gdms.sql.function.spatial.geometry.generalize.Generalize;
import org.gdms.sql.function.spatial.raster.hydrology.D8Accumulations;
import org.gdms.sql.function.spatial.raster.hydrology.D8AllOutlets;
import org.gdms.sql.function.spatial.raster.hydrology.D8AllWatersheds;
import org.gdms.sql.function.spatial.raster.hydrology.D8Directions;
import org.gdms.sql.function.spatial.raster.hydrology.D8SlopesInPercent;
import org.gdms.sql.function.spatial.raster.hydrology.D8WatershedFromOutletIndex;
import org.gdms.sql.function.spatial.raster.utilities.CropRaster;
import org.gdms.sql.function.spatial.raster.utilities.ToEnvelope;
import org.orbisgis.pluginManager.PluginActivator;

public class Register implements PluginActivator {
	public void start() throws Exception {
		FunctionManager.addFunction(ToMultiSegments.class);
		FunctionManager.addFunction(Generalize.class);

		FunctionManager.addFunction(D8SlopesInPercent.class);
		FunctionManager.addFunction(D8Directions.class);
		FunctionManager.addFunction(D8Accumulations.class);
		FunctionManager.addFunction(D8AllOutlets.class);
		FunctionManager.addFunction(D8WatershedFromOutletIndex.class);
		FunctionManager.addFunction(D8AllWatersheds.class);

		FunctionManager.addFunction(CropRaster.class);
		FunctionManager.addFunction(ToEnvelope.class);

		QueryManager.registerQuery(new ToLineNoder());
		QueryManager.registerQuery(new ShortestPath());
		QueryManager.registerQuery(new BuildTIN());

		QueryManager.registerQuery(new RasterToPoints());
		QueryManager.registerQuery(new RasterToPolygons());
		QueryManager.registerQuery(new RasterToXYZ());
		QueryManager.registerQuery(new PointsToXYZ());

		QueryManager.registerQuery(new D8ThresholdedWatershed());
		QueryManager.registerQuery(new D8StrahlerStreamOrder());
	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}