package org.orbisgis.tools.instances;

import java.awt.Graphics;

import org.orbisgis.tools.DrawingException;
import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.generated.Point;

import com.vividsolutions.jts.geom.Coordinate;

public abstract class AbstractPointTool extends Point {

	@Override
	public void transitionTo_Done() throws FinishedAutomatonException,
			TransitionException {
		Coordinate coordinate = new Coordinate(tm.getValues()[0], tm
				.getValues()[1]);
		pointDone(ToolManager.toolsGeometryFactory.createPoint(coordinate));

		transition("init"); //$NON-NLS-1$
	}

	protected abstract void pointDone(com.vividsolutions.jts.geom.Point point)
			throws TransitionException;

    @Override
    public void transitionTo_Standby() throws FinishedAutomatonException,
            TransitionException {

    }

    @Override
    public void transitionTo_Cancel() throws FinishedAutomatonException,
            TransitionException {
    }

    @Override
    public void drawIn_Standby(Graphics g) throws DrawingException {
    }

    @Override
    public void drawIn_Done(Graphics g) throws DrawingException {
    }

    @Override
    public void drawIn_Cancel(Graphics g) throws DrawingException {
    }

}
