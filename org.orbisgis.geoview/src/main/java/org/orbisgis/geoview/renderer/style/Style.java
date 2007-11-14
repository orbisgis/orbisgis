package org.orbisgis.geoview.renderer.style;

import java.awt.Color;
import java.awt.Stroke;

public interface Style {
	public Color getFillColor();

	public Color getLineColor();

	public Color getDefaultLineColor();

	public Stroke getStroke();

	public double getSizeInUnits();
	
	
	
}