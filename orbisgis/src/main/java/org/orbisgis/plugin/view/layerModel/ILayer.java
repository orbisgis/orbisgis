package org.orbisgis.plugin.view.layerModel;

import java.util.Set;

import javax.swing.Icon;

import org.geotools.styling.Style;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public interface ILayer {
	void addLayerListener(LayerListener listener);

	void removeLayerListener(LayerListener listener);

	String getName();

	void setName(final String name);

	void setName(final String name, final Set<String> allLayersNames);

	void setParent(final ILayer parent);

	public Set<String> getAllLayersNames();

	boolean isVisible();

	void setVisible(final boolean isVisible);

	ILayer getParent();

	CoordinateReferenceSystem getCoordinateReferenceSystem();

	void setCoordinateReferenceSystem(
			CoordinateReferenceSystem coordinateReferenceSystem);

	Icon getIcon();

	public void setStyle(Style style);
	
	public Envelope getEnvelope();
}