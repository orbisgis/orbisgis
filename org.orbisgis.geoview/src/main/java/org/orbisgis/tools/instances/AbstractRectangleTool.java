package org.orbisgis.tools.instances;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.net.URL;

import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.generated.ZoomIn;

public abstract class AbstractRectangleTool extends ZoomIn {

	protected double[] firstPoint;

	/**
	 * @see org.estouro.tools.generated.Rectangle#transitionTo_Standby()
	 */
	@Override
	public void transitionTo_Standby() throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#transitionTo_OnePointLeft()
	 */
	@Override
	public void transitionTo_OnePointLeft() throws TransitionException {
		firstPoint = tm.getValues();
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#transitionTo_Cancel()
	 */
	@Override
	public void transitionTo_Cancel() throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#drawIn_Standby(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Standby(Graphics g) {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#drawIn_OnePointLeft(java.awt.Graphics)
	 */
	@Override
	public void drawIn_OnePointLeft(Graphics g) {
		Point p = ec.fromMapPoint(new Point2D.Double(firstPoint[0],
				firstPoint[1]));
		int minx = Math.min(p.x, tm.getLastMouseX());
		int miny = Math.min(p.y, tm.getLastMouseY());
		int width = Math.abs(p.x - tm.getLastMouseX());
		int height = Math.abs(p.y - tm.getLastMouseY());
		g.drawRect(minx, miny, width, height);
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#drawIn_RectangleDone(java.awt.Graphics)
	 */
	@Override
	public void drawIn_RectangleDone(Graphics g) {
	}

	/**
	 * @see org.estouro.tools.generated.ZoomIn#drawIn_Cancel(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Cancel(Graphics g) {
	}

	@Override
	public Point getHotSpotOffset() {
		return new Point(0, 0);
	}

	@Override
	public URL getMouseCursorURL() {
		return null;
	}

}
