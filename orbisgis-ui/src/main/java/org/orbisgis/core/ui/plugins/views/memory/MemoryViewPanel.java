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
package org.orbisgis.core.ui.plugins.views.memory;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class MemoryViewPanel extends JPanel {
	public MemoryViewPanel() {
		final Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				MemoryViewPanel.this.repaint();
			}
		}, 500, 500);
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Runtime runtime = Runtime.getRuntime();
		final long maxMemory = runtime.maxMemory() / 1024;
		final long allocatedMemory = runtime.totalMemory() / 1024;
		final long freeMemory = runtime.freeMemory() / 1024;
		final long totalFreeMemory = freeMemory + (maxMemory - allocatedMemory);
		final long memoryUsed = maxMemory - totalFreeMemory;

		final int pos = (int) (memoryUsed * getHeight() / maxMemory);
		g.setColor(new Color(32, 128, 32));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(230, 0, 0));
		g.fillRect(0, getHeight() - pos, getWidth(), pos);

		g.setColor(Color.yellow);
		g.drawString(maxMemory / 1024 + "MB", 10, 20);
		g.drawString(memoryUsed / 1024 + "MB", 10, getHeight() - 20);
	}

}