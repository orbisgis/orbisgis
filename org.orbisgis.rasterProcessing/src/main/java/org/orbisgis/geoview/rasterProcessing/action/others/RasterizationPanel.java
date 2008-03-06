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
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.grap.archive.GetGeoRasterInformation;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.others.Orientations;
import org.grap.processing.operation.others.RasteringMode;
import org.grap.processing.operation.others.Rasterization;
import org.grap.processing.operation.others.Shadows;
import org.grap.utilities.PixelsUtil;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.rasterProcessing.sif.RasterExtendPanel;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;
import org.orbisgis.geoview.sif.RasterLayerCombo;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;
import org.sif.multiInputPanel.ComboBoxChoice;
import org.sif.multiInputPanel.DoubleType;
import org.sif.multiInputPanel.MultiInputPanel;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public class RasterizationPanel implements
		org.orbisgis.geoview.views.toc.ILayerAction {

	public static final String DIALOG_ID = "org.orbisgis.geoview.rasterProcessing.Rasterization";

	public final static Map<String, RasteringMode> rasteringMode = new HashMap<String, RasteringMode>();
	static {
		rasteringMode.put("FILL", RasteringMode.FILL);
		rasteringMode.put("DRAW", RasteringMode.DRAW);

	}


	private RasteringMode mode;

	private Double value;

	private GeoRaster geoRasterSrc;

	public boolean accepts(ILayer layer) {
		
		
		return layer instanceof VectorLayer;
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount >= 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
		showDialogInput(view,((VectorLayer) resource) );
		
		AffineTransform affineTransform = new AffineTransform();

		if (null != mode) {
			try {

				final DataSource ds = ((VectorLayer) resource).getDataSource();

				SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
						ds);

				long rowCount = sds.getRowCount();
				ArrayList<Roi> rois = new ArrayList<Roi>();

				for (int i = 0; i < rowCount; i++) {

					Geometry geom = sds.getGeometry(i);

					if (geom instanceof LineString) {
						LineString ls = (LineString) geom;
						rois.add(new ShapeRoi(new LiteShape(PixelsUtil.toPixel(
								geoRasterSrc, ls), affineTransform, false)));
					} else if (geom instanceof MultiLineString) {
						MultiLineString mls = (MultiLineString) geom;

						rois.add(new ShapeRoi(new LiteShape(PixelsUtil.toPixel(
								geoRasterSrc, mls), affineTransform, false)));
					}

				}

				if (rois.size() > 0) {

					final Operation rasterizing = new Rasterization(mode, rois,
							value);

					GeoRaster grResult = geoRasterSrc.doOperation(rasterizing);

					// save the computed GeoRaster in a tempFile
					final DataSourceFactory dsf = OrbisgisCore.getDSF();
					final String tempFile = dsf.getTempFile() + ".tif";
					grResult.save(tempFile);

					// populate the GeoView TOC with a new RasterLayer
					final ILayer newLayer = LayerFactory
							.createRasterLayer(new File(tempFile));
					view.getViewContext().getLayerModel().addLayer(newLayer);

				}

				else {
					
					JOptionPane.showMessageDialog(null, "Rasterizing works only with line or multiline.");
				}

			} catch (GeoreferencingException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (IOException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (OperationException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (LayerException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (CRSException e) {
				PluginManager.error("Cannot compute " + getClass().getName()
						+ ": " + resource.getName(), e);
			} catch (DriverException e) {
				PluginManager.error("Cannot read the datasource", e);
			}

		}
	}

	
	


	public void executeAll(GeoView2D view, ILayer[] layers) {
	}

	private void showDialogInput(GeoView2D view, VectorLayer layer) {

		
		RasterExtendPanel rasterExtendPanel = new RasterExtendPanel(view,layer );
		
		if (UIFactory.showDialog(rasterExtendPanel)) {

			mode = rasteringMode.get(rasterExtendPanel.getInput("mode"));
			value = new Double(rasterExtendPanel.getInput("AddValue"));
			final RasterLayer raster1 = (RasterLayer) view.getViewContext()
			.getLayerModel().getLayerByName(rasterExtendPanel.getInput("source1"));
			geoRasterSrc = raster1.getGeoRaster();
		}

	}


}