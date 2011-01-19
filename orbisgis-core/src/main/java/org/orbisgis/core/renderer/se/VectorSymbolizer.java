/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.persistance.se.SymbolizerType;

import org.gdms.data.feature.Feature;
import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 * This class contains common element shared by Point,Line,Area 
 * and Text Symbolizer. Those vector layers all contains :
 *   - an unit of measure (Uom)
 *   - an affine transformation def (transform)
 *
 * @author maxence
 */
public abstract class VectorSymbolizer extends Symbolizer implements UomNode {

	protected Transform transform;
	protected Uom uom;

	protected VectorSymbolizer() {
	}

	protected VectorSymbolizer(JAXBElement<? extends SymbolizerType> st) {
		super(st);
	}

	@Override
	public abstract void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt)
			throws ParameterException, IOException, DriverException;

	/**
	 * Convert a spatial feature into a LiteShape, should add parameters to handle
	 * the scale and to perform a scale dependent generalization !
	 *
	 * @param sds the data source
	 * @param fid the feature id
	 * @throws ParameterException
	 * @throws IOException
	 * @throws DriverException
	 */
	public ArrayList<Shape> getShape(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException, DriverException {

		Geometry geom = this.getTheGeom(sds, fid); // geom + function

		ArrayList<Shape> shapes = new ArrayList<Shape>();

		ArrayList<Geometry> geom2Process = new ArrayList<Geometry>();

		geom2Process.add(geom);

		while (!geom2Process.isEmpty()) {
			geom = geom2Process.remove(0);
			if (geom instanceof GeometryCollection) {
				for (int i = 0; i < geom.getNumGeometries(); i++) {
					geom2Process.add(geom.getGeometryN(i));
				}
			} else {
				Shape shape = mt.getShape(geom);
				if (shape != null) {
					if (transform != null) {
						shape = transform.getGraphicalAffineTransform(false, sds, fid, mt, (double) mt.getWidth(), (double) mt.getHeight()).createTransformedShape(shape); // TODO widht and height?
					}
					shapes.add(shape);
				}
			}
		}

		//Rectangle2D bounds2D = shape.getBounds2D();

		/*
		if (bounds2D.getHeight() + bounds2D.getWidth() < 5){
		return null;
		}
		 */
		return shapes;
	}

	/**
	 * Convert a spatial feature into a set of linear shape
	 *
	 * @param sds the data source
	 * @param fid the feature id
	 * @throws ParameterException
	 * @throws IOException
	 * @throws DriverException
	 */
	public ArrayList<Shape> getLines(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException, DriverException {

		Geometry geom = this.getTheGeom(sds, fid); // geom + function

		ArrayList<Shape> shapes = new ArrayList<Shape>();

		ArrayList<Geometry> geom2Process = new ArrayList<Geometry>();

		geom2Process.add(geom);

		while (!geom2Process.isEmpty()) {
			geom = geom2Process.remove(0);

			// Uncollectionize
			if (geom instanceof GeometryCollection) {
				for (int i = 0; i < geom.getNumGeometries(); i++) {
					geom2Process.add(geom.getGeometryN(i));
				}
			} else if (geom instanceof Polygon) {
				Polygon p = (Polygon) geom;
				Shape shape = mt.getShape(p.getExteriorRing());
				if (shape != null) {
					shapes.add(shape);
				}
				int i;
				// Be aware of polygon holes ! (requiered for
				for (i = 0; i < p.getNumInteriorRing(); i++) {
					shape = mt.getShape(p.getInteriorRingN(i));
					if (shape != null) {
						shapes.add(shape);
					}
				}
			} else {
				Shape shape = mt.getShape(geom);

				if (shape != null) {
					if (transform != null) {
						shape = transform.getGraphicalAffineTransform(false, sds, fid, mt, (double) mt.getWidth(), (double) mt.getHeight()).createTransformedShape(shape); // TODO widht and height?
					}
					shapes.add(shape);
				}
			}
		}

		return shapes;
	}

	public Point2D getPointShape(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException, DriverException {
		Geometry geom = this.getTheGeom(sds, fid); // geom + function

		AffineTransform at = mt.getAffineTransform();

		if (transform != null) {
			at.preConcatenate(transform.getGraphicalAffineTransform(false, sds, fid, mt, (double) mt.getWidth(), (double) mt.getHeight()));
		}

		Point point = geom.getInteriorPoint();
		//Point point = geom.getCentroid();

		return at.transform(new Point2D.Double(point.getX(), point.getY()), null);
	}

	public Point2D getFirstPointShape(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException, DriverException {
		Geometry geom = this.getTheGeom(sds, fid); // geom + function

		AffineTransform at = mt.getAffineTransform();
		if (transform != null) {
			at.preConcatenate(transform.getGraphicalAffineTransform(false, sds, fid, mt, (double) mt.getWidth(), (double) mt.getHeight())); // TODO width and height
		}

		Coordinate[] coordinates = geom.getCoordinates();

		return at.transform(new Point2D.Double(coordinates[0].x, coordinates[0].y), null);
	}

	public ArrayList<Point2D> getPoints(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException, DriverException {
		Geometry geom = this.getTheGeom(sds, fid); // geom + function

		ArrayList<Point2D> points = new ArrayList<Point2D>();

		AffineTransform at = mt.getAffineTransform();
		if (transform != null) {
			at.preConcatenate(transform.getGraphicalAffineTransform(false, sds, fid, mt, (double) mt.getWidth(), (double) mt.getHeight())); // TODO width and height
		}

		Coordinate[] coordinates = geom.getCoordinates();

		int i;
		for (i = 0; i < coordinates.length; i++) {
			points.add(at.transform(new Point2D.Double(coordinates[i].x, coordinates[i].y), null));
		}

		return points;
	}

	public Transform getTransform() {
		return transform;
	}

	@Override
	public Uom getUom() {
		return uom;
	}

	@Override
	public Uom getOwnUom() {
		return uom;
	}

	@Override
	public void setUom(Uom uom) {
		if (uom != null) {
			this.uom = uom;
		} else {
			this.uom = Uom.MM;
		}
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
		transform.setParent(this);
	}
}
