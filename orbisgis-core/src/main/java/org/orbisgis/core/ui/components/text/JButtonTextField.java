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
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Antoine GOURLAY, Adelin PIAU
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.components.text;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JTextField;

import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;

public class JButtonTextField extends JTextField {

	private static int columns = 8;
	private Icon icon;

	/**
	 * Create a jtextfield with an icon inside
	 * 
	 * @param icon
	 * @param columns
	 */
	public JButtonTextField(Icon icon, int columns) {
		super(columns);
		this.icon = icon;
	}

	/**
	 * Create a jtextfield with an icon inside
	 * 
	 * @param columns
	 */
	public JButtonTextField(int columns) {
		super(columns);
		this.icon = IconLoader.getIcon("small_search.png");
	}

	/**
	 * Create a jtextfield with an icon inside
	 */
	public JButtonTextField() {
		super(columns);
		this.icon = IconLoader.getIcon("small_search.png");
	}

	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		this.icon.paintIcon(null, g, 1, 1);
	}

	public Insets getInsets() {
		Insets i = super.getInsets();
		i.left += icon.getIconWidth() + 10;
		return i;
	}

}
