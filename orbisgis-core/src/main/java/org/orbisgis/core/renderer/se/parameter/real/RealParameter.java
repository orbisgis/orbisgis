package org.orbisgis.core.renderer.se.parameter.real;

import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameter;

public interface RealParameter extends SeParameter {

	void setMinValue(Double min);
	void setMaxValue(Double max);

    double getValue(Feature feat) throws ParameterException;

	@Override
	String toString();
}
