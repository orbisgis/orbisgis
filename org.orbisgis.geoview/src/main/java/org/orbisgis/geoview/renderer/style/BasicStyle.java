package org.orbisgis.geoview.renderer.style;

import java.awt.Color;

public class BasicStyle implements Style {
	private Color lineColor;

	private Color fillColor;

	public BasicStyle(final Color lineColor, final Color fillColor) {
		this.lineColor = lineColor;
		this.fillColor = fillColor;
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
}