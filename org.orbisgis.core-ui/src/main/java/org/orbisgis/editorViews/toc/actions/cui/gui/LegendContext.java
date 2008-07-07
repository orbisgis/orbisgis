package org.orbisgis.editorViews.toc.actions.cui.gui;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.layerModel.ILayer;

public interface LegendContext {

	/**
	 * Gets the type of geometries in the specified layer. One of the constants
	 * in the {@link ILegendPanelUI}
	 *
	 * @return
	 */
	int getGeometryType();

	/**
	 * return true if the layer can contain points
	 *
	 * @return
	 */
	boolean isPoint();

	/**
	 * return true if the layer can contain line
	 *
	 * @return
	 */
	boolean isLine();

	/**
	 * return true if the layer can contain polygon
	 *
	 * @return
	 */
	boolean isPolygon();

	/**
	 * Gets the constraint of the field which legend is being edited
	 *
	 * @return
	 */
	GeometryConstraint getGeometryConstraint();

	/**
	 * Gets the layer which legend is being edited
	 *
	 * @return
	 */
	ILayer getLayer();
}
