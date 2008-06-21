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
/*
 * IPanelUI.java
 *
 * Created on 22 de febrero de 2008, 8:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.Component;

import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.legend.Legend;

/**
 * 
 * @author David Ortega
 */
public interface ILegendPanelUI {
	/**
	 * This function will return the Component of the object (normally a JPanel).
	 * @return Component
	 */
	public Component getComponent();

	/**
	 * It will return the Legend created by all the variables in the panel.
	 * @return Legend
	 */
	public Legend getLegend();

	/**
	 * It receives the LegendListDecorator in order to work as a listener.
	 * Any change that you make in the panel (press any button, etc..) will
	 * update the Legend into dec in order to make effective the changes in the 
	 * final Legend composite.
	 * @param dec
	 */
	
	public void setDecoratorListener(LegendListDecorator dec);
}
