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

import java.awt.Graphics;
import java.util.Observable;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.ui.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.editors.map.tools.generated.ZoomOut;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Tool to zoom out
 * 
 * @author Fernando Gonzalez Cortes
 */
public class ZoomOutTool extends ZoomOut {

	AbstractButton button;

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

	/**
	 * @see org.orbisgis.plugins.core.ui.editors.map.tools.generated.estouro.tools.generated.ZoomOut#transitionTo_Standby()
	 */
	@Override
	public void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws TransitionException {
	}

	/**
	 * @see org.orbisgis.plugins.core.ui.editors.map.tools.generated.estouro.tools.generated.ZoomOut#transitionTo_Done()
	 */
	@Override
	public void transitionTo_Done(MapContext vc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		MapTransform mapTransform = tm.getMapTransform();
		Envelope extent = mapTransform.getExtent();
		double width = 2 * extent.getWidth();
		double height = 2 * extent.getHeight();
		double x = tm.getValues()[0];
		double y = tm.getValues()[1];
		Envelope newExtent = new Envelope(x - width / 2, x + width / 2, y
				- height / 2, y + height / 2);
		mapTransform.setExtent(newExtent);

		transition("init"); //$NON-NLS-1$
	}

	@Override
	public void transitionTo_Cancel(MapContext vc, ToolManager tm)
			throws TransitionException {
	}

	/**
	 * @see org.orbisgis.plugins.core.ui.editors.map.tools.generated.estouro.tools.generated.ZoomOut#drawIn_Standby(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.orbisgis.plugins.core.ui.editors.map.tools.generated.estouro.tools.generated.ZoomOut#drawIn_Done(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Done(Graphics g, MapContext vc, ToolManager tm) {
	}

	/**
	 * @see org.orbisgis.plugins.core.ui.editors.map.tools.generated.estouro.tools.generated.ZoomOut#drawIn_Cancel(java.awt.Graphics)
	 */
	@Override
	public void drawIn_Cancel(Graphics g, MapContext vc, ToolManager tm) {
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.layerCountGreaterThan(vc, 0);
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

	public ImageIcon getImageIcon() {
		return OrbisGISIcon.ZOOMOUT;
	}

	public String getName() {
		return I18N
				.getText("orbisgis.core.ui.editors.map.tool.zoomout_tooltip");
	}
}
