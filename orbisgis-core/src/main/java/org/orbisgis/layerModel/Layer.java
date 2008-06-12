/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.layerModel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.Services;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendFactory;
import org.orbisgis.renderer.legend.RasterLegend;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolFactory;
import org.orbisgis.renderer.legend.UniqueSymbolLegend;

import com.vividsolutions.jts.geom.Envelope;

public class Layer extends GdmsLayer {

	private SpatialDataSourceDecorator dataSource;
	private HashMap<String, Legend[]> fieldLegend = new HashMap<String, Legend[]>();

	public Layer(String name, DataSource ds,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		this.dataSource = new SpatialDataSourceDecorator(ds);
	}

	private UniqueSymbolLegend getDefaultVectorialLegend(Type fieldType) {
		GeometryConstraint gc = (GeometryConstraint) fieldType
				.getConstraint(Constraint.GEOMETRY_TYPE);

		final Random r = new Random();
		final Color c = new Color(r.nextInt(256), r.nextInt(256), r
				.nextInt(256));

		UniqueSymbolLegend legend = LegendFactory.createUniqueSymbolLegend();
		Symbol polSym = SymbolFactory.createPolygonSymbol(Color.black, c);
		Symbol pointSym = SymbolFactory.createCirclePointSymbol(Color.black,
				Color.red, 10);
		Symbol lineSym = SymbolFactory.createLineSymbol(Color.black,
				new BasicStroke(2));
		Symbol composite = SymbolFactory.createSymbolComposite(polSym,
				pointSym, lineSym);
		switch (gc.getGeometryType()) {
		case GeometryConstraint.POINT:
		case GeometryConstraint.MULTI_POINT:
			legend.setSymbol(pointSym);
			break;
		case GeometryConstraint.LINESTRING:
		case GeometryConstraint.MULTI_LINESTRING:
			legend.setSymbol(lineSym);
			break;
		case GeometryConstraint.POLYGON:
		case GeometryConstraint.MULTI_POLYGON:
			legend.setSymbol(polSym);
			break;
		case GeometryConstraint.MIXED:
			legend.setSymbol(composite);
			break;
		}

		return legend;
	}

	public SpatialDataSourceDecorator getDataSource() {
		return dataSource;
	}

	public Envelope getEnvelope() {
		Envelope result = new Envelope();

		if (null != dataSource) {
			try {
				result = dataSource.getFullExtent();
			} catch (DriverException e) {
				Services.getErrorManager().error(
						"Cannot get the extent of the layer: "
								+ dataSource.getName(), e);
			}
		}
		return result;
	}

	public void close() throws LayerException {
		super.close();
		try {
			dataSource.cancel();
		} catch (AlreadyClosedException e) {
			throw new RuntimeException("Bug!");
		} catch (DriverException e) {
			throw new LayerException(e);
		}
	}

	public void open() throws LayerException {
		try {
			dataSource.open();
			// Create a legend for each spatial field
			Metadata metadata = dataSource.getMetadata();
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				Type fieldType = metadata.getFieldType(i);
				int fieldTypeCode = fieldType.getTypeCode();
				if (fieldTypeCode == Type.GEOMETRY) {
					UniqueSymbolLegend legend = getDefaultVectorialLegend(fieldType);

					try {
						setLegend(metadata.getFieldName(i), legend);
					} catch (DriverException e) {
						// Should never reach here with UniqueSymbolLegend
						throw new RuntimeException(e);
					}

				} else if (fieldTypeCode == Type.RASTER) {
					GeoRaster gr = dataSource.getRaster(metadata
							.getFieldName(i), 0);
					RasterLegend rasterLegend;
					rasterLegend = new RasterLegend(gr.getDefaultColorModel(),
							1f);
					setLegend(metadata.getFieldName(i), rasterLegend);
				}
			}
		} catch (IOException e) {
			throw new LayerException("Cannot set legend", e);
		} catch (DriverException e) {
			throw new LayerException("Cannot set legend", e);
		}
	}

	/**
	 * Sets the legend used to draw this layer
	 *
	 * @param legends
	 * @throws DriverException
	 *             If there is some problem accessing the contents of the layer
	 */
	public void setLegend(Legend... legends) throws DriverException {
		String defaultFieldName = dataSource.getMetadata().getFieldName(
				dataSource.getSpatialFieldIndex());
		setLegend(defaultFieldName, legends);
	}

	public Legend[] getLegend() throws DriverException {
		String defaultFieldName = dataSource.getMetadata().getFieldName(
				dataSource.getSpatialFieldIndex());
		return getLegend(defaultFieldName);
	}

	public Legend[] getLegend(String fieldName) {
		return fieldLegend.get(fieldName);
	}

	public void setLegend(String fieldName, Legend... legends)
			throws DriverException {
		if (dataSource.getFieldIndexByName(fieldName) == -1) {
			throw new IllegalArgumentException("Unknown name: " + fieldName);
		} else {
			for (Legend legend : legends) {
				legend.setDataSource(dataSource);
			}
			fieldLegend.put(fieldName, legends);
			fireStyleChanged();
		}
	}

	public boolean isRaster() throws DriverException {
		return dataSource.isDefaultRaster();
	}

	public boolean isVectorial() throws DriverException {
		return dataSource.isDefaultVectorial();
	}

	public GeoRaster getRaster() throws DriverException {
		return getDataSource().getRaster(0);
	}
}