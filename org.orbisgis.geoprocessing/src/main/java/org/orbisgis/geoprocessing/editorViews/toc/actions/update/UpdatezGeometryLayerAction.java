package org.orbisgis.geoprocessing.editorViews.toc.actions.update;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.geometryUtils.CoordinatesUtils;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editorViews.toc.action.ILayerAction;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;

import com.vividsolutions.jts.geom.Geometry;

public class UpdatezGeometryLayerAction implements ILayerAction {

	public boolean accepts(MapContext mc, ILayer layer) {
		try {

			return layer.isVectorial();
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Vector type unreadable for this layer", e);
		}
		return false;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount >= 1;
	}

	public void execute(MapContext mapContext, ILayer layer) {

		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);

		DataSourceFactory dsf = dataManager.getDSF();

		try {

			SpatialDataSourceDecorator sds = layer.getDataSource();

			sds.open();

			MultiInputPanel mip = new MultiInputPanel("Update z coordinate");

			mip.addInput("field", "Numeric field", new NumericFieldLayerCombo(
					sds.getMetadata()));

			if (UIFactory.showDialog(mip)) {

				String fieldName = mip.getInput("field");
				ObjectMemoryDriver driver = new ObjectMemoryDriver(sds
						.getMetadata());

				long rowCount = sds.getRowCount();

				int fieldIndex = sds.getFieldIndexByName(fieldName);
				for (int i = 0; i < rowCount; i++) {

					double fieldValue = sds.getFieldValue(i, fieldIndex)
							.getAsDouble();
					Value[] values = sds.getRow(i);

					Geometry geom = sds.getGeometry(i);

					values[sds.getFieldIndexByName(sds.getDefaultGeometry())] = ValueFactory
							.createValue(CoordinatesUtils.updateZ(geom, fieldValue));

					driver.addValues(values);

				}

				String nameLayer = dsf.getSourceManager().nameAndRegister(
						driver);

				ILayer rsLayer = dataManager.createLayer(nameLayer);

				mapContext.getLayerModel().insertLayer(rsLayer, 0);
			}

		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the resulting datasource from the layer ", e);
		} catch (LayerException e) {
			Services.getErrorManager()
					.error(
							"Cannot insert resulting layer based on "
									+ layer.getName(), e);
		}

	}

}
