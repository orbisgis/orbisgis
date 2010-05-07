package org.orbisgis.core.renderer.se.graphic;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.RenderContextFactory;
import org.orbisgis.core.renderer.se.label.StyledLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

public class TextGraphic extends Graphic{

    public StyledLabel getStyledLabel() {
        return styledLabel;
    }

    public void setStyledLabel(StyledLabel styledLabel) {
        this.styledLabel = styledLabel;
        styledLabel.setParent(this);
    }


    /**
     * @param ds
     * @param fid
     * @todo implements !
     */
    @Override
    public RenderableGraphics getRenderableGraphics(DataSource ds, long fid) throws ParameterException, IOException{

        RenderableGraphics label = styledLabel.getImage(ds, fid);

        Rectangle2D bounds = new Rectangle2D.Double(label.getMinX(), label.getMinY(), label.getWidth(), label.getHeight());

        
        if (transform != null) {
            AffineTransform at = this.transform.getGraphicalAffineTransform(ds, fid, false);
            
            Shape atShp = at.createTransformedShape(bounds);

            RenderableGraphics rg = Graphic.getNewRenderableGraphics(atShp.getBounds2D(), 0);
            rg.drawRenderedImage(label.createRendering(RenderContextFactory.getContext()), at);
            return rg;
        }
        else{
            return label;
        }
    }

    @Override
    public double getMaxWidth(DataSource ds, long fid) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private StyledLabel styledLabel;
}
