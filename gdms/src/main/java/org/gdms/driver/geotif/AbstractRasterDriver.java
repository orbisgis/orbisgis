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
package org.gdms.driver.geotif;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.RasterTypeConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.source.SourceManager;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.utils.FileUtils;

import com.vividsolutions.jts.geom.Envelope;

public abstract class AbstractRasterDriver implements FileReadWriteDriver {

	protected GeoRaster geoRaster;
	protected RasterMetadata metadata;
	protected Envelope envelope;

	public void open(File file) throws DriverException {
		try {
			geoRaster = GeoRasterFactory
					.createGeoRaster(file.getAbsolutePath());
			geoRaster.open();
			metadata = geoRaster.getMetadata();
			envelope = metadata.getEnvelope();
		} catch (IOException e) {
			throw new DriverException("Cannot access the source: " + file, e);
		}
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public void close() throws DriverException {
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		switch (fieldId) {
		case 0:
			return ValueFactory.createValue(geoRaster);
		default:
			throw new DriverException("No such field:" + fieldId);

		}
	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		throw new UnsupportedOperationException("Cannot create an empty raster");
	}

	public long getRowCount() throws DriverException {
		return 1;
	}

	public void copy(File in, File out) throws IOException {
		FileUtils.copy(in, out);
	}

	public void writeFile(File file, DataSource dataSource, IProgressMonitor pm)
			throws DriverException {
		checkMetadata(dataSource.getMetadata());
		if (dataSource.getRowCount() == 0) {
			throw new DriverException("Cannot store an empty raster");
		} else if (dataSource.getRowCount() > 1) {
			throw new DriverException("Cannot store more than one raster");
		} else {
			SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					dataSource);
			GeoRaster gr = sds.getRaster(0);
			try {
				gr.save(file.getAbsolutePath());
			} catch (IOException e) {
				throw new DriverException("Cannot write raster", e);
			}
		}
	}

	public TypeDefinition[] getTypesDefinitions() {
		return null;
	}

	protected void checkMetadata(Metadata metadata) throws DriverException {
		if (metadata.getFieldCount() != 1) {
			throw new DriverException("This source only "
					+ "accepts an unique raster field");
		} else {
			Type fieldType = metadata.getFieldType(0);
			if (fieldType.getTypeCode() != Type.RASTER) {
				throw new DriverException("Raster field expected");
			}
		}
	}

	public Metadata getMetadata() throws DriverException {
		DefaultMetadata metadata = new DefaultMetadata();
		try {
			metadata.addField("raster", TypeFactory.createType(Type.RASTER,
					new RasterTypeConstraint(geoRaster.getType())));
		} catch (IOException e) {
			throw new DriverException("Cannot read the raster type", e);
		}
		return metadata;
	}

	public Number[] getScope(int dimension) throws DriverException {
		switch (dimension) {
		case X:
			return new Number[] { envelope.getMinX(), envelope.getMaxX() };
		case Y:
			return new Number[] { envelope.getMinY(), envelope.getMaxY() };
		default:
			return null;
		}
	}

	public String validateMetadata(Metadata metadata) throws DriverException {
		if (metadata.getFieldCount() != 1) {
			return "Cannot store more than one raster field";
		} else {
			int typeCode = metadata.getFieldType(0).getTypeCode();
			if (typeCode != Type.RASTER) {
				return "Cannot store " + TypeFactory.getTypeName(typeCode);
			}
		}
		return null;
	}

	public int getType() {
		return SourceManager.RASTER | SourceManager.FILE;
	}

	public boolean isCommitable() {
		return false;
	}

}
