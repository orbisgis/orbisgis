/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.jai.InterpolationBilinear;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.persistance.ows._1.OnlineResourceType;
import org.orbisgis.core.renderer.persistance.se.ExternalGraphicType;
import org.orbisgis.core.renderer.persistance.se.MarkGraphicType;
import org.orbisgis.core.renderer.se.graphic.ExternalGraphicSource;
import org.orbisgis.core.renderer.se.graphic.MarkGraphicSource;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 * @todo implements MarkGraphicSource
 */
public class OnlineResource implements ExternalGraphicSource, MarkGraphicSource {

	private URL url;

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
		System.out.println("Read online ressource");
		this.url = new URL(onlineResource.getHref());
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	@Override
	public PlanarImage getPlanarImage(ViewBox viewBox, Feature feat, MapTransform mt, String mimeType)
			throws IOException, ParameterException {
		PlanarImage img = JAI.create("url", url);

		System.out.println("Download external graphic from " + url);

		if (viewBox != null && mt != null && viewBox.usable()) {
			if (mt == null) {
				return null;
			}
			if (feat == null && viewBox != null && viewBox.dependsOnFeature()) {
				throw new ParameterException("View box depends on feature");
			}

			ParameterBlock pb = new ParameterBlock();
			pb.addSource(img);

			double width = img.getWidth();
			double height = img.getHeight();

			Dimension dim = viewBox.getDimensionInPixel(feat, height, width, mt.getScaleDenominator(), mt.getDpi());

			double widthDst = dim.getWidth();
			double heightDst = dim.getHeight();

			if (widthDst > 0 && heightDst > 0) {
				double ratio_x = widthDst / width;
				double ratio_y = heightDst / height;

				System.out.println("Ratios: " + ratio_x + ";" + ratio_y);

				pb.add((float) ratio_x);
				pb.add((float) ratio_y);
				pb.add(0.0F);
				pb.add(0.0F);
				pb.add(new InterpolationBilinear());

				return JAI.create("scale", pb, null);
			} else {
				return img;
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

	private Shape getTrueTypeGlyph(ViewBox viewBox, Feature feat, Double scale, Double dpi, RealParameter markIndex) throws ParameterException, IOException {

		try {
			InputStream iStream = url.openStream();
			Font font = Font.createFont(Font.TRUETYPE_FONT, iStream);

			double value = markIndex.getValue(feat);

			char[] data = {(char) value};

			String text = String.copyValueOf(data);

			// Scale is used to have an high resolution
			AffineTransform at = AffineTransform.getTranslateInstance(0, 0);

			FontRenderContext fontCtx = new FontRenderContext(at, true, true);
			TextLayout tl = new TextLayout(text, font, fontCtx);

			Shape glyphOutline = tl.getOutline(at);

			Rectangle2D bounds2D = glyphOutline.getBounds2D();



			double width = bounds2D.getWidth();
			double height = bounds2D.getHeight();

			if (viewBox != null && viewBox.usable()) {
				Dimension dim = viewBox.getDimensionInPixel(feat, height, width, scale, dpi);
				if (dim.getWidth() <= 0 || dim.getHeight() <= 0) {
					return null;
				}

				at = AffineTransform.getScaleInstance(dim.getWidth() / width,
						dim.getHeight() / height);

				fontCtx = new FontRenderContext(at, true, true);
				tl = new TextLayout(text, font, fontCtx);
				glyphOutline = tl.getOutline(at);

			}
			Rectangle2D gb = glyphOutline.getBounds2D();
			at = AffineTransform.getTranslateInstance(-gb.getCenterX(), -gb.getCenterY());

			return at.createTransformedShape(glyphOutline);

		} catch (FontFormatException ex) {
			Logger.getLogger(OnlineResource.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;

	}

	@Override
	public Shape getShape(ViewBox viewBox, Feature feat, Double scale, Double dpi, RealParameter markIndex, String mimeType) throws ParameterException, IOException {

		if (mimeType != null){
			if (mimeType.equalsIgnoreCase("application/x-font-ttf")) {
				return getTrueTypeGlyph(viewBox, feat, scale, dpi, markIndex);
			}
		}

		return null;
	}

	@Override
	public void setJAXBSource(MarkGraphicType m) {
		OnlineResourceType o = new OnlineResourceType();

		o.setHref(url.toExternalForm());

		m.setOnlineResource(o);

	}
}
