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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.gvsig.remoteClient.exceptions.ServerErrorException;
import org.gvsig.remoteClient.exceptions.WMSException;
import org.gvsig.remoteClient.wms.WMSStatus;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.WMSConnection;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.renderer.legend.RenderException;
import org.orbisgis.core.renderer.symbol.RenderUtils;
import org.orbisgis.core.renderer.symbol.SelectionSymbol;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.ui.editors.map.tool.Rectangle2DDouble;
import org.orbisgis.core.ui.plugins.orbisgisFrame.configuration.RenderingConfigurationConstants;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import java.util.Vector;
import org.gdms.data.DataSource;
import org.gdms.data.indexes.FullIterator;

public class Renderer {

        private static Logger logger = Logger.getLogger(Renderer.class.getName());
        public GeometryFactory gf = new GeometryFactory();

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
        public void draw(Graphics2D g2, int width, int height, Envelope extent,
                ILayer layer, ProgressMonitor pm) {
                setHints(g2);
                MapTransform mt = new MapTransform();
                mt.resizeImage(width, height);
                mt.setExtent(extent);
                ILayer[] layers;
                if (layer.acceptsChilds()) {
                        layers = layer.getLayersRecursively();
                } else {
                        layers = new ILayer[]{layer};
                }

                long total1 = System.currentTimeMillis();
                DefaultRendererPermission permission = new DefaultRendererPermission(
                        extent);
                for (int i = layers.length - 1; i >= 0; i--) {
                        if (pm.isCancelled()) {
                                break;
                        } else {
                                layer = layers[i];
                                if (layer.isVisible() && extent.intersects(layer.getEnvelope())) {
                                        logger.debug(I18N.getString("orbisgis-core.org.orbisgis.renderer.drawing") + layer.getName()); //$NON-NLS-1$
                                        long t1 = System.currentTimeMillis();
                                        if (layer.isWMS()) {
                                                // Iterate over next layers to make only one call to the
                                                // WMS server
                                                WMSStatus status = (WMSStatus) layer.getWMSConnection().getStatus().clone();
                                                if (i > 0) {
                                                        for (int j = i - 1; (j >= 0)
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
                                                drawWMS(g2, width, height, extent, conn);
                                        } else {
                                                DataSource sds = layer.getDataSource();
                                                if (sds != null) {
                                                        try {
                                                                if (sds.isVectorial()) {
                                                                        drawVectorLayer(mt, layer, g2, width,
                                                                                height, extent, permission, pm);
                                                                } else if (sds.isRaster()) {
                                                                        try {
                                                                                drawRasterLayer(mt, layer, g2, width,
                                                                                        height, extent, pm);
                                                                        } catch (IOException e) {
                                                                                Services.getErrorManager().error(
                                                                                        I18N.getString("orbisgis-core.org.orbisgis.renderer.cannotDrawRaster") //$NON-NLS-1$
                                                                                        + layer.getName(), e);
                                                                        }
                                                                } else {
                                                                        logger.warn(I18N.getString("orbisgis-core.org.orbisgis.renderer.notDraw") //$NON-NLS-1$
                                                                                + layer.getName());
                                                                }
                                                        } catch (DriverException e) {
                                                                Services.getErrorManager().error(
                                                                        I18N.getString("orbisgis-core.org.orbisgis.renderer.cannotDraw") + layer.getName(), e); //$NON-NLS-1$
                                                        }
                                                        pm.progressTo(100 - (100 * i) / layers.length);
                                                }
                                        }
                                        long t2 = System.currentTimeMillis();
                                        logger.info(I18N.getString("orbisgis-core.org.orbisgis.renderer.renderingTime") + (t2 - t1)); //$NON-NLS-1$
                                }
                        }
                }

                long total2 = System.currentTimeMillis();
                logger.info(I18N.getString("orbisgis-core.org.orbisgis.renderer.totalRenderingTime") + (total2 - total1)); //$NON-NLS-1$
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
                                I18N.getString("orbisgis-core.org.orbisgis.renderer.cannotGetWMSImage"), e); //$NON-NLS-1$
                } catch (ServerErrorException e) {
                        Services.getService(ErrorManager.class).error(
                                I18N.getString("orbisgis-core.org.orbisgis.renderer.cannotGetWMSImage"), e); //$NON-NLS-1$
                } catch (IOException e) {
                        Services.getService(ErrorManager.class).error(
                                I18N.getString("orbisgis-core.org.orbisgis.renderer.cannotGetWMSImage"), e); //$NON-NLS-1$
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
                ProgressMonitor pm) {
                draw(img.createGraphics(), img.getWidth(), img.getHeight(), extent,
                        layer, pm);
        }

        private void drawRasterLayer(MapTransform mt, ILayer layer, Graphics2D g2,
                int width, int height, Envelope extent, ProgressMonitor pm)
                throws DriverException, IOException {
                logger.debug(I18N.getString("orbisgis-core.org.orbisgis.renderer.rasterEnvelope") + layer.getEnvelope()); //$NON-NLS-1$
                GraphicsConfiguration configuration = null;
                boolean isHeadLess = GraphicsEnvironment.isHeadless();
                if (!isHeadLess) {
                        configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
                }
                RasterLegend[] legends = layer.getRasterLegend();
                for (RasterLegend legend : legends) {
                        if (!validScale(mt, legend)) {
                                continue;
                        }
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                legend.getOpacity()));
                        for (int i = 0; i < layer.getDataSource().getRowCount(); i++) {
                                GeoRaster geoRaster = layer.getDataSource().getRaster(i);
                                Envelope layerEnvelope = geoRaster.getMetadata().getEnvelope();
                                BufferedImage layerImage;
                                if (isHeadLess) {
                                        layerImage = new BufferedImage(width, height,
                                                BufferedImage.TYPE_INT_ARGB);
                                } else {
                                        layerImage = configuration.createCompatibleImage(width,
                                                height, BufferedImage.TYPE_INT_ARGB);
                                }

                                // part or all of the GeoRaster is visible
                                Rectangle2DDouble layerPixelEnvelope = mt.toPixel(layerEnvelope);
                                Graphics2D gLayer = layerImage.createGraphics();
                                ColorModel cm = ((RasterLegend) legend).getColorModel();
                                if (cm == null) {
                                        cm = geoRaster.getDefaultColorModel();
                                }
                                Image dataImage = geoRaster.getImage(cm);
                                gLayer.drawImage(dataImage, (int) layerPixelEnvelope.getMinX(),
                                        (int) layerPixelEnvelope.getMinY(),
                                        (int) layerPixelEnvelope.getWidth() + 1,
                                        (int) layerPixelEnvelope.getHeight() + 1, null);
                                pm.startTask(I18N.getString("orbisgis-core.org.orbisgis.renderer.drawing") + layer.getName(), 1); //$NON-NLS-1$
                                String bands = ((RasterLegend) legend).getBands();
                                if (bands != null) {
                                        g2.drawImage(invertRGB(layerImage, bands), 0, 0, null);
                                } else {
                                        g2.drawImage(layerImage, 0, 0, null);
                                }
                                pm.endTask();
                        }
                }
        }

        private void drawVectorLayer(MapTransform mt, ILayer layer, Graphics2D g2,
                int width, int height, Envelope extent,
                DefaultRendererPermission permission, ProgressMonitor pm)
                throws DriverException {

                Legend[] legends = layer.getRenderingLegend();
                DataSource sds = layer.getDataSource();

                try {

                        HashSet<Integer> selected = new HashSet<Integer>();
                        int[] selection = layer.getSelection();

                        for (int i = 0; i < selection.length; i++) {
                                selected.add(selection[i]);
                        }
                        /*
                         * Don't check the extent because it's expensive in edition
                         */
                        for (Legend legend : legends) {
                                if (!validScale(mt, legend)) {
                                        continue;
                                }
                                if (!legend.isVisible()) {
                                        continue;
                                }

                                Iterator<Integer> it = new FullIterator(sds);


                                long rowCount = sds.getRowCount();
                                pm.startTask(I18N.getString("orbisgis-core.org.orbisgis.renderer.drawing") + layer.getName(), 100); //$NON-NLS-1$
                                int i = 0;
                                while (it.hasNext()) {
                                        Integer index = it.next();
                                        if (i / 1000 == i / 1000.0) {
                                                if (pm.isCancelled()) {
                                                        break;
                                                } else {
                                                        pm.progressTo((int) (100 * i / rowCount));
                                                }
                                        }
                                        i++;
                                        Symbol sym = legend.getSymbol(sds, index);
                                        if (sym != null) {
                                                Geometry g = sds.getGeometry(index);

                                                Envelope geomEnvelope = g.getEnvelopeInternal();

                                                // Do not display geom when the envelope is too small
                                                if (geomEnvelope.intersects(extent)) {

                                                        if (selected.contains(index)) {
                                                                Symbol derivedSymbol = new SelectionSymbol(
                                                                        Color.yellow, true, true);
                                                                if (derivedSymbol != null) {
                                                                        sym = derivedSymbol;
                                                                }
                                                        }
                                                        Envelope symbolEnvelope;
                                                        if (g.getGeometryType().equals(I18N.getString("orbisgis-core.org.orbisgis.renderer.geometryCollection"))) { //$NON-NLS-1$
                                                                symbolEnvelope = drawGeometryCollection(mt, g2,
                                                                        sym, g, permission);
                                                        } else {
                                                                symbolEnvelope = sym.draw(g2, g, mt, permission);
                                                        }
                                                        if (symbolEnvelope != null) {
                                                                permission.addUsedArea(symbolEnvelope);
                                                        }
                                                }

                                        }
                                }
                        }
                        pm.endTask();

                } catch (RenderException e) {
                        Services.getErrorManager().warning(
                                I18N.getString("orbisgis-core.org.orbisgis.renderer.cannotDrawLayer") + layer.getName(), e); //$NON-NLS-1$
                }
        }

        private boolean validScale(MapTransform mt, Legend legend) {
                return (mt.getScaleDenominator() > legend.getMinScale())
                        && (mt.getScaleDenominator() < legend.getMaxScale());
        }

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
         */
        private Envelope drawGeometryCollection(MapTransform mt, Graphics2D g2,
                Symbol sym, Geometry g, DefaultRendererPermission permission)
                throws DriverException {
                if (g.getGeometryType().equals(I18N.getString("orbisgis-core.org.orbisgis.renderer.geometryCollection"))) { //$NON-NLS-1$
                        Envelope ret = null;
                        for (int j = 0; j < g.getNumGeometries(); j++) {
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

        public void draw(BufferedImage img, Envelope extent, ILayer layer) {
                draw(img, extent, layer, new NullProgressMonitor());
        }

        private class DefaultRendererPermission implements RenderContext {

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
                                I18N.getString("orbisgis-core.org.orbisgis.renderer.cannotCreatRGBCodes")); //$NON-NLS-1$
                }
        }

        /**
         * Apply some rendering rules Look at rendering configuration panel.
         *
         * @param g2
         */
        private void setHints(Graphics2D g2) {

                Properties systemSettings = System.getProperties();

                String antialiasing = systemSettings.getProperty(RenderingConfigurationConstants.SYSTEM_ANTIALIASING_STATUS);
                String composite = systemSettings.getProperty(RenderingConfigurationConstants.SYSTEM_COMPOSITE_STATUS);

                if (antialiasing != null || composite != null) {
                        if (antialiasing.equals("true")) { //$NON-NLS-1$
                                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                        }

                        if (composite.equals("true")) { //$NON-NLS-1$
                                AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC);
                                ac = AlphaComposite.getInstance(
                                        AlphaComposite.SRC_OVER,
                                        new Float(
                                        systemSettings.getProperty(RenderingConfigurationConstants.SYSTEM_COMPOSITE_VALUE)));
                                g2.setComposite(ac);
                        }
                }

        }
}
