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
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.ColorPicker;

/**
 *
 * @author maxence
 */
public class EditColorLiteralPanel extends JPanel {

	private static final int size = 16;
	private ColorLiteral color;
	private BufferedImage img;
	private JLabel label;

	public EditColorLiteralPanel(ColorLiteral c) {
		super(new FlowLayout());
		this.color = c;
		img = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);


		label = new JLabel("Color: ");
		label = new JLabel(new ImageIcon(img));
		this.add(label);
		updateButton(c.getColor(null));

		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				ColorPicker pick = new ColorPicker();

				if (UIFactory.showDialog(pick)) {
					Color newC = pick.getColor();
					color.setColor(newC);
					updateButton(newC);
				}

			}
		});
	}

	public ColorParameter getColorParameter(){
		return this.color;
	}

	private void updateButton(Color color) {
		Graphics2D g2 = img.createGraphics();
		g2.setColor(color);
		g2.fillRect(0, 0, size, size);
		label.setIcon(new ImageIcon(img));
	}
}
