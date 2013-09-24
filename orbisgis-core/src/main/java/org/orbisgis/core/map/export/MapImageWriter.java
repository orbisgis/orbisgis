/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly: info_at_ orbisgis.org
 */
package org.orbisgis.core.map.export;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;
import com.vividsolutions.jts.geom.Envelope;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.ImageRenderer;
import org.orbisgis.progress.ProgressMonitor;

import javax.media.jai.JAI;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.orbisgis.core.renderer.PdfRenderer;

/**
 * Utility class to save the image provided by the renderer into a PNG or TIFF
 * binary stream.
 *
 * @author Maxence Laurent
 * @author Tony MARTIN
 * @author Alexis Guéganno
 * @author Nicolas Fortin
 */
public class MapImageWriter {

    public static enum Format {

        TIFF, PNG, JPEG, PDF
    }
    // Static properties
    public static final double DEFAULT_PIXEL_SIZE = 0.35;
    public static final int DEFAULT_WITH = 1280;
    public static final int DEFAULT_HEIGHT = 1024;
    public static final boolean DEFAULT_ADJUST_EXTENT = true;
    public static final double MILLIMETERS_BY_INCH = 25.4;
    public static Format DEFAULT_FORMAT = Format.PNG;
    private static final int X_RES_TAG = 282; // Binary file code index
    private static final int Y_RES_TAG = 283;
    // Properties
    private double pixelSize = DEFAULT_PIXEL_SIZE;
    private int width = DEFAULT_WITH;
    private int height = DEFAULT_HEIGHT;
    private Format format = DEFAULT_FORMAT;
    private boolean adjustExtent = DEFAULT_ADJUST_EXTENT;
    private Color backgroundColor;
    // Properties without default values
    private Envelope boundingBox;
    private final ILayer rootLayer;

    /**
     * Constructor
     *
     * @param rootLayer
     */
    public MapImageWriter(ILayer rootLayer) {
        this.rootLayer = rootLayer;
        boundingBox = rootLayer.getEnvelope();
    }

    /**
     * @return Get the envelope of the final image, in the projection system
     * used by layers data.
     */
    public Envelope getBoundingBox() {
        return boundingBox;
    }

    /**
     * @return The layer used for rendering.
     */
    public ILayer getRootLayer() {
        return rootLayer;
    }

    /**
     * @param boundingBox Set the bounding box of the final image, in the
     * projection system used by layers data.
     */
    public void setBoundingBox(Envelope boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * @return Image format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * @param format Format image use on binary stream.
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * @return Height of the final image
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param backgroundColor Background color of the image.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Unset background color in order to use transparency
     */
    public void unsetBackgroundColor() {
        this.backgroundColor = null;
    }

    /**
     * @param adjustExtent If true, it avoid image distortion by updating the
     * Envelope of rendering according to width and height.
     */
    public void setAdjustExtent(boolean adjustExtent) {
        this.adjustExtent = adjustExtent;
    }

    /**
     * @param height Height of the image in pixels.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return Width of the image in pixels.
     */
    public double getPixelSize() {
        return pixelSize;
    }

    /**
     * @param pixelSize Pixel size in millimeters
     */
    public void setPixelSize(double pixelSize) {
        this.pixelSize = pixelSize;
    }

    /**
     * @return Image width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width Image width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    public void write(FileOutputStream out, ProgressMonitor pm) throws IOException {
        MapTransform mt = new MapTransform();
        mt.setAdjustExtent(adjustExtent);
        double dpi = MILLIMETERS_BY_INCH / pixelSize;
        mt.setDpi(dpi);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        mt.setImage(img);
        mt.setExtent(boundingBox);
        int dpm = (int) (1000 / pixelSize + 1);
        Graphics2D g2 = null;
        switch (format) {
            case PNG:
                g2 = prepareImageRenderer(mt, img, pm);
                // Encode in PNG
                PNGEncodeParam pEnc = PNGEncodeParam.getDefaultEncodeParam(img);
                pEnc.setPhysicalDimension(dpm, dpm, 1);
                JAI.create("Encode", img, out, "PNG", pEnc);
                out.close();
                break;
            case JPEG:
                g2 = prepareImageRenderer(mt, img, pm);
                ImageIO.write(img, "jpeg", out);
                out.close();
                break;
            case PDF:
                exportAsPDF(out, mt, pm);
                break;
            default:
                g2 = prepareImageRenderer(mt, img, pm);
                // Encode in TIFF
                long[] resolution = {dpm, 1};
                TIFFField xRes = new TIFFField(X_RES_TAG,
                        TIFFField.TIFF_RATIONAL, 1, new long[][]{resolution});
                TIFFField yRes = new TIFFField(Y_RES_TAG,
                        TIFFField.TIFF_RATIONAL, 1, new long[][]{resolution});
                TIFFEncodeParam tep = new TIFFEncodeParam();
                tep.setExtraFields(new TIFFField[]{xRes, yRes});
                JAI.create("Encode", img, out, "TIFF", tep);
                out.close();
        }
        if (g2 != null) {
            g2.dispose();
        }
    }

    /**
     * Prepare the renderer to export the map as an image
     *
     *
     * @param mt
     * @param img
     * @param pm
     * @return a {@link Graphics2D} used to renderer the data
     */
    public Graphics2D prepareImageRenderer(MapTransform mt, BufferedImage img, ProgressMonitor pm) {
        Graphics2D g2 = img.createGraphics();
        if (backgroundColor != null) {
            g2.setBackground(backgroundColor);
            g2.clearRect(0, 0, width, height);
        }
        ImageRenderer renderer = new ImageRenderer();
        renderer.draw(mt, g2, width, height, rootLayer, pm);
        return g2;
    }

    /**
     * Export the layer as a vectorial PDF file
     * 
     * @param out
     * @param mt
     * @param pm
     * @throws IOException
     */
    public void exportAsPDF(FileOutputStream out, MapTransform mt, ProgressMonitor pm) throws IOException {
        Document document = new Document(new Rectangle(width, height));
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            PdfContentByte cb = writer.getDirectContent();

            PdfTemplate templateMap = cb.createTemplate(width,
                    height);

            Graphics2D g2dMap = templateMap.createGraphics(width,
                    height);
            if (backgroundColor != null) {
                g2dMap.setBackground(backgroundColor);
                g2dMap.clearRect(0, 0, width, height);
            }
            PdfRenderer renderer2 = new PdfRenderer(templateMap, width, height);

            renderer2.draw(mt, g2dMap, width, height, rootLayer, pm);
            g2dMap.dispose();

            cb.addTemplate(templateMap, 0, 0);

        } catch (DocumentException ex) {
            throw new IOException("Cannot create the pdf", ex);
        }
        document.close();
    }
}
