/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.graphic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.media.jai.PlanarImage;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 * @todo implement in InlineContent
 */
public interface ExternalGraphicSource {
    PlanarImage getPlanarImage(ViewBox viewBox, DataSource ds, int fid) throws IOException, ParameterException;
}
