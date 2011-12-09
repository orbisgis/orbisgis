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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.gdms.driver.DriverException;


import org.gvsig.remoteClient.exceptions.ServerErrorException;
import org.gvsig.remoteClient.exceptions.WMSException;
import org.gvsig.remoteClient.wms.WMSStatus;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.WMSConnection;
import org.orbisgis.core.map.MapTransform;


import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import ij.process.ColorProcessor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.FilterDataSourceDecorator;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.DataSourceIndex;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.IndexQueryException;
import org.gdms.data.indexes.RTreeIndex;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;

/**
 * Renderer contains all the logic of the Symbology Encoding process based on java
 * Graphics2D. This is an abstract class and subclasses provided effectives methods
 * according to the rendering target (e.g. bitmap image, SVG, pdf, etc.)
 *
 * @author maxence
 */
public abstract class Renderer {

    static final double EXTRA_EXTENT_FACTOR = 0.01;


    static final int ONE_HUNDRED_I = 100;


    static final int BATCH_SIZE = 1000;


    static final int EXECP_POS = 20;


    private static OutputManager logger = Services.getOutputManager();


    /**
     * This method shall returns a graphics2D for each symbolizers in the list.
     * This is useful to make the diff bw pdf purpose and image purpose
     * Is called just before a new layer is drawn
     * @return
     */
    //public abstract HashMap<Symbolizer, Graphics2D> getGraphics2D(ArrayList<Symbolizer> symbs,
    //        Graphics2D g2, MapTransform mt);
    protected abstract void initGraphics2D(List<Symbolizer> symbs, Graphics2D g2,
                                           MapTransform mt);


    protected abstract Graphics2D getGraphics2D(Symbolizer s);


    protected abstract void releaseGraphics2D(Graphics2D g2);


    /**
     * Is called once the layer has been rendered
     * @param g2 the graphics the layer has to be drawn on
     */
    protected abstract void disposeLayer(Graphics2D g2);


    /**
     * Called before each feature
     * @param name the name of the feature
     */
    protected abstract void beginFeature(long id, DataSource sds);


    /**
     * Called after each feature
     * @param name the name of the feature
     */
    protected abstract void endFeature(long id, DataSource sds);


    /**
     * Called before each layer
     * @param name the name of the layer
     */
    protected abstract void beginLayer(String name);


    /**
     * Called after each layer
     * @param name the name of the layer
     */
    protected abstract void endLayer(String name);


    /**
     * Create a view which correspond to feature in MapContext adjusted extend
     * @param mt
     * @param sds
     * @param pm
     * @return
     * @throws DriverException
     */
    public int[] featureInExtent(MapTransform mt,
            DataSource sds, ProgressMonitor pm) throws DriverException {
        Envelope extent = mt.getAdjustedExtent();
        int sfi = sds.getSpatialFieldIndex();
        String sfn = sds.getFieldName(sfi);
        IndexManager im = sds.getDataSourceFactory().getIndexManager();
        try{
                if(im.getIndex(sds, sfn) == null){
                        im.buildIndex(sds, sfn, new NullProgressMonitor());
                }
                double gap = mt.getScaleDenominator() * EXTRA_EXTENT_FACTOR;
                Envelope envelope = new Envelope(extent.getMinX() - gap, extent.getMaxX() + gap, 
                        extent.getMinY() - gap, extent.getMaxY() + gap);
                DefaultSpatialIndexQuery iq = new DefaultSpatialIndexQuery(envelope, sfn);
                return im.queryIndex(sds, iq);
        } catch(IndexException ie){
                throw new DriverException("Can't handle the index", ie);
        } catch(NoSuchTableException nste){
                throw new DriverException("Are you sure the table actually exists ?", nste);
        }catch(IndexQueryException iqe){
                throw new DriverException("Can't query the index properly", iqe);
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
    public int drawVector(Graphics2D g2, MapTransform mt, ILayer layer,
            ProgressMonitor pm, RenderContext perm) throws DriverException {

        logger.println("Current DPI is " + mt.getDpi());
        logger.println("Current SCALE is 1: " + mt.getScaleDenominator());


        int layerCount = 0;
        DataSource sds = null;
        try {
            long tV1 = System.currentTimeMillis();

            sds = layer.getDataSource();
            sds.open();

            // Extract into drawSeLayer method !
            Style style = layer.getStyle();

            ArrayList<Symbolizer> symbs = new ArrayList<Symbolizer>();

            // i.e. TextSymbolizer are always drawn above all other layer !! Should now be handle wth symbolizer level
            //ArrayList<Symbolizer> overlays = new ArrayList<Symbolizer>();

            // Standard rules (with filter or no filter but not with elsefilter)
            ArrayList<Rule> rList = new ArrayList<Rule>();

            // Rule with ElseFilter
            ArrayList<Rule> fRList = new ArrayList<Rule>();

            // fetch symbolizers and rules
            style.getSymbolizers(mt, symbs, rList, fRList);


            long tV1b = System.currentTimeMillis();

            logger.println("Initialisation:" + (tV1b - tV1));

            // Create new dataSource with only feature in current extent
            pm.startTask("Filtering (spatial)...", 100);
            pm.progressTo(0);
            int[] featureInExtent = featureInExtent(mt, sds, pm);
            pm.progressTo(ONE_HUNDRED_I);
            pm.endTask();

            if (featureInExtent.length > 0) {

                // Assign filtered data source to each rule
//                HashMap<Rule, FilterDataSourceDecorator> rulesDs = new HashMap<Rule, FilterDataSourceDecorator>();
//
//                String elseWhere = "";
//                // Foreach rList without ElseFilter
//                for (Rule r : rList) {
//
//                    pm.startTask("Filtering (rule)...", 100);
//                    pm.progressTo(0);
//                    FilterDataSourceDecorator filteredDs = r.getFilteredDataSource(featureInExtent);
//
//                    if (!filteredDs.equals(featureInExtent)) {
//                        filteredDs.open();
//                    }
//
//                    ArrayList<Integer> fids = new ArrayList<Integer>();
//                    fids.addAll(filteredDs.getIndexMap());
//
//                    rulesDs.put(r, filteredDs);
//
//                    if (r.getWhere() != null) {
//                        if (elseWhere.isEmpty()) {
//                            elseWhere += "not (" + r.getWhere() + ")";
//                        } else {
//                            elseWhere += "and not(" + r.getWhere() + ")";
//                        }
//                    } else {
//                        elseWhere = "1 = 0";
//                    }
//                    pm.progressTo(ONE_HUNDRED_I);
//                    pm.endTask();
//                }
//
//
//                FilterDataSourceDecorator elseDs;
//                if (elseWhere.isEmpty()) {
//                    elseDs = featureInExtent;
//                } else {
//                    elseDs = new FilterDataSourceDecorator(featureInExtent, elseWhere);
//                    elseDs.open();
//                }


//                /**
//                 * Register elseRules as standard rules
//                 */
//                for (Rule elseR : fRList) {
//                    rulesDs.put(elseR, elseDs);
//                    rList.add(elseR);
//                }

                HashSet<Integer> selected = new HashSet<Integer>();
                for (long sFid : layer.getSelection()) {
                    selected.add((int) sFid);
                }

                pm.endTask();
                long tV2 = System.currentTimeMillis();
                logger.println("Filtering done in " + (tV2 - tV1) + "[ms]");

                // And now, features will be rendered
                // How many object to process ?
                long total = 0;

                long sTimer = 0;
                long sTimeFull = 0;


                // Make sure TextSymbolizer are rendered on top
                //symbs.addAll(overlays);



                // Get a graphics for each symbolizer
                //HashMap<Symbolizer, Graphics2D> g2Symbs = getGraphics2D(symbs, g2, mt);
                initGraphics2D(symbs, g2, mt);

                //for (Symbolizer s : symbs) {
                for (Rule r : rList) {
//                    total += rulesDs.get(r).getRowCount();
                    total += featureInExtent.length;
                }
                logger.println("TOTAL : " + total);

                for (Rule r : rList) {
                    long tf1 = System.currentTimeMillis();
                    beginLayer(r.getName());
                    logger.println("Drawing rule " + r.getName());
                    pm.startTask("Drawing " + layer.getName() + " (Rule " + r.getName() + ")", 100);

//                    FilterDataSourceDecorator fds = rulesDs.get(r);

                    int fid = 0;


                    long initFeats = 0;
                    for (fid = 0; fid < featureInExtent.length; fid++) {
                        initFeats -= System.currentTimeMillis();
                        if (layerCount % BATCH_SIZE == 0 && pm.isCancelled()) {
                            return layerCount;
                        }

                        long originalIndex = featureInExtent[fid];
//                        if (fds.equals(featureInExtent)) {
//                            originalIndex = fds.getOriginalIndex(fid);
//                        } else {
//                            originalIndex = featureInExtent.getOriginalIndex(fds.getOriginalIndex(fid));
//                        }

                        Geometry theGeom = null;

                        // If there is only one geometry, it is fetched now, otherwise, it up to symbolizers
                        // to retrieve the correct geometry (through the Geometry attribute)
                        try {
                            int fieldID = ShapeHelper.getGeometryFieldId(sds);
                            if (fieldID >= 0) {
                                theGeom = sds.getGeometry(originalIndex);
                                //System.out.println ("TheGeom: " + the_geom);
                            }
                        } catch (ParameterException ex){
                        }

                        boolean emphasis = selected.contains((int) originalIndex);

                        beginFeature(originalIndex, sds);
                        initFeats += System.currentTimeMillis();

                        for (Symbolizer s : r.getCompositeSymbolizer().getSymbolizerList()) {
                            sTimeFull -= System.currentTimeMillis();


                            Graphics2D g2S;
                            //if (s instanceof TextSymbolizer) {
                            // TextSymbolizer always rendered on overlay
                            //g2S = overlayImage.createGraphics();
                            //} else {
                            //g2S = g2Symbs.get(s);
                            g2S = getGraphics2D(s);
                            //}
                            sTimer -= System.currentTimeMillis();
                            s.draw(g2S, sds, originalIndex, emphasis, mt, theGeom, perm);
                            sTimer += System.currentTimeMillis();
                            //s.draw(g2, sds, originalIndex, emphasis, mt, the_geom, perm);
                            releaseGraphics2D(g2S);
                            sTimeFull += System.currentTimeMillis();
                        }
                        endFeature(originalIndex, sds);

                        pm.progressTo((int) (ONE_HUNDRED_I * (long) (++layerCount) / (long) total));
                    }
                    long tf2 = System.currentTimeMillis();
                    logger.println("  -> Rule done in  " + (tf2 - tf1) + "[ms]   featInit" + initFeats + "[ms]");

                    pm.endTask();
                    endLayer(r.getName());
                }

                long tV3 = System.currentTimeMillis();
                logger.println("All Rules done in" + (tV3 - tV2) + "[ms] (" + layerCount + "objects)");
                logger.println("Effective draw time: " + sTimer + " [ms]");
                logger.println("Full Symb time:      " + sTimeFull + " [ms]");
                disposeLayer(g2);

//                for (FilterDataSourceDecorator fds : rulesDs.values()) {
//                    if (fds.isOpen() && !fds.equals(featureInExtent)) {
//                        fds.close();
//                    }
//                }

                long tV4 = System.currentTimeMillis();
                logger.println("Images stacked :" + (tV4 - tV3) + "[ms]");

//                featureInExtent.close();

                long tV5 = System.currentTimeMillis();
                logger.println("Total Rendering Time:" + (tV5 - tV1) + "[ms]");
            }

        } catch (DriverLoadException ex) {
            printEx(ex, layer, g2);
//        } catch (DataSourceCreationException ex) {
//            printEx(ex, layer, g2);
        } catch (DriverException ex) {
            printEx(ex, layer, g2);
        } catch (ParameterException ex) {
            printEx(ex, layer, g2);
        } catch (IOException ex) {
            printEx(ex, layer, g2);
        } finally {
            if (sds != null && sds.isOpen()) {
                sds.close();
            }
        }

        return layerCount;
    }


    private static void printEx(Exception ex, ILayer layer, Graphics2D g2) {
        java.util.logging.Logger.getLogger("Could not draw " + layer.getName()).log(Level.SEVERE, "Error while drawing " + layer.getName(), ex);
        ex.printStackTrace(System.err);
        g2.setColor(Color.red);
        g2.drawString(ex.toString(), EXECP_POS, EXECP_POS);
    }


    public void draw(Graphics2D g2dMap, int width, int height,
            Envelope extent, ILayer layer, ProgressMonitor pm) {
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
            ILayer layer, ProgressMonitor pm) {

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
            ILayer lay, ProgressMonitor progressMonitor) {

        ProgressMonitor pm;
        if (progressMonitor == null) {
            pm = new NullProgressMonitor();
        } else {
            pm = progressMonitor;
        }

        g2.setRenderingHints(mt.getRenderingHints());

        Envelope extent = mt.getAdjustedExtent();

        int count = 0;

        ILayer[] layers;

        //ArrayList<Symbolizer> overlay = new ArrayList<Symbolizer>();

        if (lay.acceptsChilds()) {
            layers = lay.getLayersRecursively();
        } else {
            layers = new ILayer[]{lay};
        }

        long total1 = System.currentTimeMillis();

        Envelope graphicExtent = new Envelope(0, 0, mt.getWidth(), mt.getHeight());
        DefaultRendererPermission perm = new DefaultRendererPermission(graphicExtent);

        for (int i = layers.length - 1; i>= 0; i--) {
            if (pm.isCancelled()) {
                break;
            } else {
                ILayer layer = layers[i];
                if (layer.isVisible() && extent.intersects(layer.getEnvelope())) {
                    logger.println(I18N.getString("orbisgis-core.orbisgis.org.orbisgis.renderer.drawing") + layer.getName()); //$NON-NLS-1$
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
                                        List<?> layerNames = layers[j].getWMSConnection().getStatus().getLayerNames();
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
                        DataSource sds = layer.getDataSource();
                        if (sds != null) {
                            try {
                                if (sds.isVectorial()) {
                                    count += this.drawVector(g2, mt, layer, pm, perm);
                                } else if (sds.isRaster()) {
                                    logger.println("Raster Not Yet supported => Not drawn: " + layer.getName(), Color.red);
                                } else {
                                    logger.println(I18N.getString("orbisgis-core.orbisgis.org.orbisgis.renderer.notDraw") //$NON-NLS-1$
                                            + layer.getName(), Color.RED);
                                }
                            } catch (DriverException e) {
                                Services.getErrorManager().error(
                                        I18N.getString("orbisgis-core.orbisgis.org.orbisgis.renderer.cannotDraw") + layer.getName(), e); //$NON-NLS-1$
                            }
                            pm.progressTo(ONE_HUNDRED_I
                                    - (ONE_HUNDRED_I * i) / layers.length);
                        }
                    }
                    long t2 = System.currentTimeMillis();
                    logger.println(I18N.getString("orbisgis-core.orbisgis.org.orbisgis.renderer.renderingTime") + (t2 - t1)); //$NON-NLS-1$
                }
            }
        }

        long total2 = System.currentTimeMillis();
        logger.println(I18N.getString("orbisgis-core.orbisgis.org.orbisgis.renderer.totalRenderingTime") + (total2 - total1)); //$NON-NLS-1$



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
                    I18N.getString("orbisgis-core.orbisgis.org.orbisgis.renderer.cannotGetWMSImage"), e); //$NON-NLS-1$
        } catch (ServerErrorException e) {
            Services.getService(ErrorManager.class).error(
                    I18N.getString("orbisgis-core.orbisgis.org.orbisgis.renderer.cannotGetWMSImage"), e); //$NON-NLS-1$
        } catch (IOException e) {
            Services.getService(ErrorManager.class).error(
                    I18N.getString("orbisgis-core.orbisgis.org.orbisgis.renderer.cannotGetWMSImage"), e); //$NON-NLS-1$
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
        MapTransform mt = new MapTransform();
        mt.setExtent(extent);
        mt.setImage(img);
        draw(mt, layer, pm);
    }


    public void draw(BufferedImage img, Envelope extent, ILayer layer) {
        draw(img, extent, layer, new NullProgressMonitor());
    }


    private static class DefaultRendererPermission implements RenderContext {

        private Quadtree quadtree;


        private Envelope drawExtent;
        //private Area extent;


        public DefaultRendererPermission(Envelope drawExtent) {
            this.drawExtent = drawExtent;
            this.quadtree = new Quadtree();
            //this.extent = new Area(new Rectangle2D.Double(drawExtent.getMinX(), drawExtent.getMinY(), drawExtent.getWidth(), drawExtent.getHeight()));
        }


        @Override
        public void addUsedArea(Envelope area) {
            quadtree.insert(area, area);
        }


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


        @Override
        public Geometry getValidGeometry(Geometry geometry, double distance) {
            List<Envelope> list = quadtree.query(geometry.getEnvelopeInternal());
            GeometryFactory geometryFactory = new GeometryFactory();
            Geometry theGeom = geometry;
            for (Envelope envelope : list) {
                theGeom = theGeom.difference(geometryFactory.toGeometry(
                        envelope).buffer(distance, 1));
            }
            theGeom = theGeom.intersection(geometryFactory.toGeometry(drawExtent));

            if (theGeom.isEmpty()) {
                return null;
            } else {
                return theGeom;
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
            String bds = bands.toLowerCase();
            components[0] = getComponent(bds.charAt(0), red, green, blue);
            components[1] = getComponent(bds.charAt(1), red, green, blue);
            components[2] = getComponent(bds.charAt(2), red, green, blue);

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
                    I18N.getString("orbisgis-core.orbisgis.org.orbisgis.renderer.cannotCreatRGBCodes")); //$NON-NLS-1$
        }
    }


}
