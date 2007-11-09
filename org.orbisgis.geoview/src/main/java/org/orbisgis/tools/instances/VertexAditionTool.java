package org.orbisgis.tools.instances;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.net.URL;

import org.orbisgis.tools.CannotChangeGeometryException;
import org.orbisgis.tools.DrawingException;
import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.generated.VertexAdition;

import com.vividsolutions.jts.geom.Geometry;

public class VertexAditionTool extends VertexAdition {

	@Override
	public void transitionTo_Standby() throws FinishedAutomatonException,
			TransitionException {

	}

	@Override
	public void transitionTo_Done() throws FinishedAutomatonException,
			TransitionException {
		Point2D p = new Point2D.Double(tm.getValues()[0], tm.getValues()[1]);
		try {
			Geometry[] selection = ec.getSelectedGeometries();
			for (int i = 0; i < selection.length; i++) {
				Primitive prim = new Primitive(selection[i]);
				Geometry g = prim.insertVertex(p, tm.getTolerance());
				if (g != null) {
					ec.updateGeometry(g);
					break;
				}
			}
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		} catch (CannotChangeGeometryException e) {
			throw new TransitionException(e);
		}

		transition("init"); //$NON-NLS-1$
	}

	@Override
	public void transitionTo_Cancel() throws FinishedAutomatonException,
			TransitionException {

	}

	@Override
	public void drawIn_Standby(Graphics g) throws DrawingException {
		Point2D p = tm.getLastRealMousePosition();
		try {
			Geometry[] selection = ec.getSelectedGeometries();
			for (int i = 0; i < selection.length; i++) {
				Primitive prim = new Primitive(selection[i]);
				Geometry geom = prim.insertVertex(p, tm.getTolerance());
				tm.addGeomToDraw(geom);
			}
		} catch (CannotChangeGeometryException e) {
			throw new DrawingException(e);
		} catch (EditionContextException e) {
			throw new DrawingException(e);
		}
	}

	@Override
	public void drawIn_Done(Graphics g) throws DrawingException {

	}

	@Override
	public void drawIn_Cancel(Graphics g) throws DrawingException {

	}

	public boolean isEnabled() {
		try {
			return ec.getSelectedGeometries().length >= 1
					&& ec.isActiveThemeWritable();
		} catch (EditionContextException e) {
			return false;
		}
	}

	public boolean isVisible() {
		return true;
	}

	public URL getMouseCursor() {
		return null;
	}
}
