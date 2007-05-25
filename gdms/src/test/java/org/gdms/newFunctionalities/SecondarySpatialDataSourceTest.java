package org.gdms.newFunctionalities;

import junit.framework.TestCase;

public class SecondarySpatialDataSourceTest extends TestCase {
//
//	private DataSourceFactory dsf = new DataSourceFactory();
//
//	/**
//	 * Opens a database table with two spatial fields
//	 *
//	 * @throws Exception
//	 */
//	public void testSecondarySpatialDataSource() throws Exception {
//		SpatialDataSource secondaryDS = (SpatialDataSource) dsf
//				.executeSQL("SELECT bounds(geom) as bounds, geom as g FROM file('/home/fernando/roads.shp')");
//
//		String spatialFieldName1 = "bounds";
//		String spatialFieldName2 = "g";
//		secondaryDS.getFullExtent(spatialFieldName1);
//		secondaryDS.getFullExtent(spatialFieldName2);
//		for (int i = 0; i < secondaryDS.getRowCount(); i++) {
//			secondaryDS.getGeometry(i, spatialFieldName1);
//			secondaryDS.getGeometry(i, spatialFieldName2);
//		}
//		secondaryDS.getSpatialFieldIndex(spatialFieldName1);
//		secondaryDS.getSpatialFieldIndex(spatialFieldName2);
//		secondaryDS.getGeometryType(spatialFieldName1);
//		secondaryDS.getGeometryType(spatialFieldName2);
//	}
//
//	public void testSecondaryDataSourceEdition() throws Exception {
//		SpatialDataSource secondaryDS = (SpatialDataSource) dsf
//				.executeSQL("SELECT bounds(geom) as bounds, geom as g, name FROM file('/home/fernando/roads.shp')");
//		secondaryDS.beginTrans();
//		secondaryDS.setString(0, "name", "Valencia-Albacete road");
//		try {
//			secondaryDS.commitTrans();
//			assertTrue(false); // commit must raise an exception. This line
//			// should never be executed
//		} catch (UnsupportedOperationException e) {
//			/*
//			 * Save to a new file
//			 */
//			secondaryDS
//					.saveAs("CREATE TABLE "
//							+ "file('/home/fernando/roads2.shp') "
//							+ "(bounds geometry(polygon), g geometry(line), name text(30));");
//			/*
//			 * Save to a InternalDataSource
//			 */
//			DefaultSpatialDriverMetadata ddm = new DefaultSpatialDriverMetadata();
//			ddm.addField("bounds", "geometry", new String[]{"geometry_type"}, new String[]{"polygon"});
//			ddm.addField("g", "geometry", new String[]{"geometry_type"}, new String[]{"line"});
//			ddm.addField("name", "text", new String[]{"length"}, new String[]{"30"});
//			dsf.createDataSource(new File("/home/fernando/roads3.shp"), ddm);
//			SpatialDataSource sds = dsf.getSpatialDataSource("/home/fernando/roads3.shp");
//			secondaryDS.saveAs(sds);
//		}
//	}
//
//	public void testDirectCreation() throws Exception {
//		DefaultSpatialDriverMetadata ddm = new DefaultSpatialDriverMetadata();
//		ddm.addField("bounds", "geometry", new String[]{"geometry_type"}, new String[]{"polygon"});
//		ddm.addField("g", "geometry", new String[]{"geometry_type"}, new String[]{"line"});
//		ddm.addField("name", "text", new String[]{"length"}, new String[]{"30"});
//		dsf.createDataSource(new File("/home/fernando/roads3.shp"), ddm);
//		SpatialDataSource sds = dsf.getSpatialDataSource("/home/fernando/roads3.shp");
//		/*
//		 * Test all data is accessible
//		 */
//		sds.start();
//		sds.getAsString();
//		sds.stop();
//
//	}
}
