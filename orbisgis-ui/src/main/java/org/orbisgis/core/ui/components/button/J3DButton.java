/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.components.button;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;

import javax.swing.Icon;
import javax.swing.JButton;

public class J3DButton extends JButton {
	String text;
	boolean mouseIn = false;

	public J3DButton(String s) {
		super(s);
		text = s;
		setBorderPainted(false);
		setContentAreaFilled(false);
	}

	public J3DButton(Icon icon) {
		super(icon);
		setBorderPainted(false);
		setContentAreaFilled(false);
	}

	public J3DButton() {
		setBorderPainted(false);
		setContentAreaFilled(false);
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (getModel().isPressed()) {
			g.setColor(Color.pink);
			g2.fillRect(3, 3, getWidth() - 6, getHeight() - 6);
		}
		super.paintComponent(g);

		g2.setColor(Color.white);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(2.0f));
		Arc2D.Double ar1;
		ar1 = new Arc2D.Double(0, 0, 10, 10, 90, 90, Arc2D.OPEN);
		g2.draw(ar1);
		ar1 = new Arc2D.Double(getWidth() - 12, 1, 10, 10, 0, 90, Arc2D.OPEN);
		g2.draw(ar1);
		g2.fillRect(6, 0, getWidth() - 13, 2);
		g2.fillRect(0, 6, 2, getHeight() - 11);

		g2.setColor(Color.gray);
		ar1 = new Arc2D.Double(getWidth() - 12, getHeight() - 11, 10, 10, 270,
				90, Arc2D.OPEN);
		g2.draw(ar1);
		ar1 = new Arc2D.Double(0, getHeight() - 11, 10, 10, 180, 90, Arc2D.OPEN);
		g2.draw(ar1);
		g2.fillRect(getWidth() - 2, 7, 2, getHeight() - 13);
		g2.fillRect(6, getHeight() - 2, getWidth() - 12, 2);

		g2.dispose();
	}

}
