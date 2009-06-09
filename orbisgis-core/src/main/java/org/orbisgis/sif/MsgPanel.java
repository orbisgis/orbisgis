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
package org.orbisgis.sif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MsgPanel extends JPanel {

	private CRFlowLayout layout = new CRFlowLayout();
	private JLabel msg = new JLabel();
	private JLabel title;
	private ImageIcon image;
	private Font infoFont;
	private Font errorFont;

	/**
	 * This is the default constructor
	 */
	public MsgPanel(ImageIcon image) {
		this.image = image;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		JPanel central = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				int width = getWidth();
				int height = getHeight();

				double ax = width / 255.0;
				double lastX = 0;
				for (int i = 0; i < 255; i++) {
					g.setColor(new Color(255 - i / 2, 255 - i / 2, 255));
					g.fillRect((int) lastX, 0, (int) (lastX + ax), height);
					lastX = lastX + ax;
				}
			}

		};
		central.setLayout(layout);
		central.setBackground(Color.white);
		central.setPreferredSize(new Dimension(200, 50));

		title = new JLabel();
		title.setFont(Font.decode("Arial-BOLD-14"));
		central.add(title);
		central.add(new CarriageReturn());
		msg.setHorizontalTextPosition(SwingConstants.CENTER);
		infoFont = Font.decode("Arial-13");
		errorFont = infoFont.deriveFont(Font.BOLD);
		msg.setFont(infoFont);
		central.add(msg);
		layout.setAlignment(CRFlowLayout.LEFT);

		this.setLayout(new BorderLayout());
		JLabel lblIcon = new JLabel(image);
		lblIcon.setForeground(Color.white);
		this.setBackground(Color.white);
		this.setForeground(Color.white);
		this.add(lblIcon, BorderLayout.WEST);
		this.add(central, BorderLayout.CENTER);
	}

	/**
	 * @see org.prueba.IMsgPanel#setText(java.lang.String)
	 */
	public void setText(String text) {
		msg.setText(text);
		msg.setFont(infoFont);
		msg.setForeground(Color.black);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.prueba.IMsgPanel#setTitle(java.lang.String)
	 */
	public void setTitle(String text) {
		title.setText(text);
	}

	public int getImageHeight() {
		return image.getImage().getHeight(null);
	}

	public void setError(String text) {
		msg.setText(text);
		msg.setFont(errorFont);
		msg.setForeground(Color.red);
	}
}
