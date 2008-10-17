package org.contrib.model.jump;

import java.io.File;

import org.contrib.model.jump.adapter.FeatureCollectionAdapter;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;

import junit.framework.TestCase;

public class JUMPModelTest extends TestCase {

	public static DataSourceFactory dsf = new DataSourceFactory();
	
	public String path =  "../../datas2tests/shp/mediumshape2D/landcover2000.shp";
	
	
	public void testEnvelope() throws Exception{
		
		DataSource mydata = dsf.getDataSource(new File(path));
		
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(mydata);
		sds.open();
		
		FeatureCollectionAdapter featureCollectionAdapter = new FeatureCollectionAdapter(sds);
		
		
		
		//Global envelope
		System.out.println(featureCollectionAdapter.getEnvelope());
	
		assertTrue(featureCollectionAdapter.getEnvelope().equals(sds.getFullExtent()));
		
		sds.close();
		
	}
	
}
