package org.orbisgis.geoprocessing.editorViews.toc.actions.qa;


import java.io.IOException;

import org.gdms.driver.DriverException;
import org.grap.processing.OperationException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.geoprocessing.editorViews.toc.action.SQLGeometryAbstractProcess;

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
