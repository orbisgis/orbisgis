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
package org.orbisgis.core.renderer.se.common;

import com.kitfox.svg.app.beans.SVGIcon;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.InterpolationBicubic2;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import org.gdms.data.DataSource;

import org.orbisgis.core.map.MapTransform;

import net.opengis.ows._2.OnlineResourceType;
import net.opengis.se._2_0.core.ExternalGraphicType;
import net.opengis.se._2_0.core.MarkGraphicType;
import net.opengis.se._2_0.core.VariableOnlineResourceType;
import org.orbisgis.core.renderer.se.graphic.ExternalGraphicSource;
import org.orbisgis.core.renderer.se.graphic.MarkGraphicSource;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 * An {@code OnlineResource} is used to keep a reference to an graphic resource
 * that is sotred on disk, or in a remote location, as an image.</p>
 * <p>An online resource is directly dependant on an URL that will be used to
 * retrieve the image we need.
 * @author maxence, alexis
 * @todo implements MarkGraphicSource
 */
public class OnlineResource implements ExternalGraphicSource, MarkGraphicSource {


    private URI uri;
    private PlanarImage rawImage;
    private SVGIcon svgIcon;
    private Double effectiveWidth;
    private Double effectiveHeight;
    private Double svgInitialWidth;
    private Double svgInitialHeight;

    /**
     * Build a new {@code OnlineResource}
     */
    public OnlineResource() {
        uri = null;
        svgIcon = null;
    }

    /**
     * Build a new {@code OnlineResource} with the given String, that is supposed
     * to be an URL.
     * @param url
     * @throws MalformedURLException
     * If {@code url} can't be used to build an {@code URL} instance.
     */
    public OnlineResource(String url) throws URISyntaxException {
        this.uri = new URI(url);
        svgIcon = null;
    }

    /**
     * Build an {@code OnlineResource} from the given {@code OnlineResourceType}
     * .
     * @param onlineResource
     * @throws MalformedURLException
     * If the href embedded in {@code onlineResource} can't be used to build an
     * {@code URL} instance.
     */
    public OnlineResource(OnlineResourceType onlineResource) throws URISyntaxException {
        this.uri = new URI(onlineResource.getHref());
        svgIcon = null;
    }

    /**
     * Get the {@code URL} contained in this {@code OnlineResource}.
     * @return
     * An {@code URL} instance, that points to the location where to find the
     * resource.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Set the {@code URL} contained in this {@code OnlineResource}.
     * @param url
     * @throws MalformedURLException
     */
    public void setUri(String url) throws URISyntaxException {
        if (url == null || url.isEmpty()) {
            this.uri = null;
        } else {
            this.uri = new URI(url);
        }
    }

    //@Override
    /**
     * @deprecated
     * @param viewBox
     * @param sds
     * @param fid
     * @param mt
     * @param mimeType
     * @return
     * @throws IOException
     * @throws ParameterException
     */
    public RenderedImage getPlanarImage(ViewBox viewBox, DataSource sds, long fid, MapTransform mt, String mimeType)
            throws IOException, ParameterException {

        if (mimeType != null && mimeType.equalsIgnoreCase("image/svg+xml")) {
            return getSvgImage(viewBox, sds, fid, mt, mimeType);
        } else {
            return getJAIImage(viewBox, sds, fid, mt, mimeType);
        }
    }

    /**
     * Get the boundnig box of this {@code OnlineResource} as a {@code
     * Rectangle2D.Double} instance.
     * @param viewBox
     * @param sds
     * @param fid
     * @param mt
     * @param mimeType
     * @return
     * @throws ParameterException
     */
    public Rectangle2D.Double getJAIBounds(ViewBox viewBox, DataSource sds, long fid, MapTransform mt, String mimeType) throws ParameterException {
        try {
            if (rawImage == null) {
                rawImage = JAI.create("url", uri);
                Logger.getLogger(OnlineResource.class.getName()).log(Level.INFO, "Download ExternalGraphic from: {0}", uri);
            }
        } catch (Exception ex) {
            throw new ParameterException(ex);
        }

        double width = rawImage.getWidth();
        double height = rawImage.getHeight();

        if (viewBox != null && mt != null && viewBox.usable()) {
            if (sds == null && !viewBox.dependsOnFeature().isEmpty()) {
                throw new ParameterException("View box depends on feature"); // TODO I18n 
            }

            try {

                Point2D dim = viewBox.getDimensionInPixel(sds, fid, height, width, mt.getScaleDenominator(), mt.getDpi());

                effectiveWidth = dim.getX();
                effectiveHeight = dim.getY();

                if (effectiveWidth > 0 && effectiveHeight > 0) {
                    return new Rectangle2D.Double(-effectiveWidth / 2, -effectiveHeight / 2, effectiveWidth, effectiveHeight);
                } else {
                    effectiveWidth = null;
                    effectiveHeight = null;
                }
            } catch (Exception ex) {
                throw new ParameterException(ex);
            }
        }
        // Others cases => native image bounds
        return new Rectangle2D.Double(-width / 2, -height / 2, width, height);
    }

    /**
     *
     * @param viewBox
     * @param sds
     * @param fid
     * @param mt
     * @param mimeType
     * @return
     * @throws ParameterException
     */
    public Rectangle2D.Double getSvgBounds(ViewBox viewBox, DataSource sds,
            long fid, MapTransform mt, String mimeType) throws ParameterException {
            /*
             * Fetch SVG if not already done
             */
            if (svgIcon == null) {
                svgIcon = new SVGIcon();
                svgIcon.setSvgURI(uri);
                svgIcon.setAntiAlias(true);

                this.svgInitialHeight = (double) svgIcon.getIconHeight();
                this.svgInitialWidth = (double) svgIcon.getIconWidth();
            }

            if (viewBox != null && mt != null && viewBox.usable()) {
                if (sds == null && !viewBox.dependsOnFeature().isEmpty()) {
                    throw new ParameterException("View box depends on feature"); // TODO I18n
                }


                Point2D dim = viewBox.getDimensionInPixel(sds, fid, svgInitialWidth, svgInitialHeight, mt.getScaleDenominator(), mt.getDpi());

                effectiveWidth = dim.getX();
                effectiveHeight = dim.getY();

                if (effectiveHeight > 0 && effectiveWidth > 0) {
                    return new Rectangle2D.Double(-effectiveWidth / 2, -effectiveHeight / 2, effectiveWidth, effectiveHeight);
                } else {

                    double width = svgInitialWidth;
                    double height = svgInitialHeight;
                    effectiveWidth = null;
                    effectiveHeight = null;
                    return new Rectangle2D.Double(-width / 2, -height / 2, width, height);
                }
            } else {
                double width = svgInitialWidth;
                double height = svgInitialHeight;
                return new Rectangle2D.Double(-width / 2, -height / 2, width, height);
            }
    }

    @Override
    public Rectangle2D.Double updateCacheAndGetBounds(ViewBox viewBox, DataSource sds,
                long fid, MapTransform mt, String mimeType) throws ParameterException {
        effectiveWidth = null;
        effectiveHeight = null;
        if (mimeType != null && mimeType.equalsIgnoreCase("image/svg+xml")) {
            return getSvgBounds(viewBox, sds, fid, mt, mimeType);
        } else {
            return getJAIBounds(viewBox, sds, fid, mt, mimeType);
        }
    }

    /**
     * Draw the svg linked to this {@code Onlineresource} in the Graphics2D g2.
     * @param g2
     * @param at
     * @param opacity
     */
    public void drawSVG(Graphics2D g2, AffineTransform at, double opacity) {
        AffineTransform fat = new AffineTransform(at);

        if (effectiveHeight != null && effectiveWidth != null) {
            svgIcon.setPreferredSize(new Dimension((int) (effectiveWidth + 0.5), (int) (effectiveHeight + 0.5)));
            fat.concatenate(AffineTransform.getTranslateInstance(-effectiveWidth / 2, -effectiveHeight / 2));
        } else {
            svgIcon.setPreferredSize(new Dimension((int) (svgInitialWidth + 0.5), (int) (svgInitialHeight + 0.5)));
            fat.concatenate(AffineTransform.getTranslateInstance(-svgInitialWidth / 2, -svgInitialHeight / 2));
        }
        svgIcon.setScaleToFit(true);
        AffineTransform atMedia = new AffineTransform(g2.getTransform());
        g2.transform(fat);

        svgIcon.paintIcon((Component) null, g2, 0, 0);
        g2.setTransform(atMedia);
    }

    /**
     * Draw the image linked to this {@code Onlineresource} in the Graphics2D
     * g2.
     * @param g2
     * @param at
     * @param mt
     * @param opacity
     */
    public void drawJAI(Graphics2D g2, AffineTransform at, MapTransform mt, double opacity) {
        AffineTransform fat = new AffineTransform(at);
        double width = rawImage.getWidth();
        double height = rawImage.getHeight();

        if (effectiveHeight != null && effectiveWidth != null) {
            double ratioX = effectiveWidth / width;
            double ratioY = effectiveHeight / height;

            RenderedOp img;

            if (ratioX > 1.0 || ratioY > 1.0) {
                img = JAI.create("scale", rawImage,
                        (float) ratioX, (float) ratioY,
                        0.0f, 0.0f,
                        InterpolationBicubic2.getInstance(InterpolationBicubic2.INTERP_BICUBIC_2),
                        mt.getRenderingHints());
            } else {
                img = JAI.create("SubsampleAverage", rawImage, ratioX, ratioY, mt.getRenderingHints());
            }
            fat.concatenate(AffineTransform.getTranslateInstance(-img.getWidth() / 2.0, -img.getHeight() / 2.0));
            g2.drawRenderedImage(img, fat);
        } else {
            fat.concatenate(AffineTransform.getTranslateInstance(-width / 2.0, -height / 2.0));
            g2.drawRenderedImage(rawImage, fat);
        }
    }

    @Override
    public void draw(Graphics2D g2, AffineTransform at, MapTransform mt, double opacity, String mimeType) {
        if (mimeType != null && mimeType.equalsIgnoreCase("image/svg+xml")) {
            drawSVG(g2, at, opacity);
        } else {
            drawJAI(g2, at, mt, opacity);
        }
    }

    /**
     * @deprecated
     */
    public RenderedImage getSvgImage(ViewBox viewBox, DataSource sds, long fid, MapTransform mt, String mimeType)
            throws IOException, ParameterException {
            SVGIcon icon = new SVGIcon();
            icon.setSvgURI(uri);
            BufferedImage img;

            if (viewBox != null && mt != null && viewBox.usable()) {
                if (sds == null && !viewBox.dependsOnFeature().isEmpty()) {
                    throw new ParameterException("View box depends on feature"); // TODO I18n
                }

                double width = icon.getIconWidth();
                double height = icon.getIconHeight();

                Point2D dim = viewBox.getDimensionInPixel(sds, fid, height, width, mt.getScaleDenominator(), mt.getDpi());

                double widthDst = dim.getX();
                double heightDst = dim.getY();

                if (widthDst > 0 && heightDst > 0) {
                    img = new BufferedImage((int) (widthDst + 0.5), (int) (heightDst + 0.5), BufferedImage.TYPE_4BYTE_ABGR);
                    icon.setPreferredSize(new Dimension((int) (widthDst + 0.5), (int) (heightDst + 0.5)));
                    icon.setScaleToFit(true);
                } else {
                    img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                }
            } else {
                img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            }
            icon.setAntiAlias(true);
            Graphics2D g2 = (Graphics2D) img.getGraphics();
            if (mt != null) {
                g2.addRenderingHints(mt.getRenderingHints());
            }
            icon.paintIcon((Component) null, g2, 0, 0);
            return img;
    }

    /**
     * @deprecated
     * @param viewBox
     * @param sds
     * @param fid
     * @param mt
     * @param mimeType
     * @return
     * @throws IOException
     * @throws ParameterException
     */
    public PlanarImage getJAIImage(ViewBox viewBox, DataSource sds, long fid, MapTransform mt, String mimeType)
            throws IOException, ParameterException {

        try {
            if (rawImage == null) {
                rawImage = JAI.create("url", uri);
                Logger.getLogger(OnlineResource.class.getName()).log(Level.INFO, "Download ExternalGraphic from: {0}", uri);
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }

        PlanarImage img = rawImage;


        if (viewBox != null && mt != null && viewBox.usable()) {
            if (sds == null && !viewBox.dependsOnFeature().isEmpty()) {
                throw new ParameterException("View box depends on feature"); // TODO I18n
            }

            try {
                double width = img.getWidth();
                double height = img.getHeight();

                Point2D dim = viewBox.getDimensionInPixel(sds, fid, height, width, mt.getScaleDenominator(), mt.getDpi());

                double widthDst = dim.getX();
                double heightDst = dim.getY();

                if (widthDst > 0 && heightDst > 0) {
                    double ratio_x = widthDst / width;
                    double ratio_y = heightDst / height;

                    if (ratio_x > 1.0 || ratio_y > 1.0) {
                        return JAI.create("scale", rawImage,
                                (float) ratio_x, (float) ratio_y,
                                0.0f, 0.0f,
                                InterpolationBicubic2.getInstance(InterpolationBicubic2.INTERP_BICUBIC_2),
                                mt.getRenderingHints());
                    } else {
                        //return JAI.create("SubsampleAverage", pb, mt.getRenderingHints());
                        return JAI.create("SubsampleAverage", img, ratio_x, ratio_y, mt.getRenderingHints());
                    }
                } else {
                    return img;
                }

            } catch (Exception ex) {
                throw new ParameterException(ex);
            }

        } else {
            return img;
        }
    }

    @Override
    public void setJAXBSource(ExternalGraphicType e) {
        VariableOnlineResourceType o = new VariableOnlineResourceType();

        
        o.setHref(SeParameterFactory.createParameterValueType(uri.toString()));

        e.setOnlineResource(o);
    }

    /**
     * Get the {@code Font} linked with this {@code OnlineResource}.
     * @return
     * A {@code Font} instance if the url identifies a  valid font, null
     * otherwise.
     */
    public Font getFont() {
        InputStream iStream = null;
        try {
            iStream = uri.toURL().openStream();
            return Font.createFont(Font.TRUETYPE_FONT, iStream);
        } catch (FontFormatException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                iStream.close();
            } catch (IOException ex) {
            }
        }
        return null;
    }

    private Shape getTrueTypeGlyph(ViewBox viewBox, DataSource sds,
            long fid, Double scale, Double dpi, RealParameter markIndex) throws ParameterException, IOException {

        try {
            InputStream iStream = uri.toURL().openStream();
            Font font = Font.createFont(Font.TRUETYPE_FONT, iStream);
            iStream.close();

            double value = markIndex.getValue(sds, fid);

            char[] data = {(char) value};

            String text = String.copyValueOf(data);

            // Scale is used to have an high resolution
            AffineTransform at = AffineTransform.getTranslateInstance(0, 0);

            FontRenderContext fontCtx = new FontRenderContext(at, true, true);
            TextLayout tl = new TextLayout(text, font, fontCtx);

            Shape glyphOutline = tl.getOutline(at);

            Rectangle2D bounds2D = glyphOutline.getBounds2D();

            double width = bounds2D.getWidth();
            double height = bounds2D.getHeight();

            if (viewBox != null && viewBox.usable()) {
                Point2D dim = viewBox.getDimensionInPixel(sds, fid, height, width, scale, dpi);
                if (Math.abs(dim.getX()) <= 0 || Math.abs(dim.getY()) <= 0) {
                    return null;
                }

                at = AffineTransform.getScaleInstance(dim.getX() / width,
                        dim.getY() / height);

                fontCtx = new FontRenderContext(at, true, true);
                tl = new TextLayout(text, font, fontCtx);
                glyphOutline = tl.getOutline(at);

            }
            Rectangle2D gb = glyphOutline.getBounds2D();
            at = AffineTransform.getTranslateInstance(-gb.getCenterX(), -gb.getCenterY());

            return at.createTransformedShape(glyphOutline);

        } catch (FontFormatException ex) {
            Logger.getLogger(OnlineResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new ParameterException(ex);
        }
    }

    @Override
    public Shape getShape(ViewBox viewBox, DataSource sds, long fid, Double scale, Double dpi, RealParameter markIndex, String mimeType) throws ParameterException, IOException {

        if (mimeType != null) {
            if (mimeType.equalsIgnoreCase("application/x-font-ttf")) {
                return getTrueTypeGlyph(viewBox, sds, fid, scale, dpi, markIndex);
            }
        }

        throw new ParameterException("Unknown MIME type: " + mimeType);
    }

    //@Override
    public void setJAXBSource(MarkGraphicType m) {
        VariableOnlineResourceType o = new VariableOnlineResourceType();

        o.setHref(SeParameterFactory.createParameterValueType(uri.toString()));

        m.setOnlineResource(o);

    }

    @Override
    public double getDefaultMaxWidth(DataSource sds, long fid,
            Double scale, Double dpi, RealParameter markIndex, String mimeType)
            throws IOException, ParameterException {

        if (mimeType != null) {
            if (mimeType.equalsIgnoreCase("application/x-font-ttf")) {
                return getTrueTypeGlyphMaxSize(sds, fid, /*scale, dpi,*/ markIndex);
            }
        }

        return 0.0;

    }

    private double getTrueTypeGlyphMaxSize(DataSource sds, long fid,
            /*Double scale, Double dpi,*/ RealParameter markIndex)
            throws IOException, ParameterException {
        try {
            InputStream iStream = uri.toURL().openStream();
            Font font = Font.createFont(Font.TRUETYPE_FONT, iStream);
            iStream.close();

            double value = markIndex.getValue(sds, fid);
            char[] data = {(char) value};

            String text = String.copyValueOf(data);

            // Scale is used to have an high resolution
            AffineTransform at = AffineTransform.getTranslateInstance(0, 0);

            FontRenderContext fontCtx = new FontRenderContext(at, true, true);
            TextLayout tl = new TextLayout(text, font, fontCtx);

            Shape glyphOutline = tl.getOutline(at);

            Rectangle2D bounds2D = glyphOutline.getBounds2D();

            return Math.max(bounds2D.getWidth(), bounds2D.getHeight());

        } catch (FontFormatException ex) {
            Logger.getLogger(OnlineResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new ParameterException(ex);
        }
    }
}
