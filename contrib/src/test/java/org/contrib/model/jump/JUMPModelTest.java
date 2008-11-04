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
import org.gdms.data.values.Value;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.progress.NullProgressMonitor;

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
				featureCollectionAdapter, new NullProgressMonitor());

		FeatureCollection result = internalOverlapFinder
				.getOverlappingFeatures();

		File gdmsFile = new File("src/test/resources/backup/saveAsResult.gdms");
		gdmsFile.delete();
		dsf.getSourceManager().register("result", gdmsFile);
		DataSource ds = new FeatureCollectionDatasourceAdapter(result);
		ds.open();
		dsf.saveContents("result", ds);
		ds.close();

	}

	public void testMetaDataFeatureCollectionToDatasource() throws Exception {
		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(
				sds);

		DataSource ds = new FeatureCollectionDatasourceAdapter(
				featureCollectionAdapter);

		assertTrue(ds.getMetadata().getFieldCount() == sds.getMetadata()
				.getFieldCount());

	}

	public void testSaveAsDatasource() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(
				sds);
		File gdmsFile = new File("src/test/resources/backup/saveAsGDMS.gdms");

		gdmsFile.delete();
		dsf.getSourceManager().register("gdms", gdmsFile);

		DataSource ds = new FeatureCollectionDatasourceAdapter(
				featureCollectionAdapter);
		dsf.saveContents("gdms", ds);

		ds = dsf.getDataSource(gdmsFile);
		ds.open();
		ds.getAsString();
		ds.close();
	}

	public void testObjectDriver() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(
				sds);

		DataSource ds = new FeatureCollectionDatasourceAdapter(
				featureCollectionAdapter);

		ObjectMemoryDriver driver = new ObjectMemoryDriver(ds);
		for (int i = 0; i < driver.getRowCount(); i++) {

			for (int j = 0; j < driver.getMetadata().getFieldCount(); j++) {

				Value origineValue = mydata.getFieldValue(i, j);
				Value resultValue = driver.getFieldValue(i, j);
				assertTrue(origineValue.equals(resultValue).getAsBoolean());

			}
		}

	}

}
