package org.orbisgis.geoview.renderer.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public class BasicStyle implements Style {
	private Color lineColor;

	private Color fillColor;

	private float lineSize;

	private BasicStroke basicStroke;

	public BasicStyle(final Color lineColor, final Color fillColor) {
		this(lineColor, 1, fillColor);
	}
	
	
	
	public BasicStyle(final Color lineColor, final float lineSize, final Color fillColor) {
		this.lineColor = lineColor;
		this.lineSize = lineSize;
		this.fillColor = fillColor;
		basicStroke = new BasicStroke(lineSize);
	}
	
	

	public Color getFillColor() {
		return fillColor;
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

	public Stroke getStroke() {
		
		return basicStroke;
	}



	public double getSizeInUnits() {
		
		return 20;
	}
}