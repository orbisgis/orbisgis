package org.gdms.model;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.ValueFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class ModelTest extends BaseTest {

	public static DataSourceFactory dsf = new DataSourceFactory();

	public String path = internalData + "ile_de_nantes.shp";

	public String backup = internalData + "backup/";

	public void testMetadata() throws Exception {
		DataSource mydata = dsf.getDataSource(new File(path));

		mydata.open();

		FeatureCollectionDecorator featureCollectionDecorator = new FeatureCollectionDecorator(
				mydata);
		featureCollectionDecorator.open();
		assertTrue(featureCollectionDecorator.size() == mydata.getRowCount());
		assertTrue(featureCollectionDecorator.getFeatureSchema()
				.getAttributeCount() == mydata.getMetadata().getFieldCount());
		featureCollectionDecorator.close();
		mydata.close();
	}

	public void testEnvelope() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionDecorator featureCollectionDecorator = new FeatureCollectionDecorator(
				mydata);
		featureCollectionDecorator.open();

		assertTrue(featureCollectionDecorator.getEnvelope().equals(
				sds.getFullExtent()));

		sds.close();
		featureCollectionDecorator.close();

	}

	public void testSchema() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionDecorator featureCollectionDecorator = new FeatureCollectionDecorator(
				mydata);
		featureCollectionDecorator.open();
		FeatureSchema fs = featureCollectionDecorator.getFeatureSchema();

		for (int i = 0; i < fs.getAttributeCount(); i++) {
			assertTrue(fs.getAttributeName(i).equals(
					sds.getMetadata().getFieldName(i)));
		}
		sds.close();
		featureCollectionDecorator.close();

	}

	public void testAddField() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		FeatureCollectionDecorator featureCollectionDecorator = new FeatureCollectionDecorator(
				mydata);

		featureCollectionDecorator.open();

		String value = "new";

		FeatureSchema schema = featureCollectionDecorator.getFeatureSchema();
		schema.addAttribute("new", AttributeType.STRING);

		featureCollectionDecorator.commit();

		featureCollectionDecorator.close();

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		assertTrue(sds.getMetadata().getFieldName(
				sds.getFieldIndexByName(value)).equals(value));
		assertTrue(schema.getAttributeType("new") == AttributeType.STRING);

		sds.close();

	}

	public void testGeometries() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		FeatureCollectionDecorator featureCollectionDecorator = new FeatureCollectionDecorator(
				mydata);

		for (int i = 0; i < featureCollectionDecorator.getFeatures().size(); i++) {
			Feature feature = (Feature) featureCollectionDecorator
					.getFeatures().get(i);
			assertTrue(feature.getGeometry().equals(
					sds.getGeometry(sds.getDefaultGeometry(), i)));
		}

	}

	public void testSaveAsDatasource() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		mydata.open();

		FeatureCollectionDecorator featureCollectionDecorator = new FeatureCollectionDecorator(
				mydata);
		File gdmsFile = new File(backup + "saveAsGDMS.gdms");

		gdmsFile.delete();
		dsf.getSourceManager().register("gdms", gdmsFile);

		dsf.saveContents("gdms", featureCollectionDecorator);

		mydata.close();

	}

	public void testAddFeature() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		mydata.open();

		FeatureCollectionDecorator featureCollectionDecorator = new FeatureCollectionDecorator(
				mydata);

		WKTReader wktr = new WKTReader();
		Geometry polygon = wktr
				.read("MULTIPOLYGON (((304821.21875 2252644.5 0, 304754.59375 2252965.5 0, 305572.4375 2253241.25 0, 305832.96875 2252923.25 0, 305799.625 2252217.25 0, 304821.21875 2252644.5 0)))");
		Feature feature = new BasicFeature(featureCollectionDecorator
				.getFeatureSchema());

		feature.setGeometry(polygon);

		featureCollectionDecorator.add(feature);
		featureCollectionDecorator.commit();

		featureCollectionDecorator.close();

		File gdmsFile = new File(backup + "saveAsGDMS.gdms");

		gdmsFile.delete();
		dsf.getSourceManager().register("gdms", gdmsFile);
		dsf.saveContents("gdms", featureCollectionDecorator);

	}

	public void testIndexedFeatureCollection() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		// sds.getRow(fieldId, value);

		FeatureCollectionDecorator featureCollectionDecorator = new FeatureCollectionDecorator(
				sds);

		IndexedFeatureCollection indexedFeatureCollection = new IndexedFeatureCollection(
				featureCollectionDecorator);

		Geometry sdsGeom = sds.getGeometry(1);

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

	public void testgetRowFromValue() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();

		sds.getRows(sds.getFieldIndexByName("Id"), ValueFactory
				.createValue(9999));
		sds.close();

		assertTrue(true);
	}

	public void testCreateFeatureCollection() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		mydata.open();

		FeatureCollection featureCollectionDecorator = new FeatureCollectionDecorator(
				mydata);

		FeatureCollection fc = new FeatureDataset(featureCollectionDecorator
				.getFeatureSchema());

		WKTReader wktr = new WKTReader();
		Geometry multiLineString = wktr
				.read("MULTILINESTRING ((183567.4305275123 2427174.0239940695, 183554.57165989507 2427169.953758708, 183514.0431114669 2427155.6831430644, 183464.95219365245 2427123.7169640227, 183415.2904512123 2427073.484396957, 183399.87818631704 2427058.642956687, 183355.35386550863 2427011.264512751, 183305.12129844268 2426956.4653486786, 183284.57161191572 2426924.4991696365, 183257.17202987973 2426879.9748488283, 183197 2426813, 183176 2426791, 183155.56524649635 2426776.0847669416, 183138 2426768, 183113 2426761, 183074 2426761, 183018.5673363165 2426762.3849759237, 182967.19311999905 2426760.101677421, 182935 2426744, 182923 2426740, 182911 2426741, 182892.98591865166 2426741.8352893963, 182875 2426729, 182865 2426715, 182847.3199485917 2426696.1693193363, 182791.37913526825 2426634.5202597557, 182778.82099350175 2426607.1206777194, 182778.82099350175 2426577.4377971804, 182761.6962547293 2426524.9219316114, 182761.6962547293 2426496.380700324, 182767.40450098677 2426476.9726630487))");

		Feature feature = new BasicFeature(featureCollectionDecorator
				.getFeatureSchema());

		feature.setGeometry(multiLineString);

		fc.add(feature);

		feature = (Feature) fc.getFeatures().get(0);
		assertTrue(feature.getGeometry().equals(multiLineString));

	}

	public void testCreateandSaveFeatureCollection() throws Exception {

		DataSource mydata = dsf.getDataSource(new File(path));

		mydata.open();

		FeatureCollection featureCollectionDecorator = new FeatureCollectionDecorator(
				mydata);

		FeatureDataset fc = new FeatureDataset(featureCollectionDecorator
				.getFeatureSchema());

		WKTReader wktr = new WKTReader();
		Geometry multiLineString = wktr
				.read("MULTILINESTRING ((183567.4305275123 2427174.0239940695, 183554.57165989507 2427169.953758708, 183514.0431114669 2427155.6831430644, 183464.95219365245 2427123.7169640227, 183415.2904512123 2427073.484396957, 183399.87818631704 2427058.642956687, 183355.35386550863 2427011.264512751, 183305.12129844268 2426956.4653486786, 183284.57161191572 2426924.4991696365, 183257.17202987973 2426879.9748488283, 183197 2426813, 183176 2426791, 183155.56524649635 2426776.0847669416, 183138 2426768, 183113 2426761, 183074 2426761, 183018.5673363165 2426762.3849759237, 182967.19311999905 2426760.101677421, 182935 2426744, 182923 2426740, 182911 2426741, 182892.98591865166 2426741.8352893963, 182875 2426729, 182865 2426715, 182847.3199485917 2426696.1693193363, 182791.37913526825 2426634.5202597557, 182778.82099350175 2426607.1206777194, 182778.82099350175 2426577.4377971804, 182761.6962547293 2426524.9219316114, 182761.6962547293 2426496.380700324, 182767.40450098677 2426476.9726630487))");

		Feature feature = new BasicFeature(featureCollectionDecorator
				.getFeatureSchema());

		feature.setGeometry(multiLineString);
		fc.add(feature);
		DataSource ds = dsf.getDataSource(FeatureCollectionModelUtils
				.getObjectMemoryDriver(fc));

		File gdmsFile = new File(backup + "saveAsGDMS.gdms");

		gdmsFile.delete();
		dsf.getSourceManager().register("gdms", gdmsFile);

		dsf.saveContents("gdms", ds);

	}

}
