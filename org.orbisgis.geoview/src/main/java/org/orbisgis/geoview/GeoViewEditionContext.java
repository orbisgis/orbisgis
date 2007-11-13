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

import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.tools.EditionContext;
import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.ToolManagerNotifications;
import org.orbisgis.tools.TransitionException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

class GeoViewEditionContext implements EditionContext {
	/**
	 *
	 */
	private MapControl mapControl;
	private GeoView2D geoview;

	/**
	 * @param mapControl
	 */
	GeoViewEditionContext(GeoView2D geoview) {
		this.mapControl = geoview.getMap();
		this.geoview = geoview;
	}

	private Geometry g1;
	private Geometry g2;
	{
		g1 = new GeometryFactory().createLineString(new Coordinate[] {
				new Coordinate(0, 0), new Coordinate(10, 10),
				new Coordinate(10, 0) });
		g1.setUserData(new Integer(0));
		g2 = new GeometryFactory().createLineString(new Coordinate[] {
				new Coordinate(10, 0), new Coordinate(20, 10),
				new Coordinate(20, 20) });
		g2.setUserData(new Integer(1));
	}
	private ToolManagerNotifications tm;

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
		return new Geometry[] { g1, g2 };
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
		tm.selectionChanged();
		return true;
	}

	public void updateGeometry(Geometry g) throws EditionContextException {
		int index = (Integer) g.getUserData();
		if (index == 0) {
			this.g1 = g;
		} else if (index == 1) {
			this.g2 = g;
		}
		tm.dataChanged();
	}

	public void setCursor(Cursor cursor) {
		this.mapControl.setCursor(cursor);
	}

	public AffineTransform getTransformation() {
		return this.mapControl.getTrans();
	}

	public void error(Exception e) {
	}

	public void setToolManager(ToolManagerNotifications tm) {
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
		return this.mapControl.getAdjustedExtent();
	}

	public Image getMapImage() {
		return this.mapControl.getImage();
	}

	public void setExtent(Rectangle2D extent) {
		this.mapControl.setExtent(extent);
	}

	public ILayer getRootLayer() {
		return geoview.getMapModel().getLayers();
	}

	public GeoView2D getView() {
		return geoview;
	}
}