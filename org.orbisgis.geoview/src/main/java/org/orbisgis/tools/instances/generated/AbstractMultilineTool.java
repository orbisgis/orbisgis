package org.orbisgis.tools.instances.generated;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;

import org.orbisgis.tools.DrawingException;
import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.Messages;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public abstract class AbstractMultilineTool extends Multiline {

	protected GeometryFactory gf = new GeometryFactory();
	protected ArrayList<Coordinate> points = new ArrayList<Coordinate>();
	protected ArrayList<LineString> lines = new ArrayList<LineString>();

	@Override
	public void transitionTo_Done() throws FinishedAutomatonException,
			TransitionException {
		if (((points.size() < 2) && (points.size() > 0))
				|| ((points.size() == 0) && (lines.size() == 0)))
			throw new TransitionException(Messages.getString("MultilineTool.0")); //$NON-NLS-1$
		if (points.size() > 0) {
			addLine();
		}

		MultiLineString mls = gf.createMultiLineString(lines
				.toArray(new LineString[0]));
		if (!mls.isValid()) {
			throw new TransitionException(Messages.getString("MultilineTool.1")); //$NON-NLS-1$
		}

		multilineDone(mls);

		lines.clear();
		transition("init"); //$NON-NLS-1$
	}

	protected abstract void multilineDone(MultiLineString mls)
			throws TransitionException;

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
		if (points.size() < 2)
			throw new TransitionException(Messages.getString("MultilineTool.0")); //$NON-NLS-1$

		addLine();

		transition("init"); //$NON-NLS-1$
	}

	protected void addLine() throws TransitionException {
		LineString ls = gf.createLineString(points.toArray(new Coordinate[0]));
		if (!ls.isValid()) {
			throw new TransitionException(Messages.getString("MultilineTool.1")); //$NON-NLS-1$
		}
		lines.add(ls);
	}

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
		ArrayList<LineString> tempLines = (ArrayList<LineString>) lines.clone();
		if (tempPoints.size() >= 2) {
			tempLines.add(gf.createLineString(tempPoints
					.toArray(new Coordinate[0])));
		}

		if (tempLines.size() == 0)
			return;

		MultiLineString mls = gf.createMultiLineString(tempLines
				.toArray(new LineString[0]));

		tm.addGeomToDraw(mls);

		if (!mls.isValid()) {
			throw new DrawingException(Messages.getString("MultilineTool.1")); //$NON-NLS-1$
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
