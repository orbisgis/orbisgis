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
 
package org.gdms.sql.customQuery.spatial.geometry.crs;

import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;

import fr.cts.CoordinateOperation;
import fr.cts.Identifier;
import fr.cts.IllegalCoordinateException;
import fr.cts.crs.GeodeticCRS;
import fr.cts.op.CoordinateOperationFactory;
import fr.cts.op.CoordinateOperationSequence;
import fr.cts.util.CRSUtil;

public class ST_Transform implements CustomQuery {

	GeodeticCRS targetCRS;

	GeodeticCRS sourceCRS;

	private CoordinateOperationSequence cos;

	private Geometry geom;

	@Override
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {

		String geomField = values[0].toString();

		String name = values[1].toString();

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				tables[0]);

		targetCRS = (GeodeticCRS) CRSUtil.getCRSFromEPSG(name);
		try {
			sds.setDefaultGeometry(geomField);

			sds.open();
			sourceCRS = (GeodeticCRS) sds.getCRS();
			List<CoordinateOperation> ops = CoordinateOperationFactory
					.createCoordinateOperations(sourceCRS, targetCRS);
			cos = new CoordinateOperationSequence(new Identifier(
					ST_Transform.class, "" + " to " + ""), ops);

			Metadata metaData = MetadataUtilities.addCRSConstraint(sds
					.getMetadata(), geomField, targetCRS);
			GenericObjectDriver driver = new GenericObjectDriver(metaData);

			GeometryTransformer gt = new GeometryTransformer() {
				protected CoordinateSequence transformCoordinates(
						CoordinateSequence cs, Geometry geom) {
					Coordinate[] cc = geom.getCoordinates();
					CoordinateSequence newcs = new CoordinateArraySequence(cc);
					for (int i = 0; i < cc.length; i++) {
						Coordinate c = cc[i];
						try {
							double[] xyz = cos.transform(new double[] { c.x,
									c.y, c.z });
							newcs.setOrdinate(i, 0, xyz[0]);
							newcs.setOrdinate(i, 1, xyz[1]);
							if (xyz.length > 2)
								newcs.setOrdinate(i, 2, xyz[2]);
							else
								newcs.setOrdinate(i, 2, Double.NaN);
						} catch (IllegalCoordinateException ice) {
							ice.printStackTrace();
						}
					}
					return newcs;
				}
			};

			int geomIndex = sds.getSpatialFieldIndex();
			for (int i = 0; i < sds.getRowCount(); i++) {
				values = sds.getRow(i);
				geom = sds.getGeometry(i);
				values[geomIndex] = ValueFactory
						.createValue(gt.transform(geom));
				driver.addValues(values);
			}

			sds.close();

			return driver;
		} catch (DriverException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	@Override
	public String getDescription() {
		return "Transform a geometry from a specific CRS to an EPSG CRS";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY,
				Argument.STRING) };
	}

	@Override
	public Metadata getMetadata(Metadata[] tables) throws DriverException {

		return null;
	}

	@Override
	public String getName() {
		return "ST_Transform";
	}

	@Override
	public String getSqlOrder() {
		return "Select ST_Transform(the_geom, '4326') from myTable";
	}

}
*/