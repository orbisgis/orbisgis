/**
 *
 */
package org.orbisgis.plugin.view.layerModel;

import org.geotools.styling.Style;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public abstract class BasicLayer extends ALayer {
	private CoordinateReferenceSystem coordinateReferenceSystem;

	private Style style;

	private boolean isVisible = true;

	public BasicLayer(final String name,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name);
		this.coordinateReferenceSystem = coordinateReferenceSystem;
	}

	/**
	 *
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#getCoordinateReferenceSystem()
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return coordinateReferenceSystem;
	}

	/**
	 *
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#setCoordinateReferenceSystem(org.opengis.referencing.crs.CoordinateReferenceSystem)
	 */
	public void setCoordinateReferenceSystem(
			CoordinateReferenceSystem coordinateReferenceSystem) {
		this.coordinateReferenceSystem = coordinateReferenceSystem;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
		for (LayerListener listener : listeners) {
			listener.styleChanged(new LayerListenerEvent(this));
		}
	}

	/**
	 *
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#isVisible()
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 *
	 * @see org.orbisgis.plugin.view.layerModel.ILayer#setVisible(boolean)
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		fireVisibilityChanged();
	}
}