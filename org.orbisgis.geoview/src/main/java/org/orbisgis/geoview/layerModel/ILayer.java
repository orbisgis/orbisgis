package org.orbisgis.geoview.layerModel;

import java.util.Set;

import javax.swing.Icon;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.geoview.renderer.style.Style;

import com.vividsolutions.jts.geom.Envelope;

public interface ILayer {

	void addLayerListener(LayerListener listener);

	void removeLayerListener(LayerListener listener);

	void addLayerListenerRecursively(LayerListener listener);

	void removeLayerListenerRecursively(LayerListener listener);

	String getName();

	void setName(final String name) throws LayerException;

	void setParent(final ILayer parent) throws LayerException;

	public Set<String> getAllLayersNames();

	boolean isVisible();

	void setVisible(final boolean isVisible) throws LayerException;

	ILayer getParent();

	ILayer remove(ILayer layer) throws LayerException;

	ILayer remove(String layerName) throws LayerException;

	void put(ILayer layer) throws LayerException, CRSException;

	CoordinateReferenceSystem getCoordinateReferenceSystem();

	void setCoordinateReferenceSystem(
			CoordinateReferenceSystem coordinateReferenceSystem)
			throws LayerException;

	Icon getIcon();

	public void setStyle(Style style);

	public Envelope getEnvelope();

	boolean acceptsChilds();

	ILayer[] getChildren();

	void insertLayer(ILayer layer, int index) throws LayerException, CRSException;

	int getIndex(ILayer targetLayer);
}