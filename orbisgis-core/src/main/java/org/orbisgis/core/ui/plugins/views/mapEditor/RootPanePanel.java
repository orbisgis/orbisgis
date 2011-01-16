/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.plugins.views.mapEditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

public class RootPanePanel extends JPanel implements RootPaneContainer {

	private JRootPane rootPane;

	public RootPanePanel() {
		this(new BorderLayout());
		rootPane.setOpaque(true);
	}

	public RootPanePanel(LayoutManager layout) {
		rootPane = new JRootPane();
		rootPane.setOpaque(true);
		rootPane.getContentPane().setLayout(layout);
		super.add(rootPane);
	}

	public Container getContentPane() {
		return rootPane.getContentPane();
	}

	public Component getGlassPane() {
		return rootPane.getGlassPane();
	}

	public JLayeredPane getLayeredPane() {
		return rootPane.getLayeredPane();
	}

	public void setContentPane(Container arg0) {
		rootPane.setContentPane(arg0);
	}

	public void setGlassPane(Component arg0) {
		rootPane.setGlassPane(arg0);
	}

	public void setLayeredPane(JLayeredPane arg0) {
		rootPane.setLayeredPane(arg0);
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		if (comp == rootPane) {
			super.addImpl(comp, constraints, index);
		} else {
			getContentPane().add(comp, constraints, index);
		}
	}

	@Override
	public Component add(Component comp, int index) {
		return rootPane.getContentPane().add(comp, index);
	}

	@Override
	public void add(Component comp, Object constraints, int index) {
		rootPane.getContentPane().add(comp, constraints, index);
	}

	@Override
	public void add(Component comp, Object constraints) {
		rootPane.getContentPane().add(comp, constraints);
	}

	@Override
	public Component add(Component comp) {
		return rootPane.getContentPane().add(comp);
	}

	@Override
	public Component add(String name, Component comp) {
		return rootPane.getContentPane().add(name, comp);
	}

	public JRootPane getRootPane() {
		return rootPane;
	}

}
