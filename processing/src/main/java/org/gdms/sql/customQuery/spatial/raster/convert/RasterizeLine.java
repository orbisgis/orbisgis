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
package org.gdms.sql.customQuery.spatial.raster.convert;

import ij.gui.Line;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.others.RasteringMode;
import org.grap.processing.operation.others.Rasterization;
import org.grap.utilities.JTSConverter;
import org.grap.utilities.PixelsUtil;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

public class RasterizeLine implements CustomQuery {

	public String getDescription() {
		return "Convert a set of lines into a raster grid.";
	}

	public String getName() {
		return "RasterizeLine";
	}

	public String getSqlOrder() {
		return "select RasterizeLine(the_geom,raster, value) as raster from myTable, mydem;";
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				tables[0]);
		final SpatialDataSourceDecorator dsRaster = new SpatialDataSourceDecorator(
				tables[1]);

		try {
			dsRaster.open();
			sds.open();
			int value = values[2].getAsInt();

			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));

			long rowCount = sds.getRowCount();
			long dsRasterRowCount = dsRaster.getRowCount();

			int dsRasterIndex = dsRaster.getFieldIndexByName(values[1]
					.toString());

			for (int j = 0; j < dsRasterRowCount; j++) {

				GeoRaster raster = dsRaster.getRaster(dsRasterIndex);

				PixelsUtil pixelsUtil = new PixelsUtil(raster);
				final ArrayList<Roi> rois = new ArrayList<Roi>();
				int geomFieldIndex = sds.getFieldIndexByName(values[0]
						.toString());

				for (int i = 0; i < rowCount; i++) {
					final Geometry geom = sds.getGeometry(geomFieldIndex);
					if (geom instanceof LineString) {
						final LineString ls = (LineString) geom;
						rois.add(new ShapeRoi(JTSConverter
								.toPolygonRoi(pixelsUtil.toPixel(ls))));
					} else if (geom instanceof MultiLineString) {
						final MultiLineString mls = (MultiLineString) geom;

						if (mls.getEnvelope() instanceof Point) {

							Point2D pixelscoords = raster.fromRealWorldToPixel(
									mls.getCoordinates()[0].x, mls
											.getCoordinates()[0].y);

							rois.add(new PointRoi((int) pixelscoords.getX(),
									(int) pixelscoords.getY()));
						} else if (mls.getEnvelope() instanceof LineString) {
							Point2D pixelscoords1 = raster
									.fromRealWorldToPixel(
											mls.getCoordinates()[0].x, mls
													.getCoordinates()[0].y);
							Point2D pixelscoords2 = raster
									.fromRealWorldToPixel(
											mls.getCoordinates()[1].x, mls
													.getCoordinates()[1].y);

							rois.add(new Line((int) pixelscoords1.getX(),
									(int) pixelscoords1.getY(),
									(int) pixelscoords2.getX(),
									(int) pixelscoords2.getY()));
						} else {
							for (int k = 0; k < mls.getNumGeometries(); k++) {
								Geometry subgeom = mls.getGeometryN(k);
								final LineString ls = (LineString) subgeom;
								rois.add(JTSConverter.toPolygonRoi(pixelsUtil
										.toPixel(ls)));
							}
						}

					}
				}

				if (rois.size() > 0) {
					final Operation rasterizing = new Rasterization(
							RasteringMode.DRAW, rois, value);
					final GeoRaster grResult = raster.doOperation(rasterizing);
					
					grResult.show();
					driver.addValues(new Value[] { ValueFactory
							.createValue(grResult) });
				}

			}
			sds.cancel();
			dsRaster.cancel();
			return driver;
		} catch (DriverException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OperationException e) {
			e.printStackTrace();
		}
		return null;

	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });

	}

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 2);
		FunctionValidator.failIfNotSpatialDataSource(this, tables[0], 0);
		FunctionValidator.failIfNotRasterDataSource(this, tables[1], 0);
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 3);
		FunctionValidator.failIfNotOfType(this, types[0], Type.GEOMETRY);
		FunctionValidator.failIfNotOfType(this, types[1], Type.RASTER);
		FunctionValidator.failIfNotNumeric(this, types[2], 2);
	}
}