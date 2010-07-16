/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.driver;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.gdms.GdmsDriver;
import org.gdms.driver.gdms.GdmsReader;
import org.gdms.driver.gdms.GdmsWriter;
import org.gdms.sql.strategies.AbstractBasicSQLDriver;

/**
 * This driver writes all the content added with the {@link #addValues(Value[])}
 * method to a file saving main memory. One all calls to
 * {@link #addValues(Value[])} are done, the method {@link #writingFinished()}
 * must be called.
 * 
 * 
 * 
 */
public class DiskBufferDriver extends AbstractBasicSQLDriver {

	private Metadata metadata;
	private GdmsWriter writer;
	private File file;
	private boolean firstRow = true;
	private GdmsReader reader;

	public DiskBufferDriver(DataSourceFactory dsf, Metadata metadata)
			throws DriverException {
		this.metadata = metadata;
		// Create a temp file to populate
		file = new File(dsf.getTempFile("gdms"));
		try {
			writer = new GdmsWriter(file);
		} catch (IOException e) {
			throw new DriverException("Cannot start writing process", e);
		}
	}

	@Override
	public void start() throws DriverException {
		writingFinished();
		try {
			// Open file
			reader = new GdmsReader(file);
			reader.readMetadata();
		} catch (IOException e) {
			throw new DriverException("Cannot open temporal file for reading",
					e);
		}
		writer = null;
	}

	/**
	 * This method must be called when all the contents have been added to the
	 * file
	 * 
	 * @throws DriverException
	 */
	public void writingFinished() throws DriverException {
		// Close writing
		try {
			if (writer != null) {
				writeMetadataOnce();
				writer.writeRowIndexes();
				writer.writeExtent();
				writer.writeWritenRowCount();
				writer.close();
				writer = null;
			}
		} catch (IOException e) {
			throw new DriverException("Cannot finalize writing process", e);
		}
	}

	@Override
	public void stop() throws DriverException {
		try {
			reader.close();
		} catch (IOException e) {
			throw new DriverException("Cannot close gdms reader", e);
		}
	}

	@Override
	public Metadata getMetadata() throws DriverException {
		return metadata;
	}

	@Override
	public int getType() {
		return new GdmsDriver().getType();
	}

	@Override
	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	@Override
	public String getDriverId() {
		return new GdmsDriver().getDriverId();
	}

	@Override
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return reader.getFieldValue(rowIndex, fieldId);
	}

	@Override
	public long getRowCount() throws DriverException {
		return reader.getRowCount();
	}

	@Override
	public Number[] getScope(int dimension) throws DriverException {
		return reader.getScope(dimension);
	}

	/**
	 * Add a new row to the file
	 * 
	 * @param row
	 * @throws DriverException
	 */
	public void addValues(Value... row) throws DriverException {
		writeMetadataOnce();
		writer.addValues(row);
	}

	public File getFile() {
		return file;
	}

	private void writeMetadataOnce() throws DriverException {
		if (firstRow) {
			try {
				writer.writeMetadata(0, getMetadata());
				firstRow = false;
			} catch (IOException e) {
				throw new DriverException("Cannot write metadata", e);
			}
		}
	}
	
	/**
	 * Get all value for a row index
	 * @param rowIndex
	 * @return
	 * @throws DriverException
	 */
	public Value[] getRow(long rowIndex) throws DriverException {
		Value[] ret = new Value[getMetadata().getFieldCount()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = getFieldValue(rowIndex, i);
		}
		return ret;
	}
}
