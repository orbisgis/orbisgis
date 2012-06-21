/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.components.patterns;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

public class PatternConfigurator extends JComponent {

	private int columns;
	private int rows;
	private boolean[][] pattern;
	private ArrayList<PatternChangeListener> listeners = new ArrayList<PatternChangeListener>();

	public PatternConfigurator() {
		this.setBorder(BorderFactory.createLineBorder(Color.black));

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				int mouseX = e.getPoint().x;
				int mouseY = e.getPoint().y;
				int width = getSize().width;
				int height = getSize().height;
				double cellWidth = width / (double) columns;
				double cellHeight = height / (double) rows;
				for (int i = 0; i < columns; i++) {
					int x = (int) (i * cellWidth);
					for (int j = 0; j < rows; j++) {
						int y = (int) (j * cellHeight);
						if ((x < mouseX) && (x + cellWidth > mouseX)
								&& (y < mouseY) && (y + cellHeight > mouseY)) {
							pattern[j][i] = !pattern[j][i];
							fireChangeEvent(j, i);
							repaint();
							return;
						}
					}
				}

			}

			private void fireChangeEvent(int row, int column) {
				for (PatternChangeListener listener : listeners) {
					listener.patternChanged(row, column);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});
	}

	public void setDimensions(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		pattern = new boolean[rows][columns];
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		int width = getSize().width;
		int height = getSize().height;
		double cellWidth = width / (double) columns;
		double cellHeight = height / (double) rows;
		for (int i = 0; i < columns; i++) {
			int x = (int) (i * cellWidth);
			for (int j = 0; j < rows; j++) {
				int y = (int) (j * cellHeight);
				Color color;
				if (pattern[j][i]) {
					color = Color.gray;
				} else {
					color = Color.white;
				}
				g.setColor(color);
				g.fillRect(x, y, (int) cellWidth, (int) cellHeight);
				g.setColor(Color.black);
				g.drawRect(x, y, (int) cellWidth, (int) cellHeight);
			}
		}
	}

	public boolean[] getRowPattern(int i) {
		return pattern[i];
	}

	public void addChangeListener(PatternChangeListener listener) {
		this.listeners.add(listener);
	}

	public boolean removeChangeListener(PatternChangeListener listener) {
		return this.listeners.remove(listener);
	}

	public void setRowPattern(int rowIndex, boolean[] pattern) {
		this.pattern[rowIndex] = pattern;
	}
}
