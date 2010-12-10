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

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Observable;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Tool to zoom in
 * 
 * @author Fernando Gonzalez Cortes
 */
public class ZoomInTool extends AbstractRectangleTool {

	AbstractButton button;

	public ZoomInTool() {
	}

	@Override
	public AbstractButton getButton() {
		return button;
	}

	public void setButton(AbstractButton button) {
		this.button = button;
	}

	@Override
	public void update(Observable o, Object arg) {
		PlugInContext.checkTool(this);
	}

	@Override
	protected void rectangleDone(Rectangle2D rect,
			boolean smallerThanTolerance, MapContext vc, ToolManager tm)
			throws TransitionException {
		tm.getMapTransform().setExtent(
				new Envelope(rect.getMinX(), rect.getMaxX(), rect.getMinY(),
						rect.getMaxY()));
	}

	@Override
	protected Rectangle2D buildRectangleOnPoint(ToolManager toolManager,
			double x, double y) {
		Envelope extent = toolManager.getMapTransform().getExtent();
		double width = extent.getWidth() / 2;
		double height = extent.getHeight() / 2;
		double minx = x - width / 2;
		double miny = y - height / 2;
		Rectangle2D.Double newRect = new Rectangle2D.Double(minx, miny, width,
				height);
		return newRect;
	}

	@Override
	public Point getHotSpotOffset() {
		return new Point(5, 5);
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.layerCountGreaterThan(vc, 0);
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

	public ImageIcon getImageIcon() {
		return OrbisGISIcon.ZOOMIN;
	}

	public String getName() {
		return I18N.getText("orbisgis.core.ui.editors.map.tool.zoomin_tooltip");
	}

}
