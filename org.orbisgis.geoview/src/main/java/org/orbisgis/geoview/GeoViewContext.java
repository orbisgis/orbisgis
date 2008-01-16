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
package org.orbisgis.geoview;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.core.persistence.PersistenceException;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.ILayerAction;
import org.orbisgis.geoview.layerModel.LayerCollection;
import org.orbisgis.geoview.layerModel.LayerCollectionEvent;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.LayerListener;
import org.orbisgis.geoview.layerModel.LayerListenerEvent;
import org.orbisgis.geoview.persistence.LayerCollectionType;
import org.orbisgis.geoview.persistence.LayerType;
import org.orbisgis.geoview.persistence.SelectedLayer;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.ToolManagerListener;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Class that contains the status of the view.
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class GeoViewContext implements ViewContext {
	/**
	 *
	 */
	private MapControl mapControl;
	private GeoView2D geoview;

	private ILayer root;

	private ILayer[] selectedLayers = new ILayer[0];

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
		PluginManager.error("Error in the tool", e1);
	}

	public boolean atLeastNThemes(int i) {
		return getLayers().length >= i;
	}

	public Rectangle2D getExtent() {
		return this.mapControl.getExtent();
	}

	public Image getMapImage() {
		return this.mapControl.getImage();
	}

	public void setExtent(Rectangle2D extent) {
		this.mapControl.setExtent(extent);
		geoview.enableControls();
	}

	public ILayer getViewModel() {
		return root;
	}

	public GeoView2D getView() {
		return geoview;
	}

	public ILayer[] getLayers() {
		return getViewModel().getChildren();
	}

	public ILayer[] getSelectedLayers() {
		return selectedLayers;
	}

	public void setSelectedLayers(ILayer[] selectedLayers) {
		this.selectedLayers = selectedLayers;
		for (ViewContextListener listener : listeners) {
			listener.layerSelectionChanged(this);
		}
		geoview.enableControls();
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
				try {
					layer.open();
					layer.addLayerListenerRecursively(openerListener);
				} catch (LayerException ex) {
					PluginManager.error("Cannot open layer: " + layer.getName()
							+ ". The layer is removed from view.", ex);
					try {
						layer.getParent().remove(layer);
					} catch (LayerException e1) {
						PluginManager.error("Cannot remove layer: "
								+ layer.getName(), ex);
					}
				}
			}
		}

		public void layerRemoved(LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				layer.removeLayerListenerRecursively(openerListener);
				try {
					layer.close();
				} catch (LayerException e1) {
					PluginManager.warning("Cannot close layer: "
							+ layer.getName(), e1);
				}
			}
		}
	}

	public void saveStatus(File file) throws PersistenceException {
		org.orbisgis.geoview.persistence.ViewContext vc = new org.orbisgis.geoview.persistence.ViewContext();
		for (ILayer selected : selectedLayers) {
			SelectedLayer sl = new SelectedLayer();
			sl.setName(selected.getName());
			vc.getSelectedLayer().add(sl);
		}
		LayerType xmlRootLayer = root.getStatus();
		vc.setLayerCollection((LayerCollectionType) xmlRootLayer);

		try {
			JAXBContext jc = JAXBContext.newInstance(
					"org.orbisgis.geoview.persistence", this.getClass()
							.getClassLoader());
			jc.createMarshaller().marshal(vc, new PrintWriter(file));
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot save view context", e);
		} catch (FileNotFoundException e) {
			throw new PersistenceException("Cannot write the file: " + file);
		}

	}

	public void loadStatus(File file) throws PersistenceException {
		try {
			JAXBContext jc = JAXBContext.newInstance(
					"org.orbisgis.geoview.persistence", this.getClass()
							.getClassLoader());
			org.orbisgis.geoview.persistence.ViewContext viewContext = (org.orbisgis.geoview.persistence.ViewContext) jc
					.createUnmarshaller().unmarshal(file);

			LayerType layer = viewContext.getLayerCollection();
			ILayer layerCollection;
			try {
				layerCollection = LayerFactory.createLayer(layer);
				for (int i = 0; i < layerCollection.getLayerCount(); i++) {
					ILayer newLayer = layerCollection.getLayer(i);
					try {
						newLayer.setVisible(false);
						root.addLayer(newLayer);
					} catch (LayerException e) {
						PluginManager.error("Cannot add layer to collection: "
								+ newLayer.getName(), e);
					} catch (CRSException e) {
						PluginManager.error("Cannot add layer to collection: "
								+ newLayer.getName(), e);
					}
				}
			} catch (LayerException e1) {
				PluginManager.error("Cannot recover layer tree", e1);
			}

			List<SelectedLayer> selectedLayerList = viewContext
					.getSelectedLayer();
			final ArrayList<ILayer> selected = new ArrayList<ILayer>();
			for (final SelectedLayer selectedLayer : selectedLayerList) {
				LayerCollection.processLayersNodes(root, new ILayerAction() {

					public void action(ILayer layer) {
						if (selectedLayer.getName().equals(layer.getName())) {
							selected.add(layer);
							return;
						}
					}

				});
			}
			setSelectedLayers(selected.toArray(new ILayer[0]));
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot save view context", e);
		}

	}
}