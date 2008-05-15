/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
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
package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.progress.IProgressMonitor;

public class BuildSpatialIndex implements CustomQuery {

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		String sourceName = tables[0].getName();
		try {
			String geomField = null;
			if (values.length == 0) {
				Metadata metadata = tables[0].getMetadata();
				for (int i = 0; i < metadata.getFieldCount(); i++) {
					if (metadata.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
						geomField = metadata.getFieldName(i);
					}
				}
				if (geomField == null) {
					throw new ExecutionException(
							"No spatial field can be found on "
									+ tables[0].getName());
				}
			} else {
				geomField = values[0].toString();
			}
			dsf.getIndexManager().buildIndex(sourceName, geomField,
					IndexManager.RTREE_SPATIAL_INDEX, pm);
		} catch (IndexException e) {
			throw new ExecutionException("Cannot create the index", e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException("Source not found: " + sourceName, e);
		} catch (DriverException e) {
			throw new ExecutionException("Cannot access source: " + sourceName,
					e);
		}

		return null;
	}

	public String getName() {
		return "BuildSpatialIndex";
	}

	public String getDescription() {
		return "Builds a spatial index";
	}

	public String getSqlOrder() {
		return "select BuildSpatialIndex('spatialFieldName') from sourceName;";
	}

	public Metadata getMetadata(Metadata[] tables) {
		return null;
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 0, 1);
		if (types.length > 0) {
			FunctionValidator.failIfNotOfType(this, types[0], Type.GEOMETRY);
		}
	}

	public void validateTables(Metadata[] tables) throws SemanticException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 1);
	}
}