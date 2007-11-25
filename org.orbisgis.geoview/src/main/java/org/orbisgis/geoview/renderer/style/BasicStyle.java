package org.orbisgis.geoview.renderer.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public class BasicStyle implements Style {
	private Color lineColor;

	private Color fillColor;

	private static float lineSize = 1;

	private BasicStroke basicStroke;

	private float alpha = 1;

	public BasicStyle(final Color lineColor, final Color fillColor, final int alpha) {
		this(lineColor,lineSize , fillColor, alpha);
	}
	
	
	
	public BasicStyle(final Color lineColor, final float lineSize, final Color fillColor, final int alpha) {
		this.lineColor = lineColor;
		this.lineSize = lineSize;
		this.fillColor = fillColor;
		this.alpha  = alpha;
		basicStroke = new BasicStroke(lineSize);
	}
	
	public BasicStyle(){
		
	}
	

	public Color getFillColor() {
		return fillColor;
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Color getDefaultLineColor() {
		return Color.BLUE;
	}
	
	
	public void setLineSize(final float lineSize){
		this.lineSize = lineSize;
		basicStroke  = new BasicStroke(lineSize);
	}

	public BasicStroke getStroke() {
		
		return basicStroke;
	}



	public double getSizeInUnits() {
		
		return 20;
	}
}