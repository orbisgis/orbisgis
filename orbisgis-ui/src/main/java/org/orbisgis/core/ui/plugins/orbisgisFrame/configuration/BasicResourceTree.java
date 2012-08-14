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
package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.orbisgis.core.ui.components.resourceTree.ResourceTree;

/**
 * Resource tree that doesn't allow drag and drop or popup menu
 */
public class BasicResourceTree extends ResourceTree {
	@Override
	protected boolean doDrop(Transferable trans, Object node) {
		return false;
	}

	@Override
	protected Transferable getDragData(DragGestureEvent dge) {
		return null;
	}

	@Override
	public JPopupMenu getPopup() {
		return null;
	}

	@Override
	protected boolean isDroppable(TreePath path) {
		return false;
	}
}
