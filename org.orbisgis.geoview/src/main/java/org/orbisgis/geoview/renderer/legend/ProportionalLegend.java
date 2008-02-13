package org.orbisgis.geoview.renderer.legend;

public interface ProportionalLegend extends ClassifiedLegend {

	void setMinSymbolArea(int minArea);

	void setLinearMethod();

	void setSquareMethod(double squareFactor);

	void setLogarithmicMethod();

}
