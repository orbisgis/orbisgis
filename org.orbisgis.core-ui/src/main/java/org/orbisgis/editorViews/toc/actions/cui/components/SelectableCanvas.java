package org.orbisgis.editorViews.toc.actions.cui.components;

import java.awt.Color;
import java.awt.Graphics;

public class SelectableCanvas extends Canvas {

	private boolean selected = false;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (selected) {
			g.setColor(Color.black);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

}
