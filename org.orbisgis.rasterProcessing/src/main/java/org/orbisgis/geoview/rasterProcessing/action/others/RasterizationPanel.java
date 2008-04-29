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
package org.orbisgis.geoview.rasterProcessing.action.others;

import ij.gui.Line;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.others.RasteringMode;
import org.grap.processing.operation.others.Rasterization;
import org.grap.utilities.PixelsUtil;
import org.orbisgis.IProgressMonitor;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.rasterProcessing.sif.RasterExtendPanel;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.background.BlockingBackgroundJob;
import org.sif.UIFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

public class RasterizationPanel implements
		org.orbisgis.geoview.views.toc.ILayerAction {
	public static final String DIALOG_ID = "org.orbisgis.geoview.rasterProcessing.Rasterization";


	private RasteringMode mode;
	private Double value;
	private GeoRaster geoRasterSrc;

	public boolean accepts(ILayer layer) {
		try {
			return layer.isVectorial();
		} catch (DriverException e) {
			return false;
		}
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount >= 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
		try {
			showDialogInput(view, resource);
		} catch (DriverException e) {
			PluginManager.error("Unable to access the raster", e);
		}

		final DataSource ds = resource.getDataSource();
		final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				ds);
		PluginManager.backgroundOperation(new ExecuteRasterProcess(view, sds));
	}

	public void executeAll(GeoView2D view, ILayer[] layers) {
	}

	private void showDialogInput(GeoView2D view, ILayer layer)
			throws DriverException {
		final RasterExtendPanel rasterExtendPanel = new RasterExtendPanel(view,
				layer.getEnvelope());

		if (UIFactory.showDialog(rasterExtendPanel)) {
			mode = RasteringMode.DRAW;
			value = new Double(rasterExtendPanel.getInput("AddValue"));
			final ILayer raster1 = view.getViewContext().getLayerModel()
					.getLayerByName(rasterExtendPanel.getInput("source1"));
			geoRasterSrc = raster1.getRaster();
		}
	}

	private class ExecuteRasterProcess implements BlockingBackgroundJob {
		private SpatialDataSourceDecorator sds;
		private GeoView2D view;

		public ExecuteRasterProcess(GeoView2D view,
				SpatialDataSourceDecorator sds) {
			this.view = view;
			this.sds = sds;
		}

		public String getTaskName() {
			return "Executing raster processing";
		}

		public void run(IProgressMonitor pm) {
			final AffineTransform affineTransform = new AffineTransform();
			final long rowCount;
			try {
				rowCount = sds.getRowCount();

				final ArrayList<Roi> rois = new ArrayList<Roi>();
				for (int i = 0; i < rowCount; i++) {
					final Geometry geom = sds.getGeometry(i);
					if (geom instanceof LineString) {
						final LineString ls = (LineString) geom;
						rois.add(new ShapeRoi(new LiteShape(PixelsUtil.toPixel(
								geoRasterSrc, ls), affineTransform, false)));
					} else if (geom instanceof MultiLineString) {
						final MultiLineString mls = (MultiLineString) geom;
						if ((null != mls) && (!mls.isEmpty())) {
							final MultiLineString mlsPix = PixelsUtil.toPixel(
									geoRasterSrc, mls);
							if (mlsPix.getEnvelope() instanceof Point) {
								rois.add(new PointRoi((int) mlsPix
										.getCoordinates()[0].x, (int) mlsPix
										.getCoordinates()[0].y));
							} else if (mlsPix.getEnvelope() instanceof LineString) {
								rois.add(new Line(mlsPix.getCoordinates()[0].x,
										mlsPix.getCoordinates()[0].y, mlsPix
												.getCoordinates()[1].x, mlsPix
												.getCoordinates()[1].y));
							} else {
								rois.add(new ShapeRoi(new LiteShape(mlsPix,
										affineTransform, true)));
							}
						}
					}
				}

				if (rois.size() > 0) {
					final Operation rasterizing = new Rasterization(mode, rois,
							value);
					final GeoRaster grResult = geoRasterSrc
							.doOperation(rasterizing);
					// save the computed GeoRaster in a tempFile
					final DataSourceFactory dsf = OrbisgisCore.getDSF();
					final String tempFile = dsf.getTempFile() + ".tif";
					grResult.save(tempFile);

					// populate the GeoView TOC with a new RasterLayer
					final ILayer newLayer = LayerFactory.createLayer(new File(
							tempFile));
					view.getViewContext().getLayerModel().insertLayer(newLayer,
							0);

					pm.progressTo(100);
				} else {
					JOptionPane.showMessageDialog(null,
							"Rasterizing works only with line and multiline.");
				}
			} catch (DriverException e) {
				PluginManager.error("Cannot access the spatial data source", e);
			} catch (IOException e) {
				PluginManager
						.error(
								"Cannot save (or register in the TOC) the resulting GeoRaster",
								e);
			} catch (OperationException e) {
				PluginManager.error(
						"Error in the Grap Rasterization operation", e);
			} catch (GeoreferencingException e) {
				PluginManager.error(
						"Georeferencing error while handling the ReoRaster", e);
			} catch (LayerException e) {
				PluginManager
						.error(
								"Cannot register the resulting GeoRaster in the TOC",
								e);
			} catch (CRSException e) {
				PluginManager
						.error(
								"CRS error while registering the resulting GeoRaster in the TOC",
								e);
			} catch (DriverLoadException e) {
				PluginManager.error(
						"Cannot register the resulting GeoRaster in the TOC", e);
			} catch (NoSuchTableException e) {
				PluginManager.error(
						"Cannot register the resulting GeoRaster in the TOC", e);
			} catch (DataSourceCreationException e) {
				PluginManager.error(
						"Cannot register the resulting GeoRaster in the TOC", e);
			}
		}
	}
}