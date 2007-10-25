package org.orbisgis.tools;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This interface provides information to the tool system and receives
 * notifications from it. Also registers the tool system as a listener in order
 * to notify it about certain events during edition
 */
public interface EditionContext {

	/**
	 * If there is a theme in edition and it's visible
	 *
	 * @return
	 */
	boolean isActiveThemeVisible();

	/**
	 * If there is a theme in edition
	 *
	 * @return
	 */
	boolean thereIsActiveTheme();

	/**
	 * If the active theme is writable
	 *
	 * @return
	 */
	boolean isActiveThemeWritable();

	/**
	 * remove the selected features from the active layer. This features are
	 * thoes which their geometries where returned in the
	 * getSelectedGeometries() method
	 *
	 */
	void removeSelected();

	/**
	 * Gets the geometries of the selected features
	 *
	 * @return
	 * @throws EditionContextException
	 */
	Geometry[] getSelectedGeometries() throws EditionContextException;

	/**
	 * The geometry has the GID of one of the geometries returned by
	 * getSelectedGeometries
	 *
	 * @param g
	 */
	void updateGeometry(Geometry g) throws EditionContextException;

	/**
	 * Called by the tool system when it has performed some operation that needs
	 * a refresh in a map
	 */
	void repaint();

	/**
	 * Transforms the image point in a map point
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	Point2D toMapPoint(int x, int y);

	/**
	 * Gets the component where the edition is performed.
	 *
	 * @return
	 */
	Component getComponent();

	/**
	 * Transforms the map point into an image point
	 *
	 * @param point
	 * @return
	 */
	Point fromMapPoint(Point2D point);

	/**
	 * Gets the width of the control where the edition is performed
	 *
	 * @return
	 */
	int getImageWidth();

	/**
	 * Gets the height of the control where the edition is performed
	 *
	 * @return
	 */
	int getImageHeight();

	/**
	 * Returns if there is at least a number of selected geometries greater than
	 * the argument
	 *
	 * @param i
	 * @return
	 */
	boolean atLeastNGeometriesSelected(int i);

	/**
	 * Gets the geometry type of the theme in edition. It can be one of the
	 * constants in Primitive class.
	 *
	 * @return
	 */
	String getActiveThemeGeometryType();

	/**
	 * Creates a new feature with the geometry referenced by the argument
	 *
	 * @param g
	 * @throws EditionContextException
	 */
	void newGeometry(Geometry g) throws EditionContextException;

	/**
	 * Selects features in the theme in edition
	 *
	 * @param envelope
	 * @param toggleSelection
	 * @param contains
	 *
	 * @return if the operation changed the current selection
	 *
	 * @throws EditionContextException
	 */
	boolean selectFeatures(Geometry envelope, boolean toggleSelection,
			boolean contains) throws EditionContextException;

	/**
	 * Notifies the edition context of the cursor of the current tool.
	 *
	 * @param c
	 */
	public void setCursor(Cursor c);

	/**
	 * Sets the tool manager in order to receive the notifications of
	 *
	 * @param el
	 */
	void setToolManager(ToolManagerNotifications tm);

	/**
	 * Gets the transformation of the map representation
	 *
	 * @return
	 */
	AffineTransform getTransformation();

	/**
	 * Notifies an error in the tool system. May be caused by one of the methods
	 * in this interface
	 *
	 * @param e
	 */
	void error(Exception e);

	/**
	 * An error in the last tool transition
	 *
	 * @param e1
	 */
	void toolError(TransitionException e1);

	/**
	 * Notifies that the selected tool has changed
	 */
	void toolChanged();

	/**
	 * Notifies that the selected tool has changed its status
	 */
	void stateChanged();

	/**
	 * Gets the current extent
	 *
	 * @return
	 */
	Rectangle2D getExtent();

	/**
	 * Sets the new extent
	 *
	 * @param double1
	 */
	void setExtent(Rectangle2D extent);

	/**
	 * Get the last displayed image
	 *
	 * @return
	 */
	Image getMapImage();

	/**
	 * There are at least i themes loaded
	 * @param i
	 *
	 * @return
	 */
	boolean atLeastNThemes(int i);

}
