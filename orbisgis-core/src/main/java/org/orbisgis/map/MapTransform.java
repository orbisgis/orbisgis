package org.orbisgis.map;

import java.awt.Point;
import java.awt.Toolkit;
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

	/**
	 * Sets the painted image
	 *
	 * @param newImage
	 */
	public void setImage(BufferedImage newImage) {
		image = newImage;
	}

	/**
	 * Gets the painted image
	 *
	 * @return
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Gets the extent used to calculate the transformation. This extent is the
	 * same as the setted one but adjusted to have the same ratio than the image
	 *
	 * @return
	 */
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

	/**
	 * Gets the height of the drawn image
	 *
	 * @return
	 */
	public int getHeight() {
		if (image == null) {
			return 0;
		} else {
			return image.getHeight();
		}
	}

	/**
	 * Gets the width of the drawn image
	 *
	 * @return
	 */
	public int getWidth() {
		if (image == null) {
			return 0;
		} else {
			return image.getWidth();
		}
	}

	/**
	 * Sets the extent of the transformation. This extent is not used directly
	 * to calculate the transformation but is adjusted to obtain an extent with
	 * the same ration than the image
	 *
	 * @param newExtent
	 */
	public void setExtent(Envelope newExtent) {
		Envelope oldExtent = this.extent;
		this.extent = newExtent;
		calculateAffineTransform();
		for (TransformListener listener : listeners) {
			listener.extentChanged(oldExtent, this);
		}
	}

	/**
	 * Creates new image with the specified size
	 *
	 * @param width
	 * @param height
	 */
	public void resizeImage(int width, int height) {
		int oldWidth = getWidth();
		int oldHeight = getHeight();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		calculateAffineTransform();
		for (TransformListener listener : listeners) {
			listener.imageSizeChanged(oldWidth, oldHeight, this);
		}
	}

	/**
	 * Gets this transformation
	 *
	 * @return
	 */
	public AffineTransform getAffineTransform() {
		return trans;
	}

	/**
	 * Gets the extent
	 *
	 * @return
	 */
	public Envelope getExtent() {
		return extent;
	}

	/**
	 * Transforms an envelope in map units to image units
	 *
	 * @param geographicEnvelope
	 * @return
	 */
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

	/**
	 * Transforms an image coordinate in pixels into a map coordinate
	 *
	 * @param i
	 * @param j
	 * @return
	 */
	public Point2D toMapPoint(int i, int j) {
		try {
			return trans.createInverse().transform(new Point(i, j), null);
		} catch (NoninvertibleTransformException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Transforms the specified map point to an image pixel
	 *
	 * @param point
	 * @return
	 */
	public Point fromMapPoint(Point2D point) {
		Point2D ret = trans.transform(point, null);
		return new Point((int) ret.getX(), (int) ret.getY());
	}

	/**
	 * Gets the scale denominator. If the scale is 1:1000 this method returns
	 * 1000. The scale is not absolutely precise and errors of 2% have been
	 * measured.
	 *
	 * @return
	 */
	public double getScaleDenominator() {
		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		double metersByPixel = 0.0254 / dpi;
		double imageMeters = getWidth() * metersByPixel;

		return extent.getWidth() / imageMeters;
	}

	public void addTransformListener(TransformListener listener) {
		listeners.add(listener);
	}

	public void removeTransformListener(TransformListener listener) {
		listeners.remove(listener);
	}

}
