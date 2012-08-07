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
package org.orbisgis.core.edition;

import java.util.ArrayList;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;

public abstract class AbstractEditableElement implements EditableElement {

	private ArrayList<EditableElementListener> listeners = new ArrayList<EditableElementListener>();

	protected void fireContentChanged() {
		for (EditableElementListener listener : listeners) {
			listener.contentChanged(this);			
		}
		WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
		wbContext.setLastAction("Toc changes");
	}

	protected void fireSave() {
		for (EditableElementListener listener : listeners) {
			listener.saved(this);			
		}
		WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
		wbContext.setLastAction("Toc changes");
	}

	@Override
	public void addElementListener(EditableElementListener listener) {
		listeners.add(listener);
	}

	@Override
	public boolean removeElementListener(EditableElementListener listener) {
		return listeners.remove(listener);
	}

	protected void fireIdChanged() {
		for (EditableElementListener listener : listeners) {
			listener.idChanged(this);
		}
	}

}
