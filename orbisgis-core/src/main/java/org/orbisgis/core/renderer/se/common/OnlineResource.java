/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.graphic.ExternalGraphicSource;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 * @todo implements MarkGraphicSource
 */
public class OnlineResource implements ExternalGraphicSource {

    /**
     *
     */
    public OnlineResource() {
        url = null;
    }

    public OnlineResource(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    @Override
    public BufferedImage getBufferedImage(ViewBox viewBox, DataSource ds, int fid)
            throws IOException, ParameterException {
        return ImageIO.read(url);
    }

    private URL url;
}
