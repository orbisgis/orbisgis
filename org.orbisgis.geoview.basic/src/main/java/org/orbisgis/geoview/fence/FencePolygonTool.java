package org.orbisgis.geoview.fence;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.AbstractPolygonTool;
import org.orbisgis.tools.EditionContext;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class FencePolygonTool extends AbstractPolygonTool{

	
	DataSourceFactory dsf = OrbisgisCore.getDSF();
	private DataSource dsResult;
	
	protected void polygonDone(Polygon g) throws TransitionException {
			
		
		buildFenceDatasource(g);
		
		VectorLayer layer = LayerFactory.createVectorialLayer("fence", dsResult);
		
		try {
			ec.getRootLayer().put(layer);
		} catch (CRSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
	}

	public boolean isEnabled() {
		
		return true;
	}

	public boolean isVisible() {
		
		return true;
	}
	
	
	public void buildFenceDatasource(Geometry g){
		
		ObjectMemoryDriver driver;
		try {
			driver = new ObjectMemoryDriver(
					new String[] { "the_geom" }, new Type[] { TypeFactory
							.createType(Type.GEOMETRY) });
			
			dsResult = dsf.getDataSource(driver);
			
			dsResult.open();
			
			dsResult.insertFilledRow(new Value[] { new GeometryValue(g) });
			
			dsResult.commit();
			
						
		} catch (InvalidTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FreeingResourcesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonEditableDataSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

	
	
}
