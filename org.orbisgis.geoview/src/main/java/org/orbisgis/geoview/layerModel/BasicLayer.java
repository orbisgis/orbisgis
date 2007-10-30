/**
 *
 */
package org.orbisgis.geoview.layerModel;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.geoview.renderer.style.Style;

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
	 * @see org.orbisgis.geoview.layerModel.ILayer#getCoordinateReferenceSystem()
	 */
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return coordinateReferenceSystem;
	}

	/**
	 *
	 * @see org.orbisgis.geoview.layerModel.ILayer#setCoordinateReferenceSystem(org.opengis.referencing.crs.CoordinateReferenceSystem)
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
	 * @see org.orbisgis.geoview.layerModel.ILayer#isVisible()
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 *
	 * @see org.orbisgis.geoview.layerModel.ILayer#setVisible(boolean)
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		fireVisibilityChanged();
	}

	public void put(ILayer layer) throws CRSException {
		throw new IllegalArgumentException("This layer cannot have children");
	}

	public ILayer remove(ILayer layer) {
		throw new IllegalArgumentException("This layer does not have children");
	}

	public ILayer remove(String layerName) {
		throw new IllegalArgumentException("This layer does not have children");
	}

	public boolean acceptsChilds() {
		return false;
	}

	public ILayer[] getChildren() {
		return new ILayer[0];
	}

	public int getIndex(ILayer targetLayer) {
		return -1;
	}

	public void insertLayer(ILayer layer, int index) throws CRSException {
		throw new IllegalArgumentException("This layer cannot have children");
	}

}