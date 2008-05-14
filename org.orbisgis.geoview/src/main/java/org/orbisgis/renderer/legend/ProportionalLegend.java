package org.orbisgis.renderer.legend;

import org.gdms.driver.DriverException;

public interface ProportionalLegend extends ClassifiedLegend {

	void setMinSymbolArea(int minArea);

	void setLinearMethod() throws DriverException;

	void setSquareMethod(double squareFactor) throws DriverException;

	void setLogarithmicMethod() throws DriverException;

}
