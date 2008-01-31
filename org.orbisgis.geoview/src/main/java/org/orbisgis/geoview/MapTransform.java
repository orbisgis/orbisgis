package org.orbisgis.geoview;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class MapTransform {

	private BufferedImage image = null;

	private Rectangle2D adjustedExtent;

	private AffineTransform trans = new AffineTransform();

	private Rectangle2D extent;

	public void setImage(BufferedImage newImage) {
		image = newImage;
	}

	public BufferedImage getImage() {
		return image;
	}

	public Rectangle2D getAdjustedExtent() {
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

		double xCenter = extent.getCenterX();
		double yCenter = extent.getCenterY();
		double newHeight;
		double newWidth;

		adjustedExtent = new Rectangle2D.Double();

		double scale;
		if (escalaX < escalaY) {
			scale = escalaX;
			newHeight = getHeight() / scale;
			adjustedExtent.setRect(xCenter - (extent.getWidth() / 2.0), yCenter
					- (newHeight / 2.0), extent.getWidth(), newHeight);
		} else {
			scale = escalaY;
			newWidth = getWidth() / scale;
			adjustedExtent.setRect(xCenter - (newWidth / 2.0), yCenter
					- (extent.getHeight() / 2.0), newWidth, extent.getHeight());
		}

		translacion.setToTranslation(-adjustedExtent.getX(), -adjustedExtent
				.getY()
				- adjustedExtent.getHeight());
		escalado.setToScale(scale, -scale);

		trans.setToIdentity();
		trans.concatenate(escalado);

		trans.concatenate(translacion);

	}

	private int getHeight() {
		return image.getHeight();
	}

	private int getWidth() {
		return image.getWidth();
	}

	public void setExtent(Rectangle2D newExtent) {
		this.extent = newExtent;
		calculateAffineTransform();
	}

	public void resizeImage(int width, int height) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		calculateAffineTransform();
	}

	public AffineTransform getAffineTransform() {
		return trans;
	}

	public Rectangle2D getExtent() {
		return extent;
	}

	public void setExtent(Envelope extent) {
		Rectangle2D rect = new Rectangle2D.Double(extent.getMinX(), extent
				.getMinY(), extent.getWidth(), extent.getHeight());
		setExtent(rect);
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

}
