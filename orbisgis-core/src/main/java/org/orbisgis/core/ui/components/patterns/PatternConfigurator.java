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
