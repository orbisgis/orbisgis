package org.orbisgis.core.renderer.se.graphic;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.io.IOException;

import javax.media.jai.PlanarImage;
import javax.media.jai.RenderableGraphics;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 * an external graphic is an image such as JPG, PNG, SVG.
 * Available action on such a graphic are affine transfromations.
 * There is no way to restyle the graphic but setting opacity
 *
 * @todo Opacity not yet implemented !
 * 
 * @see MarkGraphic, Graphic
 * @author maxence
 */
public class ExternalGraphic extends Graphic {

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
    }

    public ViewBox getViewBox() {
        return viewBox;
    }

    public void setViewBox(ViewBox viewBox) {
        this.viewBox = viewBox;
        viewBox.setParent(this);
        updateGraphic();
    }

    private void updateGraphic() {
        graphic = null;

        try {
            if (source != null) {
                graphic = source.getPlanarImage(viewBox, null, 0);
            }
        } catch (IOException ex) {
            graphic = null; // TODO
        } catch (ParameterException ex) {
            // Means graphic depends on a feature
            graphic = null;
        }
    }

    public void setSource(ExternalGraphicSource src) throws IOException {
        this.source = src;
        updateGraphic();
    }

    @Override
    public RenderableGraphics getRenderableGraphics(DataSource ds, long fid) throws ParameterException, IOException {
        AffineTransform at = transform.getGraphicalAffineTransform(ds, fid, false);

        PlanarImage img;

        // Create shape based on image bbox

        if (graphic == null) {
            img = source.getPlanarImage(viewBox, ds, fid);
        } else {
            img = graphic;
        }

        double w = img.getWidth();
        double h = img.getHeight();

        // reserve the place for halo
        if (halo != null) {
            double r = Uom.toPixel(halo.getRadius().getValue(ds, fid), halo.getUom(), MapEnv.getScaleDenominator()); // TODO SCALE, DPI...
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
            halo.draw(rg, atShp, ds, fid);
        }

        // TODO how to set opacity ?

        // apply the AT and draw the ext graphic
        rg.drawRenderedImage(img, at);

        return rg;
    }

    public double getMargin(DataSource ds, long fid) throws ParameterException, IOException {
        double delta = 0.0;

        if (this.halo != null) {
            delta += Uom.toPixel(halo.getRadius().getValue(ds, fid), halo.getUom(), MapEnv.getScaleDenominator());
        }

        return delta;
    }

    @Override
    public double getMaxWidth(DataSource ds, long fid) throws ParameterException, IOException {
        double delta = 0.0;
        if (viewBox != null) {
            PlanarImage img;
            if (graphic == null) {
                img = source.getPlanarImage(viewBox, ds, fid);
            } else {
                img = graphic;
            }

            Dimension dim = viewBox.getDimension(ds, fid, img.getHeight() / img.getWidth());

            delta = Math.max(dim.getHeight(), dim.getWidth());
        }

        delta += this.getMargin(ds, fid);

        return delta;
    }
    private ExternalGraphicSource source;
    private ViewBox viewBox;
    private RealParameter opacity;
    private Halo halo;
    private PlanarImage graphic;
}
