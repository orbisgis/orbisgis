package org.gdms.sql.customQuery.geometry.raster.utilities;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.spatial.raster.utilities.ToEnvelope;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;



public class UtilitiesTest extends FunctionTest {

	public static String externalRasterData = new String("../../datas2tests/grid/sample.asc");

	private DataSourceFactory dsf;

	private Geometry rasterEnvelope;
	
	static {
		FunctionManager.addFunction(ToEnvelope.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
		dsf = new DataSourceFactory();
		File gdmsFile = new File(externalRasterData);
		dsf.getSourceManager().register("sample", gdmsFile);
		
		WKTReader wktr = new WKTReader();
		rasterEnvelope = wktr.read("POLYGON ((634592 5588395, 634592 5592875, 639252 5592875, 639252 5588395, 634592 5588395))");

		
	}
	
	
	public void testRasterEnvelope() throws Exception{
		
		dsf.getSourceManager().register("outDs",
		"select Envelope(raster) from sample ;");
		
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				dsf.getDataSource("outDs"));

		sds.open();
		
		assertTrue(sds.getGeometry(0).equals(rasterEnvelope));
			
		
	}
	
	
	
}
