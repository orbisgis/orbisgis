package org.orbisgis.geoprocessing.editorViews.toc.actions.update;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
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

public class UpdateZStartEndGeometryLayerAction implements ILayerAction {

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

			mip.addInput("field1", "Start z", new NumericFieldLayerCombo(sds
					.getMetadata()));
			mip.addInput("field2", "End z", new NumericFieldLayerCombo(sds
					.getMetadata()));

			if (UIFactory.showDialog(mip)) {

				String fieldName1 = mip.getInput("field1");
				String fieldName2 = mip.getInput("field2");

				ObjectMemoryDriver driver = new ObjectMemoryDriver(
						changeTo3DConstraint(sds.getMetadata()));

				long rowCount = sds.getRowCount();

				int fieldIndex1 = sds.getFieldIndexByName(fieldName1);
				int fieldIndex2 = sds.getFieldIndexByName(fieldName2);
				for (int i = 0; i < rowCount; i++) {

					double fieldValue1 = sds.getFieldValue(i, fieldIndex1)
							.getAsDouble();
					double fieldValue2 = sds.getFieldValue(i, fieldIndex2)
							.getAsDouble();

					Value[] values = sds.getRow(i);

					Geometry geom = sds.getGeometry(i);

					values[sds.getFieldIndexByName(sds.getDefaultGeometry())] = ValueFactory
							.createValue(CoordinatesUtils.updateZStartEnd(geom,
									fieldValue1, fieldValue2));

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

	private Metadata changeTo3DConstraint(Metadata metadata)
			throws DriverException {

		int size = metadata.getFieldCount();

		DefaultMetadata result = new DefaultMetadata();

		for (int i = 0; i < size; i++) {

			Type type = metadata.getFieldType(i);
			String name = metadata.getFieldName(i);

			if (type.getTypeCode() == Type.GEOMETRY) {

				result.addField(name, getType(type));

			} else {

				result.addField(name, type.getTypeCode());

			}

		}

		return result;
	}

	public Type getType(Type type) {

		Constraint[] constrs = type.getConstraints(Constraint.ALL
				& ~Constraint.GEOMETRY_DIMENSION);
		Constraint[] result = new Constraint[constrs.length + 1];
		System.arraycopy(constrs, 0, result, 0, constrs.length);
		result[result.length - 1] = new DimensionConstraint(3);

		return TypeFactory.createType(type.getTypeCode(), result);
	}

}
