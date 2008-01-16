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
/**
 *
 */
package org.orbisgis.geoview.tools;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;

import org.orbisgis.core.persistence.PersistenceException;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.ViewContextListener;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.ToolManagerListener;
import org.orbisgis.tools.TransitionException;

import com.vividsolutions.jts.geom.Geometry;

public class TestEditionContext implements ViewContext {

	public ArrayList<Geometry> features = new ArrayList<Geometry>();

	public ArrayList<Integer> selected = new ArrayList<Integer>();

	public MapControl mapControl = new MapControl();

	public String geometryType;

	public TestEditionContext(String geometryType) {
		mapControl.setEditionContext(this);
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

	public void setToolManagerListener(ToolManagerListener tm) {
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

	public ILayer getViewModel() {
		return null;
	}

	public GeoView2D getView() {
		return null;
	}

	public ILayer[] getLayers() {
		return null;
	}

	public ILayer[] getSelectedLayers() {
		return new ILayer[0];
	}

	public void setSelectedLayers(ILayer[] selectedLayers) {
	}

	public void addViewContextListener(ViewContextListener listener) {

	}

	public void removeViewContextListener(ViewContextListener listener) {

	}

	public ToolManager getToolManager() {
		return null;
	}

	public void setToolManager(ToolManager tm) {
	}

	public void saveStatus(File file) {

	}

	public void loadStatus(File file) throws PersistenceException {

	}

}