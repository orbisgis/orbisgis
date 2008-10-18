package org.contrib.model.jump;

import java.io.File;
import java.util.Iterator;

import org.contrib.model.jump.adapter.FeatureCollectionAdapter;
import org.contrib.model.jump.adapter.FeatureCollectionDatasourceAdapter;
import org.contrib.model.jump.adapter.TaskMonitorAdapter;
import org.contrib.model.jump.model.Feature;
import org.contrib.model.jump.model.FeatureCollection;
import org.contrib.model.jump.model.FeatureSchema;
import org.contrib.model.jump.model.IndexedFeatureCollection;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;

import com.vividsolutions.jcs.qa.InternalOverlapFinder;
import com.vividsolutions.jts.geom.Geometry;

import junit.framework.TestCase;

public class JUMPModelTest extends TestCase {

	public static DataSourceFactory dsf = new DataSourceFactory();

	public String path = "../../datas2tests/shp/mediumshape2D/landcover2000.shp";

	public void testMetadata() throws Exception {
		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(
				sds);

		assertTrue(featureCollectionAdapter.size() == sds.getRowCount());
		assertTrue(featureCollectionAdapter.getFeatureSchema()
				.getAttributeCount() == sds.getMetadata().getFieldCount());

	}

	public void testEnvelope() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(
				sds);

		assertTrue(featureCollectionAdapter.getEnvelope().equals(
				sds.getFullExtent()));

		sds.close();

	}

	public void testSchema() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(
				sds);

		FeatureSchema fs = featureCollectionAdapter.getFeatureSchema();

		for (int i = 0; i < fs.getAttributeCount(); i++) {

			assertTrue(fs.getAttributeName(i).equals(
					sds.getMetadata().getFieldName(i)));

		}

	}

	public void testGeometries() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(
				sds);

		for (int i = 0; i < featureCollectionAdapter.getFeatures().size(); i++) {
			Feature feature = (Feature) featureCollectionAdapter.getFeatures()
					.get(i);

			assertTrue(feature.getGeometry().equals(
					sds.getGeometry(sds.getDefaultGeometry(), i)));

		}

	}

	public void testIndexedFeatureCollection() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(
				sds);

		IndexedFeatureCollection indexedFeatureCollection = new IndexedFeatureCollection(
				featureCollectionAdapter);

		Geometry sdsGeom = sds.getGeometry(10);

		System.out.println(sdsGeom);

		boolean result = false;

		for (Iterator j = indexedFeatureCollection.query(
				sdsGeom.getEnvelopeInternal()).iterator(); j.hasNext();) {
			Feature featureResult = (Feature) j.next();

			if (featureResult.getGeometry().equals(sdsGeom)) {
				result = true;
				System.out.println(featureResult.getGeometry());
			}
		}

		assertTrue(result);

	}

	public void testWrapper() throws Exception {

		path = "../../datas2tests/shp/mediumshape2D/bati.shp";

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(
				sds);

		InternalOverlapFinder internalOverlapFinder = new InternalOverlapFinder(
				featureCollectionAdapter, new TaskMonitorAdapter());

		FeatureCollection result = internalOverlapFinder
				.getOverlappingFeatures();

		for (int i = 0; i < result.size(); i++) {
			Feature feature = (Feature) result.getFeatures().get(i);

			System.out.println(feature.getGeometry());

		}

	}

	public void testMetaDataFeatureCollectionToDatasource() throws Exception {
		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(
				sds);

		DataSource ds = new FeatureCollectionDatasourceAdapter(
				featureCollectionAdapter);
		
		assertTrue(ds.getMetadata().getFieldCount()==sds.getMetadata().getFieldCount());

	}

	public void testSaveAsDatasource() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(
				sds);
		File gdmsFile = new File("src/test/resources/backup/saveAsGDMS.gdms");
		dsf.getSourceManager().register("gdms", gdmsFile);

		DataSource ds = new FeatureCollectionDatasourceAdapter(
				featureCollectionAdapter);
		ds.open();
		dsf.saveContents("gdms", ds);
		ds.close();

		ds = dsf.getDataSource(gdmsFile);
		ds.open();
		ds.getAsString();
		ds.close();
	}

}
