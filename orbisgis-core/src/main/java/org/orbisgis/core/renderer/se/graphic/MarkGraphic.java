package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;

public class MarkGraphic extends Graphic{

    public Fill getFill() {
        return fill;
    }

    public void setFill(Fill fill) {
        this.fill = fill;
        fill.setParent(this);
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
    }

    public MarkGraphicSource getSource() {
        return source;
    }

    public void setSource(MarkGraphicSource source) throws IOException {
        this.source = source;
        try{
            shape = source.getShape(this.viewBox, null, 0);
        }
        catch(ParameterException ex){
            shape = null;
        }
    }

    /**
     * @param g2
     * @param ds
     * @param fid
     * @throws ParameterException
     * @throws IOException 
     * @todo implements !
     */
    @Override
    public void drawGraphic(Graphics2D g2, DataSource ds, int fid) throws ParameterException, IOException{
        Shape shp;
        if (shape == null){
            shp = source.getShape(viewBox, ds, fid);
        }
        else{
            shp = shape;
        }
        // Apply AT

        Shape atShp = this.transform.getGraphicalAffineTransform(ds, fid, false).createTransformedShape(shape);
        if (halo != null){
            halo.draw(g2, atShp, ds, fid);
        }
        if (fill != null){
            fill.draw(g2, atShp, ds, fid);
        }
        if (stroke != null){
            stroke.draw(g2, atShp, ds, fid);
        }
    }

    private MarkGraphicSource source;
    
    private ViewBox viewBox;

    private RealParameter opacity;
    private Halo halo;
    private Fill fill;
    private Stroke stroke;

    private Shape shape;
}
