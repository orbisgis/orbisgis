/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.editors.map.tools;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

public class ToolValidationUtilities {

	public static boolean isActiveLayerEditable(MapContext vc) {
		ILayer activeLayer = vc.getActiveLayer();
		if (activeLayer == null) {
			return false;
		} else {
			return activeLayer.getDataSource().isEditable();
		}
	}

	public static boolean isActiveLayerVisible(MapContext vc) {
		ILayer activeLayer = vc.getActiveLayer();
		if (activeLayer == null) {
			return false;
		} else {
			return activeLayer.isVisible();
		}
	}

	public static boolean activeSelectionGreaterThan(MapContext vc, int i) {
		ILayer activeLayer = vc.getActiveLayer();
		if (activeLayer == null) {
			return false;
		} else {
			return activeLayer.getSelection().length >= i;
		}
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
