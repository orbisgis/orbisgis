/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.grap.processing.operation;

import ij.gui.PolygonRoi;
import ij.process.ImageProcessor;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.utilities.EnvelopeUtil;
import org.grap.utilities.JTSConverter;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class Crop implements Operation {
	private LinearRing ring;

	private Rectangle2D rectangle;

	/**
	 * @param ring
	 *            expressed in real world coordinates.
	 */
	public Crop(final LinearRing ring) {
		this.ring = ring;
	}

	/**
	 * @param rectangle
	 *            expressed in real world coordinates.
	 */
	public Crop(final Rectangle2D rectangle) {
		this.rectangle = rectangle;
	}

	public GeoRaster execute(final GeoRaster geoRaster, IProgressMonitor pm)
			throws OperationException {
		if (null != ring) {
			return execute(geoRaster, ring);
		} else if (null != rectangle) {
			return execute(geoRaster, rectangle);
		} else {
			throw new OperationException(
					"No cropped zone (polygon or rectangle) has been specified");
		}
	}

	private GeoRaster execute(final GeoRaster geoRaster, final LinearRing ring)
			throws OperationException {
		try {
			final Geometry rasterEnvelope = new GeometryFactory()
					.createPolygon((LinearRing) EnvelopeUtil
							.toGeometry(geoRaster.getMetadata().getEnvelope()),
							null);

			if (rasterEnvelope.intersects(ring)) {
				final PolygonRoi roi = JTSConverter.toPolygonRoi(toPixel(
						geoRaster, ring));

				final ImageProcessor processor = geoRaster.getImagePlus()
						.getProcessor();
				processor.setRoi(roi);
				final ImageProcessor result = processor.crop();

				final Envelope newEnvelope = toWorld(geoRaster, roi
						.getBoundingRect());

				final double originX = newEnvelope.getMinX();
				final double originY = newEnvelope.getMaxY();
				final RasterMetadata metadataResult = new RasterMetadata(
						originX, originY, geoRaster.getMetadata()
								.getPixelSize_X(), geoRaster.getMetadata()
								.getPixelSize_Y(), result.getWidth(), result
								.getHeight(), geoRaster.getMetadata()
								.getRotation_X(), geoRaster.getMetadata()
								.getRotation_Y(), (float) geoRaster
								.getMetadata().getNoDataValue());

				return GeoRasterFactory.createGeoRaster(result, metadataResult);
			} else {
				return GeoRasterFactory.createNullGeoRaster();
			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private GeoRaster execute(final GeoRaster geoRaster, final Rectangle2D roi)
			throws OperationException {
		try {
			final Envelope roiEnv = new Envelope(new Coordinate(roi.getMinX(),
					roi.getMinY()),
					new Coordinate(roi.getMaxX(), roi.getMaxY()));
			if (roiEnv.intersects(geoRaster.getMetadata().getEnvelope())) {

				final Rectangle2D pixelRoi = toPixel(geoRaster, roi);
				final ImageProcessor processor = geoRaster.getImagePlus()
						.getProcessor();
				processor.setRoi((int) pixelRoi.getMinX(), (int) pixelRoi
						.getMinY(), (int) pixelRoi.getWidth(), (int) pixelRoi
						.getHeight());
				final ImageProcessor result = processor.crop();

				final Envelope newEnvelope = toWorld(geoRaster, pixelRoi);
				final double originX = newEnvelope.getMinX();
				final double originY = newEnvelope.getMaxY();

				final RasterMetadata metadataResult = new RasterMetadata(
						originX, originY, geoRaster.getMetadata()
								.getPixelSize_X(), geoRaster.getMetadata()
								.getPixelSize_Y(), result.getWidth(), result
								.getHeight(), geoRaster.getMetadata()
								.getRotation_X(), geoRaster.getMetadata()
								.getRotation_Y(), (float) geoRaster
								.getMetadata().getNoDataValue());

				return GeoRasterFactory.createGeoRaster(result, metadataResult);
			} else {
				return GeoRasterFactory.createNullGeoRaster();
			}
		} catch (IOException e) {
			throw new OperationException(e);
		}
	}

	private LinearRing toPixel(final GeoRaster geoRaster, final LinearRing ring) {
		final Coordinate[] realWorldCoords = ring.getCoordinates();
		final Coordinate[] pixelGridCoords = new Coordinate[realWorldCoords.length];
		for (int i = 0; i < pixelGridCoords.length; i++) {
			final Point2D p = geoRaster.fromRealWorldToPixel(
					realWorldCoords[i].x, realWorldCoords[i].y);
			pixelGridCoords[i] = new Coordinate(p.getX(), p.getY());
		}
		return new GeometryFactory().createLinearRing(pixelGridCoords);
	}

	private Rectangle2D toPixel(final GeoRaster geoRaster,
			final Rectangle2D rectangle) {
		// TODO following Math.ceil() must be validated !
		final Point2D min = geoRaster.fromRealWorldToPixel(
				rectangle.getMinX(), rectangle.getMinY());
		final Point2D max = geoRaster.fromRealWorldToPixel(
				rectangle.getMaxX(), rectangle.getMaxY());
		final int minx = (int) Math.min(min.getX(), max.getX());
		final int maxx = (int) Math
				.ceil(Math.max(min.getX(), max.getX()) + 0.5);
		final int miny = (int) Math.min(min.getY(), max.getY());
		final int maxy = (int) Math
				.ceil(Math.max(min.getY(), max.getY()) + 0.5);
		return new Rectangle(minx, miny, maxx - minx, maxy - miny);
	}

	private Envelope toWorld(final GeoRaster geoRaster, Rectangle2D rectangle) {
		// TODO following (int) cast must be validated !
		final Point2D min = geoRaster.fromPixelToRealWorld(
				(int) rectangle.getMinX(), (int) rectangle.getMinY());
		final Point2D max = geoRaster.fromPixelToRealWorld(
				(int) rectangle.getMaxX(), (int) rectangle.getMaxY());
		final double minx = Math.min(min.getX(), max.getX());
		final double maxx = Math.max(min.getX(), max.getX());
		final double miny = Math.min(min.getY(), max.getY());
		final double maxy = Math.max(min.getY(), max.getY());
		return new Envelope(new Coordinate(minx, miny), new Coordinate(maxx,
				maxy));
	}
}