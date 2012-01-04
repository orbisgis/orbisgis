package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.io.IOException;

import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;

import net.opengis.se._2_0.core.ExternalGraphicType;
import net.opengis.se._2_0.core.ObjectFactory;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.UomNode;

import org.orbisgis.core.renderer.se.ViewBoxNode;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.common.VariableOnlineResource;

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 * An external graphic is an image such as JPG, PNG, SVG.
 * Available action on such a graphic are affine transformations.
 * There is no way to re-style the graphic but setting opacity. It is dependant
 * upon the following values : <p>
 * <ul><li>OnlineResource : An URI where to retrieve the remote graphic.
 * Exclusive with InlineContent.</li>
 * <li>InlineContent : The content of a graphic is included inline. Exclusive
 * with OnlineResource.</li>
 * <li>Format : the MIME type of this graphic.</li>
 * <li>uom : A unit of measure</li>
 * <li>ViewBox : A box where the graphic will be rendered.</li>
 * <li>Transform : The affine transformation to apply on the graphic.</li>
 * <li>Opacity : Alpha filter to apply on the graphic. It must be a double value,
 * in the [0;1] range.</li>
 * <li>Halo : The halo to draw around the graphic.</li>
 * </ul>
 *
 * @todo Opacity not yet implemented !
 * 
 * @see MarkGraphic, Graphic, ViewBox
 * @author maxence, alexis
 */
public final class ExternalGraphic extends Graphic implements UomNode, TransformNode,
        ViewBoxNode {

    private ExternalGraphicSource source;
    private ViewBox viewBox;
    private RealParameter opacity;
    private Halo halo;
    private Transform transform;
    //private PlanarImage graphic;
    private Uom uom;
    private String mimeType;

    public ExternalGraphic() {
    }

    /**
     * Build a new {@code ExternalGraphic}, using the given JAXBElement. The
     * value in this JAXBElement must be an {@code ExternalGraphicType}.
     * @param extG
     * @throws IOException
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
     */
    ExternalGraphic(JAXBElement<ExternalGraphicType> extG) throws IOException, InvalidStyle {
        ExternalGraphicType t = extG.getValue();

        if (t.getHalo() != null) {
            this.setHalo(new Halo(t.getHalo()));
        }

        if (t.getOpacity() != null) {
            this.setOpacity(SeParameterFactory.createRealParameter(t.getOpacity()));
        }

        if (t.getTransform() != null) {
            this.setTransform(new Transform(t.getTransform()));
        }

        if (t.getUom() != null) {
            this.setUom(Uom.fromOgcURN(t.getUom()));
        }

        if (t.getViewBox() != null) {
            this.setViewBox(new ViewBox(t.getViewBox()));
        }
        //try{
        if (t.getOnlineResource() != null) {
            this.setSource(new VariableOnlineResource(t.getOnlineResource()));
        }
        //}catch (URISyntaxException e){
        //        throw new InvalidStyle("There's a malformed URI in your style", e);
        //}

        this.mimeType = t.getFormat();
    }

    @Override
    public Uom getUom() {
        if (uom != null) {
            return this.uom;
        } else {
            return parent.getUom();
        }
    }

    @Override
    public Uom getOwnUom() {
        return uom;
    }

    @Override
    public void setUom(Uom uom) {
        this.uom = uom;
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    @Override
    public void setTransform(Transform transform) {
        this.transform = transform;
        if (transform != null) {
            transform.setParent(this);
        }
    }

    /**
     * Get the {@link Halo} that must be rendered around this {@code
     * ExternalGraphic}.
     * @return
     * A {@link Halo} instance, or null if not set.
     */
    public Halo getHalo() {
        return halo;
    }

    /**
     * Set the {@link Halo} that must be rendered around this {@code
     * ExternalGraphic}.
     * @param halo
     */
    public void setHalo(Halo halo) {
        this.halo = halo;
        if (halo != null) {
            halo.setParent(this);
        }
    }

    /**
     * Get the opacity applied to this {@code ExternalGraphic} at rendering time.
     * @return
     * The opacity as a {@link RealParameter} instance, in a {@link
     * RealParameterContext}.
     */
    public RealParameter getOpacity() {
        return opacity;
    }

    /**
     * Set the opacity applied to this {@code ExternalGraphic} at rendering time.
     * @param opacity
     */
    public void setOpacity(RealParameter opacity) {
        this.opacity = opacity;
        if (this.opacity != null) {
            this.opacity.setContext(RealParameterContext.PERCENTAGE_CONTEXT);
        }
    }

        @Override
    public ViewBox getViewBox() {
        return viewBox;
    }

        @Override
    public void setViewBox(ViewBox viewBox) {
        this.viewBox = viewBox;
        viewBox.setParent(this);
        updateGraphic();
    }

    @Override
    public void updateGraphic() {
        //graphic = null;
        /*
        try {
        if (source != null) {
        graphic = source.getPlanarImage(viewBox, null, null);
        }
        } catch (Exception ex) {
        ex.printStackTrace();
        }*/
    }

    public void setSource(ExternalGraphicSource src) throws IOException {
        this.source = src;
        updateGraphic();
    }

    public ExternalGraphicSource getSource() {
        return source;
    }

    @Override
    public Rectangle2D getBounds(DataSource sds, long fid, MapTransform mt) throws ParameterException, IOException {
        Rectangle2D.Double bounds = source.updateCacheAndGetBounds(viewBox, sds, fid, mt, mimeType);

        double px = bounds.getMinX();
        double py = bounds.getMinY();

        double width = bounds.getWidth();
        double height = bounds.getHeight();

        AffineTransform at = null;
        if (transform != null) {
            at = transform.getGraphicalAffineTransform(false, sds, fid, mt, width, height);
        }

        // reserve the place for halo
        if (halo != null) {
            double r = halo.getHaloRadius(sds, fid, mt);
            width += 2 * r;
            height += 2 * r;
            px -= r;
            py -= r;

            bounds = new Rectangle2D.Double(px, py, width, height);
        }

        /*
        if (at != null) {
            // take into account AT
            return at.createTransformedShape(bounds).getBounds2D();
        } else {
            return bounds;
        }*/
        return bounds;
    }

    @Override
    public void draw(Graphics2D g2, DataSource sds, long fid,
            boolean selected, MapTransform mt, AffineTransform fat) throws ParameterException, IOException {


        Rectangle2D.Double bounds = source.updateCacheAndGetBounds(viewBox, sds, fid, mt, mimeType);

        AffineTransform at = new AffineTransform(fat);

        double px = bounds.getMinX();
        double py = bounds.getMinY();

        double width = bounds.getWidth();
        double height = bounds.getHeight();

        if (transform != null) {
            at.concatenate(transform.getGraphicalAffineTransform(false, sds, fid, mt, width, height));
        }

        // reserve the place for halo
        if (halo != null) {
            /*double r = halo.getHaloRadius(sds, fid, mt);
            width += 2 * r;
            height += 2 * r;
            px -= r;
            py -= r;

            bounds = new Rectangle2D.Double(px, py, width, height);*/

            // Draw it
            halo.draw(g2, sds, fid, selected, at.createTransformedShape(bounds), mt, selected);
        }

        double op = 1.0;
        if (opacity != null){
            op = opacity.getValue(sds, fid);
        }

        source.draw(g2, at, mt, op, mimeType);
    }

    /*@Override
    public RenderableGraphics getRenderableGraphics(DataSource sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {
    // TODO Implements SELECTED!

    RenderedImage img;

    //if (graphic == null) {
    img = source.getPlanarImage(viewBox, sds, fid, mt, mimeType);
    //} else {
    //    img = graphic;
    //}

    if (img == null){
    return null;
    }

    double w = img.getWidth();
    double h = img.getHeight();

    AffineTransform at = new AffineTransform();
    if (transform != null){
    at = transform.getGraphicalAffineTransform(false, sds, fid, mt, w, h);
    }

    double px = 0;
    double py = 0;

    // reserve the place for halo
    if (halo != null) {
    double r = halo.getHaloRadius(sds, fid, mt);
    w += 2 * r;
    h += 2 * r;
    px = r;
    py = r;
    }

    Rectangle2D bounds = new Rectangle2D.Double(0.0, 0.0, w, h);

    at.concatenate(AffineTransform.getTranslateInstance(-w / 2.0, -h / 2.0));

    // Apply the AT to the bbox
    Shape atShp = at.createTransformedShape(bounds);
    Rectangle2D imageSize = atShp.getBounds2D();

    RenderableGraphics rg = Graphic.getNewRenderableGraphics(imageSize, 0, mt);

    if (halo != null) {
    halo.draw(rg, sds, fid, selected, atShp, mt, false);
    // and add a translation to center img on halo
    at.concatenate(AffineTransform.getTranslateInstance(px, py));
    }

    // TODO how to set opacity ?


    // apply the AT and draw the ext graphic
    rg.drawRenderedImage(img, at);

    return rg;
    }*/

    /*public double getMargin(DataSource sds, long fid, MapTransform mt) throws ParameterException, IOException {
    double delta = 0.0;

    if (this.halo != null) {
    delta += halo.getHaloRadius(sds, fid, mt);
    }

    return delta;
    }*/

    /*
    @Override
    public double getMaxWidth(DataSource sds, long fid, MapTransform mt) throws ParameterException, IOException {
    double delta = 0.0;
    if (viewBox != null && viewBox.usable()) {
    RenderedImage img;
    if (graphic == null) {
    img = source.getPlanarImage(viewBox, sds, fid, mt, mimeType);
    } else {
    img = graphic;
    }

    if (img != null){
    Point2D dim = viewBox.getDimensionInPixel(sds, fid, img.getHeight(), img.getWidth(), mt.getScaleDenominator(), mt.getDpi());

    delta = Math.max(dim.getY(), dim.getX());
    }
    else{
    return 0.0;
    }
    }

    delta += this.getMargin(sds, fid, mt);

    return delta;
    }*/
    @Override
    public String dependsOnFeature() {

        String h = "";
        String o = "";
        String t = "";
        String v = "";


        if (halo != null) {
            h = halo.dependsOnFeature();
        }
        if (opacity != null) {
            o = opacity.dependsOnFeature();
        }
        if (transform != null) {
            t = transform.dependsOnFeature();
        }
        if (viewBox != null) {
            v = viewBox.dependsOnFeature();
        }

        return (h + " " + o + " " + t + " " + v + " ").trim();
    }

    @Override
    public JAXBElement<ExternalGraphicType> getJAXBElement() {
        ExternalGraphicType e = new ExternalGraphicType();

        if (halo != null) {
            e.setHalo(halo.getJAXBType());
        }

        if (source != null) {
            source.setJAXBSource(e);
        }

        if (mimeType != null) {
            e.setFormat(mimeType);
        }

        if (opacity != null) {
            e.setOpacity(opacity.getJAXBParameterValueType());
        }

        if (transform != null) {
            e.setTransform(transform.getJAXBType());
        }

        if (uom != null) {
            e.setUom(uom.toURN());
        }

        if (viewBox != null) {
            e.setViewBox(viewBox.getJAXBType());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createExternalGraphic(e);
    }
}
