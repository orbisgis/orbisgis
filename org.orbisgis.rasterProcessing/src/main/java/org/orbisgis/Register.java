package org.orbisgis;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.spatial.geometry.convert.PointsToXYZ;
import org.gdms.sql.customQuery.spatial.geometry.jgrapht.ShortestPath;
import org.gdms.sql.customQuery.spatial.geometry.tin.BuildTIN;
import org.gdms.sql.customQuery.spatial.geometry.tin.Cdt;
import org.gdms.sql.customQuery.spatial.geometry.topology.ToLineNoder;
import org.gdms.sql.customQuery.spatial.raster.convert.RasterToPoints;
import org.gdms.sql.customQuery.spatial.raster.convert.RasterToPolygons;
import org.gdms.sql.customQuery.spatial.raster.convert.RasterToXYZ;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.spatial.geometry.extract.ToMultiSegments;
import org.gdms.sql.function.spatial.geometry.generalize.Generalize;
import org.gdms.sql.function.spatial.raster.hydrology.D8Accumulation;
import org.gdms.sql.function.spatial.raster.hydrology.D8AllOutlets;
import org.gdms.sql.function.spatial.raster.hydrology.D8AllWatersheds;
import org.gdms.sql.function.spatial.raster.hydrology.D8ConstrainedAccumulation;
import org.gdms.sql.function.spatial.raster.hydrology.D8Direction;
import org.gdms.sql.function.spatial.raster.hydrology.D8Slope;
import org.gdms.sql.function.spatial.raster.hydrology.D8StrahlerStreamOrder;
import org.gdms.sql.function.spatial.raster.hydrology.D8ThresholdedWatershed;
import org.gdms.sql.function.spatial.raster.hydrology.D8WatershedFromOutletIndex;
import org.gdms.sql.function.spatial.raster.utilities.CropRaster;
import org.gdms.sql.function.spatial.raster.utilities.ToEnvelope;
import org.gdms.utilities.RandomGeometry;
import org.orbisgis.pluginManager.PluginActivator;
import org.orbisgis.rasterProcessing.tin.Generate2DMesh;

public class Register implements PluginActivator {
	public void start() throws Exception {

		// Raster processing

		QueryManager.registerQuery(new RasterToPoints());
		QueryManager.registerQuery(new RasterToPolygons());
		QueryManager.registerQuery(new RasterToXYZ());

		FunctionManager.addFunction(D8Slope.class);
		FunctionManager.addFunction(D8Direction.class);
		FunctionManager.addFunction(D8Accumulation.class);
		FunctionManager.addFunction(D8AllOutlets.class);
		FunctionManager.addFunction(D8WatershedFromOutletIndex.class);
		FunctionManager.addFunction(D8AllWatersheds.class);
		FunctionManager.addFunction(D8ThresholdedWatershed.class);
		FunctionManager.addFunction(D8StrahlerStreamOrder.class);
		FunctionManager.addFunction(D8ConstrainedAccumulation.class);
		FunctionManager.addFunction(CropRaster.class);
		FunctionManager.addFunction(ToEnvelope.class);

		// Vector processing

		QueryManager.registerQuery(new Generate2DMesh());
		QueryManager.registerQuery(new PointsToXYZ());
		QueryManager.registerQuery(new ToLineNoder());
		QueryManager.registerQuery(new ShortestPath());
		QueryManager.registerQuery(new BuildTIN());
		QueryManager.registerQuery(new RandomGeometry());

		QueryManager.registerQuery(new Cdt());

		FunctionManager.addFunction(ToMultiSegments.class);
		FunctionManager.addFunction(Generalize.class);
	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}