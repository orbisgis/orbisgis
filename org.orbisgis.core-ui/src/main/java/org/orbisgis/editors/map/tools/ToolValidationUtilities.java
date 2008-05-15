package org.orbisgis.editors.map.tools;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.layerModel.MapContext;

public class ToolValidationUtilities {

	public static boolean isActiveLayerEditable(MapContext vc) {
		return vc.getActiveLayer().getDataSource().isEditable();
	}

	public static boolean isActiveLayerVisible(MapContext vc) {
		return vc.getActiveLayer().isVisible();
	}

	public static boolean activeSelectionGreaterThan(MapContext vc, int i) {
		return vc.getActiveLayer().getSelection().length >= i;
	}

	public static boolean geometryTypeIs(MapContext vc, int... geometryTypes) {
		try {
			SpatialDataSourceDecorator sds = vc.getActiveLayer()
					.getDataSource();
			Type type = sds.getFieldType(sds.getSpatialFieldIndex());
			int geometryType = type.getIntConstraint(Constraint.GEOMETRY_TYPE);
			for (int geomType : geometryTypes) {
				if (geomType == geometryType) {
					return true;
				}
			}
		} catch (DriverException e) {
		}
		return false;
	}

	public static boolean layerCountGreaterThan(MapContext vc, int i) {
		return vc.getLayerModel().getLayersRecursively().length > i;
	}
}
