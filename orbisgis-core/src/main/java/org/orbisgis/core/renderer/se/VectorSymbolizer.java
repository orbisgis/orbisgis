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
