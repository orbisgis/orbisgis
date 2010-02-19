package org.gdms.data.feature;

import java.io.File;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.generic.GenericObjectDriver;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class FeatureTest extends SourceTest {

	public void testGetFeature() throws Exception {
		DataSource ds = dsf.getDataSource(new File(internalData
				+ "landcover2000.shp"));
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		Feature feature = sds.getFeature(0);
		assertTrue(sds.getFieldValue(0, 1).equals(feature.getValues()[1])
				.getAsBoolean());
		assertTrue(sds.getGeometry(0).equals(feature.getGeometry()));
		sds.close();
	}

	public void testIncorecctMetadataValueSize() throws Exception {
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("name", Type.STRING);
		metadata.addField("surname", Type.STRING);
		metadata.addField("the_geom", Type.GEOMETRY);

		Value[] values = new Value[] { ValueFactory.createValue(12d),
				ValueFactory.createValue("erwan") };

		Feature feature = new Feature(metadata);
		feature.setValues(values);

		assertTrue(metadata.getFieldCount() != feature.getValues().length);

	}

	public void testAddFeatureOnExisting() throws Exception {
		dsf.getSourceManager().register("landcover2000",
				new File(internalData + "landcover2000.shp"));
		dsf.executeSQL("select register('" + backupDir
				+ "/addFeature.gdms','temp')");
		dsf
				.executeSQL("create table temp as select explode() from landcover2000;");
		DataSource ds = dsf.getDataSource("temp");

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		Feature feature = sds.getFeature(100);
		Geometry geom = feature.getGeometry().buffer(20);
		feature.addValue(sds.getSpatialFieldIndex(), ValueFactory
				.createValue(geom));
		sds.addFeature(feature);
		sds.commit();
		sds.close();
		sds.open();
		assertTrue(sds.getGeometry(sds.getRowCount() - 1).equals(geom));
		sds.close();
	}

	public void testAddNewFeature() throws Exception {
		dsf.getSourceManager().register("landcover2000",
				new File(internalData + "landcover2000.shp"));
		dsf.executeSQL("select register('" + backupDir
				+ "/addFeature.gdms','temp')");
		dsf
				.executeSQL("create table temp as select explode() from landcover2000;");
		DataSource ds = dsf.getDataSource("temp");
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		Feature feature = new Feature(sds.getMetadata());
		Geometry geom = sds.getGeometry(12);
		feature.addValue(sds.getSpatialFieldIndex(), ValueFactory
				.createValue(geom));
		sds.addFeature(feature);
		sds.commit();
		sds.close();

		sds.open();
		assertTrue(sds.getGeometry(12).equals(geom));
		sds.close();
	}

	public void testAddGeometryTimeComparator() throws Exception {
		dsf.getSourceManager().register("landcover2000",
				new File(internalData + "landcover2000.shp"));
		dsf.executeSQL("select register('" + backupDir
				+ "/addFeature1.gdms','temp1')");
		dsf
				.executeSQL("create table temp1 as select explode() from landcover2000;");
		DataSource ds = dsf.getDataSource("temp1");

		long start = System.currentTimeMillis();
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();

		long count = sds.getRowCount();

		for (int i = 0; i < count; i++) {
			Geometry geom = sds.getGeometry(i).buffer(20);
			sds.setGeometry(100, geom);
		}
		sds.commit();
		sds.close();

		long end = System.currentTimeMillis();
		long time = end - start;
		System.out.println("Time with gdms " + time);

		dsf.executeSQL("select register('" + backupDir
				+ "/addFeature2.gdms','temp2')");
		dsf
				.executeSQL("create table temp2 as select explode() from landcover2000;");
		ds = dsf.getDataSource("temp2");

		start = System.currentTimeMillis();
		sds = new SpatialDataSourceDecorator(ds);
		sds.open();

		count = sds.getRowCount();

		for (int i = 0; i < count; i++) {
			Feature feature = new Feature(sds.getMetadata());
			feature.addValue(sds.getSpatialFieldIndex(), ValueFactory
					.createValue(sds.getGeometry(i).buffer(20)));
			sds.addFeature(feature);
		}
		sds.commit();
		sds.close();

		end = System.currentTimeMillis();
		time = end - start;
		System.out.println("Time with feature object " + time);

	}

	public void testGetGeometryComparator() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.getSourceManager().register("landcover2000",
				new File(internalData + "landcover2000.shp"));
		dsf.executeSQL("select register('" + backupDir
				+ "/addFeature1.gdms','temp1')");
		dsf
				.executeSQL("create table temp1 as select explode() from landcover2000;");
		DataSource ds = dsf.getDataSource("temp1");

		long start = System.currentTimeMillis();
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		sds.open();

		long count = sds.getRowCount();

		for (int i = 0; i < count; i++) {
			assertTrue(sds.getFeature(i).getGeometry().equals(
					sds.getGeometry(i)));

		}
		sds.close();

		long end = System.currentTimeMillis();
		long time = end - start;
		System.out.println("Time  " + time);

	}

	public void addFeatureValuesInGenericDriver() throws DriverException,
			ParseException {

		long start = System.currentTimeMillis();

		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("id", Type.INT);
		metadata.addField("name", Type.STRING);
		metadata.addField("the_geom", Type.GEOMETRY);

		GenericObjectDriver genericObjectDriver = new GenericObjectDriver(
				metadata);
		
		 FileSourceDefinition def = new FileSourceDefinition(
				new File("/tmp/test.gdms"));
		 
		 dsf.registerDataSource("data", def);

		for (int i = 0; i < 10000; i++) {
			Value[] values = new Value[] {
					ValueFactory.createValue(i),
					ValueFactory.createValue("erwan_" + i),
					ValueFactory.createValue(new WKTReader().read(
							"POINT(10"+i + " 12" + i+ ")")) };
			Feature feature = new Feature(metadata);
			feature.setValues(values);
			genericObjectDriver.addValues(feature.getValues());
		}
		dsf.saveContents("data", dsf.getDataSource(genericObjectDriver));
		long end = System.currentTimeMillis();
		long time = end - start;
		System.out.println("Time to create data " + time);

	}

}
