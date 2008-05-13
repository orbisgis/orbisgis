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
package org.orbisgis.geoview.layerModel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.geoview.renderer.legend.Legend;
import org.orbisgis.geoview.renderer.legend.LegendFactory;
import org.orbisgis.geoview.renderer.legend.RasterLegend;
import org.orbisgis.geoview.renderer.legend.Symbol;
import org.orbisgis.geoview.renderer.legend.SymbolFactory;
import org.orbisgis.geoview.renderer.legend.UniqueSymbolLegend;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Envelope;

class Layer extends GdmsLayer {

	private SpatialDataSourceDecorator dataSource;
	private HashMap<String, Legend[]> fieldLegend = new HashMap<String, Legend[]>();

	Layer(String name, DataSource ds,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		this.dataSource = new SpatialDataSourceDecorator(ds);
	}

	private UniqueSymbolLegend getDefaultVectorialLegend() {
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
		legend.setSymbol(composite);
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
				PluginManager.error("Cannot get the extent of the layer: "
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
				int fieldType = metadata.getFieldType(i).getTypeCode();
				if (fieldType == Type.GEOMETRY) {
					UniqueSymbolLegend legend = getDefaultVectorialLegend();

					try {
						setLegend(metadata.getFieldName(i), legend);
					} catch (DriverException e) {
						// Should never reach here with UniqueSymbolLegend
						throw new RuntimeException(e);
					}

				} else if (fieldType == Type.RASTER) {
					setLegend(metadata.getFieldName(i),
							getDefaultRasterLegend(i));
				}
			}
		} catch (DriverException e) {
			throw new LayerException(e);
		}
	}

	private RasterLegend getDefaultRasterLegend(int fieldIndex)
			throws DriverException {
		GeoRaster gr = dataSource.getRaster(fieldIndex);
		try {
			return new RasterLegend(gr.getDefaultColorModel());
		} catch (IOException e) {
			throw new DriverException("Cannot access the default color model",
					e);
		} catch (GeoreferencingException e) {
			throw new DriverException("Cannot access the default color model",
					e);
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

	public boolean isRaster() throws DriverException {
		return dataSource.isDefaultRaster();
	}

	public boolean isVectorial() throws DriverException {
		return dataSource.isDefaultVectorial();
	}

	public GeoRaster getRaster() throws DriverException {
		return getDataSource().getRaster(0);
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
}