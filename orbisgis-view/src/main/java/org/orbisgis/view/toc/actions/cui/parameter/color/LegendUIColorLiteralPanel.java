/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.actions.cui.parameter.color;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.sif.components.ColorPicker;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;

/**
 *
 * @author Maxence Laurent
 */
public abstract class LegendUIColorLiteralPanel extends LegendUIComponent implements LegendUIColorComponent {

	private static final int size = 16;
	private ColorLiteral color;
	private BufferedImage img;
	private JLabel label;

	public LegendUIColorLiteralPanel(String name, LegendUIController controller, LegendUIComponent parent, ColorLiteral c, boolean isNullable) {
		super(name, controller, parent, 0, isNullable);
		this.color = c;

		img = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);

		//label = new JLabel("Color: ");
		label = new JLabel(new ImageIcon(img));

		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				ColorPicker pick = new ColorPicker(color.getColor(null, -1));

				if (UIFactory.showDialog(pick)) {
					Color newC = pick.getColor();
					color.setColor(newC);
					updateButton(newC);
				}

			}
		});

	}

	private void updateButton(Color color) {
		Graphics2D g2 = img.createGraphics();
		g2.setColor(color);
		g2.fillRect(0, 0, size, size);
		label.setIcon(new ImageIcon(img));
	}

	@Override
	public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
	}

	@Override
	protected void mountComponent() {
		editor.add(label);
		updateButton(color.getColor(null, -1));
	}

	@Override
	public ColorParameter getColorParameter() {
		return this.color;
	}

	@Override
	public Class getEditedClass() {
		return ColorLiteral.class;
	}

	@Override
	protected void turnOff() {
		colorChanged(null);
	}

	@Override
	protected void turnOn() {
		colorChanged(this.color);
	}

	protected abstract void colorChanged(ColorLiteral color);
}
