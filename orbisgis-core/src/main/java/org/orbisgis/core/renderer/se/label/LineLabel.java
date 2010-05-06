/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.label;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.liteShape.LiteShape;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 * @todo implements
 */
public class LineLabel extends Label {

    /**
     *
     */
    @Override
    public void draw(Graphics2D g2, LiteShape shp, DataSource ds, int fid) throws ParameterException, IOException {
        
        RenderableGraphics l = this.label.getImage(ds, fid);

        // convert lineShape to a point
        // create AT according to rotation and exclusionZone

        /*g2.drawImage(label,
                     new AffineTransformOp(AT,
                                           AffineTransformOp.TYPE_BICUBIC),
                      -label.getWidth() / 2,
                      -label.getHeight() / 2);

         */

    }

}
