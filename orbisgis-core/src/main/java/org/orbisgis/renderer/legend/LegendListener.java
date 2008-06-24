package org.orbisgis.renderer.legend;

public interface LegendListener {

	/**
	 * Called when a legend has changed so that the symbols to draw a layer can
	 * have changed
	 *
	 * @param legend
	 */
	void invalidateLegend(Legend legend);
}
