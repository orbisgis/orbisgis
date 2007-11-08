package org.orbisgis.tools.instances;

import java.awt.geom.Rectangle2D;

import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.TransitionException;

public abstract class BasicRectangleTool extends AbstractRectangleTool {

	/**
	 * @see org.estouro.tools.generated.ZoomIn#transitionTo_RectangleDone()
	 */
	@Override
	public void transitionTo_RectangleDone() throws TransitionException, FinishedAutomatonException {
		double[] v = tm.getValues();

		double minx = Math.min(firstPoint[0], v[0]);
		double miny = Math.min(firstPoint[1], v[1]);

		Rectangle2D rect = new Rectangle2D.Double(minx, miny, Math
				.abs(v[0] - firstPoint[0]), Math.abs(v[1] - firstPoint[1]));

		rectangleDone(rect);

		transition("init"); //$NON-NLS-1$
	}

	protected abstract void rectangleDone(Rectangle2D rect);

}
