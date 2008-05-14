package org.orbisgis.editors.map;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class MapTransform {

	private BufferedImage image = null;

	private Envelope adjustedExtent;

	private AffineTransform trans = new AffineTransform();

	private Envelope extent;

	private ArrayList<TransformListener> listeners = new ArrayList<TransformListener>();

	public void setImage(BufferedImage newImage) {
		image = newImage;
	}

	public BufferedImage getImage() {
		return image;
	}

	public Envelope getAdjustedExtent() {
		return adjustedExtent;
	}

	/**
	 *
	 * @throws RuntimeException
	 */
	private void calculateAffineTransform() {
		if (extent == null) {
			return;
		} else if ((image == null) || (getWidth() == 0) || (getHeight() == 0)) {
			return;
		}

		AffineTransform escalado = new AffineTransform();
		AffineTransform translacion = new AffineTransform();

		double escalaX;
		double escalaY;

		escalaX = getWidth() / extent.getWidth();
		escalaY = getHeight() / extent.getHeight();

		double xCenter = extent.getMinX() + extent.getWidth() / 2.0;
		double yCenter = extent.getMinY() + extent.getHeight() / 2.0;
		double newHeight;
		double newWidth;

		adjustedExtent = new Envelope();

		double scale;
		if (escalaX < escalaY) {
			scale = escalaX;
			newHeight = getHeight() / scale;
			double newX = xCenter - (extent.getWidth() / 2.0);
			double newY = yCenter - (newHeight / 2.0);
			adjustedExtent = new Envelope(newX, newX + extent.getWidth(), newY,
					newY + newHeight);
		} else {
			scale = escalaY;
			newWidth = getWidth() / scale;
			double newX = xCenter - (newWidth / 2.0);
			double newY = yCenter - (extent.getHeight() / 2.0);
			adjustedExtent = new Envelope(newX, newX + newWidth, newY, newY
					+ extent.getHeight());
		}

		translacion.setToTranslation(-adjustedExtent.getMinX(), -adjustedExtent
				.getMinY()
				- adjustedExtent.getHeight());
		escalado.setToScale(scale, -scale);

		trans.setToIdentity();
		trans.concatenate(escalado);

		trans.concatenate(translacion);

	}

	public int getHeight() {
		if (image == null) {
			return 0;
		} else {
			return image.getHeight();
		}
	}

	public int getWidth() {
		if (image == null) {
			return 0;
		} else {
			return image.getWidth();
		}
	}

	public void setExtent(Envelope newExtent) {
		Envelope oldExtent = this.extent;
		this.extent = newExtent;
		calculateAffineTransform();
		for (TransformListener listener : listeners) {
			listener.extentChanged(oldExtent, this);
		}
	}

	public void resizeImage(int width, int height) {
		int oldWidth = getWidth();
		int oldHeight = getHeight();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		calculateAffineTransform();
		for (TransformListener listener : listeners) {
			listener.imageSizeChanged(oldWidth, oldHeight, this);
		}
	}

	public AffineTransform getAffineTransform() {
		return trans;
	}

	public Envelope getExtent() {
		return extent;
	}

	public Envelope toPixel(Envelope geographicEnvelope) {
		final Point2D lowerRight = new Point2D.Double(geographicEnvelope
				.getMaxX(), geographicEnvelope.getMinY());
		final Point2D upperLeft = new Point2D.Double(geographicEnvelope
				.getMinX(), geographicEnvelope.getMaxY());

		final Point2D ul = trans.transform(upperLeft, null);
		final Point2D lr = trans.transform(lowerRight, null);

		return new Envelope(new Coordinate(ul.getX(), ul.getY()),
				new Coordinate(lr.getX(), lr.getY()));
	}

	public Point2D toMapPoint(int i, int j) {
		try {
			return trans.createInverse().transform(new Point(i, j), null);
		} catch (NoninvertibleTransformException e) {
			throw new RuntimeException(e);
		}
	}

	public Point fromMapPoint(Point2D point) {
		Point2D ret = trans.transform(point, null);
		return new Point((int) ret.getX(), (int) ret.getY());
	}

	public void addTransformListener(TransformListener listener) {
		listeners.add(listener);
	}

	public void removeTransformListener(TransformListener listener) {
		listeners.remove(listener);
	}

}
