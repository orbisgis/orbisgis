/**
 *
 */
package org.orbisgis.geoview;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerCollection;
import org.orbisgis.geoview.layerModel.LayerCollectionEvent;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.LayerListener;
import org.orbisgis.geoview.layerModel.LayerListenerEvent;
import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.ToolManagerListener;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;

import com.vividsolutions.jts.geom.Geometry;

public class GeoViewContext implements ViewContext {
	/**
	 *
	 */
	private MapControl mapControl;
	private GeoView2D geoview;

	private LayerCollection root;

	public ILayer[] selectedLayers = new ILayer[0];

	private ToolManager tm;
	public ArrayList<ViewContextListener> listeners = new ArrayList<ViewContextListener>();
	private ToolManagerListener tml;
	private OpenerListener openerListener;

	/**
	 * @param mapControl
	 */
	GeoViewContext(GeoView2D geoview) {
		this.root = LayerFactory.createLayerCollection("root");
		openerListener = new OpenerListener();
		this.root.addLayerListenerRecursively(openerListener);

		this.mapControl = geoview.getMap();
		this.geoview = geoview;
	}

	public void addViewContextListener(ViewContextListener listener) {
		listeners.add(listener);
	}

	public void removeViewContextListener(ViewContextListener listener) {
		listeners.remove(listener);
	}

	public Point2D toMapPoint(int i, int j) {
		try {
			return this.mapControl.getTrans().createInverse().transform(
					new Point(i, j), null);
		} catch (NoninvertibleTransformException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean thereIsActiveTheme() {
		return true;
	}

	public void repaint() {
		this.mapControl.repaint();
	}

	public void removeSelected() {
		throw new RuntimeException();
	}

	public boolean isActiveThemeWritable() {
		return true;
	}

	public boolean isActiveThemeVisible() {
		return true;
	}

	public Geometry[] getSelectedGeometries() {
		return new Geometry[] {};
	}

	public int getImageWidth() {
		return this.mapControl.getWidth();
	}

	public int getImageHeight() {
		return this.mapControl.getHeight();
	}

	public Component getComponent() {
		return this.mapControl;
	}

	public Point fromMapPoint(Point2D point) {
		Point2D ret = this.mapControl.getTrans().transform(point, null);
		return new Point((int) ret.getX(), (int) ret.getY());
	}

	public boolean atLeastNGeometriesSelected(int i) {
		return true;
	}

	public String getActiveThemeGeometryType() {
		return Primitive.LINE_GEOMETRY_TYPE;
	}

	public void newGeometry(Geometry g) throws EditionContextException {
		throw new UnsupportedOperationException();
	}

	public boolean selectFeatures(Geometry envelope, boolean toggleSelection,
			boolean contains) throws EditionContextException {
		tml.selectionChanged();
		return true;
	}

	public void updateGeometry(Geometry g) throws EditionContextException {
		int index = (Integer) g.getUserData();
		if (index == 0) {
		} else if (index == 1) {
		}
		tml.dataChanged();
	}

	public ToolManager getToolManager() {
		return tm;
	}

	public void setCursor(Cursor cursor) {
		this.mapControl.setCursor(cursor);
	}

	public AffineTransform getTransformation() {
		return this.mapControl.getTrans();
	}

	public void error(Exception e) {
	}

	public void setToolManagerListener(ToolManagerListener listener) {
		this.tml = listener;
	}

	public void setToolManager(ToolManager tm) {
		this.tm = tm;
	}

	public void stateChanged() {

	}

	public void toolChanged() {

	}

	public void toolError(TransitionException e1) {

	}

	public boolean atLeastNThemes(int i) {
		return true;
	}

	public Rectangle2D getExtent() {
		return this.mapControl.getExtent();
	}

	public Image getMapImage() {
		return this.mapControl.getImage();
	}

	public void setExtent(Rectangle2D extent) {
		this.mapControl.setExtent(extent);
	}

	public ILayer getRootLayer() {
		return root;
	}

	public GeoView2D getView() {
		return geoview;
	}

	public ILayer[] getLayers() {
		return getRootLayer().getChildren();
	}

	public ILayer[] getSelectedLayers() {
		return selectedLayers;
	}

	public void setSelectedLayers(ILayer[] selectedLayers) {
		this.selectedLayers = selectedLayers;
		for (ViewContextListener listener : listeners) {
			listener.layerSelectionChanged(this);
		}
	}

	private final class OpenerListener implements LayerListener {
		public void visibilityChanged(LayerListenerEvent e) {
		}

		public void styleChanged(LayerListenerEvent e) {
		}

		public void nameChanged(LayerListenerEvent e) {
		}

		public void layerMoved(LayerCollectionEvent e) {
		}

		public void layerAdded(LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				layer.addLayerListenerRecursively(openerListener);
				layer.open();
			}
		}

		public void layerRemoved(LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				layer.removeLayerListenerRecursively(openerListener);
				layer.close();
			}
		}
	}
}