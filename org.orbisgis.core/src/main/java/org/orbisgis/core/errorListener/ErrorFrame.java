package org.orbisgis.core.errorListener;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.orbisgis.core.IWindow;

public class ErrorFrame extends JFrame implements IWindow{

	private ErrorPanel errorPanel;

	public ErrorFrame() {
		this.errorPanel = new ErrorPanel(this);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(errorPanel, BorderLayout.CENTER);
		this.setLocationRelativeTo(null);
	}

	public void showWindow() {
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
}
