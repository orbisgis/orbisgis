/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.renderer;

import ij.process.ColorProcessor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.IndexException;
import org.gdms.driver.DriverException;


import org.gvsig.remoteClient.exceptions.ServerErrorException;
import org.gvsig.remoteClient.exceptions.WMSException;
import org.gvsig.remoteClient.wms.WMSStatus;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.WMSConnection;
import org.orbisgis.core.map.MapTransform;


import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import java.util.ArrayList;
import java.util.HashMap;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.orbisgis.core.renderer.se.FeatureTypeStyle;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;

public class Renderer {

	private static Logger logger = Logger.getLogger(Renderer.class.getName());

	private Iterator<Integer> getFeatureIdInExtent(MapTransform mt,
			SpatialDataSourceDecorator sds,
			IProgressMonitor pm) throws DriverException {

		sds.open();
		// TODO dont execute the query if mt.getAdjustedExtent > sds.getFullExtent()
		DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(
				mt.getAdjustedExtent(), sds.getSpatialFieldName());

		try {
			if (sds.getDataSourceFactory().getIndexManager().getIndex(sds.getName(), sds.getSpatialFieldName()) == null) {
				pm.startTask("Build spatial index");
				sds.getDataSourceFactory().getIndexManager().buildIndex(sds.getName(), sds.getSpatialFieldName(), pm);
				pm.endTask();
			}
		} catch (NoSuchTableException ex) {
			java.util.logging.Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IndexException ex) {
			java.util.logging.Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
		}

		Iterator<Integer> queryIndex = sds.queryIndex(query);
		sds.close();
		return queryIndex;
	}

	/**
	 * This method return a new data source containing only features which are
	 * in the current map extent
	 * The returned dataSource is a copy containing a special PK field (fieldID = 0)
	 * which contains the initial index of the feature is sds
	 *
	 * The returned data source must be purged by the caller
	 *
	 * @param mt the current map transform
	 * @param sds the layer to render
	 * @return
	 */
	private SpatialDataSourceDecorator getNewSdsPkFeatureInExtent(MapTransform mt, SpatialDataSourceDecorator sds, IProgressMonitor pm) {
		/*
		if (mt.getAdjustedExtent() > sds.getFullExtent()){
		}
		 */

		Envelope extent = mt.getAdjustedExtent();

		try {
			String query = "select autonumeric()-1 as pk, * from " + sds.getName();

			// TODO FIND A WAY TO AVOID THIS DATASOURCE CREATION !
			DataSource dsWithPk = sds.getDataSourceFactory().getDataSourceFromSQL(query);

			System.out.println("DataSource With PK: " + dsWithPk.getName());
			SpatialDataSourceDecorator newSds = null;

			// Test if the current map extent contains the layer full extent,
			// we don't need to execute the query !
			if (!extent.contains(sds.getFullExtent())) {
				// fetch only feature in the current map extent
				query = "SELECT * from " + dsWithPk.getName()
						+ " WHERE ST_Intersects(ST_GeomFromText('POLYGON(("
						+ extent.getMinX() + " " + extent.getMinY() + ","
						+ extent.getMinX() + " " + extent.getMaxY() + ","
						+ extent.getMaxX() + " " + extent.getMaxY() + ","
						+ extent.getMaxX() + " " + extent.getMinY() + ","
						+ extent.getMinX() + " " + extent.getMinY() + "))'), " + sds.getSpatialFieldName() + ")";
				newSds = new SpatialDataSourceDecorator(sds.getDataSourceFactory().getDataSourceFromSQL(query));
				System.out.println("SpatialDataSource with feature in extent : " + newSds.getName());
			} else {
				// Return the whole layer with the special PK field (fieldId = 0)
				newSds = new SpatialDataSourceDecorator(dsWithPk);
				System.out.println("DataSource with all features: " + newSds.getName());
			}

			return newSds;
		} catch (Exception e) {
			// Query failed
			return null;
		}
	}

	/**
	 * Draws the content of the Vector Layer
	 *
	 * @param g2
	 *            Object to draw to
	 * @param width
	 *            Width of the generated image
	 * @param height
	 *            Height of the generated image
	 * @param extent
	 *            Extent of the data to draw
	 * @param layer
	 *            Source of information
	 * @param pm
	 *            Progress monitor to report the status of the drawing
	 * @return the number of rendered objects
	 */
	public int drawVector(Graphics2D g2,
			MapTransform mt, ILayer layer, IProgressMonitor pm) {

		int layerCount = 0;
		try {
			long tV1 = System.currentTimeMillis();
			SpatialDataSourceDecorator sds = layer.getDataSource();
			// Extract into drawSeLayer method !
			FeatureTypeStyle fts = layer.getFeatureTypeStyle();

			fts.hardSetSymbolizerLevel();

			ArrayList<Symbolizer> symbs = new ArrayList<Symbolizer>();
			// i.e. TextSymbolizer are always drawn above all other layer !!
			ArrayList<Symbolizer> overlays = new ArrayList<Symbolizer>();

			// Standard rules (with filter) matching current domain
			ArrayList<Rule> rList = new ArrayList<Rule>();

			// Rule which match current domain and ElseFilter
			ArrayList<Rule> fRList = new ArrayList<Rule>();

			// fetch symbolizers and rules
			fts.getSymbolizers(mt, symbs, overlays, rList, fRList);

			// Create new dataSource with only feature in current extent
			SpatialDataSourceDecorator featureInExtent = this.getNewSdsPkFeatureInExtent(mt, sds, pm);

			if (featureInExtent != null) {

				HashSet<Integer> allFid = new HashSet<Integer>();
				HashSet<Integer> elseFid = new HashSet<Integer>();

				featureInExtent.open();
				/*
				 * Extract features id
				 * Hash sets contain features in current extent
				 * elseFid will be used for feature which doesn't match any rule
				 */
				for (int i = 0; i < featureInExtent.getRowCount(); i++) {
					Integer index = featureInExtent.getFieldValue(i, 0).getAsInt();
					allFid.add(index);
					elseFid.add(index);
				}

				/*
				 * Assign features id to rules
				 */
				HashMap<Rule, HashSet<Integer>> rulesFid = new HashMap<Rule, HashSet<Integer>>();
				for (Rule r : rList) {

					SpatialDataSourceDecorator filteredDs = r.getFilteredDataSource(featureInExtent);

					boolean newDataSource = !filteredDs.getName().equalsIgnoreCase(featureInExtent.getName());

					if (newDataSource) {
						filteredDs.open();
					}

					HashSet<Integer> fids = new HashSet<Integer>();

					for (int i = 0; i < filteredDs.getRowCount(); i++) {
						Integer index = filteredDs.getFieldValue(i, 0).getAsInt();
						fids.add(index);
						/* Every feature that match a rule is removed from elsefid set*/
						elseFid.remove(index);
					}
					if (newDataSource) {
						filteredDs.close();
						try {
							// Once we have fids into hash set, we doesn't need filteredDs anymore
							filteredDs.getDataSourceFactory().executeSQL("DROP TABLE " + filteredDs.getName() + " PURGE;");
						} catch (ExecutionException ex) {
							java.util.logging.Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, "Could nod purge internal datasource!", ex);
						}
					}

					rulesFid.put(r, fids);
				}

				featureInExtent.close();
				try {
					featureInExtent.getDataSourceFactory().executeSQL("DROP TABLE " + featureInExtent.getName() + " PURGE;");
				} catch (ExecutionException ex) {
					java.util.logging.Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, "Could not purge 'inExtent' internal DataSource!", ex);
				}

				// ElseFilter
				// Assign features which doesn't match any rule to FallbackRules
				for (Rule elseR : fRList) {
					rulesFid.put(elseR, elseFid);
				}

				HashSet<Integer> selected = new HashSet<Integer>();
				int[] selection = layer.getSelection();

				/* Populate selected fid with only fid within extent */
				for (int f_i = 0; f_i < selection.length; f_i++) {
					if (allFid.contains(selection[f_i])) {
						selected.add(selection[f_i]);
					}
				}

				long tV2 = System.currentTimeMillis();
				System.out.println("Filtering done in " + (tV2 - tV1) + "[ms]");

				//
				// And now, features will be rendered
				//

				// How many object to process ?
				// - e.g. 1 feature with 3 effective symbolizers is count as 3 objects
				long total = 0;
				for (Symbolizer s : symbs) {
					total += rulesFid.get(s.getRule()).size();
				}

				for (Symbolizer s : symbs) {
					pm.startTask("Drawing " + layer.getName() + " (" + s.getName() + ")");
					Iterator<Integer> featIt = rulesFid.get(s.getRule()).iterator();

					Integer fid = 0;

					long tf1 = System.currentTimeMillis();
					while (featIt.hasNext()) {
						if (layerCount % 1000 == 0) {
							if (pm.isCancelled()) {
								return layerCount;
							}
						}

						fid = featIt.next();
						s.draw(g2, sds.getFeature(fid), selected.contains(fid), mt);
						pm.progressTo((int) (100 * ++layerCount / total));
					}
					long tf2 = System.currentTimeMillis();
					System.out.println("Level done in " + (tf2 - tf1) + "[ms]");
					pm.endTask();
				}
				long tV3 = System.currentTimeMillis();
				System.out.println("Rendering done :" + (tV3 - tV2) + "[ms] (" + layerCount + "objects)");
			}

		} catch (Exception ex) {
			java.util.logging.Logger.getLogger("Could not draw " + layer.getName()).log(Level.SEVERE, "Error while drawing " + layer.getName(), ex);
		}
		return layerCount;
	}

	public void draw(Graphics2D g2dMap, int width, int height,
			Envelope extent, ILayer layer, IProgressMonitor pm) {
		MapTransform mt = new MapTransform();
		mt.resizeImage(width, height);
		mt.setExtent(extent);

		this.draw(mt, g2dMap, width, height, layer, pm);
	}

	/**
	 * Draws the content of the layer in the specified graphics
	 *
	 * @param g2
	 *            Object to draw to
	 * @param width
	 *            Width of the generated image
	 * @param height
	 *            Height of the generated image
	 * @param extent
	 *            Extent of the data to draw
	 * @param layer
	 *            Source of information
	 * @param pm
	 *            Progress monitor to report the status of the drawing
	 */
	public void draw(MapTransform mt,
			ILayer layer, IProgressMonitor pm) {

		BufferedImage image = mt.getImage();
		Graphics2D g2 = image.createGraphics();



		this.draw(mt, g2, image.getWidth(), image.getHeight(), layer, pm);


	}

	/**
	 * Draws the content of the layer in the specified graphics
	 *
	 * @param g2
	 *            Object to draw to
	 * @param width
	 *            Width of the generated image
	 * @param height
	 *            Height of the generated image
	 * @param extent
	 *            Extent of the data to draw
	 * @param layer
	 *            Source of information
	 * @param pm
	 *            Progress monitor to report the status of the drawing
	 */
	public void draw(MapTransform mt, Graphics2D g2, int width, int height,
			ILayer layer, IProgressMonitor pm) {

		g2.setRenderingHints(mt.getRenderingHints());

		Envelope extent = mt.getAdjustedExtent();

		int count = 0;

		ILayer[] layers;

		ArrayList<Symbolizer> overlay = new ArrayList<Symbolizer>();

		if (layer.acceptsChilds()) {
			layers = layer.getLayersRecursively();
		} else {
			layers = new ILayer[]{layer};
		}

		long total1 = System.currentTimeMillis();

		for (int i = layers.length - 1; i
				>= 0; i--) {
			if (pm.isCancelled()) {
				break;
			} else {
				layer = layers[i];
				if (layer.isVisible() && extent.intersects(layer.getEnvelope())) {
					logger.debug("Drawing " + layer.getName());
					long t1 = System.currentTimeMillis();
					if (layer.isWMS()) {
						System.out.println("   -> WMS Layer...");
						// Iterate over next layers to make only one call to the
						// WMS server
						WMSStatus status = (WMSStatus) layer.getWMSConnection().getStatus().clone();
						if (i > 0) {
							for (int j = i - 1;
									(j >= 0)
									&& (layers[j].isWMS() || !layers[j].isVisible()); j--) {
								if (layers[j].isVisible()) {
									i = j;
									if (sameServer(layer, layers[j])) {
										Vector<?> layerNames = layers[j].getWMSConnection().getStatus().getLayerNames();
										for (Object layerName : layerNames) {
											status.addLayerName(layerName.toString());
										}
									}
								}
							}
						}
						WMSConnection conn = new WMSConnection(layer.getWMSConnection().getClient(), status);
						drawWMS(
								g2, width, height, extent, conn);
					} else {
						SpatialDataSourceDecorator sds = layer.getDataSource();
						if (sds != null) {
							try {
								if (sds.isDefaultVectorial()) {
									count += this.drawVector(g2, mt, layer, pm);
								} else if (sds.isDefaultRaster()) {
									logger.warn("Raster Not Yet supported => Not drawn: " + layer.getName());
								} else {
									logger.warn("Not drawn: " + layer.getName());
								}
							} catch (DriverException ex) {
								java.util.logging.Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
							}
							pm.progressTo(100
									- (100 * i) / layers.length);


						}


					}
					long t2 = System.currentTimeMillis();
					logger.info("Rendering time:" + (t2 - t1) + " for " + count + " features");


				}
			}
		}

		long total2 = System.currentTimeMillis();
		logger.info("Total rendering time:" + (total2 - total1));



	}

	private boolean sameServer(ILayer layer, ILayer layer2) {
		return layer.getWMSConnection().getClient().getHost().equals(
				layer2.getWMSConnection().getClient().getHost());
	}

	private void drawWMS(Graphics2D g2, int width, int height, Envelope extent,
			WMSConnection connection) {
		WMSStatus status = connection.getStatus();
		status.setWidth(width);
		status.setHeight(height);
		status.setExtent(new Rectangle2D.Double(extent.getMinX(), extent.getMinY(), extent.getWidth(), extent.getHeight()));

		try {
			File file = connection.getClient().getMap(status, null);
			BufferedImage image = ImageIO.read(file);
			g2.drawImage(image, 0, 0, null);
		} catch (WMSException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot get WMS image", e);
		} catch (ServerErrorException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot get WMS image", e);
		} catch (IOException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot get WMS image", e);
		}
	}

	/**
	 * Draws the content of the layer in the specified image.
	 *
	 * @param img
	 *            Image to draw the data
	 * @param extent
	 *            Extent of the data to draw in the image
	 * @param layer
	 *            Layer to get the information
	 * @param pm
	 *            Progress monitor to report the status of the drawing
	 */
	public void draw(BufferedImage img, Envelope extent, ILayer layer,
			IProgressMonitor pm) {
		MapTransform mt = new MapTransform();
		mt.setExtent(extent);
		mt.setImage(img);
		draw(mt, layer, pm);
	}

	/*
	private Iterator<Integer> getIterator(Envelope adjustedExtent,
	SpatialDataSourceDecorator sds) throws DriverException {
	if (adjustedExtent.equals(sds.getFullExtent())) {
	return new DataSourceIterator(sds);
	} else {
	DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(
	adjustedExtent, sds.getMetadata().getFieldName(
	sds.getSpatialFieldIndex()));
	return sds.queryIndex(query);
	}

	}

	private boolean validScale(MapTransform mt, Legend legend) {
	return (mt.getScaleDenominator() > legend.getMinScale())
	&& (mt.getScaleDenominator() < legend.getMaxScale());
	}
	 */
	/**
	 * For geometry collections we need to filter the symbol composite before
	 * drawing
	 *
	 * @param mt
	 * @param g2
	 * @param sym
	 * @param g
	 * @param permission
	 * @throws DriverException

	private Envelope drawGeometryCollection(MapTransform mt, Graphics2D g2,
	Symbol sym, Geometry g, DefaultRendererPermission permission)
	throws DriverException {
	if (g.getGeometryType().equals("GeometryCollection")) {
	Envelope ret = null;


	for (int j = 0; j
	< g.getNumGeometries(); j++) {
	Geometry childGeom = g.getGeometryN(j);
	Envelope area = drawGeometryCollection(mt, g2, sym, childGeom,
	permission);
	if (ret == null) {
	ret = area;
	} else {
	ret.expandToInclude(area);
	}
	}

	return ret;
	} else {
	sym = RenderUtils.buildSymbolToDraw(sym, g);
	if (sym != null) {
	return sym.draw(g2, g, mt, permission);
	} else {
	return null;
	}
	}
	}
	 */
	public void draw(BufferedImage img, Envelope extent, ILayer layer) {
		draw(img, extent, layer, new NullProgressMonitor());










	}

	private class DefaultRendererPermission implements RenderPermission {

		private Quadtree quadtree;
		private Envelope drawExtent;

		public DefaultRendererPermission(Envelope drawExtent) {
			this.drawExtent = drawExtent;
			this.quadtree = new Quadtree();
		}

		public void addUsedArea(Envelope area) {
			quadtree.insert(area, area);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean canDraw(Envelope area) {
			List<Envelope> list = quadtree.query(area);
			for (Envelope envelope : list) {
				if ((envelope.intersects(area)) || envelope.contains(area)) {
					return false;
				}
			}

			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Geometry getValidGeometry(Geometry geometry, double distance) {
			List<Envelope> list = quadtree.query(geometry.getEnvelopeInternal());
			GeometryFactory geometryFactory = new GeometryFactory();
			for (Envelope envelope : list) {
				geometry = geometry.difference(geometryFactory.toGeometry(
						envelope).buffer(distance, 1));
			}
			geometry = geometry.intersection(geometryFactory.toGeometry(drawExtent));

			if (geometry.isEmpty()) {
				return null;
			} else {
				return geometry;
			}
		}
	}

	/**
	 * Method to change bands order only on the BufferedImage.
	 *
	 * @param bufferedImage
	 * @return new bufferedImage
	 */
	public Image invertRGB(BufferedImage bufferedImage, String bands) {

		ColorModel colorModel = bufferedImage.getColorModel();

		if (colorModel instanceof DirectColorModel) {
			DirectColorModel directColorModel = (DirectColorModel) colorModel;
			int red = directColorModel.getRedMask();
			int blue = directColorModel.getBlueMask();
			int green = directColorModel.getGreenMask();
			int alpha = directColorModel.getAlphaMask();

			int[] components = new int[3];
			bands = bands.toLowerCase();
			components[0] = getComponent(bands.charAt(0), red, green, blue);
			components[1] = getComponent(bands.charAt(1), red, green, blue);
			components[2] = getComponent(bands.charAt(2), red, green, blue);

			directColorModel = new DirectColorModel(32, components[0],
					components[1], components[2], alpha);
			ColorProcessor colorProcessor = new ColorProcessor(bufferedImage);
			colorProcessor.setColorModel(directColorModel);

			return colorProcessor.createImage();
		}
		return bufferedImage;
	}

	/**
	 * Gets the component specified by the char between the int components
	 * passed as parameters in red, green blue
	 *
	 * @param rgbChar
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	private int getComponent(char rgbChar, int red, int green, int blue) {
		if (rgbChar == 'r') {
			return red;




		} else if (rgbChar == 'g') {
			return green;




		} else if (rgbChar == 'b') {
			return blue;




		} else {
			throw new IllegalArgumentException(
					"The RGB code doesn't contain RGB codes");



		}
	}
	/**
	 * Apply some rendering rules Look at rendering configuration panel.
	 *
	 * @param g2

	private void setHints(Graphics2D g2) {

	Properties systemSettings = System.getProperties();

	String antialiasing = systemSettings.getProperty(RenderingConfiguration.SYSTEM_ANTIALIASING_STATUS);
	String composite = systemSettings.getProperty(RenderingConfiguration.SYSTEM_COMPOSITE_STATUS);



	if (antialiasing != null || composite != null) {
	if (antialiasing.equals("true")) {
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	RenderingHints.VALUE_ANTIALIAS_ON);


	}

	if (composite.equals("true")) {
	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC);
	ac = AlphaComposite.getInstance(
	AlphaComposite.SRC_OVER,
	new Float(
	systemSettings.getProperty(RenderingConfiguration.SYSTEM_COMPOSITE_VALUE)));
	g2.setComposite(ac);

	}
	}

	}
	 */
}
