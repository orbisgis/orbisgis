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

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
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
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class CreateWebGrid implements CustomQuery {
	private final static double DPI = 2 * Math.PI;
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	private double deltaR;
	private double deltaT;
	private Coordinate centroid;

	private SpatialDataSourceDecorator inSds;
	private ObjectMemoryDriver driver;

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		try {
			deltaR = values[0].getAsDouble();
			deltaT = values[1].getAsDouble();
			inSds = new SpatialDataSourceDecorator(tables[0]);
			inSds.open();

			// built the driver for the resulting datasource and register it...
			driver = new ObjectMemoryDriver(getMetadata(MetadataUtilities
					.fromTablesToMetadatas(tables)));
			createGrid(inSds.getFullExtent());
			inSds.cancel();
			return driver;
		} catch (AlreadyClosedException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		}
	}

	public String getName() {
		return "CreateWebGrid";
	}

	public String getDescription() {
		return "Calculate a regular grid that may be optionnaly oriented";
	}

	public String getSqlOrder() {
		return "select CreateWebGrid(4000,1000) from myTable;";
	}

	private void createGrid(final Envelope env) throws DriverException {
		final double R = 0.5 * Math.sqrt(env.getWidth() * env.getWidth()
				+ env.getHeight() * env.getHeight());
		centroid = env.centre();
		final double perimeter = DPI * R;
		final int Nr = (int) Math.ceil(R / deltaR);
		deltaR = R / Nr; // TODO : to be comment
		final int Nt = (int) Math.ceil(perimeter / (2 * deltaT));
		deltaT = DPI / Nt;

		int gridCellIndex = 0;
		for (int t = 0; t < Nt; t++) {
			for (int r = 0; r < Nr; r++) {
				createGridCell(r, t, gridCellIndex);
				gridCellIndex++;
			}
		}
	}

	private void createGridCell(final int r, final int t,
			final int gridCellIndex) {
		final Coordinate[] summits = new Coordinate[5];
		summits[0] = polar2cartesian(r, t);
		summits[1] = polar2cartesian(r + 1, t);
		summits[2] = polar2cartesian(r + 1, t + 1);
		summits[3] = polar2cartesian(r, t + 1);
		summits[4] = summits[0];
		createGridCell(summits, gridCellIndex);
	}

	private Coordinate polar2cartesian(final int r, final int t) {
		final double rr = r * deltaR;
		final double tt = t * deltaT;
		return new Coordinate(centroid.x + rr * Math.cos(tt), centroid.y + rr
				* Math.sin(tt));
	}

	private void createGridCell(final Coordinate[] summits,
			final int gridCellIndex) {
		final LinearRing g = geometryFactory.createLinearRing(summits);
		final Geometry gg = geometryFactory.createPolygon(g, null);
		driver.addValues(new Value[] { ValueFactory.createValue(gg),
				ValueFactory.createValue(gridCellIndex) });
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.INT) }, new String[] { "the_geom",
				"index" });
	}

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 1);
		FunctionValidator.failIfNotSpatialDataSource(this, tables[0], 0);
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 2);
		FunctionValidator.failIfNotNumeric(this, types[0], 1);
		FunctionValidator.failIfNotNumeric(this, types[1], 2);
	}
}