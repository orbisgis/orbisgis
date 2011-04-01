package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;

import java.io.IOException;

import javax.media.jai.PlanarImage;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import net.opengis.se._2_0.core.ExternalGraphicType;
import net.opengis.se._2_0.core.ObjectFactory;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.UomNode;

import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.OnlineResource;
import org.orbisgis.core.renderer.se.common.Uom;

import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 * an external graphic is an image such as JPG, PNG, SVG.
 * Available action on such a graphic are affine transformations.
 * There is no way to re-style the graphic but setting opacity
 *
 * @todo Opacity not yet implemented !
 * 
 * @see MarkGraphic, Graphic
 * @author maxence
 */
public final class ExternalGraphic extends Graphic implements UomNode, TransformNode {

    private ExternalGraphicSource source;
    private ViewBox viewBox;
    private RealParameter opacity;
    private Halo halo;
    private Transform transform;
    private PlanarImage graphic;
    private Uom uom;
    private String mimeType;

    public ExternalGraphic() {
    }

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

        if (t.getOnlineResource() != null) {
            this.setSource(new OnlineResource(t.getOnlineResource()));
        }

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

    public Halo getHalo() {
        return halo;
    }

    public void setHalo(Halo halo) {
        this.halo = halo;
        if (halo != null) {
            halo.setParent(this);
        }
    }

    public RealParameter getOpacity() {
        return opacity;
    }

    public void setOpacity(RealParameter opacity) {
        this.opacity = opacity;
        if (this.opacity != null) {
            this.opacity.setContext(RealParameterContext.percentageContext);
        }
    }

    public ViewBox getViewBox() {
        return viewBox;
    }

    public void setViewBox(ViewBox viewBox) {
        this.viewBox = viewBox;
        viewBox.setParent(this);
        updateGraphic();
    }

    @Override
    public void updateGraphic() {
        graphic = null;
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
    public Rectangle2D getBounds(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        // TODO Implements SELECTED!
        RenderedImage img;
        img = source.getPlanarImage(viewBox, sds, fid, mt, mimeType);

        if (img == null) {
            return null;
        }

        double w = img.getWidth();
        double h = img.getHeight();

        AffineTransform at = null;
        if (transform != null) {
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

        if (at != null) {
            return at.createTransformedShape(bounds).getBounds2D();
        } else {
            return bounds;
        }
    }

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid,
            boolean selected, MapTransform mt, AffineTransform fat) throws ParameterException, IOException {
        // TODO Implements SELECTED!
        AffineTransform at = new AffineTransform(fat);
        RenderedImage img;

        //if (graphic == null) {
        img = source.getPlanarImage(viewBox, sds, fid, mt, mimeType);
        //} else {
        //    img = graphic;
        //}

        if (img == null) {
            System.out.println("Image is null !!!");
            return;
        }

        double w = img.getWidth();
        double h = img.getHeight();

        if (transform != null) {
            at.concatenate(transform.getGraphicalAffineTransform(false, sds, fid, mt, w, h));
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
        //Rectangle2D imageSize = atShp.getBounds2D();

        //RenderableGraphics rg = Graphic.getNewRenderableGraphics(imageSize, 0, mt);

        if (halo != null) {
            halo.draw(g2, sds, fid, selected, atShp, mt, false);
            // and add a translation to center img on halo
            //at.concatenate(AffineTransform.getTranslateInstance(px, py));
        }
        // TODO how to set opacity ?
        // apply the AT and draw the ext graphic
        g2.drawRenderedImage(img, at);
    }

    /*@Override
    public RenderableGraphics getRenderableGraphics(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {
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

    /*public double getMargin(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
    double delta = 0.0;

    if (this.halo != null) {
    delta += halo.getHaloRadius(sds, fid, mt);
    }

    return delta;
    }*/

    /*
    @Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
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
