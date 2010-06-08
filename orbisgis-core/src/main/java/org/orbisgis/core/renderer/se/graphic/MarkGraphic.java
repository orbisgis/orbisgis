package org.orbisgis.core.renderer.se.graphic;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.MarkGraphicType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.OnlineResource;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.transform.Transform;

public final class MarkGraphic extends Graphic {

    public MarkGraphic() {
    }

    public void setToSquare10(){
        try {
            this.setSource(WellKnownName.SQUARE);
            this.setViewBox(new ViewBox(new RealLiteral(10.0)));
            this.setFill(new SolidFill());
            this.setStroke(new PenStroke());
        } catch (IOException ex) {
            // Will never occurs while WellKnownName doesn't throw anything
        }
    }

    MarkGraphic(JAXBElement<MarkGraphicType> markG) throws IOException {
        MarkGraphicType t = markG.getValue();

        if (t.getUnitOfMeasure() != null) {
            this.setUom(Uom.fromOgcURN(t.getUnitOfMeasure()));
        }

        if (t.getViewBox() != null) {
            this.setViewBox(new ViewBox(t.getViewBox()));
        }

        if (t.getPerpendicularOffset() != null) {
            this.setpOffset(SeParameterFactory.createRealParameter(t.getPerpendicularOffset()));
        }

        if (t.getTransform() != null) {
            this.setTransform(new Transform(t.getTransform()));
        }

        if (t.getHalo() != null) {
            this.setHalo(new Halo(t.getHalo()));
        }

        if (t.getFill() != null) {
            this.setFill(Fill.createFromJAXBElement(t.getFill()));
        }

        if (t.getStroke() != null) {
            this.setStroke(Stroke.createFromJAXBElement(t.getStroke()));
        }


        // Source 
        if (t.getWellKnownName() != null) {
            this.setSource(WellKnownName.fromString(t.getWellKnownName()));
        } else {
            if (t.getOnlineResource() != null) {
                this.setSource((MarkGraphicSource) new OnlineResource(t.getOnlineResource()));
            } else if (t.getInlineContent() != null) {
                // TODO Not yer implemented
            }
        }
    }

    public Fill getFill() {
        return fill;
    }

    public void setFill(Fill fill) {
        this.fill = fill;
        if (fill != null) {
            fill.setParent(this);
        }
    }

    public Halo getHalo() {
        return halo;
    }

    public void setHalo(Halo halo) {
        this.halo = halo;
        halo.setParent(this);
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
        stroke.setParent(this);
    }

    public ViewBox getViewBox() {
        return viewBox;
    }

    public void setViewBox(ViewBox viewBox) {
        this.viewBox = viewBox;
        viewBox.setParent(this);
        updateGraphic();
    }

    public MarkGraphicSource getSource() {
        return source;
    }

    @Override
    public void setUom(Uom uom) {
        this.uom = uom;
        updateGraphic();
    }

    public RealParameter getpOffset() {
        return pOffset;
    }

    public void setpOffset(RealParameter pOffset) {
        this.pOffset = pOffset;
    }

    /*
     * This method must be called after each modification of uom, viewbox, source
     *
     */
    @Override
    public void updateGraphic() {
        try {
            System.out.println ("SOUrce: " + source);
            shape = source.getShape(viewBox, null, 0);
            System.out.println("Mark successfully cached with vBox: " + viewBox);

        } catch (Exception e) {
            System.out.println("Unable to cache mark graphic" + e);
            //e.printStackTrace();
            shape = null;
        }
    }

    public void setSource(MarkGraphicSource source) throws IOException {
        this.source = source;
        updateGraphic();
    }

    /**
     * @param ds
     * @param fid
     * @throws ParameterException
     * @throws IOException 
     * @todo implements !
     */
    @Override
    public RenderableGraphics getRenderableGraphics(DataSource ds, long fid, boolean selected) throws ParameterException, IOException {
        Shape shp;

        // If the shape doesn't depends on feature (i.e. not null), we used the cached one
        if (shape == null) {
            shp = source.getShape(viewBox, ds, fid);
        } else {
            shp = shape;
        }

        // TODO Add a cache for AT
        // Apply AT
        Shape atShp = shp;

        if (transform != null) {
            atShp = this.transform.getGraphicalAffineTransform(ds, fid, false).createTransformedShape(shp);
        }

        Rectangle2D bounds = atShp.getBounds2D();

        double margin = this.getMargin(ds, fid);

        RenderableGraphics rg = Graphic.getNewRenderableGraphics(bounds, margin);

        if (halo != null) {
            halo.draw(rg, atShp, ds, fid);
        }
        if (fill != null) {
            fill.draw(rg, atShp, ds, fid, selected);
        }
        if (stroke != null) {
            stroke.draw(rg, atShp, ds, fid, selected);
        }

        return rg;

    }

    /**
     * compute required extra space. This extra space equals the max bw stroke width and halo radius
     * @param ds
     * @param fid
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    private double getMargin(DataSource ds, long fid) throws ParameterException, IOException {
        double sWidth = 0.0;
        double haloR = 0.0;

        if (stroke != null) {
            sWidth += stroke.getMaxWidth(ds, fid);
        }

        if (this.halo != null) {
            haloR = Uom.toPixel(halo.getRadius().getValue(ds, fid), halo.getUom(), MapEnv.getScaleDenominator());
        }

        return Math.max(sWidth, haloR);
    }

    @Override
    public double getMaxWidth(DataSource ds, long fid) throws ParameterException, IOException {
        double delta = 0.0;

        if (viewBox != null) {
            Dimension dim = viewBox.getDimensionInPixel(ds, fid, 1);
            delta = Math.max(dim.getHeight(), dim.getWidth());
        }

        delta += this.getMargin(ds, fid);

        return delta;
    }

    @Override
    public JAXBElement<MarkGraphicType> getJAXBElement() {
        MarkGraphicType m = new MarkGraphicType();

        if (halo != null) {
            m.setHalo(halo.getJAXBType());
        }

        source.setJAXBSource(m);

        if (transform != null) {
            m.setTransform(transform.getJAXBType());
        }

        if (uom != null) {
            m.setUnitOfMeasure(uom.toURN());
        }

        if (viewBox != null) {
            m.setViewBox(viewBox.getJAXBType());
        }

        if (fill != null) {
            m.setFill(fill.getJAXBElement());
        }

        if (stroke != null) {
            m.setStroke(stroke.getJAXBElement());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createMarkGraphic(m);
    }
    
    private MarkGraphicSource source;
    private ViewBox viewBox;
    private RealParameter pOffset;
    private Halo halo;
    private Fill fill;
    private Stroke stroke;
    // cached shape : only available with shape that doesn't depends on features
    private Shape shape;
}
