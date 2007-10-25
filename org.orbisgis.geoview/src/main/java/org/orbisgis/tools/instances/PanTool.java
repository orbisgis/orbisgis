/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.tools.instances;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.orbisgis.tools.FinishedAutomatonException;
import org.orbisgis.tools.NoSuchTransitionException;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.generated.Pan;

/**
 * Tool to move the map extent
 *
 */
public class PanTool extends Pan {

	private double[] firstPoint;

	/**
	 * @see org.estouro.tools.generated.Pan#transitionTo_Standby()
	 */
	@Override
	public void transitionTo_Standby() throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.Pan#transitionTo_OnePointLeft()
	 */
	@Override
	public void transitionTo_OnePointLeft() throws TransitionException {
		firstPoint = tm.getValues();
	}

	/**
	 * @throws FinishedAutomatonException
	 * @throws NoSuchTransitionException
	 * @see org.estouro.tools.generated.Pan#transitionTo_RectangleDone()
	 */
	@Override
	public void transitionTo_RectangleDone() throws TransitionException, FinishedAutomatonException {
		double[] v = tm.getValues();
		double dx = firstPoint[0] - v[0];
		double dy = firstPoint[1] - v[1];

		Rectangle2D extent = ec.getExtent();
        ec
				.setExtent(new Rectangle2D.Double(extent.getX() + dx, extent
						.getY()
						+ dy, extent.getWidth(), extent.getHeight()));

		transition("init"); //$NON-NLS-1$
	}

	/**
	 * @see org.estouro.tools.generated.Pan#transitionTo_Cancel()
	 */
	@Override
	public void transitionTo_Cancel() throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.Pan#drawIn_Standby(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Standby(Graphics g) {
	}

	/**
	 * @see org.estouro.tools.generated.Pan#drawIn_OnePointLeft(java.awt.Graphics)
	 */
	@Override
	public void drawIn_OnePointLeft(Graphics g) {
		Point p = ec.fromMapPoint(new Point2D.Double(firstPoint[0], firstPoint[1]));
        int height = ec.getMapImage().getHeight(null);
        int width = ec.getMapImage().getWidth(null);
        g.clearRect(0, 0, width, height);
		g.drawImage(ec.getMapImage(),
				tm.getLastMouseX() - p.x, tm.getLastMouseY() - p.y, null);
	}

	/**
	 * @see org.estouro.tools.generated.Pan#drawIn_RectangleDone(java.awt.Graphics)
	 */
	@Override
	public void drawIn_RectangleDone(Graphics g) {
	}

	/**
	 * @see org.estouro.tools.generated.Pan#drawIn_Cancel(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Cancel(Graphics g) {
	}

    public boolean isEnabled() {
        return ec.atLeastNThemes(1);
    }

    public boolean isVisible() {
        return true;
    }

}
