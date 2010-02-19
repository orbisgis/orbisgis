/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2009 Erwan BOCHER, Pierre-yves FADET
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    Pierre-Yves.Fadet_at_ec-nantes.fr
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.plugins.core.ui;

import java.util.ArrayList;
import java.util.Observer;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.views.ViewDecorator;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;

import com.vividsolutions.jts.util.Assert;

public class UpdateViewPlugInFactory {

	private WorkbenchContext workbenchContext;

	public UpdateViewPlugInFactory(WorkbenchContext workbenchContext,
			PlugIn plugIn) {
		Assert.isTrue(workbenchContext != null);
		workbenchContext.addObserver((Observer) plugIn);
		this.workbenchContext = workbenchContext;
	}

	/****** ViewPlugIn : Open/Close View PlugIn in docking window environment ******/
	public void loadView(String id) {
		if (getViewDecorator(
				workbenchContext.getWorkbench().getFrame().getViews(), id)
				.isOpen()) {
			getViewDecorator(
					workbenchContext.getWorkbench().getFrame().getViews(), id)
					.close();
		} else {
			EditorManager em = Services.getService(EditorManager.class);
			IEditor activeEditor = em.getActiveEditor();
			getViewDecorator(
					workbenchContext.getWorkbench().getFrame().getViews(), id)
					.open(workbenchContext.getWorkbench().getFrame().getRoot(),
							activeEditor, em.getEditorId(activeEditor));
		}
	}

	public boolean viewIsOpen(String id) {
		return getViewDecorator(
				workbenchContext.getWorkbench().getFrame().getViews(), id)
				.isOpen();
	}

	private ViewDecorator getViewDecorator(ArrayList<ViewDecorator> views,
			String id) {
		for (ViewDecorator view : views) {
			if (view.getId().equals(id)) {
				return view;
			}
		}
		return null;
	}
}
