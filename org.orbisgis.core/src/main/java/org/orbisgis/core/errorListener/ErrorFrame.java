package org.orbisgis.core.errorListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import org.orbisgis.core.IWindow;

public class ErrorFrame extends JFrame implements IWindow {

	private ErrorPanel errorPanel;

	public ErrorFrame() {
		this.errorPanel = new ErrorPanel(this);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(errorPanel, BorderLayout.CENTER);
		this.setLocationRelativeTo(null);
	}

	public void showWindow() {
		if (errorPanel.isCollapsed()) {
			packSmall();
		}
		this.setVisible(true);
	}

	public void addError(ErrorMessage errorMessage) {
		if (errorMessage.isError()) {
			this.setTitle("ERROR");
		} else {
			this.setTitle("WARNING");
		}
		errorPanel.addError(errorMessage);
	}

	void packSmall() {
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		int width = frameSize.width;
		int height = frameSize.height;
		if (frameSize.width > dim.width / 2) {
			width = dim.width / 2;
		}
		if (frameSize.height > dim.height / 2) {
			height = dim.height / 2;
		}
		if ((width != frameSize.getWidth())
				|| (height != frameSize.getHeight())) {
			setSize(new Dimension(width, height));
		}
	}
}
