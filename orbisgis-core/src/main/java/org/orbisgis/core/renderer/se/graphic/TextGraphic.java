package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.label.StyledLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

public class TextGraphic extends Graphic{

    public StyledLabel getStyledLabel() {
        return styledLabel;
    }

    public void setStyledLabel(StyledLabel styledLabel) {
        this.styledLabel = styledLabel;
    }


    /**
     * @param g2
     * @param ds
     * @param fid
     * @todo implements !
     */
    @Override
    public void drawGraphic(Graphics2D g2, DataSource ds, int fid) throws ParameterException{

        BufferedImage label = styledLabel.getImage(ds, fid);

        g2.drawImage(label,
                     new AffineTransformOp(transform.getGraphicalAffineTransform(ds, fid, false),
                                           AffineTransformOp.TYPE_BICUBIC),
                      -label.getWidth() / 2,
                      -label.getHeight() / 2);
    }

    private StyledLabel styledLabel;
}
