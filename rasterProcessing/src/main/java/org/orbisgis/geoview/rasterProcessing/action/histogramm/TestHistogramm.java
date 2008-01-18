package org.orbisgis.geoview.rasterProcessing.action.histogramm;

import ij.ImagePlus;
import ij.gui.HistogramWindow;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.GrapImagePlus;

public class TestHistogramm {

	private static GrapImagePlus imp;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String src = "../../datas2tests/grid/sample.asc";
		
		//String src = "../../datas2tests/geotif/440607.tif";

		GeoRaster geoRaster = null;
		
		try {
			geoRaster = GeoRasterFactory.createGeoRaster(src);
			geoRaster.open();
			System.out.println(geoRaster.getGrapImagePlus().getType());
			imp = geoRaster.getGrapImagePlus();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeoreferencingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	JFrame frame = new JFrame();

	ColorBalancePanel colorBalancePanel = new ColorBalancePanel(geoRaster);
	
	
	frame.add(colorBalancePanel);
	frame.pack();
	frame.show();
		


	}

}
