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
package org.orbisgis.views.geocatalog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.Timer;

import org.orbisgis.Services;
import org.orbisgis.errorManager.ErrorListener;
import org.orbisgis.window.EPWindowHelper;
import org.orbisgis.window.IWindow;

public class ErrorButton extends JButton {

	private Color original;
	private Timer timer;

	private int blinks;

	public ErrorButton(String text) {
		super(text);
		Services.getErrorManager().addErrorListener(new ErrorListener() {

			public void warning(String userMsg, Throwable e) {
				startBlinking();
			}

			public void error(String userMsg, Throwable exception) {
				startBlinking();
			}

		});
		this.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				IWindow[] wnds = EPWindowHelper
						.getWindows("org.orbisgis.core.ErrorWindow");
				IWindow wnd;
				if (wnds.length == 0) {
					wnd = EPWindowHelper
							.createWindow("org.orbisgis.core.ErrorWindow");
				} else {
					wnd = wnds[0];
				}
				wnd.showWindow();
				stopBlinking();
			}

		});

	}

	private void startBlinking() {
		if ((timer != null) && (timer.isRunning())) {
			timer.stop();
		}
		timer = new Timer(500, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (ErrorButton.this.getBackground() == Color.red) {
					ErrorButton.this.setBackground(original);
				} else {
					original = ErrorButton.this.getBackground();
					ErrorButton.this.setBackground(Color.red);
				}
				blinks--;
				if (blinks == 0) {
					stopBlinking();
				}
			}

		});
		timer.start();
		blinks = 20;
	}

	private void stopBlinking() {
		if ((timer != null) && timer.isRunning()) {
			timer.stop();
			if (original != null) {
				setBackground(original);
			}
		}
	}
}
