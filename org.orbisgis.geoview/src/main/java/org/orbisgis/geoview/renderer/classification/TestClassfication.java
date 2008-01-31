package org.orbisgis.geoview.renderer.classification;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;

public class TestClassfication {

	
	private static Range[] ranges;

	public static void main(String[] args) {
		DataSourceFactory dsf = new DataSourceFactory();
		
		File src = new File(
		"../../datas2tests/shp/bigshape2D/cantons.shp");

		DataSource ds;
		try {
			ds = dsf.getDataSource(src);
			ds.open();
			ClassificationMethod intervalsDicretizationMethod = new ClassificationMethod(ds, "PTOT99", 4);
			
			intervalsDicretizationMethod.disecMoyennes();
			
			ranges = intervalsDicretizationMethod.getRanges();
			
			for (int i = 0; i < ranges.length; i++) {
				System.out.println("Classes " + i + " :  Min "+ ranges[i].getMinRange()
						+ " Max : " + ranges[i].getMaxRange());
				
			}
			
			ds.cancel();
			
		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
