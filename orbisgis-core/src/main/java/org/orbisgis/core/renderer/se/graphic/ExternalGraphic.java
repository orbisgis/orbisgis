package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public class ExternalGraphic extends Graphic{

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
    }

    public void setSource(ExternalGraphicSource src) throws IOException{
        this.source = src;
        try{
            this.graphic = source.getBufferedImage(viewBox, null, 0);
        }
        catch(ParameterException ex){
            this.graphic = null;
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
    public void drawGraphic(Graphics2D g2, DataSource ds, int fid) throws ParameterException, IOException {
        BufferedImage g;
        // graphic is null if it's viewbox depends on the feature
        if (graphic == null){
            g = source.getBufferedImage(viewBox, ds, fid);   
        }
        else{
            g = graphic;
        }

        if (halo != null){
            // TODO 
            //halo.draw(g2, g.toShape()..., ds, fid);
            // Find a way to convert the graphic into a fillable element ...
            // Easy ways is to fetch the bbox
        }

        g2.drawImage(g,
                     new AffineTransformOp(transform.getGraphicalAffineTransform(ds, fid, false),
                                           AffineTransformOp.TYPE_BICUBIC),
                      -g.getWidth() / 2,
                      -g.getHeight() / 2);
    }


    private ExternalGraphicSource source;
    
    private ViewBox viewBox;

    private RealParameter opacity;
    private Halo halo;

    private BufferedImage graphic;
}
