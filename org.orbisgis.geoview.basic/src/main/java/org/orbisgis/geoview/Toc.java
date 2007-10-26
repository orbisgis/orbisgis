package org.orbisgis.geoview;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Toc implements IView {

	public Component getComponent() {
		JPanel pnl = new JPanel();
		JButton btn = new JButton("Click the TOC!!!!");
		pnl.setLayout(new BorderLayout());
		pnl.add(btn, BorderLayout.CENTER);
		return pnl;
	}

}
