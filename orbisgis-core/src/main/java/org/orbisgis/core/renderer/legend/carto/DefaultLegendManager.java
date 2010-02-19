package org.orbisgis.core.renderer.legend.carto;

import java.util.ArrayList;

import org.orbisgis.core.renderer.legend.Legend;

public class DefaultLegendManager implements LegendManager {

	private ArrayList<Legend> newInstances = new ArrayList<Legend>();

	@Override
	public Legend getNewLegend(String legendId) {
		for (Legend legend : newInstances) {
			if (legend.getLegendTypeId().equals(legendId)) {
				return legend.newInstance();
			}
		}
		return null;
	}

	@Override
	public Legend[] getAvailableLegends() {
		return newInstances.toArray(new Legend[0]);
	}

	@Override
	public void addLegend(Legend legend) {
		if (getNewLegend(legend.getLegendTypeId()) != null) {
			throw new IllegalArgumentException(
					"There is already a legend with that id: "
							+ legend.getLegendTypeId());
		}
		newInstances.add(legend);
	}
}
