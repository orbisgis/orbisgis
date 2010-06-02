/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.awt.Dimension;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.media.jai.InterpolationBilinear;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.ows._1.OnlineResourceType;
import org.orbisgis.core.renderer.persistance.se.ExternalGraphicType;
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

    public OnlineResource(OnlineResourceType onlineResource) throws MalformedURLException {
        this.url = new URL(onlineResource.getHref());
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    @Override
    public PlanarImage getPlanarImage(ViewBox viewBox, DataSource ds, long fid)
            throws IOException, ParameterException {
        PlanarImage img = JAI.create("url", url);

        if (viewBox != null) {
            if (ds == null && viewBox != null && viewBox.dependsOnFeature()) {
                throw new ParameterException("View box depends on feature");
            }

            ParameterBlock pb = new ParameterBlock();
            pb.addSource(img);

            double width = img.getWidth();
            double height = img.getHeight();

            Dimension dim = viewBox.getDimension(ds, fid, height / width);

            double widthDst = dim.getWidth();
            double heightDst = dim.getHeight();

            if (widthDst > 0 && heightDst > 0) {

                double ratio_x = widthDst / width;
                double ratio_y = heightDst / height;

                pb.add((float) ratio_x);
                pb.add((float) ratio_y);
                pb.add(0.0F);
                pb.add(0.0F);
                pb.add(new InterpolationBilinear());

                return JAI.create("scale", pb, null);
            } else {
                return null;
            }
        } else {
            return img;
        }
    }

    @Override
    public void setJAXBSource(ExternalGraphicType e) {
        OnlineResourceType o = new OnlineResourceType();

        o.setHref(url.toExternalForm());

        e.setOnlineResource(o);
    }
    private URL url;
}
