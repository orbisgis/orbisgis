package org.orbisgis.tools.instances;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;

import org.orbisgis.tools.DrawingException;
import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.generated.Multipolygon;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public abstract class AbstractMultipolygonTool extends Multipolygon {

	private GeometryFactory gf = new GeometryFactory();
	private ArrayList<Coordinate> points = new ArrayList<Coordinate>();
	private ArrayList<Polygon> polygons = new ArrayList<Polygon>();

	@Override
	public void transitionTo_Standby() throws FinishedAutomatonException,
			TransitionException {
		points.clear();
	}

	@Override
	public void transitionTo_Point() throws FinishedAutomatonException,
			TransitionException {
		points.add(new Coordinate(tm.getValues()[0], tm.getValues()[1]));
	}

	@Override
	public void transitionTo_Line() throws FinishedAutomatonException,
			TransitionException {
		if (points.size() < 3)
			throw new TransitionException(Messages
					.getString("MultipolygonTool.0")); //$NON-NLS-1$

		addPolygon();

		transition("init"); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	private void addPolygon() throws TransitionException {
		ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
				.clone();
		tempPoints.add(new Coordinate(points.get(0).x, points.get(0).y));
		Coordinate[] coords = tempPoints.toArray(new Coordinate[0]);
		Polygon p = gf.createPolygon(gf.createLinearRing(coords),
				new LinearRing[0]);
		if (!p.isValid()) {
			throw new TransitionException(Messages
					.getString("MultipolygonTool.2")); //$NON-NLS-1$
		}
		polygons.add(p);
	}

	@Override
	public void transitionTo_Done() throws FinishedAutomatonException,
			TransitionException {
		if (((points.size() < 3) && (points.size() > 0))
				|| ((points.size() == 0) && (polygons.size() == 0)))
			throw new TransitionException(Messages
					.getString("MultipolygonTool.0")); //$NON-NLS-1$
		if (points.size() > 0) {
			addPolygon();
		}
		MultiPolygon mp = gf.createMultiPolygon(polygons
				.toArray(new Polygon[0]));
		if (!mp.isValid()) {
			throw new TransitionException(Messages
					.getString("MultipolygonTool.2")); //$NON-NLS-1$
		}
		multipolygonDone(mp);

		polygons.clear();
		transition("init"); //$NON-NLS-1$
	}

	protected abstract void multipolygonDone(MultiPolygon mp)
			throws TransitionException;

	@Override
	public void transitionTo_Cancel() throws FinishedAutomatonException,
			TransitionException {
	}

	@Override
	public void drawIn_Standby(Graphics g) throws DrawingException {
		drawIn_Point(g);
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	@Override
	public void drawIn_Point(Graphics g) throws DrawingException {
		Point2D current = tm.getLastRealMousePosition();
		ArrayList<Coordinate> tempPoints = (ArrayList<Coordinate>) points
				.clone();
		tempPoints.add(new Coordinate(current.getX(), current.getY()));
		tempPoints
				.add(new Coordinate(tempPoints.get(0).x, tempPoints.get(0).y));
		ArrayList<Polygon> tempPolygons = (ArrayList<Polygon>) polygons.clone();
		if (tempPoints.size() >= 4) {
			tempPolygons.add(gf.createPolygon(gf.createLinearRing(tempPoints
					.toArray(new Coordinate[0])), new LinearRing[0]));
		}

		if (tempPolygons.size() == 0)
			return;

		MultiPolygon mp = gf.createMultiPolygon(tempPolygons
				.toArray(new Polygon[0]));

		tm.addGeomToDraw(mp);

		if (!mp.isValid()) {
			throw new DrawingException(Messages.getString("MultipolygonTool.2")); //$NON-NLS-1$
		}
	}

	@Override
	public void drawIn_Line(Graphics g) throws DrawingException {
	}

	@Override
	public void drawIn_Done(Graphics g) throws DrawingException {
	}

	@Override
	public void drawIn_Cancel(Graphics g) throws DrawingException {
	}

	public URL getMouseCursor() {
		return null;
	}

}
