/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */


package org.orbisgis.core.renderer.se.common;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
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

			Point2D dim = viewBox.getDimensionInPixel(feat, height, width, mt.getScaleDenominator(), mt.getDpi());

			double widthDst = dim.getX();
			double heightDst = dim.getY();

			if (widthDst > 0 && heightDst > 0) {
				double ratio_x = widthDst / width;
				double ratio_y = heightDst / height;

				//System.out.println("Ratios: " + ratio_x + ";" + ratio_y);

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

	public Font getFont(){
		InputStream iStream = null;
		try {
			iStream = url.openStream();
			return Font.createFont(Font.TRUETYPE_FONT, iStream);
		} catch (FontFormatException ex) {
		} catch (IOException ex) {
		} finally {
			try {
				iStream.close();
			} catch (IOException ex) {
			}
		}
		return null;
	}

	private Shape getTrueTypeGlyph(ViewBox viewBox, Feature feat, Double scale, Double dpi, RealParameter markIndex) throws ParameterException, IOException {

		try {
			InputStream iStream = url.openStream();
			Font font = Font.createFont(Font.TRUETYPE_FONT, iStream);
			iStream.close();

			System.out.println ("Font: " + font.getNumGlyphs());

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
				Point2D dim = viewBox.getDimensionInPixel(feat, height, width, scale, dpi);
				if (Math.abs(dim.getX()) <= 0 || Math.abs(dim.getY()) <= 0) {
					return null;
				}

				at = AffineTransform.getScaleInstance(dim.getX() / width,
						dim.getY() / height);

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
