package org.orbisgis.core.ui.editors.map.actions.export;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.orbisgis.core.map.export.Scale;

public class ScalePreview extends JComponent {

	private Scale scale;

	public ScalePreview() {
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}

	public Scale getModel() {
		return scale;
	}

	public void setModel(Scale scale) {
		this.scale = scale;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (scale != null) {
			Graphics2D g2 = (Graphics2D) g;
			AffineTransform at = g2.getTransform();
			g.translate(10, 10);
			scale.drawScale(g2, Toolkit.getDefaultToolkit()
					.getScreenResolution());
			g2.setTransform(at);
		}
	}

}
