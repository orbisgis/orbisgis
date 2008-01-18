package org.orbisgis.geoview.rasterProcessing.action.histogramm;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.IOException;

import javax.swing.*;

import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.model.GrapImagePlus;

public class Histogramme extends JPanel {
   private GrapImagePlus grapImagePlus;   
   private final int largeur = 256;
   private final int hauteur = 180;
   private Graphics2D dessin;
   private int[] rouge;
   private int[] vert;
   private int[] bleu;
   private int[] rvb;
   
   public Histogramme(GeoRaster geoRaster) {
      try {
		this.grapImagePlus = geoRaster.getGrapImagePlus();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (GeoreferencingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      this.setPreferredSize(new Dimension(largeur, hauteur));
   }
   protected void paintComponent(Graphics surface) {
      super.paintComponent(surface);
  
		if (grapImagePlus!=null) {
		     dessin = (Graphics2D) surface;
		     getRVBValues();
		     tracerHistogrammesRVB();
		  }
	
   }
   private void getRVBValues() {
	   ImageProcessor ip = grapImagePlus.getProcessor();
	   IndexColorModel modele = (IndexColorModel) grapImagePlus.getProcessor().getColorModel();
	   rouge = new int[256];
	   vert = new int[256];
	   bleu = new int[256];
	   rvb = new int[256];
	   
	   if (grapImagePlus.getType() == ImagePlus.GRAY8){
		   for(int i=0;i<ip.getWidth();i++)
		     {
		               for(int j=0;j<ip.getHeight();j++){
		            	   rouge[modele.getRed(ip.getPixel(i, j))]++;
		            	  
		               }
		     }
		      
		     for (int k=0; k<256; k++) {
		        rvb[k] = (rouge[k]+vert[k]+bleu[k])/2;
		     }
		     
	   }
	   else if (grapImagePlus.getType()==ImagePlus.COLOR_RGB) {
				
		   
		   for(int i=0;i<ip.getWidth();i++)
		     {
		               for(int j=0;j<ip.getHeight();j++){
		            	   rouge[modele.getRed(ip.getPixel(i, j))]++;
		            	   vert[modele.getGreen(ip.getPixel(i, j))]++;
		            	   bleu[modele.getBlue(ip.getPixel(i, j))]++;
		               }
		     }
		      
		     for (int k=0; k<256; k++) {
		        rvb[k] = (rouge[k]+vert[k]+bleu[k])/2;
		     }
		     
	   }
	        
     
     
}
   private void tracerHistogrammesRVB() {
	      Rectangle2D rectangle = new Rectangle2D.Double(0, 0, largeur-1, hauteur-1);
	      dessin.draw(rectangle);
	      dessin.setPaint(new Color(1F, 1F, 1F, 0.2F));
	      dessin.fill(rectangle);      
	      changerAxes();
	      dessin.setPaint(new Color(1F, 0F, 0F, 0.4F));
	      tracerHistogramme(rouge);
	      dessin.setPaint(new Color(0F, 1F, 0F, 0.4F));
	      tracerHistogramme(vert);
	      dessin.setPaint(new Color(0F, 0F, 1F, 0.4F));
	      tracerHistogramme(bleu);
	      dessin.setPaint(new Color(0F, 0F, 0F, 0.4F));
	      tracerHistogramme(rvb);
	   }
   
   private void tracerHistogrammeGRAY() {
      Rectangle2D rectangle = new Rectangle2D.Double(0, 0, largeur-1, hauteur-1);
      dessin.draw(rectangle);
      dessin.setPaint(new Color(1F, 1F, 1F, 0.2F));
      dessin.fill(rectangle);      
      changerAxes();
      dessin.setPaint(new Color(1F, 0F, 0F, 0.4F));
      tracerHistogramme(rouge);
      dessin.setPaint(new Color(0F, 1F, 0F, 0.4F));
      tracerHistogramme(vert);
      dessin.setPaint(new Color(0F, 0F, 1F, 0.4F));
      tracerHistogramme(bleu);
      dessin.setPaint(new Color(0F, 0F, 0F, 0.4F));
      tracerHistogramme(rvb);
   }

   private void changerAxes() {
      dessin.translate(0, hauteur);
      double surfaceImage = 0;	
		surfaceImage = grapImagePlus.getWidth()*grapImagePlus.getHeight();
	
      double surfaceHistogramme = largeur*hauteur;
      dessin.scale(1, -surfaceHistogramme/surfaceImage/3.7);      
   }   
   
   private void tracerHistogramme(int[] couleur) {
      for (int i=0; i<255; i++) 
         dessin.drawLine(i, 0, i, couleur[i]);              
   }  
   
   
  
}