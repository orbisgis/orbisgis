package org.orbisgis.core.renderer.legend.carto;

import org.orbisgis.core.renderer.legend.AbstractLegend;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.carto.persistence.LegendContainer;
import org.orbisgis.core.renderer.symbol.collection.persistence.SimpleSymbolType;

public abstract class AbstractCartoLegend extends AbstractLegend implements
		Legend {

	public AbstractCartoLegend() {
		setName(getLegendTypeName());
	}

	@Override
	public String getJAXBContext() {
		return getPackage(LegendContainer.class) + ":"
				+ getPackage(SimpleSymbolType.class);
	}

	private String getPackage(Class<?> persistenceClass) {
		String className = persistenceClass.getName();
		String packageName = className.substring(0, className.lastIndexOf('.'));
		return packageName;
	}

}
