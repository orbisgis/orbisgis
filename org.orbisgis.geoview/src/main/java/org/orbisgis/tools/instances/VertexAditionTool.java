package org.orbisgis.tools.instances;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.net.URL;

import org.orbisgis.tools.CannotChangeGeometryException;
import org.orbisgis.tools.DrawingException;
import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;
import org.orbisgis.tools.instances.generated.VertexAdition;

import com.vividsolutions.jts.geom.Geometry;

public class VertexAditionTool extends VertexAdition {

	@Override
	public void transitionTo_Standby(ViewContext vc, ToolManager tm) throws FinishedAutomatonException,
			TransitionException {

	}

	@Override
	public void transitionTo_Done(ViewContext vc, ToolManager tm) throws FinishedAutomatonException,
			TransitionException {
		Point2D p = new Point2D.Double(tm.getValues()[0], tm.getValues()[1]);
		try {
			Geometry[] selection = vc.getSelectedGeometries();
			for (int i = 0; i < selection.length; i++) {
				Primitive prim = new Primitive(selection[i]);
				Geometry g = prim.insertVertex(p, tm.getTolerance());
				if (g != null) {
					vc.updateGeometry(g);
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
	public void transitionTo_Cancel(ViewContext vc, ToolManager tm) throws FinishedAutomatonException,
			TransitionException {

	}

	@Override
	public void drawIn_Standby(Graphics g, ViewContext vc, ToolManager tm) throws DrawingException {
		Point2D p = tm.getLastRealMousePosition();
		try {
			Geometry[] selection = vc.getSelectedGeometries();
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
	public void drawIn_Done(Graphics g, ViewContext vc, ToolManager tm) throws DrawingException {

	}

	@Override
	public void drawIn_Cancel(Graphics g, ViewContext vc, ToolManager tm) throws DrawingException {

	}

	public boolean isEnabled(ViewContext vc, ToolManager tm) {
		try {
			return vc.getSelectedGeometries().length >= 1
					&& vc.isActiveThemeWritable();
		} catch (EditionContextException e) {
			return false;
		}
	}

	public boolean isVisible(ViewContext vc, ToolManager tm) {
		return true;
	}

	public URL getMouseCursor() {
		return null;
	}
}
