package org.orbisgis.core.ui.components.jlist;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicListUI;

public class OGList extends JList {

	private boolean ignoreSelection = false;
	private MyListUI listUI;

	public OGList() {
		listUI = new MyListUI(this);
		setUI(listUI);

	}

	public void ignoreSelection(boolean b) {
		this.ignoreSelection = b;
	}

	@Override
	public void setSelectionInterval(int anchor, int lead) {
		if (!ignoreSelection) {
			super.setSelectionInterval(anchor, lead);
		}
	}

	/**
	 * Installs listeners to be executed before and after UI listeners. They
	 * enable drag just before UI process to have a drag-enabled feel and
	 * disable later to have a custom d&d management.
	 * 
	 * It also ignores selection during drag
	 * 
	 * @author Fernando Gonzalez Cortes
	 * 
	 */
	private class MyListUI extends BasicListUI {

		private StartDragListener startDragListener;
		private EndDragListener endDragListener;
		private OGList ogList;

		public MyListUI(OGList ogList) {
			this.ogList = ogList;
		}

		@Override
		protected void installListeners() {
			startDragListener = new StartDragListener();
			ogList.addMouseListener(startDragListener);
			ogList.addMouseMotionListener(startDragListener);

			super.installListeners();

			endDragListener = new EndDragListener();
			ogList.addMouseListener(endDragListener);
			ogList.addMouseMotionListener(endDragListener);
		}

		public void dispose() {
			list.removeMouseListener(startDragListener);
			list.removeMouseMotionListener(startDragListener);
			list.removeMouseListener(endDragListener);
			list.removeMouseMotionListener(endDragListener);
		}

		private final class EndDragListener extends MouseAdapter {
			@Override
			public void mousePressed(MouseEvent e) {
				list.setDragEnabled(false);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				list.setDragEnabled(false);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				ogList.ignoreSelection(false);
			}
		}

		private final class StartDragListener extends MouseAdapter {
			@Override
			public void mousePressed(MouseEvent e) {
				list.setDragEnabled(true);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				list.setDragEnabled(true);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				ogList.ignoreSelection(true);
			}
		}

	}

}
