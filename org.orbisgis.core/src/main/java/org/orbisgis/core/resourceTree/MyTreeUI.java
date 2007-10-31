package org.orbisgis.core.resourceTree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.plaf.basic.BasicTreeUI;

public class MyTreeUI extends BasicTreeUI {

	@Override
	protected void installListeners() {
		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				tree.setDragEnabled(true);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				tree.setDragEnabled(true);
			}

		});

		super.installListeners();

		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				tree.setDragEnabled(false);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				tree.setDragEnabled(false);
			}
		});
	}

	public void startDrag() {
		tree.setDragEnabled(false);
	}

}
