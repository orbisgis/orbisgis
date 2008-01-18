package org.orbisgis.geoview.rasterProcessing.action.histogramm;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JSlider;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GrapImagePlus;

import java.awt.Point;
import java.io.IOException;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;

public class ColorBalancePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel previewImagePanel = null;
	private JPanel Plot = null;
	private JSlider jSlider = null;
	private JSlider jSlider1 = null;
	private JSlider jSlider2 = null;
	private Plot plot = null;
	private GeoRaster geoRaster;
	private GrapImagePlus imp;
	private ImageStatistics stats;
	private JLabel minimLabel = null;
	private JLabel maxLabel = null;
	private JLabel brightLabel = null;
	private ImageProcessor impResize;
	private JPanel jPanel = null;

	/**
	 * This is the default constructor
	 */
	public ColorBalancePanel(GeoRaster georaster) {
		this.geoRaster=georaster;
		
		try {
			imp = geoRaster.getGrapImagePlus();
			
			impResize = imp.getProcessor().resize(200);
			
			if (imp.getType()==ImagePlus.COLOR_RGB) {
				int w = imp.getWidth();
				int h = imp.getHeight();
				byte[] r = new byte[w*h];
				byte[] g = new byte[w*h];
				byte[] b = new byte[w*h];
				((ColorProcessor)imp.getProcessor()).getRGB(r,g,b);
				byte[] pixels=null;
				
					pixels = r;
					pixels = g;
					pixels = b;
				ImageProcessor ip = new ByteProcessor(w, h, pixels, null);
				stats = ImageStatistics.getStatistics(ip, 0, imp.getCalibration());
			} else
				stats = imp.getStatistics();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeoreferencingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialize();
		
	}
	
	private void initialize() {
		brightLabel = new JLabel();
		brightLabel.setBounds(new Rectangle(12, 215, 88, 16));
		brightLabel.setText("Brightness");
		maxLabel = new JLabel();
		maxLabel.setBounds(new Rectangle(12, 180, 88, 20));
		maxLabel.setText("Maximum");
		minimLabel = new JLabel();
		minimLabel.setBounds(new Rectangle(12, 145, 88, 21));
		minimLabel.setText("Minimum");
		this.setSize(503, 277);
		this.setLayout(null);
		plot = new Plot(stats);
		plot.setSize(new Dimension(257, 129));
		plot.setLocation(new Point(3, 13));
		
		this.add(plot, null);		
		
		this.add(getJSlider(), null);
		this.add(getJSlider1(), null);
		this.add(getJSlider2(), null);
		this.add(minimLabel, null);
		this.add(maxLabel, null);
		this.add(brightLabel, null);
		this.add(getJPanel(), null);
	
	}



	/**
	 * This method initializes jSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getJSlider() {
		if (jSlider == null) {
			jSlider = new JSlider();
			jSlider.setBounds(new Rectangle(5, 165, 240, 17));			
		}
		return jSlider;
	}

	/**
	 * This method initializes jSlider1	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getJSlider1() {
		if (jSlider1 == null) {
			jSlider1 = new JSlider();
			jSlider1.setBounds(new Rectangle(5, 195, 240, 24));
		}
		return jSlider1;
	}

	/**
	 * This method initializes jSlider2	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getJSlider2() {
		if (jSlider2 == null) {
			jSlider2 = new JSlider();
			jSlider2.setBounds(new Rectangle(5, 230, 240, 21));
		}
		return jSlider2;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			PreviewImagePanel previewImagePanel = new PreviewImagePanel(impResize);
			jPanel.setLayout(new GridBagLayout());
			jPanel.setBounds(new Rectangle(258, 12, 235, 182));
			jPanel.add(previewImagePanel, new GridBagConstraints());
		}
		return jPanel;
	}

}  
