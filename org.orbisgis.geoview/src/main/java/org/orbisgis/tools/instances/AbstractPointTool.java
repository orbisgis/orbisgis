package org.orbisgis.tools.instances;

import java.awt.Graphics;

import org.orbisgis.tools.DrawingException;
import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.generated.Point;

import com.vividsolutions.jts.geom.Coordinate;

public abstract class AbstractPointTool extends Point {

	@Override
	public void transitionTo_Done(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
		Coordinate coordinate = new Coordinate(tm.getValues()[0], tm
				.getValues()[1]);
		pointDone(ToolManager.toolsGeometryFactory.createPoint(coordinate), vc,
				tm);

		transition("init"); //$NON-NLS-1$
	}

	protected abstract void pointDone(com.vividsolutions.jts.geom.Point point,
			ViewContext vc, ToolManager tm) throws TransitionException;

	@Override
	public void transitionTo_Standby(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {

	}

	@Override
	public void transitionTo_Cancel(ViewContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException {
	}

	@Override
	public void drawIn_Standby(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {
	}

	@Override
	public void drawIn_Done(Graphics g, ViewContext vc, ToolManager tm)
			throws DrawingException {
	}

	@Override
	public void drawIn_Cancel(Graphics g, ViewContext vc,
			ToolManager tm) throws DrawingException {
	}

}
