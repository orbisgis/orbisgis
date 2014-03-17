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
package org.orbisgis.coremap.renderer.se.common;

import com.kitfox.svg.app.beans.SVGIcon;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import net.opengis.se._2_0.core.ExternalGraphicType;
import net.opengis.se._2_0.core.MarkGraphicType;
import net.opengis.se._2_0.core.VariableOnlineResourceType;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.graphic.ExternalGraphicSource;
import org.orbisgis.coremap.renderer.se.graphic.MarkGraphicSource;
import org.orbisgis.coremap.renderer.se.graphic.ViewBox;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.string.StringParameter;
import org.orbisgis.coremap.renderer.se.visitors.FeaturesVisitor;

/**
 * This class intends to make the link between an online image and the current symbolizing tree. It can be used for
 * constant symbols and for classification. Indeed, the inner URL is stored in a StringParameter. Consequently, it can
 * be computed through a SE String function.
 * In order to improve performances, this class embeds two image caches : one for SVG images, the other one for raster
 * images. If the underlying StringParameter changes, these caches are emptied in order to avoid incoherences between
 * this class content and what is drawn on the map.
 * @author Maxence Laurent
 * @author Alexis Guéganno
 */
public class VariableOnlineResource extends AbstractSymbolizerNode implements ExternalGraphicSource, MarkGraphicSource {

    private StringParameter url;
    private Map<URL,PlanarImage> imageCache = new HashMap<URL,PlanarImage>();
    private Map<URL,Rectangle2D.Double> jaiBounds = new HashMap<URL,Rectangle2D.Double>();
    private Map<URI,SVGIcon> svgCache = new HashMap<URI,SVGIcon>();
    private Map<URI,Rectangle2D.Double> svgBounds = new HashMap<URI,Rectangle2D.Double>();


    /**
     * Builds a {@code VariableOnlineResource} that has an empty URL and caches.
     */
    public VariableOnlineResource() {
        url = null;
    }

    /**
     * Builds a {@code VariableOnlineResource} whose image is stored at {@code url}.
     * @param url The URL as a {@link StringParameter}. This way, it is possible to change the image according to
     *            the processed data.
     * @throws MalformedURLException
     */
    public VariableOnlineResource(StringParameter url) throws MalformedURLException {
        this.url = url;
        this.url.setParent(this);
    }

    /**
     * Builds a new {@code VariableOnlineResource} from its JaXB representation?
     * @param onlineResource The JaXB representation of a {@code VariableOnlineResource}.
     * @throws MalformedURLException If the given JaXB object contains malformed URLs.
     * @throws InvalidStyle If the input object can't be recognized as a valid SE style element.
     */
    public VariableOnlineResource(VariableOnlineResourceType onlineResource)
            throws MalformedURLException, InvalidStyle {
        this.url = SeParameterFactory.createStringParameter(onlineResource.getHref());
        this.url.setParent(this);
    }

    /**
     * Gets the inner StringParameter that stores the URL(s) used to get the image(s) backed by this object.
     * @return The inner StringParameter.
     */
    public StringParameter getUrl() {
        return url;
    }

    /**
     * Sets the inner StringParameter that stores the URL(s) used to get the image(s) backed by this object.
     * @param url The new inner StringParameter.
     */
    public void setUrl(StringParameter url) {
        this.url = url;
        this.url.setParent(this);
    }

    /**
     * Gets the {@code PlanarImage} associated to a particular parameter configuration.
     * @param map The input configuration.
     * @return The {@code PlanarImage} for the given configuration
     * @throws ParameterException If the given configuration can't be processed.
     */
    public PlanarImage getPlanarJAI(Map<String, Object> map) throws ParameterException {
        try {
            URL link = new URL(url.getValue(map));
            if (!imageCache.containsKey(link)) {
                PlanarImage raw = JAI.create("url", link);
                imageCache.put(link, raw);
                Logger.getLogger(VariableOnlineResource.class.getName()).log(Level.INFO, "Download ExternalGraphic from: {0}", url);
            }
            return imageCache.get(link);
        } catch (Exception ex) {
            throw new ParameterException("Can't process the input URL",ex);
        }
    }

    /**
     * Gets the bounds of this {@code VariableOnlineResource} for the given configuration.
     * @param viewBox The ViewBox of the symbol.
     * @param map The input configuration.
     * @param mt The current {@link MapTransform}.
     * @param mimeType The input MIME type.
     * @return The bounds oif the image.
     * @throws ParameterException
     */
    public Rectangle2D.Double getJAIBounds(ViewBox viewBox,
                                           Map<String, Object> map, MapTransform mt,
                                           String mimeType) throws ParameterException {
        PlanarImage raw = getPlanarJAI(map);
        double width = raw.getWidth();
        double height = raw.getHeight();
        if (viewBox != null && mt != null && viewBox.usable()) {
            FeaturesVisitor fv = new FeaturesVisitor();
            viewBox.acceptVisitor(fv);
            if (map == null && !fv.getResult().isEmpty()) {
                throw new ParameterException("View box depends on feature"); // TODO I18n
            }
            Point2D dim = viewBox.getDimensionInPixel(map, height, width, mt.getScaleDenominator(), mt.getDpi());
            double effectiveWidth = dim.getX();
            double effectiveHeight = dim.getY();
            if (effectiveWidth > 0 && effectiveHeight > 0) {
                Rectangle2D.Double rect = new Rectangle2D.Double(-effectiveWidth / 2, -effectiveHeight / 2, effectiveWidth, effectiveHeight);
                try{
                    jaiBounds.put(new URL(url.getValue(map)),rect);
                } catch (MalformedURLException mue){
                    throw new ParameterException("Can't process the input URL", mue);
                }
                return rect;
            }
        }
        // Others cases => native image bounds
        return new Rectangle2D.Double(-width / 2, -height / 2, width, height);
    }

    /**
     * Gets the {@code SVGIcon} associated to a particular parameter configuration.
     * @param map The input configuration.
     * @return The {@code SVGIcon} for the given configuration
     * @throws ParameterException If the given configuration can't be processed.
     */
    public SVGIcon getSVGIcon(Map<String,Object> map) throws ParameterException {
        try {
            URI uri = new URI(url.getValue(map));
            if(!svgCache.containsKey(uri)){
                SVGIcon svgIcon = new SVGIcon();
                svgIcon.setSvgURI(new URI(url.getValue(map)));
                svgIcon.setAntiAlias(true);
                svgCache.put(uri,svgIcon);
            }
            return svgCache.get(uri);
        } catch (URISyntaxException e) {
            throw new ParameterException("Can't process the input URI", e);
        }
    }


    /**
     * Gets the bounds of this {@code VariableOnlineResource} for the given configuration.
     * @param viewBox The ViewBox of the symbol.
     * @param map The input configuration.
     * @param mt The current {@link MapTransform}.
     * @param mimeType The input MIME type.
     * @return The bounds oif the image.
     * @throws ParameterException
     */
    public Rectangle2D.Double getSvgBounds(ViewBox viewBox,
                                           Map<String,Object> map, MapTransform mt,
                                           String mimeType) throws ParameterException {
        SVGIcon svgIcon = getSVGIcon(map);
        double svgInitialHeight = (double) svgIcon.getIconHeight();
        double svgInitialWidth = (double) svgIcon.getIconWidth();
        if (viewBox != null && mt != null && viewBox.usable()) {
            FeaturesVisitor fv = new FeaturesVisitor();
            viewBox.acceptVisitor(fv);
            if (map == null && !fv.getResult().isEmpty()) {
                throw new ParameterException("View box depends on feature");
            }
            Point2D dim = viewBox.getDimensionInPixel(map, svgInitialWidth,
                    svgInitialHeight, mt.getScaleDenominator(), mt.getDpi());
            double effectiveWidth = dim.getX();
            double effectiveHeight = dim.getY();
            Rectangle2D.Double rect;
            if (effectiveHeight > 0 && effectiveWidth > 0) {
                rect = new Rectangle2D.Double(-effectiveWidth / 2, -effectiveHeight / 2, effectiveWidth, effectiveHeight);
            } else {
                double width = svgInitialWidth;
                double height = svgInitialHeight;
                rect = new Rectangle2D.Double(-width / 2, -height / 2, width, height);
            }
            try {
                URI u = new URI(url.getValue(map));
                svgBounds.put(u,rect);
            } catch (URISyntaxException e) {
                throw new ParameterException("Can't process the input URI",e);
            }
            return rect;
        } else {
            double width = svgInitialWidth;
            double height = svgInitialHeight;
            return new Rectangle2D.Double(-width / 2, -height / 2, width, height);
        }
    }


    @Override
    public Rectangle2D.Double updateCacheAndGetBounds(ViewBox viewBox,
                                                      Map<String,Object> map, MapTransform mt,
                                                      String mimeType) throws ParameterException {
        if (mimeType != null && mimeType.equalsIgnoreCase("image/svg+xml")) {
            return getSvgBounds(viewBox, map, mt, mimeType);
        } else {
            return getJAIBounds(viewBox, map, mt, mimeType);
        }
    }

    /*
     * Draw the svg on g2
     */

    /**
     *
     * @param g2
     * @param map
     * @param at
     * @param opacity
     * @throws ParameterException
     */
    public void drawSVG(Graphics2D g2, Map<String,Object> map, AffineTransform at, double opacity)
            throws ParameterException {
        try {
            AffineTransform fat = new AffineTransform(at);
            URI u = new URI(url.getValue(map));
            Rectangle2D.Double rect = svgBounds.get(u);
            SVGIcon svgIcon = getSVGIcon(map);

            if (rect != null) {
                double effectiveWidth = rect.getWidth();
                double effectiveHeight = rect.getHeight();
                svgIcon.setPreferredSize(new Dimension((int) (effectiveWidth + 0.5), (int) (effectiveHeight + 0.5)));
                fat.concatenate(AffineTransform.getTranslateInstance(-effectiveWidth / 2, -effectiveHeight / 2));
            } else {
                double svgInitialWidth = svgIcon.getIconWidth();
                double svgInitialHeight = svgIcon.getIconHeight();
                svgIcon.setPreferredSize(new Dimension((int) (svgInitialWidth + 0.5), (int) (svgInitialHeight + 0.5)));
                fat.concatenate(AffineTransform.getTranslateInstance(-svgInitialWidth / 2, -svgInitialHeight / 2));
            }
            svgIcon.setScaleToFit(true);
            AffineTransform atMedia = new AffineTransform(g2.getTransform());
            g2.transform(fat);

            svgIcon.paintIcon((Component) null, g2, 0, 0);
            g2.setTransform(atMedia);
        } catch (URISyntaxException e){
            throw new ParameterException("Can't process the input URI",e);
        }
    }

    /**
     * Draw an image on the map with JAI.
     * @param g2 The Graphics used to draw the symbol.
     * @param map The input parameters.
     * @param at The AffineTransform used on the input image
     * @param mt The MapTransform used to put the resulting image on the map.
     * @param opacity The opacity of the image.
     * @throws ParameterException
     */
    public void drawJAI(Graphics2D g2, Map<String,Object> map, AffineTransform at, MapTransform mt,
                        double opacity) throws ParameterException{
        try{
            AffineTransform fat = new AffineTransform(at);
            PlanarImage rawImage = getPlanarJAI(map);
            double width = rawImage.getWidth();
            double height = rawImage.getHeight();
            Rectangle2D.Double rect = jaiBounds.get(new URL(url.getValue(map)));

            if (rect != null) {
                double ratioX = rect.getWidth() / width;
                double ratioY = rect.getHeight() / height;
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
        } catch (MalformedURLException e){
            throw new ParameterException("Can't process the input URL",e);
        }
    }


    @Override
    public void draw(Graphics2D g2, Map<String,Object> map, AffineTransform at, MapTransform mt,
                     double opacity, String mimeType) throws ParameterException {
        if (mimeType != null && mimeType.equalsIgnoreCase("image/svg+xml")) {
            drawSVG(g2, map, at, opacity);
        } else {
            drawJAI(g2, map, at, mt, opacity);
        }
    }

    @Override
    public void setJAXBSource(ExternalGraphicType e) {
        VariableOnlineResourceType o = new VariableOnlineResourceType();

        o.setHref(url.getJAXBParameterValueType());
        e.setOnlineResource(o);
    }


    public Font getFont(Map<String,Object> map) {
        InputStream iStream;
        try {
            URL u = new URL(this.url.getValue(map));
            iStream = u.openStream();
            return Font.createFont(Font.TRUETYPE_FONT, iStream);
        } catch (FontFormatException ex) {
        } catch (ParameterException ex) {
        } catch (IOException ex) {
        }
        return null;
    }


    private Shape getTrueTypeGlyph(ViewBox viewBox,
                                   Map<String,Object> map,
                                   Double scale, Double dpi,
                                   RealParameter markIndex) throws ParameterException, IOException {

        try {
            URL u = new URL(this.url.getValue(map));
            InputStream iStream = u.openStream();
            Font font = Font.createFont(Font.TRUETYPE_FONT, iStream);
            iStream.close();

            double value = markIndex.getValue(map);

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
                Point2D dim = viewBox.getDimensionInPixel(map, height, width, scale, dpi);
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
            Logger.getLogger(VariableOnlineResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new ParameterException(ex);
        }
    }

    @Override
    public void update(){
        svgBounds = new HashMap<URI,Rectangle2D.Double>();
        jaiBounds = new HashMap<URL,Rectangle2D.Double>();
        svgCache = new HashMap<URI,SVGIcon>();
        imageCache = new HashMap<URL,PlanarImage>();
        SymbolizerNode par = getParent();
        if(par != null) {
            getParent().update();
        }
    }


    @Override
    public Shape getShape(ViewBox viewBox, Map<String,Object> map, Double scale,
            Double dpi, RealParameter markIndex, String mimeType) throws ParameterException, IOException {

        if (mimeType != null) {
            if (mimeType.equalsIgnoreCase("application/x-font-ttf")) {
                return getTrueTypeGlyph(viewBox, map, scale, dpi, markIndex);
            }
        }

        throw new ParameterException("Unknown MIME type: " + mimeType);
    }

    public void setJAXBSource(MarkGraphicType m) {
        VariableOnlineResourceType o = new VariableOnlineResourceType();
        o.setHref(url.getJAXBParameterValueType());
        m.setOnlineResource(o);
    }


    @Override
    public double getDefaultMaxWidth(Map<String,Object> map,
                                     Double scale, Double dpi,
                                     RealParameter markIndex, String mimeType)
            throws IOException, ParameterException {
        if (mimeType != null) {
            if (mimeType.equalsIgnoreCase("application/x-font-ttf")) {
                return getTrueTypeGlyphMaxSize(map, /*scale, dpi,*/ markIndex);
            }
        }
        return 0.0;

    }


    private double getTrueTypeGlyphMaxSize(Map<String,Object> map,
                                           /*Double scale, Double dpi,*/ RealParameter markIndex)
            throws IOException, ParameterException {
        try {
            URL u = new URL(url.getValue(map));
            InputStream iStream = u.openStream();
            Font font = Font.createFont(Font.TRUETYPE_FONT, iStream);
            iStream.close();
            double value = markIndex.getValue(map);
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
            Logger.getLogger(VariableOnlineResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new ParameterException(ex);
        }
    }


    @Override
    public List<SymbolizerNode> getChildren() {
        List<SymbolizerNode> ret = new ArrayList<SymbolizerNode>();
        ret.add(url);
        return ret;
    }
}
