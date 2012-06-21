/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.editorViews.toc;

import ij.ImagePlus;

import java.io.IOException;

import javax.swing.Icon;

import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint; 
import org.gdms.data.types.GeometryDimensionConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public abstract class TocAbstractRenderer {

	protected Icon getLayerIcon(ILayer layer) throws DriverException,
			IOException {
		if (layer.acceptsChilds()) {
			return OrbisGISIcon.LAYERS;
		} else {
			if (layer.isWMS()) {
				return OrbisGISIcon.SERVER_CONNECT;
			} else {
				DataSource dataSource = layer.getDataSource();
				if (!dataSource.isOpen()) {
					return null;
				}
				int spatialField = dataSource.getSpatialFieldIndex();
				// Create a legend for each spatial field
				Metadata metadata = dataSource.getMetadata();
				Type fieldType = metadata.getFieldType(spatialField);
                                int typeCode = fieldType.getTypeCode();
				if ((typeCode & Type.GEOMETRY) != 0) {
                                        switch(typeCode){
                                                case Type.NULL:
							return OrbisGISIcon.LAYER_MIXE;
                                                case Type.GEOMETRY:
                                                case Type.GEOMETRYCOLLECTION:
                                                        GeometryDimensionConstraint gdc = 
                                                                (GeometryDimensionConstraint) fieldType.getConstraint(Constraint.DIMENSION_2D_GEOMETRY);
                                                        if(gdc == null){
                                                                return OrbisGISIcon.LAYER_MIXE;
                                                        } else {
                                                                switch(gdc.getDimension()){
                                                                        case GeometryDimensionConstraint.DIMENSION_POINT:
                                                                                return OrbisGISIcon.LAYER_POINT;
                                                                        case GeometryDimensionConstraint.DIMENSION_CURVE:
                                                                                return OrbisGISIcon.LAYER_LINE;
                                                                        case GeometryDimensionConstraint.DIMENSION_SURFACE:
                                                                                return OrbisGISIcon.LAYER_POLYGON;
                                                                        default :
                                                                                return OrbisGISIcon.LAYER_POLYGON;                                                                }
                                                        }
                                                case Type.POINT:
                                                case Type.MULTIPOINT:
							return OrbisGISIcon.LAYER_POINT;
                                                case Type.LINESTRING:
                                                case Type.MULTILINESTRING:
							return OrbisGISIcon.LAYER_LINE;
                                                case Type.POLYGON:
                                                case Type.MULTIPOLYGON:
							return OrbisGISIcon.LAYER_POLYGON;
                                                default:
                                                        throw new RuntimeException(I18N.getString("orbisgis.org.orbisgis.toc.tocAbstractRenderer.bug")); //$NON-NLS-1$
                                        }

				} else {
					if (layer.getRaster().getType() == ImagePlus.COLOR_RGB) {
						return OrbisGISIcon.LAYER_RGB;
					} else {
						return OrbisGISIcon.RASTER;
					}

				}
			}
		}
	}

}
