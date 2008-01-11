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
package org.gdms.driver.dbf;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class DBFTests {

	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		DataSource ds = dsf.getDataSource(new File("../../datas2tests/"
				+ "shp/bigshape2D/communes.dbf"));
		long t1 = System.currentTimeMillis();
		int numIterations = 10;
		for (int j = 0; j < numIterations; j++) {
			ds.open();
			for (int i = 0; i < ds.getRowCount(); i++) {
				ds.getFieldValue(i, 0);
			}
			ds.cancel();
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Time to read: " + ((t2 - t1) / numIterations));
	}

	public static void main2(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("string", Type.STRING,
				new Constraint[] { ConstraintFactory.createConstraint(
						ConstraintNames.LENGTH, "10") });
		metadata.addField("the_geom", Type.GEOMETRY,
				new Constraint[] { new GeometryConstraint(
						GeometryConstraint.POINT_2D) });
		File file = new File("new.shp");
		file.delete();
		dsf.createDataSource(new FileSourceCreation(file, metadata));
		DataSource ds = dsf.getDataSource(file);
		ds.open();
		GeometryFactory gf = new GeometryFactory();
		for (int i = 0; i < 100000; i++) {
			ds.insertEmptyRow();
			ds.setFieldValue(i, 0, ValueFactory.createValue(gf
					.createPoint(new Coordinate(i, 0))));
			ds.setString(i, 1, Integer.toString(i));
		}
		long t1 = System.currentTimeMillis();
		ds.commit();
		long t2 = System.currentTimeMillis();
		System.out.println("Time to write: " + (t2 - t1));
		ds.open();
		t1 = System.currentTimeMillis();
		int numIterations = 25;
		for (int i = 0; i < numIterations; i++) {
			gdmsProcess(ds);
		}
		t2 = System.currentTimeMillis();
		System.out.println((t2 - t1) / numIterations);
		ds.cancel();
	}

	private static void gdmsProcess(DataSource ds) throws Exception {
		for (int i = 0; i < ds.getRowCount(); i++) {
			ds.getFieldValue(i, 0);
		}
	}

}
