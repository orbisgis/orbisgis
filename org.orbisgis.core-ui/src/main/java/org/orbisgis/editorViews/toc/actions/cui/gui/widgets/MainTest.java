package org.orbisgis.editorViews.toc.actions.cui.gui.widgets;

import java.awt.Dimension;

import javax.swing.JFrame;

public class MainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FlowLayoutPreviewWindow ven = new FlowLayoutPreviewWindow();
		
		JFrame fra = new JFrame();
		fra.add(ven);
		fra.setSize(new Dimension(300, 300));
		
        fra.pack();
        fra.setVisible(true);
        fra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
