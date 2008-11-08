package org.contrib.ui.editorViews.toc.actions.geometry.qa;


import java.io.IOException;

import org.contrib.ui.editorViews.toc.actions.geometry.action.SQLGeometryAbstractProcess;
import org.gdms.driver.DriverException;
import org.grap.processing.OperationException;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

public class GeometriesStatisticsAction extends SQLGeometryAbstractProcess {

	@Override
	protected String evaluateResult(ILayer layer, MapContext mapContext) throws OperationException, IOException, DriverException {
		
		try {
			String geometryField = layer.getDataSource().getDefaultGeometry();

			StringBuffer stringBuffer = new StringBuffer("SELECT ");

			stringBuffer.append(geometryField);
			stringBuffer.append(" , ");
			
			stringBuffer.append("NumPoints(" + geometryField + ") as nPts");
			stringBuffer.append(" , ");

			stringBuffer.append("NumInteriorRing(" + geometryField
					+ ") as nHoles");
			stringBuffer.append(" , ");
			stringBuffer.append("GeometryN(" + geometryField + ") as nGeom");
			stringBuffer.append(" , ");
			stringBuffer.append("Area(" + geometryField + ") as area");
			stringBuffer.append(" , ");
			stringBuffer.append("Length(" + geometryField + ") as length");
			stringBuffer.append(" FROM  " + layer.getName() + " ;");

			return stringBuffer.toString();
		} catch (DriverException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	

}
