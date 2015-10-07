/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.vividsolutions.jts.geom.Polygon;
import org.h2gis.utilities.RasterMetaData;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.coremap.ui.editors.map.tool.Rectangle2DDouble;
import org.slf4j.*;
import org.orbisgis.corejdbc.ReadRowSet;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.LayerException;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.Rule;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.coremap.renderer.se.Symbolizer;
import org.orbisgis.coremap.renderer.se.VectorSymbolizer;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.stream.GeoStream;
import org.orbisgis.commons.progress.NullProgressMonitor;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.SpatialResultSetMetaData;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Renderer contains all the logic of the Symbology Encoding process based on java
 * Graphics2D. This is an abstract class and subclasses provided effectives methods
 * according to the rendering target (e.g. bitmap image, SVG, pdf, etc.)
 *
 * @author Maxence Laurent
 */
public abstract class Renderer {

        static final int BATCH_SIZE = 1000;
        private static final Logger LOGGER = LoggerFactory.getLogger(Renderer.class);
        private static final I18n I18N = I18nFactory.getI18n(Renderer.class);
        private ResultSetProviderFactory rsProvider = null;

        /**
         * Change the way this renderer gather the table content of a layer.
         * @param rsProvider result set provider instance.
         */
        public void setRsProvider(ResultSetProviderFactory rsProvider) {
            this.rsProvider = rsProvider;
        }

    /**
         * This method shall returns a graphics2D for each symbolizers in the list.
         * This is useful to make the diff bw pdf purpose and image purpose
         * Is called just before a new layer is drawn
         */
        //public abstract HashMap<Symbolizer, Graphics2D> getGraphics2D(ArrayList<Symbolizer> symbs,
        //        Graphics2D g2, MapTransform mt);
        protected abstract void initGraphics2D(List<Symbolizer> symbs, Graphics2D g2,
                MapTransform mt);

        /**
         * Gets the {@code Graphics2D} instance that is associated to the {@code
         * Symbolizer s}.
         * @param s
         * @return
         */
        protected abstract Graphics2D getGraphics2D(Symbolizer s);

        protected abstract void releaseGraphics2D(Graphics2D g2);

        /**
         * Is called once the layer has been rendered
         * @param g2 the graphics the layer has to be drawn on
         */
        protected abstract void disposeLayer(Graphics2D g2);

        /**
         * Called before each feature
         * @param id index of the feature
         * @param rs Result set
         */
        protected abstract void beginFeature(long id, ResultSet rs);

        /**
         * Called after each feature
         * @param id index of the feature
         * @param rs Result set
         */
        protected abstract void endFeature(long id, ResultSet rs);

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
         * Draws the content of the Vector Layer
         *
         * @param g2
         *            Object to draw to
         * @param layer
         *            Source of information
         * @param pm
         *            Progress monitor to report the status of the drawing
         * @return the number of rendered objects
         */
        public int drawVector(Graphics2D g2, MapTransform mt, ILayer layer,
                ProgressMonitor pm) throws SQLException {
                Envelope extent = mt.getAdjustedExtent();
                int layerCount = 0;
                List<Style> styles = layer.getStyles();
                for(Style style : styles){
                        layerCount +=drawStyle(style, g2, mt, layer, pm, extent);
                }
                return layerCount;
        }        

        private int drawStyle(Style style, Graphics2D g2,MapTransform mt, ILayer layer,
                              ProgressMonitor pm, Envelope extent) throws SQLException {
            int layerCount = 0;
            LinkedList<Symbolizer> symbs = new LinkedList<Symbolizer>();
            ResultSetProviderFactory layerDataFactory = rsProvider;
            if(layerDataFactory == null) {
                if(layer.getDataManager() != null && layer.getDataManager().getDataSource() != null) {
                    layerDataFactory = new DefaultResultSetProviderFactory();
                } else {
                    throw new SQLException("There is neither a ResultSetProviderFactory instance nor available DataSource in the vectorial layer");
                }
            }
            try {
                // i.e. TextSymbolizer are always drawn above all other layer !! Should now be handle with symbolizer level
                // Standard rules (with filter or no filter but not with elsefilter)
                LinkedList<Rule> rList = new LinkedList<Rule>();
                // Rule with ElseFilter
                LinkedList<Rule> fRList = new LinkedList<Rule>();
                // fetch symbolizers and rules
                style.getSymbolizers(mt, symbs, rList, fRList);
                // Create new dataSource with only feature in current extent
                Set<Long> selectedRows = layer.getSelection();
                // And now, features will be rendered
                // Get a graphics for each symbolizer
                initGraphics2D(symbs, g2, mt);
                ProgressMonitor rulesProgress = pm.startTask(rList.size());
                for (Rule r : rList) {
                    beginLayer(r.getName());
                    try(ResultSetProviderFactory.ResultSetProvider resultSetProvider = layerDataFactory.getResultSetProvider(layer, new String[0],rulesProgress)) {
                        try(SpatialResultSet rs = resultSetProvider.execute(rulesProgress, extent)) {
                            int pkColumn = rs.findColumn(resultSetProvider.getPkName());
                            int fieldID = rs.getMetaData().unwrap(SpatialResultSetMetaData.class).getFirstGeometryFieldIndex();
                            ProgressMonitor rowSetProgress;
                            // Read row count for progress monitor
                            if(rs instanceof ReadRowSet) {
                                rowSetProgress = rulesProgress.startTask("Drawing " + layer.getName() + " (Rule " + r.getName() + ")", ((ReadRowSet) rs).getRowCount());
                            } else {
                                rowSetProgress = rulesProgress.startTask("Drawing " + layer.getName() + " (Rule " + r.getName() + ")", 1);
                            }
                            while (rs.next()) {
                                if (rulesProgress.isCancelled()) {
                                    break;
                                }
                                Geometry theGeom = null;
                                // If there is only one geometry, it is fetched now, otherwise, it up to symbolizers
                                // to retrieve the correct geometry (through the Geometry attribute)
                                if (fieldID >= 0) {
                                    theGeom = rs.getGeometry(fieldID);
                                }
                                // Do not display the geometry when the envelope
                                //doesn't intersect the current mapcontext area.
                                if (theGeom == null || theGeom.getEnvelopeInternal().intersects(extent)) {
                                    long row = rs.getLong(pkColumn);
                                    boolean selected = selectedRows.contains(row);

                                    beginFeature(row, rs);

                                    List<Symbolizer> sl = r.getCompositeSymbolizer().getSymbolizerList();
                                    for (Symbolizer s : sl) {
                                        boolean res = drawFeature(s, theGeom, rs, row,
                                                extent, selected, mt);
                                    }
                                    endFeature(row, rs);
                                }
                                rowSetProgress.endTask();
                            }
                            endLayer(r.getName());
                        }
                    } catch (SQLException ex) {
                        if(!rulesProgress.isCancelled()) {
                            printEx(ex, layer, g2);
                        }
                    }
                    rulesProgress.endTask();
                }
                disposeLayer(g2);
            } catch (ParameterException ex) {
                printEx(ex, layer, g2);
            } catch (IOException ex) {
                printEx(ex, layer, g2);
            }
            return layerCount;
        }

        private boolean drawFeature(Symbolizer s, Geometry geom, ResultSet rs,
                        long rowIdentifier, Envelope extent, boolean selected,
                        MapTransform mt) throws ParameterException,
                        IOException, SQLException {
                Geometry theGeom = geom;
                boolean somethingReached = false;
                if(theGeom == null){
                        //We try to retrieve a geometry. If we fail, an
                        //exception will be thrown by the call to draw,
                        //and a message will be shown to the user...
                        VectorSymbolizer vs = (VectorSymbolizer)s;
                        theGeom = vs.getGeometry(rs, rowIdentifier);
                        if(theGeom != null && theGeom.getEnvelopeInternal().intersects(extent)){
                                somethingReached = true;
                        }
                }
                if(somethingReached || theGeom != null){
                        Graphics2D g2S;
                        g2S = getGraphics2D(s);
                        s.draw(g2S, rs, rowIdentifier, selected, mt, theGeom);
                        releaseGraphics2D(g2S);
                        return true;
                }else {
                        return false;
                }
        }

        private static void printEx(Exception ex, ILayer layer, Graphics2D g2) {
                LOGGER.warn("Could not draw " +layer.getName(), ex);
//                g2.setColor(Color.red);
//                g2.drawString(ex.toString(), EXECP_POS, EXECP_POS);
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
         * @param mt
         *            Drawing parameters
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
         * @param lay
         *            Source of information
         * @param progressMonitor
         *            Progress monitor to report the status of the drawing
         */
        public void draw(MapTransform mt, Graphics2D g2, int width, int height,
                ILayer lay, ProgressMonitor progressMonitor) {

                g2.setRenderingHints(mt.getRenderingHints());

                Envelope extent = mt.getAdjustedExtent();


                ILayer[] layers;

                //ArrayList<Symbolizer> overlay = new ArrayList<Symbolizer>();

                if (lay.acceptsChilds()) {
                        layers = lay.getLayersRecursively();
                } else {
                        layers = new ILayer[]{lay};
                }

                // long total1 = System.currentTimeMillis();
                int numLayers = layers.length;
                ProgressMonitor pm;
                if (progressMonitor == null) {
                    pm = new NullProgressMonitor();
                } else {
                    pm = progressMonitor.startTask(numLayers);
                }
                for (int i = numLayers - 1; i >= 0; i--) {
                        if (pm.isCancelled()) {
                                break;
                        } else {
                                ILayer layer = layers[i];
                                if (layer.isVisible() && extent.intersects(layer.getEnvelope())) {
                                        try {
                                                if (layer.isStream()) {
                                                    drawStreamLayer(g2, layer, width, height, extent, pm);
                                                } else if(layer.isVectorial()) {
                                                    drawVector(g2, mt, layer, pm);
                                                } else if(layer.isRaster()) {
                                                    drawRaster(g2, mt, layer, width, height, pm);
                                                }
                                        } catch (SQLException | LayerException e) {
                                                LOGGER.error(I18N.tr("Layer {0} not drawn",layer.getName()), e);
                                        }
                                }
                        }
                        pm.endTask();
                }
        }

        private void drawStreamLayer(Graphics2D g2, ILayer layer, int width, int height, Envelope extent, ProgressMonitor pm) {
                try {
                        layer.open();
                        GeoStream geoStream = layer.getStream();
                        Image img = geoStream.getMap(width, height, extent, pm);
                        g2.drawImage(img, 0, 0, null);
                } catch (LayerException | IOException e) {
                        LOGGER.error(
                                I18N.tr("Cannot get Stream image"), e);
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
    private static ImageReader fetchImageReader(ImageInputStream is) throws SQLException {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(is);
        if(readers.hasNext()) {
            return readers.next();
        } else {
            throw new SQLException(I18N.tr("Cannot found driver for reading provided raster"));
        }

    }
     /**
     * A workarround to draw a rasterlayer This method wil be updated with the
     * RasterSymbolizer
     *
     * @param g2
     * @param mt
     * @param layer
     * @param width
     * @param height
     * @param pm
     */
    private void drawRaster(Graphics2D g2, MapTransform mt, ILayer layer, int width, int height, ProgressMonitor pm) throws SQLException {
        GraphicsConfiguration configuration = null;
        boolean isHeadLess = GraphicsEnvironment.isHeadless();
        if (!isHeadLess) {
            configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }
        ResultSetProviderFactory layerDataFactory = rsProvider;
        if(layerDataFactory == null) {
            if(layer.getDataManager() != null && layer.getDataManager().getDataSource() != null) {
                layerDataFactory = new DefaultResultSetProviderFactory();
            } else {
                throw new SQLException("There is neither a ResultSetProviderFactory instance nor available DataSource in the vectorial layer");
            }
        }
        beginLayer(layer.getName());
        // Raster MetaData need to be queried along with Raster content
        try(Connection connection = layer.getDataManager().getDataSource().getConnection()) {
            List<String> rasterFields =
                    SFSUtilities.getRasterFields(connection, TableLocation.parse(layer.getTableReference()));
            if(rasterFields.isEmpty()) {
                LOGGER.error("The table " + layer.getTableReference() + " does not contain raster fields");
                return;
            }
            Envelope envView = mt.getAdjustedExtent();
            String rasterField = rasterFields.get(0);
            String[] sqlFields = new String[]{"ST_METADATA("+TableLocation.quoteIdentifier(rasterField)+") raster_metadata"};
            try (ResultSetProviderFactory.ResultSetProvider resultSetProvider = layerDataFactory.getResultSetProvider(layer,sqlFields, pm)) {
                try (SpatialResultSet rs = resultSetProvider.execute(pm, mt.getAdjustedExtent())) {
                    ProgressMonitor rowSetProgress;
                    // Read row count for progress monitor
                    if (rs instanceof ReadRowSet) {
                        rowSetProgress = pm.startTask("Drawing " + layer.getName(), ((ReadRowSet) rs).getRowCount());
                    } else {
                        rowSetProgress = pm.startTask("Drawing " + layer.getName(), 1);
                    }
                    // Keep reader until there is a problem with reading raster
                    ImageReader lastReader = null;
                    while (rs.next()) {
                        if(pm.isCancelled()) {
                            break;
                        }
                        try {
                            RasterMetaData metaData = RasterMetaData.fetchRasterMetaData(rs, "raster_metadata");
                            if(metaData != null) {
                                // Fetch ImageReader
                                ImageInputStream is = ImageIO.createImageInputStream(rs.getBlob(rasterField));
                                if (is == null) {
                                    throw new SQLException(I18N.tr("No input stream driver for Blob type"));
                                }
                                if(lastReader == null) {
                                    lastReader = fetchImageReader(is);
                                }
                                Polygon env = metaData.convexHull();
                                // prepare image read
                                lastReader.setInput(is);
                                ImageReadParam readParam = lastReader.getDefaultReadParam();
                                // Compute pixel envelope source
                                // As raster can be transformed, all corners are retrieved
                                int[] p0 = metaData.getPixelFromCoordinate(new Coordinate(envView.getMinX(),
                                        envView.getMinY()));
                                int[] p1 = metaData.getPixelFromCoordinate(new Coordinate(envView.getMaxX(),
                                        envView.getMinY()));
                                int[] p2 = metaData.getPixelFromCoordinate(new Coordinate(envView.getMaxX(),
                                        envView.getMaxY()));
                                int[] p3 = metaData.getPixelFromCoordinate(new Coordinate(envView.getMinX(),
                                        envView.getMaxY()));
                                int minX = Math.max(0, Math.min(Math.min(Math.min(p0[0], p1[0]), p2[0]), p3[0]));
                                int maxX = Math.min(metaData.getWidth(), Math.max(Math.max(Math.max(p0[0], p1[0]),
                                        p2[0]), p3[0]));
                                int minY = Math.max(0, Math.min(Math.min(Math.min(p0[1], p1[1]), p2[1]), p3[1]));
                                int maxY = Math.min(metaData.getHeight(), Math.max(Math.max(Math.max(p0[1], p1[1]),
                                        p2[1]), p3[1]));
                                Rectangle envPixSource = new Rectangle(minX, minY, maxX - minX,
                                        maxY - minY);
                                if(!(envPixSource.width > 0 && envPixSource.height > 0)) {
                                    continue;
                                    // Skip this raster if there is no pixel to read
                                }
                                readParam.setSourceRegion(envPixSource);
                                // TODO {@link ImageReadParam#setSourceSubsampling}
                                // Acquire image fragment
                                AffineTransform rasterTransform = new AffineTransform(mt.getAffineTransform());
                                rasterTransform.concatenate(metaData.getTransform());
                                // Compute the pixel offset
                                // how many pixel at pixel source are contained into 1 pixel at destination
                                try {
                                    AffineTransform invTransf = rasterTransform.createInverse();
                                    int pixelWidth = (int)Math.floor(invTransf.transform(new Point(1, 0), null).getX()
                                            - invTransf.transform(new Point(0, 0), null).getX());
                                    int pixelHeight = (int)Math.floor(invTransf.transform(new Point(0, 1), null).getY()
                                            - invTransf.transform(new Point(0, 0), null).getY());
                                    if(pixelWidth > 1 || pixelHeight > 1) {
                                        // Renderer does not need all pixels from source. Then skip some pixels to
                                        // get a smaller image to process.
                                        readParam.setSourceSubsampling(pixelWidth, pixelHeight, minX, minY);
                                    }
                                } catch (NoninvertibleTransformException ex) {
                                    // Nothing
                                }
                                final AffineTransform originalTransform = g2.getTransform();
                                try {
                                    g2.setTransform(rasterTransform);
                                    BufferedImage rasterImage = lastReader.read(lastReader.getMinIndex(), readParam);
                                    g2.drawImage(rasterImage, minX, minY, null);
                                } finally {
                                    g2.setTransform(originalTransform);
                                }
                            }
                            rowSetProgress.endTask();
                        } catch (IOException ex) {
                            LOGGER.error(I18N.tr("Cannot read raster field"), ex);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * A simple method to display an empty image
     *
     * @param width
     * @param height
     * @return
     */
    private BufferedImage createEmptyImage(int width, int height) {
        final String noImage = "Image Unavailable";

        if (width == 0 || height == 0) {
            return null;
        }
        BufferedImage bufferedImage =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setBackground(Color.WHITE);

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);

        // Create our font
        Font font = new Font("SansSerif", Font.PLAIN, 18);
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics();

        int length = metrics.stringWidth(noImage);
        while (length + 6 >= width) {
            font = font.deriveFont((float) (font.getSize2D() * 0.9)); // Scale our font
            graphics.setFont(font);
            metrics = graphics.getFontMetrics();
            length = metrics.stringWidth(noImage);
        }

        int lineHeight = metrics.getHeight();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawString(noImage, (width - length) / 2, (height + lineHeight) / 2);

        return bufferedImage;
    }
}
