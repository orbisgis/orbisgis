package org.orbisgis.core.renderer.legend.carto;

import org.orbisgis.core.renderer.legend.Legend;

/**
 * Keeps a collection of available legends in the system. The collection can be
 * increased by adding new legend types and new legends of the available types
 * can be created
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface LegendManager {

	/**
	 * creates a new legend of the specified type
	 *
	 * @param legendId
	 * @return The created legend or null if there is no legend with that id
	 */
	Legend getNewLegend(String legendId);

	/**
	 * Adds a new type of legend.
	 *
	 * @param legend
	 *            instance of the new type of legend
	 * @throws IllegalArgumentException
	 *             If there is already a legend with that id
	 */
	void addLegend(Legend legend) throws IllegalArgumentException;

	/**
	 * Get an array of the available legends in the manager
	 *
	 * @return
	 */
	Legend[] getAvailableLegends();
}
