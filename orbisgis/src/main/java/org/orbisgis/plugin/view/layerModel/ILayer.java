package org.orbisgis.plugin.view.layerModel;

import javax.swing.Icon;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

public interface ILayer {
	void addLayerListener(LayerListener listener);

	void removeLayerListener(LayerListener listener);

	String getName();

	void setName(final String name);

	boolean isVisible();

	void setVisible(boolean isVisible);

	ILayer getParent();

	CoordinateReferenceSystem getCoordinateReferenceSystem();

	void setCoordinateReferenceSystem(
			CoordinateReferenceSystem coordinateReferenceSystem);

	Icon getIcon();
}