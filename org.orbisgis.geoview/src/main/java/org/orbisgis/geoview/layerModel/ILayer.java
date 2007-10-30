package org.orbisgis.geoview.layerModel;

import java.util.Set;

import javax.swing.Icon;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.geoview.renderer.style.Style;

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

	ILayer remove(ILayer layer);

	ILayer remove(String layerName);

	void put(ILayer layer) throws CRSException;

	CoordinateReferenceSystem getCoordinateReferenceSystem();

	void setCoordinateReferenceSystem(
			CoordinateReferenceSystem coordinateReferenceSystem);

	Icon getIcon();

	public void setStyle(Style style);

	public Envelope getEnvelope();

	boolean acceptsChilds();

	ILayer[] getChildren();

	void insertLayer(ILayer layer, int index) throws CRSException;

	int getIndex(ILayer targetLayer);
}