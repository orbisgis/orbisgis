package org.orbisgis.geoview.rasterProcessing.action.histogramm;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Graphics;

import javax.swing.JPanel;

import org.grap.model.GrapImagePlus;
import org.h2.result.UpdatableRow;

public class PreviewImagePanel extends JPanel {

	
	private ImageProcessor impResize;

	public PreviewImagePanel(ImageProcessor impResize){
		this.impResize = impResize;
		
	}
	
	
	public void setImageProcessor(ImageProcessor impResize){
		this.impResize = impResize;
		
		
	}
	public void paint(Graphics g){
		
		g.drawImage(impResize.createImage(), impResize.getHeight(), impResize.getWidth(), null);
		
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
}

