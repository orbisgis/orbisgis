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
