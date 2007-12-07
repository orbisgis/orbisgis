package org.orbisgis.core.errorListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFrame;

import org.orbisgis.core.windows.IWindow;
import org.orbisgis.core.windows.PersistenceContext;

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
		this.setMaximumSize(new Dimension(width/2, height/2));
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

	public void load(PersistenceContext pc) {

	}

	public void save(PersistenceContext pc) {

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
