/**
 *
 */
package org.orbisgis.plugin.view.tools;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.orbisgis.plugin.view.ui.MapControl;

import com.vividsolutions.jts.geom.Geometry;

public class TestEditionContext implements EditionContext {

	public ArrayList<Geometry> features = new ArrayList<Geometry>();

	public ArrayList<Integer> selected = new ArrayList<Integer>();

	public MapControl mapControl = new MapControl();

	public String geometryType;

	public TestEditionContext(String geometryType) {
		this.geometryType = geometryType;
		mapControl.setSize(100, 100);
		mapControl.setExtent(new Rectangle2D.Double(0, 0, 100, 100));
	}

	public boolean atLeastNGeometriesSelected(int i) {
		return selected.size() >= i;
	}

	public boolean atLeastNThemes(int i) {
		/*
		 * we always have only one theme
		 */
		return 1 >= i;
	}

	public void error(Exception e) {
		throw new RuntimeException();
	}

	public Point fromMapPoint(Point2D point) {
		Point2D ret = mapControl.getTrans().transform(point, null);
		return new Point((int) ret.getX(), (int) ret.getY());
	}

	public String getActiveThemeGeometryType() {
		return geometryType;
	}

	public Component getComponent() {
		throw new RuntimeException();
	}

	public Rectangle2D getExtent() {
		return mapControl.getAdjustedExtent();
	}

	public int getImageHeight() {
		return mapControl.getHeight();
	}

	public int getImageWidth() {
		return mapControl.getWidth();
	}

	public Image getMapImage() {
		throw new RuntimeException();
	}

	public Geometry[] getSelectedGeometries() throws EditionContextException {
		return getSelection().toArray(new Geometry[0]);
	}

	private ArrayList<Geometry> getSelection() {
		ArrayList<Geometry> ret = new ArrayList<Geometry>();
		for (int i = 0; i < selected.size(); i++) {
			Geometry g = features.get(selected.get(i));
			g.setUserData(new Integer(selected.get(i)));
			ret.add(g);
		}

		return ret;
	}

	public AffineTransform getTransformation() {
		return mapControl.getTrans();
	}

	public boolean isActiveThemeVisible() {
		return true;
	}

	public boolean isActiveThemeWritable() {
		return true;
	}

	public void newGeometry(Geometry g) throws EditionContextException {
		features.add(g);
	}

	public void removeSelected() {
		features.removeAll(getSelection());
		selected.clear();
	}

	public void repaint() {
	}

	public boolean selectFeatures(Geometry envelope, boolean toggleSelection,
			boolean contains) throws EditionContextException {
		boolean change = false;
		ArrayList<Integer> newSelection = new ArrayList<Integer>();
        for (int i = 0; i < features.size(); i++) {
            Geometry g = features.get(i);

            if (!contains ) {
                if (g.intersects(envelope)) {
                	change = true;
                    newSelection.add(i);
                }
            } else {
                if (envelope.contains(g)) {
                	change = true;
                    newSelection.add(i);
                }
            }
        }

        if (toggleSelection) {
        	for (int i = 0; i < newSelection.size(); i++) {
				if (selected.contains(newSelection.get(i))) {
					selected.remove(i);
				} else {
					selected.add(i);
				}
			}
        } else {
            selected = newSelection;
        }

        return change;
	}

	public void setCursor(Cursor c) {
	}

	public void setExtent(Rectangle2D extent) {
		mapControl.setExtent(extent);
	}

	public void setToolManager(ToolManagerNotifications tm) {
		/*
		 * We don't notify anything
		 */
	}

	public void stateChanged() {

	}

	public boolean thereIsActiveTheme() {
		return true;
	}

	public Point2D toMapPoint(int x, int y) {
		try {
			return mapControl.getTrans().createInverse().transform(
					new Point(x, y), null);
		} catch (NoninvertibleTransformException e) {
			throw new RuntimeException(e);
		}
	}

	public void toolChanged() {
	}

	public void toolError(TransitionException e1) {
		throw new RuntimeException();
	}

	public void updateGeometry(Geometry g) throws EditionContextException {
		Integer index = (Integer) g.getUserData();
		features.set(index, g);
	}

}