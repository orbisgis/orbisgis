/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.rasterDrivers;

import java.io.File;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.source.SourceManager;

public class TifDriver implements FileDriver {

	public String getName() {
		return "tif with world file driver";
	}

	public int getType() {
		return SourceManager.TFW;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public void close() throws DriverException {
	}

	public String completeFileName(String fileName) {
		if (!fileName.toLowerCase().endsWith(".tif")) {
			return fileName + ".tif";
		} else {
			return fileName;
		}
	}

	public boolean fileAccepted(File f) {
		String upperName = f.getName().toUpperCase();
		return upperName.endsWith(".TIF") || upperName.endsWith(".TIFF");
	}

	public void open(File file) throws DriverException {
	}

	public Metadata getMetadata() throws DriverException {
		return new DefaultMetadata();
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		return null;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return null;
	}

	public long getRowCount() throws DriverException {
		return 0;
	}

	public Number[] getScope(int dimension) throws DriverException {
		return null;
	}

}
