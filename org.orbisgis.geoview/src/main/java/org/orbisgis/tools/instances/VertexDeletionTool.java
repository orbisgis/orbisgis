package org.orbisgis.tools.instances;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;

import org.orbisgis.tools.CannotChangeGeometryException;
import org.orbisgis.tools.DrawingException;
import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.Handler;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.generated.VertexDeletion;

public class VertexDeletionTool extends VertexDeletion {

    @Override
    public void transitionTo_Standby() throws FinishedAutomatonException,
            TransitionException {

    }

    @Override
    public void transitionTo_Done() throws FinishedAutomatonException,
            TransitionException {
        Point2D p = tm.getLastRealMousePosition();
        ArrayList<Handler> handlers = tm.getCurrentHandlers();

        for (Handler handler : handlers) {
            if (p.distance(handler.getPoint()) < tm.getTolerance()) {
                try {
                    if (p.distance(handler.getPoint()) < tm.getTolerance()) {
                    	ec.updateGeometry(handler.remove());
                        break;
                    }
                     } catch (CannotChangeGeometryException e) {
                    throw new TransitionException(e.getMessage());
                } catch (EditionContextException e) {
                    throw new TransitionException(e);
                }
            }
        }
        transition("init"); //$NON-NLS-1$
    }

    @Override
    public void transitionTo_Cancel() throws FinishedAutomatonException,
            TransitionException {

    }

    @Override
    public void drawIn_Standby( Graphics g) throws DrawingException {
        Point2D p = tm.getLastRealMousePosition();
        ArrayList<Handler> handlers = tm.getCurrentHandlers();

        for (Handler handler : handlers) {
            try {
                if (p.distance(handler.getPoint()) < tm.getTolerance()) {
                	tm.addGeomToDraw(handler.remove());
                    break;
                }
            } catch (CannotChangeGeometryException e) {
                throw new DrawingException(Messages.getString("VertexDeletionTool.1")); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void drawIn_Done( Graphics g) throws DrawingException {

    }

    @Override
    public void drawIn_Cancel( Graphics g) throws DrawingException {

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
