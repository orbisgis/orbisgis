/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoprocessing;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.spatial.geometry.convert.PointsToXYZ;
import org.gdms.sql.customQuery.spatial.geometry.extract.IntersectsPolygon;
import org.gdms.sql.customQuery.spatial.geometry.others.RandomGeometry;
import org.gdms.sql.customQuery.spatial.geometry.tin.BuildTIN;
import org.gdms.sql.customQuery.spatial.geometry.topology.ToLineNoder;
import org.gdms.sql.customQuery.spatial.geometry.topology.TopologyPlanarGraph;
import org.gdms.sql.customQuery.spatial.geometry.triangulation.CheckCDT;
import org.gdms.sql.customQuery.spatial.geometry.triangulation.CheckDelaunayProperty;
import org.gdms.sql.customQuery.spatial.geometry.triangulation.ConstrainedTINProcessing;
import org.gdms.sql.customQuery.spatial.geometry.triangulation.QualityMeasuresOfTIN;
import org.gdms.sql.customQuery.spatial.geometry.triangulation.TINProcessing;
import org.gdms.sql.customQuery.spatial.geometry.update.InsertPoint;
import org.gdms.sql.customQuery.spatial.geometry.update.UpdateZGeometry;
import org.gdms.sql.customQuery.spatial.raster.convert.RasterToPoints;
import org.gdms.sql.customQuery.spatial.raster.convert.RasterToPolygons;
import org.gdms.sql.customQuery.spatial.raster.convert.RasterToXYZ;
import org.gdms.sql.customQuery.spatial.raster.convert.RasterizeLine;
import org.gdms.sql.customQuery.spatial.raster.convert.VectorizeLine;
import org.gdms.sql.customQuery.spatial.raster.interpolate.GeometryToRasterTINInterpolation;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.alphanumeric.SubString;
import org.gdms.sql.function.spatial.geometry.convert.PointsToLine;
import org.gdms.sql.function.spatial.geometry.extract.ToMultiSegments;
import org.gdms.sql.function.spatial.geometry.generalize.Generalize;
import org.gdms.sql.function.spatial.raster.hydrology.D8Accumulation;
import org.gdms.sql.function.spatial.raster.hydrology.D8AllOutlets;
import org.gdms.sql.function.spatial.raster.hydrology.D8ConstrainedAccumulation;
import org.gdms.sql.function.spatial.raster.hydrology.D8Direction;
import org.gdms.sql.function.spatial.raster.hydrology.D8DistanceToTheOutlet;
import org.gdms.sql.function.spatial.raster.hydrology.D8RiverDistance;
import org.gdms.sql.function.spatial.raster.hydrology.D8Slope;
import org.gdms.sql.function.spatial.raster.hydrology.D8StrahlerStreamOrder;
import org.gdms.sql.function.spatial.raster.hydrology.D8Watershed;
import org.gdms.sql.function.spatial.raster.hydrology.FillSinks;
import org.gdms.sql.function.spatial.raster.hydrology.LSFactor;
import org.gdms.sql.function.spatial.raster.hydrology.StreamPowerIndex;
import org.gdms.sql.function.spatial.raster.hydrology.WetnessIndex;
import org.geoalgorithm.orbisgis.grid.BigCreateGrid;
import org.geoalgorithm.orbisgis.grid.CreateGrid;
import org.geoalgorithm.orbisgis.grid.CreateWebGrid;
import org.geoalgorithm.urbsat.direction.MainDirections;
import org.geoalgorithm.urbsat.kmeans.KMeans;
import org.geoalgorithm.urbsat.landcoverIndicators.CircleCompacity;
import org.geoalgorithm.urbsat.landcoverIndicators.MeanSpacingBetweenBuildingsInACell;
import org.geoalgorithm.urbsat.topography.GetZDEM;
import org.orbisgis.pluginManager.PluginActivator;

public class Register implements PluginActivator {
	public void start() throws Exception {

		// Raster processing

		QueryManager.registerQuery(RasterToPoints.class);
		QueryManager.registerQuery(RasterToPolygons.class);
		QueryManager.registerQuery(RasterToXYZ.class);

		QueryManager.registerQuery(RasterizeLine.class);
		QueryManager.registerQuery(GeometryToRasterTINInterpolation.class);
		FunctionManager.addFunction(D8Slope.class);
		FunctionManager.addFunction(D8Direction.class);
		FunctionManager.addFunction(D8Accumulation.class);
		FunctionManager.addFunction(D8AllOutlets.class);
		FunctionManager.addFunction(D8Watershed.class);
		FunctionManager.addFunction(D8StrahlerStreamOrder.class);
		FunctionManager.addFunction(D8ConstrainedAccumulation.class);
		FunctionManager.addFunction(FillSinks.class);
		FunctionManager.addFunction(D8DistanceToTheOutlet.class);
		FunctionManager.addFunction(D8RiverDistance.class);
		FunctionManager.addFunction(PointsToLine.class);
		FunctionManager.addFunction(WetnessIndex.class);
		FunctionManager.addFunction(LSFactor.class);
		FunctionManager.addFunction(StreamPowerIndex.class);

		QueryManager.registerQuery(VectorizeLine.class);

		// Vector processing

		QueryManager.registerQuery(TopologyPlanarGraph.class);
		QueryManager.registerQuery(UpdateZGeometry.class);
		QueryManager.registerQuery(PointsToXYZ.class);
		QueryManager.registerQuery(ToLineNoder.class);
		QueryManager.registerQuery(BuildTIN.class);
		QueryManager.registerQuery(RandomGeometry.class);
		QueryManager.registerQuery(IntersectsPolygon.class);

		FunctionManager.addFunction(ToMultiSegments.class);
		FunctionManager.addFunction(Generalize.class);
		FunctionManager.addFunction(MeanSpacingBetweenBuildingsInACell.class);
		FunctionManager.addFunction(CircleCompacity.class);

		QueryManager.registerQuery(TINProcessing.class);
		QueryManager.registerQuery(ConstrainedTINProcessing.class);

		QueryManager.registerQuery(InsertPoint.class);

		QueryManager.registerQuery(CreateGrid.class);
		QueryManager.registerQuery(BigCreateGrid.class);
		QueryManager.registerQuery(GetZDEM.class);
		QueryManager.registerQuery(CreateWebGrid.class);
		QueryManager.registerQuery(KMeans.class);
		QueryManager.registerQuery(MainDirections.class);
		QueryManager.registerQuery(CheckDelaunayProperty.class);
		QueryManager.registerQuery(CheckCDT.class);
		QueryManager.registerQuery(QualityMeasuresOfTIN.class);

	}

	public void stop() throws Exception {
	}

	public boolean allowStop() {
		return true;
	}
}