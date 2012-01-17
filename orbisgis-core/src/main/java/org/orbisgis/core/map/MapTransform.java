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
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT
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
package org.orbisgis.core.map;

import com.vividsolutions.jts.awt.PointTransformation;
import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.renderable.RenderContext;
import java.util.ArrayList;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editors.map.tool.Rectangle2DDouble;

public class MapTransform implements PointTransformation  {

    private boolean adjustExtent;
    private BufferedImage image = null;
    private Envelope adjustedExtent;
    private AffineTransform trans = new AffineTransform();
    private AffineTransform transInv = new AffineTransform();
    private Envelope extent;
    private ArrayList<TransformListener> listeners = new ArrayList<TransformListener>();
    private ShapeWriter converter;
    private RenderContext currentRenderContext = screenContext;
    private static RenderContext draftContext;
    private static RenderContext screenContext;
    private double dpi;
    private static final double DEFAULT_DPI = 96.0;

    static {
        ImageLayout layout = new ImageLayout();
        layout.setColorModel(ColorModel.getRGBdefault());

        RenderingHints screenHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
        screenHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        screenHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        screenHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        screenHints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        screenHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        screenHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        screenHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        screenHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        screenContext = new RenderContext(AffineTransform.getTranslateInstance(0.0, 0.0), screenHints);

        RenderingHints draftHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);

        draftHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        draftHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        draftHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        draftHints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        draftHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        draftHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        draftHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        draftHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        draftContext = new RenderContext(AffineTransform.getTranslateInstance(0.0, 0.0), draftHints);
    }

    public MapTransform() {
        adjustExtent = true;
        try {
            this.dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        } catch (HeadlessException e) {
            Services.getOutputManager().println("Could not retrieve current DPI. Use 96.0!", Color.ORANGE);
            this.dpi = DEFAULT_DPI;
        }
    }

    /**
     * When true, the rendered map will always respects the CRS aspect ratio
     * When false, the Map extent will be bound to the output extent and may re-scale the map
     * 
     * @return
     */
    public boolean isAdjustExtent() {
        return adjustExtent;
    }

    public void setAdjustExtent(boolean adjustExtent) {
        this.adjustExtent = adjustExtent;
    }

    /**
     * Sets the painted image
     *
     * @param newImage
     */
    public void setImage(BufferedImage newImage) {
        image = newImage;
        calculateAffineTransform();
    }

    public void switchToDraft() {
        this.currentRenderContext = MapTransform.draftContext;
        Services.getOutputManager().println("Switch to draft!");
    }

    public void switchToScreen() {
        this.currentRenderContext = MapTransform.screenContext;
        Services.getOutputManager().println("Switch to Hign-Quality!");
    }

    public RenderContext getCurrentRenderContext() {
        return this.currentRenderContext;
    }

    public RenderingHints getRenderingHints() {
        return this.currentRenderContext.getRenderingHints();
    }

    /**
     * Gets the painted image
     *
     * @return
     */
    public BufferedImage getImage() {
        return image;
    }

    public double getDpi() {
        return dpi;
    }

    public void setDpi(double dpi) {
        this.dpi = dpi;
    }

    /**
     * Gets the extent used to calculate the transformation. This extent is the
     * same as the setted one but adjusted to have the same ratio than the image
     *
     * @return
     */
    public Envelope getAdjustedExtent() {
        return adjustedExtent;
    }

    /**
     *
     * @throws RuntimeException
     */
    private void calculateAffineTransform() {
        if (extent == null) {
            return;
        } else if ((image == null) || (getWidth() == 0) || (getHeight() == 0)) {
            return;
        }

        if (adjustExtent) {
            double escalaX;
            double escalaY;

            escalaX = getWidth() / extent.getWidth();
            escalaY = getHeight() / extent.getHeight();

            double xCenter = extent.getMinX() + extent.getWidth() / 2.0;
            double yCenter = extent.getMinY() + extent.getHeight() / 2.0;
            double newHeight;
            double newWidth;

            adjustedExtent = null;

            double scale;
            if (escalaX < escalaY) {
                scale = escalaX;
                newHeight = getHeight() / scale;
                double newX = xCenter - (extent.getWidth() / 2.0);
                double newY = yCenter - (newHeight / 2.0);
                adjustedExtent = new Envelope(newX, newX + extent.getWidth(), newY,
                        newY + newHeight);
            } else {
                scale = escalaY;
                newWidth = getWidth() / scale;
                double newX = xCenter - (newWidth / 2.0);
                double newY = yCenter - (extent.getHeight() / 2.0);
                adjustedExtent = new Envelope(newX, newX + newWidth, newY, newY
                        + extent.getHeight());
            }

            trans.setToIdentity();
            trans.concatenate(AffineTransform.getScaleInstance(scale, -scale));
            trans.concatenate(AffineTransform.getTranslateInstance(-adjustedExtent.getMinX(),
                    -adjustedExtent.getMinY() - adjustedExtent.getHeight()));
        } else {
            adjustedExtent = new Envelope(extent);
            trans.setToIdentity();
            double scaleX = getWidth() / extent.getWidth();
            double scaleY = getHeight() / extent.getHeight();

            /**
             * Map Y axis grows downward but CRS grows upward => -1
             */
            trans.concatenate(AffineTransform.getScaleInstance(scaleX, -scaleY));
            trans.concatenate(AffineTransform.getTranslateInstance(-extent.getMinX(), -extent.getMinY() - extent.getHeight()));
        }
        try {
            transInv = trans.createInverse();
        } catch (NoninvertibleTransformException ex) {
            transInv = null;
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets the height of the drawn image
     *
     * @return
     */
    public int getHeight() {
        if (image == null) {
            return 0;
        } else {
            return image.getHeight();
        }
    }

    /**
     * Gets the width of the drawn image
     *
     * @return
     */
    public int getWidth() {
        if (image == null) {
            return 0;
        } else {
            return image.getWidth();
        }
    }

    /**
     * Sets the extent of the transformation. This extent is not used directly
     * to calculate the transformation but is adjusted to obtain an extent with
     * the same ration than the image
     *
     * @param newExtent
     */
    public void setExtent(Envelope newExtent) {
        if ((newExtent != null)
                && ((newExtent.getWidth() == 0) || (newExtent.getHeight() == 0))) {
            newExtent.expandBy(10);
        }
        Envelope oldExtent = this.extent;
        boolean modified = true;
        /* Set extent when Envelope is modified */
        if (extent != null) {
            if (extent.equals(newExtent)) {
                modified = false;
            }
        }
        if (modified) {
            this.extent = newExtent;
            calculateAffineTransform();
            for (TransformListener listener : listeners) {
                listener.extentChanged(oldExtent, this);
            }
        }
    }

    /**
     * Creates new image with the specified size
     *
     * @param width
     * @param height
     */
    public void resizeImage(int width, int height) {
        int oldWidth = getWidth();
        int oldHeight = getHeight();
        // image = new BufferedImage(width, height,
        // BufferedImage.TYPE_INT_ARGB);
        GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        image = configuration.createCompatibleImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        calculateAffineTransform();
        for (TransformListener listener : listeners) {
            listener.imageSizeChanged(oldWidth, oldHeight, this);
        }
    }

    /**
     * Gets this transformation
     *
     * @return
     */
    public AffineTransform getAffineTransform() {
        return trans;
    }

    /**
     * Gets the extent
     *
     * @return
     */
    public Envelope getExtent() {
        return extent;
    }

    /**
     * Transforms an envelope in map units to image units
     *
     * @param geographic
     *            envelope
     * @return Rectangle2DDouble
     */
    public Rectangle2DDouble toPixel(Envelope geographicEnvelope) {
        final Point2D lowerRight = new Point2D.Double(geographicEnvelope.getMaxX(), geographicEnvelope.getMinY());
        final Point2D upperLeft = new Point2D.Double(geographicEnvelope.getMinX(), geographicEnvelope.getMaxY());

        final Point2D ul = trans.transform(upperLeft, null);
        final Point2D lr = trans.transform(lowerRight, null);

        return new Rectangle2DDouble(ul.getX(), ul.getY(), lr.getX()
                - ul.getX(), lr.getY() - ul.getY());
    }

    /**
     * Transforms an image coordinate in pixels into a map coordinate
     *
     * @param i
     * @param j
     * @return
     */
    public Point2D toMapPoint(int i, int j) {
        if (transInv != null) {
            return transInv.transform(new Point2D.Double(i, j), null);
        } else {
            throw new RuntimeException("NonInvertibleMatrix");
        }
    }

    public Shape fromMapToWorld(Polygon p){
        if (transInv != null) {
            return transInv.createTransformedShape(p);
        } else {
            throw new RuntimeException("NonInvertibleMatrix");
        }
    }

    /**
     * Transforms the specified map point to an image pixel
     *
     * @param point
     * @return
     */
    public Point fromMapPoint(Point2D point) {
        Point2D ret = trans.transform(point, null);
        return new Point((int) ret.getX(), (int) ret.getY());
    }

    /**
     * Gets the scale denominator. If the scale is 1:1000 this method returns
     * 1000. The scale is not absolutely precise and errors of 2% have been
     * measured.
     *
     * @return
     */
    public double getScaleDenominator() {
        if (adjustedExtent == null) {
            return 0;
        } else {
            double metersByPixel = 0.0254 / dpi;
            double imageMeters = getWidth() * metersByPixel;

            return adjustedExtent.getWidth() / imageMeters;
        }
    }

    public void addTransformListener(TransformListener listener) {
        listeners.add(listener);
    }

    public void removeTransformListener(TransformListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void transform(Coordinate src, Point2D dest) {
        dest.setLocation(src.x, src.y);
        trans.transform(dest, dest);
    }

    public ShapeWriter getShapeWriter() {
        if (converter == null) {
            converter = new ShapeWriter(this);
        }
        return converter;
    }

    /**
     * @param geom
     * @return
     */
    public Shape getShape(Geometry geom) {

        Rectangle2DDouble rectangle2dDouble = toPixel(geom.getEnvelopeInternal());

        if ((rectangle2dDouble.getHeight() <= 1)
                && (rectangle2dDouble.getWidth() <= 1)) {
            return rectangle2dDouble;
        }

        //return new LiteShape(geom, this.trans, true);
        return getShapeWriter().toShape(geom);
    }

    public void redraw() {
        for (TransformListener listener : listeners) {
            listener.extentChanged(this.adjustedExtent, this);
        }
    }
}
