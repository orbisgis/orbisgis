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
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
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
package org.gdms.newFunctionalities;

import junit.framework.TestCase;

public class SecondarySpatialDataSourceTest extends TestCase {
	//
	// private DataSourceFactory dsf = new DataSourceFactory();
	//
	// /**
	// * Opens a database table with two spatial fields
	// *
	// * @throws Exception
	// */
	// public void testSecondarySpatialDataSource() throws Exception {
	// SpatialDataSource secondaryDS = (SpatialDataSource) dsf
	// .executeSQL("SELECT bounds(geom) as bounds, geom as g FROM
	// file('/home/fernando/roads.shp')");
	//
	// String spatialFieldName1 = "bounds";
	// String spatialFieldName2 = "g";
	// secondaryDS.getFullExtent(spatialFieldName1);
	// secondaryDS.getFullExtent(spatialFieldName2);
	// for (int i = 0; i < secondaryDS.getRowCount(); i++) {
	// secondaryDS.getGeometry(i, spatialFieldName1);
	// secondaryDS.getGeometry(i, spatialFieldName2);
	// }
	// secondaryDS.getSpatialFieldIndex(spatialFieldName1);
	// secondaryDS.getSpatialFieldIndex(spatialFieldName2);
	// secondaryDS.getGeometryType(spatialFieldName1);
	// secondaryDS.getGeometryType(spatialFieldName2);
	// }
	//
	// public void testSecondaryDataSourceEdition() throws Exception {
	// SpatialDataSource secondaryDS = (SpatialDataSource) dsf
	// .executeSQL("SELECT bounds(geom) as bounds, geom as g, name FROM
	// file('/home/fernando/roads.shp')");
	// secondaryDS.beginTrans();
	// secondaryDS.setString(0, "name", "Valencia-Albacete road");
	// try {
	// secondaryDS.commitTrans();
	// assertTrue(false); // commit must raise an exception. This line
	// // should never be executed
	// } catch (UnsupportedOperationException e) {
	// /*
	// * Save to a new file
	// */
	// secondaryDS
	// .saveAs("CREATE TABLE "
	// + "file('/home/fernando/roads2.shp') "
	// + "(bounds geometry(polygon), g geometry(line), name text(30));");
	// /*
	// * Save to a DataSource
	// */
	// DefaultSpatialDriverMetadata ddm = new DefaultSpatialDriverMetadata();
	// ddm.addField("bounds", "geometry", new String[]{"geometry_type"}, new
	// String[]{"polygon"});
	// ddm.addField("g", "geometry", new String[]{"geometry_type"}, new
	// String[]{"line"});
	// ddm.addField("name", "text", new String[]{"length"}, new String[]{"30"});
	// dsf.createDataSource(new File("/home/fernando/roads3.shp"), ddm);
	// SpatialDataSource sds =
	// dsf.getSpatialDataSource("/home/fernando/roads3.shp");
	// secondaryDS.saveAs(sds);
	// }
	// }
	//
	// public void testDirectCreation() throws Exception {
	// DefaultSpatialDriverMetadata ddm = new DefaultSpatialDriverMetadata();
	// ddm.addField("bounds", "geometry", new String[]{"geometry_type"}, new
	// String[]{"polygon"});
	// ddm.addField("g", "geometry", new String[]{"geometry_type"}, new
	// String[]{"line"});
	// ddm.addField("name", "text", new String[]{"length"}, new String[]{"30"});
	// dsf.createDataSource(new File("/home/fernando/roads3.shp"), ddm);
	// SpatialDataSource sds =
	// dsf.getSpatialDataSource("/home/fernando/roads3.shp");
	// /*
	// * Test all data is accessible
	// */
	// sds.start();
	// sds.getAsString();
	// sds.stop();
	//
	// }
}
