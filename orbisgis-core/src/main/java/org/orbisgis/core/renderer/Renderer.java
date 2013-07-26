/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.grap.model.GeoRaster;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.VectorSymbolizer;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.ui.editors.map.tool.Rectangle2DDouble;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sputilities.SFSUtilities;
import org.orbisgis.sputilities.SpatialResultSet;
import org.orbisgis.sputilities.SpatialResultSetMetaData;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.sql.RowSet;

/**
 * Renderer contains all the logic of the Symbology Encoding process based on java
 * Graphics2D. This is an abstract class and subclasses provided effectives methods
 * according to the rendering target (e.g. bitmap image, SVG, pdf, etc.)
 *
 * @author Maxence Laurent
 */
public abstract class Renderer {

        static final double EXTRA_EXTENT_FACTOR = 0.01;
        static final int ONE_HUNDRED_I = 100;
        static final int BATCH_SIZE = 1000;
        static final int EXECP_POS = 20;
        private static final Logger LOGGER = Logger.getLogger(Renderer.class);
        private static final I18n I18N = I18nFactory.getI18n(Renderer.class);
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
                ProgressMonitor pm, RenderContext perm) throws SQLException {
                Envelope extent = mt.getAdjustedExtent();
                int layerCount = 0;
                String tableReference = layer.getTableReference();
                DataSource dataSource = Services.getService(DataSource.class);
                DataManager dataManager = Services.getService(DataManager.class);
                Connection connection = dataSource.getConnection();
                try {
                        List<String> geometryFields = SFSUtilities.getGeometryFields(connection, SFSUtilities.splitCatalogSchemaTableName(tableReference));
                        if(geometryFields.isEmpty()) {
                            throw new SQLException(I18N.tr("Table {0} does not contains geometry fields",tableReference));
                        }
                        PreparedStatement st = connection.prepareStatement(
                                String.format("select * from %s where %s && $1::Geometry",tableReference,geometryFields.get(0)));
                        ResultSet rs = dataManager.getDataSource(st);
                        GeometryFactory geometryFactory = new GeometryFactory();
                        st.setString(1, geometryFactory.toGeometry(extent).toText()); // Filter geometry by envelope
                        // long tV1 = System.currentTimeMillis();
                        long rowCount = ((RowSet) rs).getMaxRows();
                        // Extract into drawSeLayer method !
                        List<Style> styles = layer.getStyles();
                        for(Style style : styles){
                                layerCount +=drawStyle(style, rs.unwrap(SpatialResultSet.class), g2, mt, layer, pm, perm, rowCount, extent);
                        }
                } catch (SQLException ex) {
                        printEx(ex, layer, g2);
                } finally {
                        connection.close();
                }
                return layerCount;
        }

        private int drawStyle(Style style, SpatialResultSet rs,Graphics2D g2,MapTransform mt, ILayer layer,
                        ProgressMonitor pm, RenderContext perm, long rowCount, Envelope extent) throws SQLException {
                int layerCount = 0;
                LinkedList<Symbolizer> symbs = new LinkedList<Symbolizer>();
                try {
                        // i.e. TextSymbolizer are always drawn above all other layer !! Should now be handle with symbolizer level
                        // Standard rules (with filter or no filter but not with elsefilter)
                        LinkedList<Rule> rList = new LinkedList<Rule>();
                        // Rule with ElseFilter
                        LinkedList<Rule> fRList = new LinkedList<Rule>();
                        // fetch symbolizers and rules
                        style.getSymbolizers(mt, symbs, rList, fRList);
                        // Create new dataSource with only feature in current extent
                        Set<Integer> selected = layer.getSelection();
                        // And now, features will be rendered
                        // Get a graphics for each symbolizer
                        initGraphics2D(symbs, g2, mt);
                        for (Rule r : rList) {
                                beginLayer(r.getName());
                                pm.startTask("Drawing " + layer.getName() + " (Rule " + r.getName() + ")", 100);
                                int fieldID = rs.getMetaData().unwrap(SpatialResultSetMetaData.class).getFirstGeometryFieldIndex();
                                int i = 0;
                                rs.beforeFirst();
                                while (rs.next()) {
                                        if (i / 1000 == i / 1000.0) {
                                                if (pm.isCancelled()) {
                                                        break;
                                                } else {
                                                        pm.progressTo((int) (100 * i / rowCount));
                                                }
                                        }
                                        i++;
                                        if (layerCount % BATCH_SIZE == 0 && pm.isCancelled()) {
                                                return layerCount;
                                        }
                                        Geometry theGeom = null;
                                        // If there is only one geometry, it is fetched now, otherwise, it up to symbolizers
                                        // to retrieve the correct geometry (through the Geometry attribute)
                                        if (fieldID >= 0) {
                                                theGeom = rs.getGeometry(fieldID);
                                        }
                                        // Do not display the geometry when the envelope
                                        //doesn't intersect the current mapcontext area.
                                        if (theGeom == null || (theGeom != null &&
                                                    theGeom.getEnvelopeInternal().intersects(extent))) {
                                                int row = rs.getRow();
                                                boolean emphasis = selected.contains(row); //TODO find selection identifier

                                                beginFeature(row, rs);

                                                List<Symbolizer> sl = r.getCompositeSymbolizer().getSymbolizerList();
                                                for (Symbolizer s : sl) {
                                                        boolean res = drawFeature(s, theGeom, rs, row,
                                                                extent, emphasis, mt, perm);
                                                }
                                                endFeature(row, rs);
                                        }
                                }
                                pm.endTask();
                                endLayer(r.getName());
                        }
                        disposeLayer(g2);
                } catch (ParameterException ex) {
                        printEx(ex, layer, g2);
                } catch (IOException ex) {
                        printEx(ex, layer, g2);
                } catch (SQLException ex) {
                        printEx(ex, layer, g2);
                }
                return layerCount;
        }

        private boolean drawFeature(Symbolizer s, Geometry geom, ResultSet rs,
                        Integer originalIndex, Envelope extent, boolean selected,
                        MapTransform mt, RenderContext perm) throws ParameterException,
                        IOException, SQLException {
                Geometry theGeom = geom;
                boolean somethingReached = false;
                if(theGeom == null){
                        //We try to retrieve a geometry. If we fail, an
                        //exception will be thrown by the call to draw,
                        //and a message will be shown to the user...
                        VectorSymbolizer vs = (VectorSymbolizer)s;
                        theGeom = vs.getGeometry(rs, originalIndex.longValue());
                        if(theGeom != null && theGeom.getEnvelopeInternal().intersects(extent)){
                                somethingReached = true;
                        }
                }
                if(somethingReached || theGeom != null){
                        Graphics2D g2S;
                        g2S = getGraphics2D(s);
                        s.draw(g2S, rs, originalIndex, selected, mt, theGeom, perm);
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

                ProgressMonitor pm;
                if (progressMonitor == null) {
                        pm = new NullProgressMonitor();
                } else {
                        pm = progressMonitor;
                }

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

                Envelope graphicExtent = new Envelope(0, 0, mt.getWidth(), mt.getHeight());
                DefaultRendererPermission perm = new DefaultRendererPermission(graphicExtent);
                int numLayers = layers.length;
                for (int i = numLayers - 1; i >= 0; i--) {
                        if (pm.isCancelled()) {
                                break;
                        } else {
                                ILayer layer = layers[i];
                                if (layer.isVisible() && extent.intersects(layer.getEnvelope())) {
                                        try {
                                                // TODO
                                                //
                                                // if (layer.isStream()) {
                                                //   drawStreamLayer(g2, layer, width, height, extent, pm);
                                                this.drawVector(g2, mt, layer, pm, perm);
                                                // TODO
                                                // if (layer.isRaster()) {
                                                // this.drawRaster(g2, mt, layer,width,height, pm, perm);
                                                pm.progressTo(ONE_HUNDRED_I - (ONE_HUNDRED_I * i) / layers.length);
                                        } catch (SQLException e) {
                                                LOGGER.error(I18N.tr("Layer {0} not drawn",layer.getName()), e);
                                        }
                                }
                        }
                }
        }

        private void drawStreamLayer(Graphics2D g2, ILayer layer, int width, int height, Envelope extent, ProgressMonitor pm) {
                try {
                        layer.open();
                        /*
                        TODO GeoStream with h2
                        for (int i = 0 ; i < layer.getTableReference().getRowCount() ; i++) {
                                GeoStream geoStream = layer.getTableReference().getStream(i);

                                Image img = geoStream.getMap(width, height, extent, pm);
                                g2.drawImage(img, 0, 0, null);
                        }
                        */
                } catch (LayerException e) {
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
     * @param perm
     */
    private void drawRaster(Graphics2D g2, MapTransform mt, ILayer layer, int width, int height, ProgressMonitor pm, DefaultRendererPermission perm) throws SQLException {
        //TODO Raster with h2
        throw new UnsupportedOperationException("Not supported yet.");
        /*

        GraphicsConfiguration configuration = null;
        boolean isHeadLess = GraphicsEnvironment.isHeadless();
        if (!isHeadLess) {
            configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }
        DataSource ds = layer.getTableReference();
        long rowCount = ds.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            GeoRaster geoRaster = ds.getRaster(i);
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

            
            try {
                ColorModel cm = geoRaster.getDefaultColorModel();
                Image dataImage = geoRaster.getImage(cm);
                gLayer.drawImage(dataImage, (int) layerPixelEnvelope.getMinX(),
                        (int) layerPixelEnvelope.getMinY(),
                        (int) layerPixelEnvelope.getWidth() + 1,
                        (int) layerPixelEnvelope.getHeight() + 1, null);

            } catch (IOException ex) {
                layerImage = createEmptyImage(width, height);
            }

            g2.drawImage(layerImage, 0, 0, null);
        }
         */
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

        private static class DefaultRendererPermission implements RenderContext {

                private Quadtree quadtree;
                private Envelope drawExtent;
                //private Area extent;

                DefaultRendererPermission(Envelope drawExtent) {
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

        

       
}
