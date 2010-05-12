package org.orbisgis.core.renderer.se.graphic;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.MarkGraphicType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;

public class MarkGraphic extends Graphic {

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

    /*
     * This method must be called after each modification of uom, viewbox, source
     *
     */
    private void updateGraphic() {
        try {
            shape = source.getShape(viewBox, null, 0);
        } catch (Exception e) {
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
    public RenderableGraphics getRenderableGraphics(DataSource ds, long fid) throws ParameterException, IOException {

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
            fill.draw(rg, atShp, ds, fid);
        }
        if (stroke != null) {
            stroke.draw(rg, atShp, ds, fid);
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
        
        if (stroke != null){
            sWidth += stroke.getMaxWidth(ds, fid);
        }

        if (this.halo != null){
            haloR = Uom.toPixel(halo.getRadius().getValue(ds, fid), halo.getUom(), MapEnv.getScaleDenominator());
        }

        return Math.max(sWidth, haloR);
    }


    @Override
    public double getMaxWidth(DataSource ds, long fid) throws ParameterException, IOException {
        double delta = 0.0;
        
        if (viewBox != null){
            Dimension dim = viewBox.getDimension(ds, fid, 1);
            delta = Math.max(dim.getHeight(), dim.getWidth());
        }
        
        delta += this.getMargin(ds, fid);

        return delta;
    }


    @Override
    public JAXBElement<MarkGraphicType> getJAXBInstance(){
        MarkGraphicType m = new MarkGraphicType();

        if (halo != null){
            m.setHalo(halo.getJAXBType());
        }

        source.setJAXBSource(m);

        if (transform != null){
            m.setTransform(transform.getJAXBType());
        }

        if (uom != null){
            m.setUnitOfMeasure(uom.toURN());
        }

        if (viewBox != null){
            m.setViewBox(viewBox.getJAXBType());
        }

        if (fill != null){
            m.setFill(fill.getJAXBInstance());
        }

        if (stroke != null){
            m.setStroke(stroke.getJAXBInstance());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createMarkGraphic(m);
    }

    private MarkGraphicSource source;
    private ViewBox viewBox;
    private Halo halo;
    private Fill fill;
    private Stroke stroke;

    // cached shape : only available with shape that doesn't depends on features
    private Shape shape;
}
