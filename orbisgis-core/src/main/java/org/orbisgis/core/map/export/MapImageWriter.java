package org.orbisgis.core.map.export;

import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;
import com.vividsolutions.jts.geom.Envelope;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.ImageRenderer;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.progress.ProgressMonitor;

import javax.media.jai.JAI;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility class to save the image provided by the renderer into a PNG or TIFF binary stream.
 *
 * @author Maxence Laurent
 * @author Tony MARTIN
 * @author Alexis Guéganno
 * @author Nicolas Fortin
 */
public class MapImageWriter {
    public enum Format {TIFF, PNG}
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
     * @param rootLayer
     */
    public MapImageWriter(ILayer rootLayer) {
        this.rootLayer = rootLayer;
        boundingBox = rootLayer.getEnvelope();
    }

    /**
     * @return Get the envelope of the final image, in the projection system used by layers data.
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
     * @param boundingBox Set the bounding box of the final image, in the projection system used by layers data.
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
     * @param adjustExtent If true, it avoid image distortion by updating the Envelope of rendering according to width and height.
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

    public void write(OutputStream out, ProgressMonitor pm) throws IOException {
        MapTransform mt = new MapTransform();
        mt.setAdjustExtent(adjustExtent);
        double dpi = MILLIMETERS_BY_INCH / pixelSize;
        mt.setDpi(dpi);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        mt.setImage(img);
        mt.setExtent(boundingBox);
        Graphics2D g2 = img.createGraphics();
        if(backgroundColor != null) {
            g2.setBackground(backgroundColor);
            g2.clearRect(0, 0, width, height);
        }
        Renderer renderer = new ImageRenderer();
        renderer.draw(mt, g2, width, height, rootLayer, pm);
        int dpm = (int) (1000 / pixelSize + 1);
        if (format == Format.PNG) {
            // Encode in PNG
            PNGEncodeParam pEnc = PNGEncodeParam.getDefaultEncodeParam(img);
            pEnc.setPhysicalDimension(dpm, dpm, 1);

            JAI.create("Encode", img, out, "PNG", pEnc);
            out.close();
        } else {
            // Encode in TIFF
            long[] resolution = { dpm, 1 };
            TIFFField xRes = new TIFFField(X_RES_TAG,
                    TIFFField.TIFF_RATIONAL, 1, new long[][] { resolution });
            TIFFField yRes = new TIFFField(Y_RES_TAG,
                    TIFFField.TIFF_RATIONAL, 1, new long[][] { resolution });
            TIFFEncodeParam tep = new TIFFEncodeParam();
            tep.setExtraFields(new TIFFField[] { xRes, yRes });
            JAI.create("Encode", img, out, "TIFF", tep);
            out.close();
        }
        g2.dispose();
    }
}
