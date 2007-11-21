package org.orbisgis.tools.instances;

import java.awt.Graphics;
import java.util.ArrayList;

import org.orbisgis.tools.DrawingException;
import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.generated.Multipoint;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;

public abstract class AbstractMultipointTool extends Multipoint {

	private ArrayList<Coordinate> point = new ArrayList<Coordinate>();

	@Override
	public void transitionTo_Standby(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
	}

	@Override
	public void transitionTo_Point(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		Coordinate c = new Coordinate(tm.getValues()[0], tm.getValues()[1]);
		point.add(c);
	}

	@Override
	public void transitionTo_Done(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		MultiPoint g = ToolManager.toolsGeometryFactory.createMultiPoint(point
				.toArray(new Coordinate[0]));
		multipointDone(g, vc, tm);

		point = new ArrayList<Coordinate>();
		transition("init"); //$NON-NLS-1$
	}

	protected abstract void multipointDone(MultiPoint mp, ViewContext vc,
			ToolManager tm) throws TransitionException;

	@Override
	public void transitionTo_Cancel(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
	}

	@Override
	public void drawIn_Standby(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {

	}

	@Override
	public void drawIn_Point(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {
		Geometry geom = ToolManager.toolsGeometryFactory.createMultiPoint(point
				.toArray(new Coordinate[0]));
		tm.addGeomToDraw(geom);
	}

	@Override
	public void drawIn_Done(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {

	}

	@Override
	public void drawIn_Cancel(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {
	}

}
