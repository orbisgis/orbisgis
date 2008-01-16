/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.tools;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;

import org.orbisgis.core.persistence.PersistenceException;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.ViewContextListener;
import org.orbisgis.geoview.layerModel.ILayer;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This interface provides information to the tool system and receives
 * notifications from it. Also registers the tool system as a listener in order
 * to notify it about certain events during edition
 */
public interface ViewContext {

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
	void setToolManagerListener(ToolManagerListener tm);

	/**
	 * Sets the tool manager of this view context
	 *
	 * @param tm
	 */
	void setToolManager(ToolManager tm);

	/**
	 * Gets the tool manager of this context. It is useful to interact with
	 * tools programatically
	 *
	 * @return
	 */
	ToolManager getToolManager();

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
	 *
	 * @param i
	 *
	 * @return
	 */
	boolean atLeastNThemes(int i);

	/**
	 * Gets the root layer of the layer collection in this edition context
	 *
	 * @return
	 */
	ILayer getViewModel();

	/**
	 * Gets the geoview in this edition context
	 *
	 * @return
	 */
	GeoView2D getView();

	public ILayer[] getLayers();

	public ILayer[] getSelectedLayers();

	public void addViewContextListener(ViewContextListener listener);

	public void removeViewContextListener(ViewContextListener listener);

	public void setSelectedLayers(ILayer[] selectedLayers);

	void saveStatus(File file) throws PersistenceException;

	void loadStatus(File file) throws PersistenceException;
}
