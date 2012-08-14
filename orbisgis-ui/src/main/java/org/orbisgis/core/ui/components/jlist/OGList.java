/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
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
