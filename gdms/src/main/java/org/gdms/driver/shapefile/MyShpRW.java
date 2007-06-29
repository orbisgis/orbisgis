package org.gdms.driver.shapefile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.gdms.data.DataSourceCreationException;
import org.gdms.driver.DriverException;
import org.gdms.geotoolsAdapter.FeatureCollectionAdapter;
import org.gdms.geotoolsAdapter.FeatureTypeAdapter;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;

import com.hardcode.driverManager.DriverLoadException;

public class MyShpRW {

	private ShapefileDataStore input;

	public MyShpRW(File src) throws MalformedURLException {
		input = new ShapefileDataStore(src.toURI().toURL());
	}

	public FeatureSource read() throws IOException {
		final String typeName = input.getTypeNames()[0];
		return input.getFeatureSource(typeName);
	}

	public FeatureSource filter() {
		return null;
	}

	public void write(FeatureSource featureSource, File dst) throws IOException {
		FeatureType ft = featureSource.getSchema();
		FeatureCollection featureCollection = featureSource.getFeatures();

		ShapefileDataStore newShapefileDataStore = new ShapefileDataStore(dst
				.toURI().toURL());
		newShapefileDataStore.createSchema(ft);
		FeatureSource newFeatureSource = newShapefileDataStore
				.getFeatureSource(ft.getTypeName());
		FeatureStore newFeatureStore = (FeatureStore) newFeatureSource;

		Transaction transaction = newFeatureStore.getTransaction();
		newFeatureStore.addFeatures(featureCollection);
		transaction.commit();
		transaction.close();
	}

	public void write(SpatialDataSourceDecorator ds, File dst)
			throws DriverException, IOException {
		ds.open();
		FeatureType ft = new FeatureTypeAdapter(ds);

		ShapefileDataStore newShapefileDataStore = new ShapefileDataStore(dst
				.toURI().toURL());
		newShapefileDataStore.createSchema(ft);
		FeatureSource newFeatureSource = newShapefileDataStore
				.getFeatureSource(ft.getTypeName());
		FeatureStore newFeatureStore = (FeatureStore) newFeatureSource;

		Transaction transaction = newFeatureStore.getTransaction();
		newFeatureStore.addFeatures(new FeatureCollectionAdapter(ds));
		transaction.commit();
		transaction.close();
		ds.cancel();
		// final SpatialDataSourceDecorator sds = new
		// SpatialDataSourceDecorator(
		// ds);
		// sds.open();
		//
		// final String spatialFieldName = sds.getDefaultGeometry();
		// final CoordinateReferenceSystem crs = sds.getCRS(spatialFieldName);
		//
		// final ShapefileDataStore shapefileDataStore = new ShapefileDataStore(
		// dst.toURI().toURL());
		// shapefileDataStore.createSchema(new FeatureTypeAdapter(sds));
		// final String typeName = shapefileDataStore.getTypeNames()[0];
		// final FeatureSource featureSource = shapefileDataStore
		// .getFeatureSource(typeName);
		// final FeatureStore featureStore = (FeatureStore) featureSource;
		// final Transaction transaction = featureStore.getTransaction();
		// FeatureCollection featureCollection = new
		// FeatureCollectionAdapter(sds);
		// shapefileDataStore.forceSchemaCRS(crs);
		// featureStore.addFeatures(featureCollection);
		// transaction.commit();
		// transaction.close();
	}

	public static void main(String[] args) throws IOException, DriverException,
			DriverLoadException, DataSourceCreationException {
		long start = System.currentTimeMillis();

		File src = new File("../../datas2tests/shp/bigshape3D/point3D_modified.shp");
		File dst1 = new File("1.shp");
		// File dst2 = new File("2.shp");
		//
		// DataSourceFactory dsf = new DataSourceFactory();
		// SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(dsf
		// .getDataSource(src));

		MyShpRW x = new MyShpRW(src);
		FeatureSource featureSource = x.read();
		x.write(featureSource, dst1);
		// x.write(sds, dst2);

		// SpatialDataSourceDecorator sds1 = new SpatialDataSourceDecorator(dsf
		// .getDataSource(dst1));
		// SpatialDataSourceDecorator sds2 = new SpatialDataSourceDecorator(dsf
		// .getDataSource(dst2));

		// sds.open();
		// sds1.open();
		// sds2.open();
		// System.out.println(sds.getAsString().equals(sds1.getAsString()));
		// System.out.println(sds.getAsString().equals(sds2.getAsString()));
		// sds.cancel();
		// sds1.cancel();
		// sds2.cancel();

		System.out.println(System.currentTimeMillis() - start + " ms");
		// new Utility().show(new DataSource[] { sds });
		// new Utility().show(new DataSource[] { sds1 });
		// new Utility().show(new DataSource[] { sds2 });
	}
}