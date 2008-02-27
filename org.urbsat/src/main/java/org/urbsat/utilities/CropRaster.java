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
package org.urbsat.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
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
import org.grap.processing.OperationException;
import org.grap.processing.operation.Crop;
import org.grap.utilities.EnvelopeUtil;
import org.orbisgis.core.OrbisgisCore;

import com.vividsolutions.jts.geom.LinearRing;

public class CropRaster implements CustomQuery {
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		try {
			final Source dem = dsf.getSourceManager().getSource(
					values[0].toString());
			int type = dem.getType();

			GeoRaster geoRaster;
			String path;

			if ((type & SourceManager.RASTER) == SourceManager.RASTER) {
				if (dem.isFileSource()) {
					path = dem.getFile().getAbsolutePath();
					geoRaster = GeoRasterFactory.createGeoRaster(path);
					geoRaster.open();
				} else {
					throw new ExecutionException("The DEM must be a file !");
				}
			} else {
				throw new UnsupportedOperationException(
						"Cannot understand source type: " + type);
			}

			final SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
					tables[0]);
			inSds.open();

			// build the resulting driver
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(MetadataUtilities.fromTablesToMetadatas(tables)));
			final LinearRing polygon = (LinearRing) EnvelopeUtil
					.toGeometry(inSds.getFullExtent());
			final Crop crop = new Crop(polygon);
			final GeoRaster croppedGeoRaster = geoRaster.doOperation(crop);

			driver.addValues(new Value[] { ValueFactory.createValue(0),
					ValueFactory.createValue(polygon),
					ValueFactory.createValue(path) });
			inSds.cancel();

			// save the cropped GeoRaster in a tempFile
			final String tempFileName = dsf.getTempFile() + ".tif";
			croppedGeoRaster.save(tempFileName);

			// insert the cropped GeoRaster in the GeoCatalog TOC
			final File tempFile = new File(tempFileName);
			OrbisgisCore.registerInDSF("Cropped Zone",
					new FileSourceDefinition(tempFile));

			return driver;
		} catch (FileNotFoundException e) {
			throw new ExecutionException(e);
		} catch (IOException e) {
			throw new ExecutionException(e);
		} catch (GeoreferencingException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (OperationException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "This custom query produces a new DEM and new Datasource with 3 fields : id, the_geom (geometry), path";
	}

	public String getSqlOrder() {
		return "select CropRaster('the_DEM') from myTable;";
	}

	public String getName() {
		return "CropRaster";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.STRING) }, new String[] { "index",
				"the_geom", "path" });
	}

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 1);
		FunctionValidator.failIfNotSpatialDataSource(this, tables[0], 0);
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 1);
		FunctionValidator.failIfNotOfType(this, types[0], Type.STRING);
	}
}