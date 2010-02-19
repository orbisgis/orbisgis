package org.orbisgis.core.ui.editors.map.tools;

import org.orbisgis.core.layerModel.MapContext;

/**
 * Interface implemented by some tools that create new geometries and add them
 * to some layer
 * 
 * @author Fernando Gonzalez Cortes
 */
public interface InsertionTool {

	/**
	 * Gets the Z component of all the coordinates introduced by the user
	 * 
	 * @param mapContext
	 * 
	 * @return The desired Z component or Double.NaN if the coordinate does not
	 *         have Z component
	 */
	double getInitialZ(MapContext mapContext);

}
