/*
 * UrbSAT is a set of spatial functionalities to build morphological
 * and aerodynamic urban indicators. It has been developed on
 * top of GDMS and OrbisGIS. UrbSAT is distributed under GPL 3
 * license. It is produced by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of UrbSAT.
 *
 * UrbSAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UrbSAT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UrbSAT. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.rasterProcessing.sql.customQuery;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class RasterToPoints implements CustomQuery {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		try {
			final Source raster = dsf.getSourceManager().getSource(
					values[0].toString());
			final int type = raster.getType();
			GeoRaster geoRaster;

			if ((type & SourceManager.RASTER) == SourceManager.RASTER) {
				if (raster.isFileSource()) {
					geoRaster = GeoRasterFactory.createGeoRaster(raster
							.getFile().getAbsolutePath());
					geoRaster.open();
				} else {
					throw new ExecutionException("The raster must be a file !");
				}
			} else {
				throw new UnsupportedOperationException(
						"Cannot understand source type: " + type);
			}

			// built the driver for the resulting datasource and populate it...
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			final float[] pixels = geoRaster.getGrapImagePlus()
					.getFloatPixels();
			for (int l = 0, i = 0; l < geoRaster.getHeight(); l++) {
				for (int c = 0; c < geoRaster.getWidth(); c++) {
					final float height = pixels[i];
					if (!Float.isNaN(height)) {
						// geoRaster.getGrapImagePlus().getPixelValue(c, l);
						final Point2D point2D = geoRaster
								.fromPixelGridCoordToRealWorldCoord(c, l);
						final Geometry point = geometryFactory
								.createPoint(new Coordinate(point2D.getX(),
										point2D.getY(), height));
						driver.addValues(new Value[] {
								ValueFactory.createValue(i),
								ValueFactory.createValue(point),
								ValueFactory.createValue(height) });
					}
					i++;
				}
			}
			return driver;
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (FileNotFoundException e) {
			throw new ExecutionException(e);
		} catch (IOException e) {
			throw new ExecutionException(e);
		} catch (GeoreferencingException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "Transform a Raster into a spatial datasource (set of centroids points)";
	}

	public String getName() {
		return "RasterToPoints";
	}

	public String getSqlOrder() {
		return "select RasterToPoints('myRaster');";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.DOUBLE) }, new String[] { "index",
				"the_geom", "height" });
	}

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 0);
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 1);
		FunctionValidator.failIfNotOfType(this, types[0], Type.STRING);
	}
}