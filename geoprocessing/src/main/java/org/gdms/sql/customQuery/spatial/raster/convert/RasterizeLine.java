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

import ij.gui.Roi;
import ij.gui.ShapeRoi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.FunctionException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.others.RasteringMode;
import org.grap.processing.operation.others.Rasterization;
import org.grap.utilities.JTSConverter;
import org.grap.utilities.PixelsUtil;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class RasterizeLine implements CustomQuery {
	public String getDescription() {
		return "Convert a set of lines into a raster grid.";
	}

	public String getName() {
		return "RasterizeLine";
	}

	public String getSqlOrder() {
		return "select RasterizeLine(the_geom, raster, value) as raster from myTable, mydem;";
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
			long dsGeomRowCount = sds.getRowCount();
			long dsRasterRowCount = dsRaster.getRowCount();

			sds.setDefaultGeometry(values[0].toString());
			dsRaster.setDefaultGeometry(values[1].toString());

			for (int rasterIdx = 0; rasterIdx < dsRasterRowCount; rasterIdx++) {
				final GeoRaster raster = dsRaster.getRaster(rasterIdx);
				final PixelsUtil pixelsUtil = new PixelsUtil(raster);
				final ArrayList<Roi> rois = new ArrayList<Roi>();

				for (int geomIdx = 0; geomIdx < dsGeomRowCount; geomIdx++) {
					final Geometry geom = sds.getGeometry(geomIdx);
					try {
						rois.addAll(getRoi(geom, pixelsUtil));
					} catch (FunctionException e) {
						// TODO maybe should we warn the user ?
					}
				}

				if (rois.size() > 0) {
					final Operation rasterizing = new Rasterization(
							RasteringMode.DRAW, rois, value);
					final GeoRaster grResult = raster.doOperation(rasterizing);
					driver.addValues(new Value[] { ValueFactory
							.createValue(grResult) });
				}
			}
			sds.close();
			dsRaster.close();
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(
					"Problem trying to access input datasources", e);
		} catch (IOException e) {
			throw new ExecutionException(
					"Problem trying to raster input datasource", e);
		} catch (OperationException e) {
			throw new ExecutionException(
					"error with GRAP Rasterization operation", e);
		}
	}

	private List<Roi> getRoi(Point point, PixelsUtil pixelsUtil)
			throws FunctionException {
		throw new FunctionException("Cannot handle Point!");
	}

	private List<Roi> getRoi(LineString lineString, PixelsUtil pixelsUtil)
			throws FunctionException {
		final Geometry env = lineString.getEnvelope();
		if ((env instanceof LineString) && (lineString.getNumPoints() > 2)) {
			// simplification process
			return getRoi((LineString) env, pixelsUtil);
		}
		return Arrays.asList(new Roi[] { new ShapeRoi(JTSConverter
				.toPolygonRoi(pixelsUtil.toPixel(lineString))) });
	}

	private List<Roi> getRoi(Polygon polygon, PixelsUtil pixelsUtil)
			throws FunctionException {
		return getRoi(polygon.getExteriorRing(), pixelsUtil);
	}

	private List<Roi> getRoi(GeometryCollection gc, PixelsUtil pixelsUtil)
			throws FunctionException {
		final List<Roi> result = new ArrayList<Roi>();
		for (int i = 0; i < gc.getNumGeometries(); i++) {
			result.addAll(getRoi(gc.getGeometryN(i), pixelsUtil));
		}
		return result;
	}

	private List<Roi> getRoi(Geometry geometry, PixelsUtil pixelsUtil)
			throws FunctionException {
		if (geometry instanceof Point) {
			return getRoi((Point) geometry, pixelsUtil);
		} else if (geometry instanceof LineString) {
			return getRoi((LineString) geometry, pixelsUtil);
		} else if (geometry instanceof Polygon) {
			return getRoi((Polygon) geometry, pixelsUtil);
		} else {
			return getRoi((GeometryCollection) geometry, pixelsUtil);
		}
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY,
				TableDefinition.RASTER };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY,
				Argument.RASTER, Argument.NUMERIC) };
	}
}