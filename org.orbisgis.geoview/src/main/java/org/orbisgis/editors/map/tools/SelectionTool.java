/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
package org.orbisgis.editors.map.tools;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.orbisgis.editors.map.tool.CannotChangeGeometryException;
import org.orbisgis.editors.map.tool.DrawingException;
import org.orbisgis.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.editors.map.tool.Handler;
import org.orbisgis.editors.map.tool.Rectangle2DDouble;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.editors.map.tools.generated.Selection;
import org.orbisgis.layerModel.MapContext;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Tool to select geometries
 *
 * @author Fernando Gonzlez Corts
 */
public class SelectionTool extends Selection {

	private Rectangle2DDouble rect = new Rectangle2DDouble();

	private ArrayList<Handler> selected = new ArrayList<Handler>();

	/**
	 * @see org.estouro.tools.generated.Selection#transitionTo_Standby()
	 */
	@Override
	public void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws TransitionException {
		if (ToolValidationUtilities.activeSelectionGreaterThan(vc, 0)) {
			setStatus("Selection"); //$NON-NLS-1$
		}
	}

	/**
	 * @throws FinishedAutomatonException
	 * @throws
	 * @see org.estouro.tools.generated.Selection#transitionTo_OnePoint()
	 */
	@Override
	public void transitionTo_OnePoint(MapContext vc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		// Rectangle2DDouble p = new Rectangle2DDouble(tm.getValues()[0]
		// - tm.getTolerance() / 2, tm.getValues()[1] - tm.getTolerance()
		// / 2, tm.getTolerance(), tm.getTolerance());
		//
		// try {
		// if ((tm.getMouseModifiers() & MouseEvent.CTRL_DOWN_MASK) ==
		// MouseEvent.CTRL_DOWN_MASK) {
		// boolean change = vc
		// .selectFeatures(p.getEnvelope(), true, false);
		// if (!change) {
		// transition("no-selection");
		// } else {
		// if (vc.atLeastNGeometriesSelected(1)) {
		// transition("selection"); //$NON-NLS-1$
		// } else {
		// transition("init"); //$NON-NLS-1$
		// }
		// }
		// } else {
		// vc.selectFeatures(p.getEnvelope(), false, false);
		// if (vc.atLeastNGeometriesSelected(1)) {
		// transition("selection"); //$NON-NLS-1$
		// } else {
		// transition("no-selection"); //$NON-NLS-1$
		// rect.setRect(tm.getValues()[0], tm.getValues()[1], 0, 0);
		// }
		// }
		// } catch (EditionContextException e) {
		// transition("no-selection"); //$NON-NLS-1$
		// throw new TransitionException(e);
		// }
	}

	/**
	 * @see org.estouro.tools.generated.Selection#transitionTo_OnePointLeft()
	 */
	@Override
	public void transitionTo_OnePointLeft(MapContext vc, ToolManager tm)
			throws TransitionException {
	}

	/**
	 * @see org.estouro.tools.generated.Selection#transitionTo_TwoPoints()
	 */
	@Override
	public void transitionTo_TwoPoints(MapContext vc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		// boolean intersects = true;
		// if (rect.getMinX() < tm.getValues()[0]) {
		// intersects = false;
		// }
		// rect.add(tm.getValues()[0], tm.getValues()[1]);
		//
		// try {
		// vc
		// .selectFeatures(
		// rect.getEnvelope(),
		// (tm.getMouseModifiers() & MouseEvent.CTRL_DOWN_MASK) ==
		// MouseEvent.CTRL_DOWN_MASK,
		// !intersects);
		// } catch (EditionContextException e) {
		// throw new TransitionException(e);
		// }
		//
		// if (vc.atLeastNGeometriesSelected(1)) {
		// transition("selection"); //$NON-NLS-1$
		// } else {
		// transition("no-selection"); //$NON-NLS-1$
		// }
	}

	/**
	 * @see org.estouro.tools.generated.Selection#transitionTo_Selection()
	 */
	@Override
	public void transitionTo_Selection(MapContext vc, ToolManager tm)
			throws TransitionException {
		rect = new Rectangle2DDouble();
	}

	/**
	 * @see org.estouro.tools.generated.Selection#transitionTo_PointWithSelection()
	 */
	@Override
	public void transitionTo_PointWithSelection(MapContext vc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		Point2D p = new Point2D.Double(tm.getValues()[0], tm.getValues()[1]);

		HashSet<Object> geom = new HashSet<Object>();
		ArrayList<Handler> handlers = tm.getCurrentHandlers();
		selected.clear();
		for (int i = 0; i < handlers.size(); i++) {
			Handler handler = handlers.get(i);

			/*
			 * Don't select two handlers from the same geometry
			 */
			if (geom.contains(handler.getGeometryId()))
				continue;

			if (p.distance(handler.getPoint()) < tm.getTolerance()) {
				if (!ToolValidationUtilities.isActiveLayerEditable(vc)) {
					throw new TransitionException(Messages
							.getString("SelectionTool.10")); //$NON-NLS-1$
				}
				selected.add(handler);
				geom.add(handler.getGeometryId());
			}
		}

		if (selected.size() == 0) {
			transition("out-handler"); //$NON-NLS-1$
			rect.setRect(tm.getValues()[0], tm.getValues()[1], 0, 0);
		} else {
			transition("in-handler"); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.estouro.tools.generated.Selection#transitionTo_Movement()
	 */
	@Override
	public void transitionTo_Movement(MapContext vc, ToolManager tm)
			throws TransitionException {

	}

	/**
	 * @see org.estouro.tools.generated.Selection#transitionTo_MakeMove()
	 */
	@Override
	public void transitionTo_MakeMove(MapContext vc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		//
		// for (int i = 0; i < selected.size(); i++) {
		// Handler handler = selected.get(i);
		// Geometry g;
		// try {
		// g = handler.moveTo(tm.getValues()[0], tm.getValues()[1]);
		// } catch (CannotChangeGeometryException e1) {
		// throw new TransitionException(e1);
		// }
		//
		// try {
		// vc.updateGeometry(g);
		// } catch (EditionContextException e) {
		// throw new TransitionException(e);
		// }
		// }
		//
		// transition("empty"); //$NON-NLS-1$
	}

	/**
	 * @see org.estouro.tools.generated.Selection#drawIn_Standby(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
	}

	/**
	 * @see org.estouro.tools.generated.Selection#drawIn_OnePoint(java.awt.Graphics)
	 */
	@Override
	public void drawIn_OnePoint(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.estouro.tools.generated.Selection#drawIn_OnePointLeft(java.awt.Graphics)
	 */
	@Override
	public void drawIn_OnePointLeft(Graphics g, MapContext vc, ToolManager tm) {
		Point p = tm.getMapTransform().fromMapPoint(
				new Point2D.Double(rect.getX(), rect.getY()));
		int minx = Math.min(p.x, tm.getLastMouseX());
		int miny = Math.min(p.y, tm.getLastMouseY());
		int width = Math.abs(p.x - tm.getLastMouseX());
		int height = Math.abs(p.y - tm.getLastMouseY());
		if (tm.getLastMouseX() < p.x) {
			((Graphics2D) g).setStroke(new BasicStroke(1,
					BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10,
					new float[] { 10, 3 }, 0));
		} else {
			((Graphics2D) g).setStroke(new BasicStroke());
		}
		g.drawRect(minx, miny, width, height);
	}

	/**
	 * @see org.estouro.tools.generated.Selection#drawIn_TwoPoints(java.awt.Graphics)
	 */
	@Override
	public void drawIn_TwoPoints(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.estouro.tools.generated.Selection#drawIn_Selection(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Selection(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.estouro.tools.generated.Selection#drawIn_PointWithSelection(java.awt.Graphics)
	 */
	@Override
	public void drawIn_PointWithSelection(Graphics g, MapContext vc,
			ToolManager tm) {
	}

	/**
	 * @see org.estouro.tools.generated.Selection#drawIn_Movement(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Movement(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
		Point2D p = tm.getLastRealMousePosition();
		try {
			for (int i = 0; i < selected.size(); i++) {
				Handler handler = selected.get(i);
				Geometry geom = handler.moveTo(p.getX(), p.getY());
				tm.addGeomToDraw(geom);
			}
		} catch (CannotChangeGeometryException e) {
			throw new DrawingException(
					Messages.getString("SelectionTool.11") + e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.estouro.tools.generated.Selection#drawIn_MakeMove(java.awt.Graphics)
	 */
	@Override
	public void drawIn_MakeMove(Graphics g, MapContext vc, ToolManager tm) {

	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolValidationUtilities.isActiveLayerEditable(vc)
				&& ToolValidationUtilities.isActiveLayerVisible(vc);
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

	public URL getMouseCursor() {
		return null;
	}

}
