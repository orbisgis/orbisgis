/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.label;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.liteShape.LiteShape;

/**
 *
 * @author maxence
 * @todo implements
 */
public class LineLabel extends Label {

    /**
     *
     * @param g2
     * @param shp
     * @param ds
     * @param fid
     */
    @Override
    public void draw(Graphics2D g2, LiteShape shp, DataSource ds, int fid) {
        BufferedImage l = this.label.getImage(ds, fid);

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
