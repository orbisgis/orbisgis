package org.orbisgis.plugin.view.ui.style;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.units.Unit;

import org.geotools.coverage.Category;
import org.geotools.coverage.FactoryFinder;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.renderer.lite.gridcoverage2d.RasterSymbolizerSupport;
import org.geotools.styling.SLDParser;

import org.geotools.styling.ColorMap;
import org.geotools.styling.NamedLayer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryFinder;
import org.geotools.styling.StyledLayerDescriptor;


import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;


import javax.media.jai.RasterFactory;

import org.geotools.util.NumberRange;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.spatialschema.geometry.Envelope;


/**
 * 
 * @author Erwan Bocher Style utilities.
 * 
 * 
 */

public class UtilStyle {

	public static Style loadStyleFromXml(String url) throws Exception {

		StyleFactory factory = StyleFactoryFinder.createStyleFactory();

		SLDParser parser = new SLDParser(factory, url);

		Style style = parser.readXML()[0];

		return style;
	}
	
	
	 /*
     * This method apply a new set of Colors to the RasterLayer It has to read
     * the raster data, build new categories, create a new GridCoverage and
     * apply it to the RasterLayer
     */
    public static GridCoverage changeRasterColors(GridCoverage gc) {
    	
    	 GridCoverage2D  gc2D = (GridCoverage2D) gc;
    	
    	 /*
         * Set the pixel values.  Because we use only one tile with one band, the code below
         * is pretty similar to the code we would have if we were just setting the values in
         * a matrix.
         */
        final int width  = gc2D.getRenderedImage().getWidth();
        final int height = gc2D.getRenderedImage().getHeight();
        WritableRaster raster = RasterFactory.createBandedRaster(DataBuffer.TYPE_FLOAT,
                                                              width, height, 1, null);
        
        WritableRaster oldData = (WritableRaster) gc2D.getRenderedImage().getData();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
            	raster.setSample(j, i, 0, oldData.getSampleFloat(j, i, 0));
            }
        }
    	
    	 GridCoverageFactory factory = FactoryFinder.getGridCoverageFactory(null);
    	 
    	 //Color[] colors = new Color[] {Color.BLACK, Color.CYAN, Color.WHITE, Color.YELLOW, Color.RED};
         
    	 Color[] colors = new Color[] {Color.BLACK, Color.WHITE};
    	 
    	 GridCoverage newgc = factory.create("My colored coverage", raster, gc.getEnvelope(),
                             null, null, null, new Color[][] {colors}, null);
         
    	 
    	
    	 
    	 
         return newgc;
               

    }
    
    
    public static GridCoverage loadRasterStyleFromXml(GridCoverage gc, String url ) throws FileNotFoundException {
        
    	StyleFactory factory = StyleFactoryFinder.createStyleFactory();

		SLDParser parser = new SLDParser(factory, url);
		
		StyledLayerDescriptor sld = parser.parseSLD();
		
		NamedLayer layerOne = (NamedLayer)sld.getStyledLayers()[0];
        RasterSymbolizer symbolizer = (RasterSymbolizer)layerOne.getStyles()[0].getFeatureTypeStyles()[0].getRules()[0].getSymbolizers()[0];
        
        System.out.println(symbolizer.getOpacity());
        
        RasterSymbolizerSupport rsp = new RasterSymbolizerSupport(symbolizer);
            		
    	GridCoverage2D recoloredGridCoverage = (GridCoverage2D) rsp.recolorCoverage(gc);
    	
    			
    	return recoloredGridCoverage;
    	   
    	 
    }
    
    
    
    
}