package org.orbisgis.geoview.renderer.style;

import java.awt.BasicStroke;
import java.awt.Color;

public interface Style {
	
	public Color getFillColor();

	public Color getLineColor();

	public Color getDefaultLineColor();

	public BasicStroke getBasicStroke();

		
	
}