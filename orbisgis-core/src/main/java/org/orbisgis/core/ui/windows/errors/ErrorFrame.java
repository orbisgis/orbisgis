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
package org.orbisgis.core.ui.windows.errors;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.JFrame;

import org.orbisgis.core.ui.window.IWindow;

public class ErrorFrame extends JFrame implements IWindow {

	private ErrorPanel errorPanel;

	public ErrorFrame() {
		this.errorPanel = new ErrorPanel(this);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(errorPanel, BorderLayout.CENTER);
		Dimension frameSize = getSize();
		int width = frameSize.width;
		int height = frameSize.height;
		this.setMinimumSize(new Dimension(400, 200));
		this.setMaximumSize(new Dimension(width / 2, height / 2));
		this.setLocationRelativeTo(null);
	}

	public void showWindow() {
		this.pack();
		this.setVisible(true);
	}

	public void addError(ErrorMessage errorMessage) {
		this.pack();
		if (errorMessage.isError()) {
			this.setTitle("ERROR");
		} else {
			this.setTitle("WARNING");
		}
		errorPanel.addError(errorMessage);
	}

	public void load(Map<String, String> properties) {

	}

	public Map<String, String> save() {
		return null;
	}

	public Rectangle getPosition() {
		return this.getBounds();
	}

	public void setPosition(Rectangle position) {
		this.setBounds(position);
	}

	public boolean isOpened() {
		return this.isVisible();
	}

	public void delete() {
		this.setVisible(false);
		this.dispose();
	}

	public static void main(String[] args) {
		ErrorFrame ef = new ErrorFrame();
		ef.setDefaultCloseOperation(EXIT_ON_CLOSE);
		ef.addError(new ErrorMessage("The data have been "
				+ "returned to the database " + "while opening the connection",
				new Exception(), true));
		ef.pack();
		ef.setVisible(true);
	}

}
