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
package org.orbisgis.core.renderer.manual;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.classification.RangeMethod;
import org.orbisgis.core.renderer.legend.carto.IntervalLegend;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.ProportionalLegend;
import org.orbisgis.core.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;

import com.vividsolutions.jts.geom.Envelope;

public class TestClassfication {

	private static Range[] ranges;
	static DataSourceFactory dsf = new DataSourceFactory();

	public static void main(String[] args) throws Exception {

		File src = new File("../../datas2tests/shp/bigshape2D/cantons.shp");

		// testRangeMethod(src);
		testProportionnalMethod(src);

	}

	public static void testRangeMethod(File src) {
		DataSource ds;
		try {
			ds = dsf.getDataSource(src);
			ds.open();
			RangeMethod intervalsDicretizationMethod = new RangeMethod(ds,
					"PTOT90", 4);

			intervalsDicretizationMethod.disecQuantiles();

			ranges = intervalsDicretizationMethod.getRanges();

			Symbol[] s = new Symbol[4];
			s[0] = SymbolFactory.createPolygonSymbol(Color.black, new Color(
					192, 192, 192));
			s[1] = SymbolFactory.createPolygonSymbol(Color.black, new Color(
					128, 128, 128));
			s[2] = SymbolFactory.createPolygonSymbol(Color.black, new Color(96,
					96, 96));
			s[3] = SymbolFactory.createPolygonSymbol(Color.black, new Color(32,
					32, 32));
			Symbol defaultSymbol = SymbolFactory
					.createPolygonSymbol(Color.black);
			IntervalLegend l = LegendFactory.createIntervalLegend();
			l.setClassificationField("PTOT90", ds);
			l.setDefaultSymbol(defaultSymbol);
			for (int i = 0; i < ranges.length; i++) {
				l.addInterval(
						ValueFactory.createValue(ranges[i].getMinRange()),
						true,
						ValueFactory.createValue(ranges[i].getMaxRange()),
						false, s[i], "");

				System.out.println("Classes " + i + " :  Min "
						+ ranges[i].getMinRange() + " Max : "
						+ ranges[i].getMaxRange());

			}

			ILayer root = getDataManager().createLayerCollection("root");
			ILayer layer = getDataManager().createLayer(ds);

			root.addLayer(layer);
			layer.open();
			layer.setLegend(l);

			Envelope extent = layer.getEnvelope();
			BufferedImage img = new BufferedImage(400, 400,
					BufferedImage.TYPE_INT_ARGB);
			Renderer r = new Renderer();
			// int size = 350;
			// extent = new Envelope(new Coordinate(extent.centre().x - size,
			// extent.centre().y - size), new Coordinate(extent.centre().x
			// + size, extent.centre().y + size));
			r.draw(img, extent, root);
			JFrame frm = new JFrame();
			frm.getContentPane().add(new JLabel(new ImageIcon(img)));
			frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frm.pack();
			frm.setLocationRelativeTo(null);
			frm.setVisible(true);

		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static DataManager getDataManager() {
		return (DataManager) Services.getService(DataManager.class);
	}

	public static void testProportionnalMethod(File src) {

		DataSource ds;
		try {
			ds = dsf.getDataSource(src);
			ds.open();
			ProportionalLegend l = LegendFactory.createProportionalLegend();
			l.setClassificationField("PTOT90");
			l.setMaxSize(100);
			// l.setSquareMethod(2);
			l.setMethod(ProportionalLegend.LINEAR);

			Symbol defaultSymbol = SymbolFactory
					.createPolygonSymbol(Color.black);
			UniqueSymbolLegend l2 = LegendFactory.createUniqueSymbolLegend();
			l2.setSymbol(defaultSymbol);

			ILayer root = getDataManager().createLayerCollection("root");
			ILayer layer = getDataManager().createLayer(ds);

			root.addLayer(layer);
			layer.open();
			layer.setLegend(l2, l);

			Envelope extent = layer.getEnvelope();
			BufferedImage img = new BufferedImage(400, 400,
					BufferedImage.TYPE_INT_ARGB);
			Renderer r = new Renderer();
			// int size = 185350;
			// extent = new Envelope(new Coordinate(extent.centre().x - size,
			// extent.centre().y - size), new Coordinate(extent.centre().x
			// + size, extent.centre().y + size));
			r.draw(img, extent, root);
			JFrame frm = new JFrame();
			frm.getContentPane().add(new JLabel(new ImageIcon(img)));
			frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frm.pack();
			frm.setLocationRelativeTo(null);
			frm.setVisible(true);

		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
