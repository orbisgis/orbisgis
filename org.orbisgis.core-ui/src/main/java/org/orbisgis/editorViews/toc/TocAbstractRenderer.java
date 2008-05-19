package org.orbisgis.editorViews.toc;

import ij.ImagePlus;

import java.io.IOException;

import javax.swing.Icon;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.images.IconLoader;
import org.orbisgis.layerModel.ILayer;

public abstract class TocAbstractRenderer {

	protected Icon getLayerIcon(ILayer layer) throws DriverException,
			IOException {
		if (layer.acceptsChilds()) {
			return IconLoader.getIcon("layers.png");
		} else {
			if (!layer.getDataSource().isOpen()) {
				return null;
			}
			SpatialDataSourceDecorator dataSource = layer.getDataSource();
			int spatialField = dataSource.getSpatialFieldIndex();
			// Create a legend for each spatial field
			Metadata metadata = dataSource.getMetadata();
			Type fieldType = metadata.getFieldType(spatialField);
			if (fieldType.getTypeCode() == Type.GEOMETRY) {
				GeometryConstraint geomTypeConstraint = (GeometryConstraint) fieldType
						.getConstraint(Constraint.GEOMETRY_TYPE);
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
					return IconLoader.getIcon("layermixe.png");
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
