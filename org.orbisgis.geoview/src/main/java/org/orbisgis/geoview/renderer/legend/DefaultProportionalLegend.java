package org.orbisgis.geoview.renderer.legend;

import java.util.ArrayList;

import org.gdms.driver.DriverException;

public class DefaultProportionalLegend extends AbstractClassifiedLegend
		implements ProportionalLegend {

	@Override
	protected ArrayList<Symbol> doClassification(int fieldIndex)
			throws DriverException {
		// Iterate on the datasource and create the circle symbols of the
		// desired size
		return null;
	}

}
