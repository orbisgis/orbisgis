package org.orbisgis.renderer.legend;

import java.awt.Color;

import org.gdms.driver.DriverException;

public interface ProportionalLegend extends ClassifiedLegend {

	void setMinSymbolArea(int minArea);

	void setLinearMethod() throws DriverException;

	void setSquareMethod(double squareFactor) throws DriverException;

	void setLogarithmicMethod() throws DriverException;

	public void setOutlineColor(Color outline);
	
	public void setFillColor(Color fill);
	
	public Color getFillColor();
	
	public Color getOutlineColor();
	
}
