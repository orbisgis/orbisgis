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
package org.gdms.sql.customQuery.spatial.convert;

import java.util.LinkedList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

// select * from ds where the_geom IS NOT NULL and isVali

//select Explode() from points;
//select Explode(the_geom) from points;

public class Explode implements CustomQuery {
	public String getName() {
		return "Explode";
	}

	public String getSqlOrder() {
		return "select Explode() from myTable;";
	}

	public String getDescription() {
		return "Convert any GeometryCollection into a set of single Geometries";
	}

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (tables.length != 1) {
			throw new ExecutionException("Explode only operates on one table");
		}
		if (values.length > 1) {
			throw new ExecutionException(
					"Explode operates with no more than one value");
		}

		try {
			final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					tables[0]);
			sds.open();
			if (1 == values.length) {
				// if no spatial's field's name is provided, the default (first)
				// one is arbitrarily choose.
				final String spatialFieldName = values[0].toString();
				sds.setDefaultGeometry(spatialFieldName);
			}
			final int spatialFieldIndex = sds.getSpatialFieldIndex();

			final Metadata metadata = sds.getMetadata();
			// a simple :
			// final ObjectMemoryDriver driver = new
			// ObjectMemoryDriver(metadata);
			// is not enough... we also need to remove all pk or unique
			// constraints !
			final int fieldCount = metadata.getFieldCount();
			final Type[] fieldsTypes = new Type[fieldCount];
			final String[] fieldsNames = new String[fieldCount];

			for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
				fieldsNames[fieldId] = metadata.getFieldName(fieldId);
				fieldsTypes[fieldId] = metadata.getFieldType(fieldId);

				if ((null == fieldsTypes[fieldId]
						.getConstraint(ConstraintNames.PK))
						|| ((null == fieldsTypes[fieldId]
								.getConstraint(ConstraintNames.UNIQUE)))) {
					// let us try to remove the PK/UNIQUE constraint...
					final List<Constraint> lc = new LinkedList<Constraint>();
					for (Constraint c : fieldsTypes[fieldId].getConstraints()) {
						if ((ConstraintNames.PK != c.getConstraintName())
								&& (ConstraintNames.UNIQUE != c
										.getConstraintName())) {
							lc.add(c);
						}
					}
					fieldsTypes[fieldId] = TypeFactory.createType(
							fieldsTypes[fieldId].getTypeCode(),
							fieldsTypes[fieldId].getDescription(), lc
									.toArray(new Constraint[0]));
				}
			}
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					fieldsNames, fieldsTypes);

			long nbOfRows = sds.getRowCount();
			for (long rowIndex = 0; rowIndex < nbOfRows; rowIndex++) {
				final Value[] fieldsValues = sds.getRow(rowIndex);
				final Geometry geometry = sds.getGeometry(rowIndex);

				if (geometry instanceof GeometryCollection) {
					final long nbOfGeometries = geometry.getNumGeometries();
					for (int i = 0; i < nbOfGeometries; i++) {
						fieldsValues[spatialFieldIndex] = ValueFactory
								.createValue(geometry.getGeometryN(i));
						driver.addValues(fieldsValues);
					}

				} else {
					driver.addValues(fieldsValues);
				}
			}
			sds.cancel();

			return driver;
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (InvalidTypeException e) {
			throw new ExecutionException(e);
		}
	}

	public Metadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		// TODO Auto-generated method stub

	}

	public void validateTables(Metadata[] tables) throws SemanticException {
		// TODO Auto-generated method stub

	}
}