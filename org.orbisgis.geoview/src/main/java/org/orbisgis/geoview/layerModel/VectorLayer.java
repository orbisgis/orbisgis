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
import java.util.Random;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.geoview.renderer.legend.Legend;
import org.orbisgis.geoview.renderer.legend.LegendFactory;
import org.orbisgis.geoview.renderer.legend.Symbol;
import org.orbisgis.geoview.renderer.legend.SymbolFactory;
import org.orbisgis.geoview.renderer.legend.UniqueSymbolLegend;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Envelope;

public class VectorLayer extends GdmsLayer {

	private SpatialDataSourceDecorator dataSource;
	private Legend legend;

	VectorLayer(String name, DataSource ds,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		this.dataSource = new SpatialDataSourceDecorator(ds);

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
		// TODO create a composite symbol

		try {
			setLegend(legend);
		} catch (DriverException e) {
			// Should never reach here with UniqueSymbolLegend
			throw new RuntimeException(e);
		}
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
		} catch (DriverException e) {
			throw new LayerException(e);
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
		this.legend = LegendFactory.createLegendComposite(legends);
		legend.setDataSource(dataSource);
		fireStyleChanged();
	}

	public Legend getLegend() {
		return this.legend;
	}
}