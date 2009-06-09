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
package org.orbisgis.core.ui.editorViews.toc;

import ij.ImagePlus;

import java.io.IOException;

import javax.swing.Icon;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.images.IconLoader;

public abstract class TocAbstractRenderer {

	protected Icon getLayerIcon(ILayer layer) throws DriverException,
			IOException {
		if (layer.acceptsChilds()) {
			return IconLoader.getIcon("layers.png");
		} else {
			if (layer.isWMS()) {
				return IconLoader.getIcon("server_connect.png");
			} else {
				SpatialDataSourceDecorator dataSource = layer.getDataSource();
				if (!dataSource.isOpen()) {
					return null;
				}
				int spatialField = dataSource.getSpatialFieldIndex();
				// Create a legend for each spatial field
				Metadata metadata = dataSource.getMetadata();
				Type fieldType = metadata.getFieldType(spatialField);
				if (fieldType.getTypeCode() == Type.GEOMETRY) {
					GeometryConstraint geomTypeConstraint = (GeometryConstraint) fieldType
							.getConstraint(Constraint.GEOMETRY_TYPE);
					if (geomTypeConstraint == null) {
						return IconLoader.getIcon("layermixe.png");
					} else {
						int geomType = geomTypeConstraint.getGeometryType();

						if ((geomType == GeometryConstraint.POLYGON)
								|| (geomType == GeometryConstraint.MULTI_POLYGON)) {
							return IconLoader.getIcon("layerpolygon.png");
						} else if ((geomType == GeometryConstraint.LINESTRING)
								|| (geomType == GeometryConstraint.MULTI_LINESTRING)) {
							return IconLoader.getIcon("layerline.png");
						} else if ((geomType == GeometryConstraint.POINT)
								|| (geomType == GeometryConstraint.MULTI_POINT)) {
							return IconLoader.getIcon("layerpoint.png");
						} else {
							throw new RuntimeException("Bug");
						}
					}

				} else {
					if (layer.getRaster().getType() == ImagePlus.COLOR_RGB) {
						return IconLoader.getIcon("layerrgb.png");
					} else {
						return IconLoader.getIcon("raster.png");
					}

				}
			}
		}
	}

}
