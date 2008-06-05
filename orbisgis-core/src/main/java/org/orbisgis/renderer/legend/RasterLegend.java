package org.orbisgis.renderer.legend;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
public class RasterLegend extends AbstractLegend implements Legend {
	
	

	private ColorModel colorModel = null;

	private float opacity = 1.0f;

	private String bandsCode;

	public RasterLegend(ColorModel colorModel, float opacity) {
		this.colorModel = colorModel;
		this.opacity = opacity;
	}

	public ColorModel getColorModel() {
		return colorModel;
	}
	
	
	public void setColorModel(ColorModel colorModel){
		this.colorModel= colorModel;
	}

	public float getOpacity() {
		return opacity;
	}

	public int getNumLayers() {
		return 1;
	}

	public Symbol getSymbol(long row) throws RenderException {
		return null;
	}

	public String getLegendTypeName() {
		return "Raster legend";
	}
	
	public void setBands(String bandsCode){
		
		this.bandsCode = bandsCode;
		
	}
	
	public String getBands(){
		return bandsCode;
	}

	 public void setRangeColors(final double[] ranges, final Color[] colors) throws Exception
	 {
	 checkRangeColors(ranges, colors);
	
	// // TODO : is it really necessary ?
	// setRangeValues(ranges[0], ranges[ranges.length - 1]);
	//
	 final int nbOfColors = 256;
	 final byte[] reds = new byte[nbOfColors];
	 final byte[] greens = new byte[nbOfColors];
	 final byte[] blues = new byte[nbOfColors];
	 final double delta = (ranges[ranges.length - 1] - ranges[0])
	 / (nbOfColors - 1);
	 double x = ranges[0] + delta;
	
	 for (int i = 1, j = 0; i < nbOfColors; i++, x += delta) {
	 while (!((x >= ranges[j]) && (x < ranges[j + 1]))
	 && (colors.length > j + 1)) {
	 j++;
	 }
	 reds[i] = (byte) colors[j].getRed();
	 greens[i] = (byte) colors[j].getGreen();
	 blues[i] = (byte) colors[j].getBlue();
	 }
	 // default color for NaN pixels :
	 reds[0] = (byte) Color.BLACK.getRed();
	 greens[0] = (byte) Color.BLACK.getGreen();
	 blues[0] = (byte) Color.BLACK.getBlue();
	
	  setColorModel(new IndexColorModel(8, nbOfColors, reds, greens, blues));
	 }
	
	 private void checkRangeColors(final double[] ranges, final Color[]
	 colors) throws Exception {
	 if (ranges.length != colors.length + 1) {
	 throw new Exception(
	 "Ranges.length not equal to Colors.length + 1 !");
	 }
	for (int i = 1; i < ranges.length; i++) {
	if (ranges[i - 1] > ranges[i]) {
	 throw new Exception(
	 "Ranges array needs to be sorted !");
	 }
	 }
	 if (colors.length > 256) {
	 throw new Exception(
	"Colors.length must be less than 256 !");
	 }
	}

}