package org.orbisgis.renderer.legend.carto;

import org.orbisgis.renderer.legend.AbstractLegend;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.carto.persistence.LegendContainer;

public abstract class AbstractCartoLegend extends AbstractLegend implements Legend {

	@Override
	public String getJAXBContext() {
		String className = LegendContainer.class.getName();
		className = className.substring(0, className.lastIndexOf('.'));
		return className;
	}

}
