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
package org.gdms.manual;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.values.Value;
import org.gdms.sql.strategies.FirstStrategy;

import com.vividsolutions.jts.geom.Envelope;

public class IndexTest extends TestCase {
	public void testname() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.getSourceManager().register(
				"roads",
				new FileSourceDefinition(new File(
						"../../datas2tests/shp/bigshape2D/communes.shp")));
		dsf.getIndexManager().buildIndex("roads", "the_geom",
				SpatialIndex.SPATIAL_INDEX);
		DataSource ds = dsf.getDataSource("roads");
		ds.open();
		SpatialIndexQuery query = new SpatialIndexQuery(new Envelope(),
				"the_geom");
		ds.queryIndex(query);
		ds.cancel();

		ds = dsf.executeSQL("select * from roads where Contains("
				+ "GeomFromText('POLYGON (( 250000 2300000, "
				+ "300000 2300000, " + "300000 2330000, " + "250000 2330000, "
				+ "250000 2300000))')" + ", the_geom);");

		FirstStrategy.indexes = false;
		long t1 = System.currentTimeMillis();
		ds = dsf.executeSQL("select * from roads where Contains("
				+ "GeomFromText('POLYGON (( 250000 2300000, "
				+ "300000 2300000, " + "300000 2330000, " + "250000 2330000, "
				+ "250000 2300000))')" + ", the_geom);");
		long t2 = System.currentTimeMillis();
		System.out.println((t2 - t1) + " ms");

		FirstStrategy.indexes = true;
		t1 = System.currentTimeMillis();
		DataSource ds2 = dsf.executeSQL("select * from roads where Contains("
				+ "GeomFromText('POLYGON (( 250000 2300000, "
				+ "300000 2300000, " + "300000 2330000, " + "250000 2330000, "
				+ "250000 2300000))')" + ", the_geom);");
		t2 = System.currentTimeMillis();
		System.out.println((t2 - t1) + " ms");

		Set<Value> contents = new TreeSet<Value>(new Comparator<Value>() {

			public int compare(Value o1, Value o2) {
				return o1.toString().compareTo(o2.toString());
			}

		});
		ds.open();
		System.out.println(ds.getRowCount());
		for (int i = 0; i < ds.getRowCount(); i++) {
			contents.add(ds.getFieldValue(i, 0));
		}
		ds.cancel();

		ds2.open();
		System.out.println(ds2.getRowCount());
		for (int i = 0; i < ds2.getRowCount(); i++) {
			contents.remove(ds2.getFieldValue(i, 0));
		}
		ds2.cancel();

		System.out.println(contents.size());

	}
}
