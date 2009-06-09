package org.orbisgis.core.renderer.symbol;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.RenderPermission;
import org.orbisgis.core.renderer.liteShape.LiteShape;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

public class ImageSymbol extends AbstractSymbol implements Symbol {

	protected Image img;
	protected URL url;

	public ImageSymbol() {
		setName(getClassName());
		setErrorImage();
	}

	public boolean acceptGeometry(Geometry geom) {
		return (geom instanceof Point) || (geom instanceof MultiPoint);
	}

	public boolean acceptGeometryType(GeometryConstraint geometryConstraint) {
		if (geometryConstraint == null) {
			return true;
		} else {
			int gt = geometryConstraint.getGeometryType();
			switch (gt) {
			case GeometryConstraint.POINT:
			case GeometryConstraint.MULTI_POINT:
				return true;
			default:
				return false;
			}
		}
	}

	public Symbol cloneSymbol() {
		ImageSymbol ret = new ImageSymbol();
		ret.img = this.img;
		ret.url = this.url;
		return ret;
	}

	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		LiteShape ls = new LiteShape(geom, at, false);
		PathIterator pi = ls.getPathIterator(null);
		double[] coords = new double[6];
		while (!pi.isDone()) {
			pi.currentSegment(coords);
			g.drawImage(img, (int) coords[0] - img.getWidth(null) / 2,
					(int) coords[1] - img.getHeight(null) / 2, null);
			pi.next();
		}

		return null;
	}

	public String getClassName() {
		return "Image on point";
	}

	public String getId() {
		return "org.orbisgis.symbols.point.Image";
	}

	public Map<String, String> getPersistentProperties() {
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.putAll(super.getPersistentProperties());
		ret.put("url", url.toString());

		return ret;
	}

	public void setPersistentProperties(Map<String, String> props) {
		super.setPersistentProperties(props);
		try {
			setImageURL(new URL(props.get("url")));
		} catch (MalformedURLException e) {
			setErrorImage();
		} catch (IOException e) {
			setErrorImage();
		}
	}

	private void setErrorImage() {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		FontMetrics fm = g.getFontMetrics();
		String str = "Image not available";
		Rectangle2D bounds = fm.getStringBounds(str, g);
		bi = new BufferedImage((int) bounds.getWidth(), (int) bounds
				.getHeight(), BufferedImage.TYPE_INT_ARGB);
		g = bi.createGraphics();
		g.setColor(Color.black);
		g.drawString(str, 0, (int) bounds.getHeight());
		img = bi;
	}

	/**
	 * Sets the URL of the image to display
	 *
	 * @param url
	 * @throws IOException
	 *             If the image cannot be loaded
	 */
	public void setImageURL(URL url) throws IOException {
		this.url = url;
		try {
			img = ImageIO.read(url);
		} catch (IOException e) {
			setErrorImage();
			throw e;
		}
	}

	/**
	 * Get the URL of the image used to draw this symbol
	 *
	 * @return
	 */
	public URL getImageURL() {
		return url;
	}

	@Override
	public Symbol deriveSymbol(Color color) {
		return null;
	}

}
