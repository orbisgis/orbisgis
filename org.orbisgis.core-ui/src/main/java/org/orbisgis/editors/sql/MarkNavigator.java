package org.orbisgis.editors.sql;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JComponent;

public class MarkNavigator extends JComponent {

	private ArrayList<Long> errorLines = new ArrayList<Long>();
	private long totalLines = 1;

	public void setTotalLines(long totalLines) {
		this.totalLines = totalLines;
		repaint();
	}

	public void setErrorLines(ArrayList<Long> errorLines) {
		this.errorLines = errorLines;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		int markHeight = 4;
		int height = getHeight() + markHeight;

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		for (Long errorLine : errorLines) {
			double linePos = errorLine / (double) totalLines * height;
			int intLinePos = (int) linePos;
			g.setColor(Color.red);
			g.fillRect(0, intLinePos, getWidth(), markHeight);
		}
	}
}
