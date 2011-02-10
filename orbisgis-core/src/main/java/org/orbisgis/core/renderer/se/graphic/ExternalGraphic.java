package org.orbisgis.core.renderer.se.graphic;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.io.IOException;

import javax.media.jai.PlanarImage;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.renderer.persistance.se.ExternalGraphicType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

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
public final class ExternalGraphic extends Graphic {

    private ExternalGraphicSource source;
    private ViewBox viewBox;
    private RealParameter opacity;
    private Halo halo;
    private PlanarImage graphic;

	private String mimeType;

    public ExternalGraphic(){
    }

    ExternalGraphic(JAXBElement<ExternalGraphicType> extG) throws IOException, InvalidStyle {
        ExternalGraphicType t = extG.getValue();

        if (t.getHalo() != null){
            this.setHalo(new Halo(t.getHalo()));
        }

        if (t.getOpacity() != null){
            this.setOpacity(SeParameterFactory.createRealParameter(t.getOpacity()));
        }

        if (t.getTransform() != null){
            this.setTransform(new Transform(t.getTransform()));
        }

        if (t.getUnitOfMeasure() != null){
            this.setUom(Uom.fromOgcURN(t.getUnitOfMeasure()));
        }

        if (t.getViewBox() != null){
            this.setViewBox(new ViewBox(t.getViewBox()));
        }
        
        if (t.getOnlineResource() != null){
            this.setSource(new OnlineResource(t.getOnlineResource()));
        }

		this.mimeType = t.getFormat();
    }

    public Halo getHalo() {
        return halo;
    }

    public void setHalo(Halo halo) {
        this.halo = halo;
        halo.setParent(this);
    }

    public RealParameter getOpacity() {
        return opacity;
    }

    public void setOpacity(RealParameter opacity) {
        this.opacity = opacity;
		if (this.opacity != null){
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

    @Override
    public RenderableGraphics getRenderableGraphics(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {
        // TODO Implements SELECTED!

        PlanarImage img;

        // Create shape based on image bbox

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


        // reserve the place for halo
        if (halo != null) {
			double r = halo.getHaloRadius(sds, fid, mt);
            w += 2 * r;
            h += 2 * r;
        }

        Rectangle2D bounds = new Rectangle2D.Double(0.0, 0.0, w, h);

        at.concatenate(AffineTransform.getTranslateInstance(-w / 2.0, -h / 2.0));

        // Apply the AT to the bbox
        Shape atShp = at.createTransformedShape(bounds);
        Rectangle2D imageSize = atShp.getBounds2D();

        RenderableGraphics rg = Graphic.getNewRenderableGraphics(imageSize, 0);

        if (halo != null) {
            halo.draw(rg, sds, fid, atShp, mt);
        }

        // TODO how to set opacity ?

        // apply the AT and draw the ext graphic
        rg.drawRenderedImage(img, at);

        return rg;
    }

    public double getMargin(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        double delta = 0.0;

        if (this.halo != null) {
			delta += halo.getHaloRadius(sds, fid, mt);
        }

        return delta;
    }

    @Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        double delta = 0.0;
        if (viewBox != null && viewBox.usable()) {
            PlanarImage img;
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
    }


    @Override
    public boolean dependsOnFeature() {
        if (halo != null && this.getHalo().dependsOnFeature()){
            return true;
        }
        if (opacity != null && this.getOpacity().dependsOnFeature()){
            return true;
        }
        if (transform != null && this.getTransform().dependsOnFeature()){
            return true;
        }
        if (viewBox != null && this.getViewBox().dependsOnFeature()){
            return true;
        }

        return false;
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

		if (mimeType != null){
			e.setFormat(mimeType);
		}

        if (opacity != null) {
            e.setOpacity(opacity.getJAXBParameterValueType());
        }

        if (transform != null) {
            e.setTransform(transform.getJAXBType());
        }

        if (uom != null) {
            e.setUnitOfMeasure(uom.toURN());
        }

        if (viewBox != null) {
            e.setViewBox(viewBox.getJAXBType());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createExternalGraphic(e);
    }
}
