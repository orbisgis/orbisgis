package org.orbisgis.geoview.renderer.classification;

import java.awt.Color;
import java.awt.Image;
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
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.renderer.Renderer;
import org.orbisgis.geoview.renderer.legend.IntervalLegend;
import org.orbisgis.geoview.renderer.legend.Legend;
import org.orbisgis.geoview.renderer.legend.LegendFactory;
import org.orbisgis.geoview.renderer.legend.ProportionalLegend;
import org.orbisgis.geoview.renderer.legend.Symbol;
import org.orbisgis.geoview.renderer.legend.SymbolFactory;
import org.orbisgis.geoview.renderer.legend.UniqueSymbolLegend;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class TestClassfication {

	private static Range[] ranges;
	static DataSourceFactory dsf = new DataSourceFactory();
	
	public static void main(String[] args) throws Exception {
		
		File src = new File("../../datas2tests/shp/bigshape2D/cantons.shp");

		//testRangeMethod(src);
		testProportionnalMethod(src);

	}
	
	
	public static void testRangeMethod(File src){
		DataSource ds;
		try {
			ds = dsf.getDataSource(src);
			ds.open();
			RangeMethod intervalsDicretizationMethod = new RangeMethod(
					ds, "PTOT90", 4);

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
			l.setClassificationField("PTOT90");
			l.setDefaultSymbol(defaultSymbol);
			for (int i = 0; i < ranges.length; i++) {
			l.addInterval(
					ValueFactory.createValue(ranges[i].getMinRange()),
						true,
					ValueFactory.createValue(ranges[i].getMaxRange()),
						false, s[i]);

				System.out.println("Classes " + i + " :  Min "
						+ ranges[i].getMinRange() + " Max : "
						+ ranges[i].getMaxRange());

			}

			ILayer root = LayerFactory.createLayerCollection("root");
			ILayer layer = LayerFactory.createVectorialLayer(ds);

			root.addLayer(layer);
			layer.open();
			VectorLayer vl = (VectorLayer) layer;
			vl.setLegend(l);

			Envelope extent = layer.getEnvelope();
			Image img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
			Renderer r = new Renderer();
			int size = 350;
//			extent = new Envelope(new Coordinate(extent.centre().x - size,
//					extent.centre().y - size), new Coordinate(extent.centre().x
//					+ size, extent.centre().y + size));
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
		} catch (CRSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public static void testProportionnalMethod(File src){
		
		DataSource ds;
		try {
			ds = dsf.getDataSource(src);
			ds.open();
			ProportionalLegend l = LegendFactory.createProportionalLegend();
			l.setClassificationField("PTOT90");
			l.setMinSymbolArea(500);
			
			Symbol defaultSymbol = SymbolFactory
			.createPolygonSymbol(Color.black);
			UniqueSymbolLegend l2 = LegendFactory.createUniqueSymbolLegend();
			l2.setSymbol(defaultSymbol);
			Legend lc = LegendFactory.createLegendComposite(l2, l);
			
			ILayer root = LayerFactory.createLayerCollection("root");
			ILayer layer = LayerFactory.createVectorialLayer(ds);

			root.addLayer(layer);
			layer.open();
			VectorLayer vl = (VectorLayer) layer;
			vl.setLegend(lc);

			Envelope extent = layer.getEnvelope();
			Image img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
			Renderer r = new Renderer();
			int size = 185350;
//			extent = new Envelope(new Coordinate(extent.centre().x - size,
//					extent.centre().y - size), new Coordinate(extent.centre().x
//					+ size, extent.centre().y + size));
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
		} catch (CRSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
		
	}
}
