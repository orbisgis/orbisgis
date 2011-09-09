/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
package org.orbisgis.core.ui.editors.map.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.CannotChangeGeometryException;
import org.orbisgis.core.ui.editors.map.tool.DrawingException;
import org.orbisgis.core.ui.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.core.ui.editors.map.tool.Handler;
import org.orbisgis.core.ui.editors.map.tool.Rectangle2DDouble;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.editors.map.tools.generated.Selection;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Tool to select geometries
 * 
 * @author Fernando Gonzalez Cortes
 */
public abstract class AbstractSelectionTool extends Selection {

	private Rectangle2DDouble rect = new Rectangle2DDouble();

	private ArrayList<Handler> selected = new ArrayList<Handler>();

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#transitionTo_Standby()
	 */
	@Override
	public void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws TransitionException {
		if (ToolUtilities.activeSelectionGreaterThan(vc, 0)) {
			setStatus("Selection"); //$NON-NLS-1$
		}
	}

	private int[] toggleSelection(int[] sel, int selectedItem) {
		int indexInSel = -1;
		for (int j = 0; j < sel.length; j++) {
			if (sel[j] == selectedItem) {
				indexInSel = j;
				break;
			}
		}

		if (indexInSel != -1) {
			int[] newSel = new int[sel.length - 1];
			System.arraycopy(sel, 0, newSel, 0, indexInSel);
			System.arraycopy(sel, indexInSel + 1, newSel, indexInSel,
					sel.length - (indexInSel + 1));
			return newSel;
		} else {
			int[] newSel = new int[sel.length + 1];
			System.arraycopy(sel, 0, newSel, 0, sel.length);
			newSel[sel.length] = selectedItem;
			return newSel;
		}
	}

	/**
	 * @throws FinishedAutomatonException
	 * @throws
	 * @see org.estouro.tools.generated.Selection#transitionTo_OnePoint()
	 */
	@Override
	public void transitionTo_OnePoint(MapContext mc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		Rectangle2DDouble p = new Rectangle2DDouble(tm.getValues()[0]
				- tm.getTolerance() / 2, tm.getValues()[1] - tm.getTolerance()
				/ 2, tm.getTolerance(), tm.getTolerance());

		Geometry selectionRect = p.getEnvelope(ToolManager.toolsGeometryFactory);

		ILayer activeLayer = getLayer(mc);
		SpatialDataSourceDecorator ds = activeLayer.getSpatialDataSource();
		try {
			Iterator<Integer> l = queryLayer(activeLayer.getSpatialDataSource(), p);
			while (l.hasNext()) {
				int rowIndex = l.next();
				Geometry g = (Geometry) ds.getGeometry(rowIndex);
				if (g.intersects(selectionRect)) {
					if ((tm.getMouseModifiers() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
						int[] newSel = toggleSelection(activeLayer
								.getSelection(), rowIndex);
                                                mc.checkSelectionRefresh(newSel, activeLayer.getSelection(), ds);
						activeLayer.setSelection(newSel);
						if (newSel.length > 0) {
							transition("selection"); //$NON-NLS-1$
						} else {
							transition("init"); //$NON-NLS-1$
						}

						return;
					} else {
						int[] newSelection = new int[] { rowIndex };
                                                mc.checkSelectionRefresh(newSelection, activeLayer.getSelection(), ds);
						activeLayer.setSelection(newSelection);
						transition("selection"); //$NON-NLS-1$
						return;
					}
				}
			}

			transition("no-selection"); //$NON-NLS-1$
			rect.setRect(tm.getValues()[0], tm.getValues()[1], 0, 0);
		} catch (DriverException e) {
			transition("no-selection"); //$NON-NLS-1$
			throw new TransitionException(e);
		}
	}

	protected abstract ILayer getLayer(MapContext mc);

	private Iterator<Integer> queryLayer(SpatialDataSourceDecorator ds,
			Rectangle2DDouble rect) throws DriverException {
		String geomFieldName = ds.getMetadata().getFieldName(
				ds.getSpatialFieldIndex());
		Envelope env = new Envelope(rect.getMinX(), rect.getMaxX(), rect
				.getMinY(), rect.getMaxY());
		Iterator<Integer> res = ds.queryIndex(new DefaultSpatialIndexQuery(env,
				geomFieldName));

		return res;
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#transitionTo_OnePointLeft()
	 */
	@Override
	public void transitionTo_OnePointLeft(MapContext vc, ToolManager tm)
			throws TransitionException {
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#transitionTo_TwoPoints()
	 */
	@Override
	public void transitionTo_TwoPoints(MapContext mc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		ILayer activeLayer = getLayer(mc);
		boolean intersects = true;
		if (rect.getMinX() < tm.getValues()[0]) {
			intersects = false;
		}
		rect.add(tm.getValues()[0], tm.getValues()[1]);

		Geometry selectionRect = rect.getEnvelope(ToolManager.toolsGeometryFactory);

		SpatialDataSourceDecorator ds = activeLayer.getSpatialDataSource();
		try {
			ArrayList<Integer> newSelection = new ArrayList<Integer>();
			Iterator<Integer> l = queryLayer(ds, rect);
			while (l.hasNext()) {
				int index = l.next();
				Geometry g = (Geometry) ds.getGeometry(index);

				if (intersects) {
					if (g.intersects(selectionRect)) {
						newSelection.add(index);
					}
				} else {
					if (selectionRect.contains(g)) {
						newSelection.add(index);
					}
				}
			}

			if ((tm.getMouseModifiers() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK) {
				int[] newSel = activeLayer.getSelection();
				for (int i = 0; i < newSelection.size(); i++) {
					newSel = toggleSelection(newSel, newSelection.get(i));
				}
                                mc.checkSelectionRefresh(newSel, activeLayer.getSelection(), ds);
				activeLayer.setSelection(newSel);

			} else {
				int[] ns = new int[newSelection.size()];
				for (int i = 0; i < ns.length; i++) {
					ns[i] = newSelection.get(i);
				}
                                mc.checkSelectionRefresh(ns, activeLayer.getSelection(), ds);
				activeLayer.setSelection(ns);
			}

			if (activeLayer.getSelection().length == 0) {
				transition("no-selection"); //$NON-NLS-1$
			} else {
				transition("selection"); //$NON-NLS-1$
			}
		} catch (DriverException e) {
			transition("no-selection"); //$NON-NLS-1$
			throw new TransitionException(e);
		}
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#transitionTo_Selection()
	 */
	@Override
	public void transitionTo_Selection(MapContext vc, ToolManager tm)
			throws TransitionException {
		rect = new Rectangle2DDouble();
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#transitionTo_PointWithSelection()
	 */
	@Override
	public void transitionTo_PointWithSelection(MapContext mc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		Point2D p = new Point2D.Double(tm.getValues()[0], tm.getValues()[1]);

		BitSet geom = new BitSet();
		ArrayList<Handler> handlers = tm.getCurrentHandlers();
		selected.clear();
		for (int i = 0; i < handlers.size(); i++) {
			Handler handler = handlers.get(i);

			/*
			 * Don't select two handlers from the same geometry
			 */
			if (geom.get(handler.getGeometryIndex())) {
				continue;
			}

			if (p.distance(handler.getPoint()) < tm.getTolerance()) {
				if (!ToolUtilities.isActiveLayerEditable(mc)) {
					throw new TransitionException(
							I18N
									.getString("orbisgis.core.ui.editors.map.tool.selectionTool_0")); //$NON-NLS-1$
				}
				selected.add(handler);
				geom.set(handler.getGeometryIndex());
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
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#transitionTo_Movement()
	 */
	@Override
	public void transitionTo_Movement(MapContext vc, ToolManager tm)
			throws TransitionException {

	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#transitionTo_MakeMove()
	 */
	@Override
	public void transitionTo_MakeMove(MapContext mc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		SpatialDataSourceDecorator ds = getLayer(mc).getSpatialDataSource();
		for (int i = 0; i < selected.size(); i++) {
			Handler handler = selected.get(i);
			Geometry g;
			try {
				g = handler.moveTo(tm.getValues()[0], tm.getValues()[1]);
			} catch (CannotChangeGeometryException e1) {
				throw new TransitionException(e1);
			}

			try {
				ds.setGeometry(handler.getGeometryIndex(), g);
			} catch (DriverException e) {
				throw new TransitionException(e);
			}
		}

		transition("empty"); //$NON-NLS-1$
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#drawIn_Standby(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#drawIn_OnePoint(java.awt.Graphics)
	 */
	@Override
	public void drawIn_OnePoint(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#drawIn_OnePointLeft(java.awt.Graphics)
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
		Rectangle2DDouble shape = new Rectangle2DDouble(minx, miny, width,
				height);
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(new Color(255, 204, 51, 50));
		g2.fill(shape);
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g2.setColor(new Color(255, 204, 51));
		g2.draw(shape);
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#drawIn_TwoPoints(java.awt.Graphics)
	 */
	@Override
	public void drawIn_TwoPoints(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#drawIn_Selection(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Selection(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#drawIn_PointWithSelection(java.awt.Graphics)
	 */
	@Override
	public void drawIn_PointWithSelection(Graphics g, MapContext vc,
			ToolManager tm) {
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#drawIn_Movement(java.awt.Graphics)
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
					I18N
							.getString("org.orbisgis.core.ui.editors.map.tool.selectionTool_1") + e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#drawIn_MakeMove(java.awt.Graphics)
	 */
	@Override
	public void drawIn_MakeMove(Graphics g, MapContext vc, ToolManager tm) {

	}

}
