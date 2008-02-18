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
package org.orbisgis.geoview;

import java.awt.Image;
import java.awt.geom.Rectangle2D;

import org.orbisgis.IProgressMonitor;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerCollection;
import org.orbisgis.geoview.layerModel.LayerCollectionEvent;
import org.orbisgis.geoview.layerModel.LayerListener;
import org.orbisgis.geoview.layerModel.LayerListenerEvent;
import org.orbisgis.geoview.renderer.Renderer;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.background.DefaultProcessId;
import org.orbisgis.pluginManager.background.LongProcess;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class OGMapControlModel implements MapControlModel {

	private MapControl mapControl;

	private ModelLayerListener layerListener;

	private ILayer root;

	public OGMapControlModel(ILayer root) {
		this.root = root;
		layerListener = new ModelLayerListener();
		root.addLayerListenerRecursively(layerListener);
	}

	public void setMapControl(MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public MapControl getMapControl() {
		return mapControl;
	}

	public void draw(final Image image) {
		Drawer d = new Drawer(image);
		PluginManager.backgroundOperation(new DefaultProcessId(
				"org.orbisgis.geoview.MapControl"), d);
	}

	public Rectangle2D getMapArea() {
		final LayerAction la = new LayerAction();
		LayerCollection.processLayersLeaves(root, la);
		final Envelope globalEnv = la.getGlobalEnvelope();
		return (null == globalEnv) ? null : new Rectangle2D.Double(globalEnv
				.getMinX(), globalEnv.getMinY(), globalEnv.getWidth(),
				globalEnv.getHeight());
	}

	private class ModelLayerListener implements LayerListener {
		public void layerAdded(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				layer.addLayerListenerRecursively(this);
				if (mapControl.getAdjustedExtent() == null) {
					final Envelope e = layer.getEnvelope();
					if (e != null) {
						final Rectangle2D.Double newExtent = new Rectangle2D.Double(
								e.getMinX(), e.getMinY(), e.getWidth(), e
										.getHeight());

						mapControl.setExtent(newExtent);
					}
				} else {
					mapControl.drawMap();
				}
			}
		}

		public void layerMoved(LayerCollectionEvent listener) {
			mapControl.drawMap();
		}

		public void layerRemoved(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				layer.removeLayerListenerRecursively(this);
				mapControl.drawMap();
			}
		}

		public void nameChanged(LayerListenerEvent e) {
		}

		public void visibilityChanged(LayerListenerEvent e) {
			mapControl.drawMap();
		}

		public void styleChanged(LayerListenerEvent e) {
			mapControl.drawMap();
		}
	}

	public ILayer getLayers() {
		return root;
	}

	public class Drawer implements LongProcess {

		private Image image;

		public Drawer(Image image) {
			this.image = image;
		}

		public String getTaskName() {
			return "Drawing";
		}

		public void run(IProgressMonitor pm) {
			try {
				Rectangle2D adjustedExtent = mapControl.getAdjustedExtent();
				final Envelope env = new Envelope(new Coordinate(adjustedExtent
						.getMinX(), adjustedExtent.getMinY()), new Coordinate(
						adjustedExtent.getMaxX(), adjustedExtent.getMaxY()));

				Renderer renderer = new Renderer();
				renderer.draw(image, env, getLayers(), pm);

			} catch (RuntimeException e) {
				throw e;
			} catch (Error e) {
				throw e;
			} finally {
				getMapControl().drawFinished();
			}
		}
	}
}