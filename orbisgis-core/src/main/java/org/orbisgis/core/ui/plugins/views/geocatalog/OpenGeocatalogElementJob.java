/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY, Adelin PIAU
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */

package org.orbisgis.core.ui.plugins.views.geocatalog;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.progress.IProgressMonitor;

public class OpenGeocatalogElementJob implements BackgroundJob {

	private EditableElement[] elements;

	public OpenGeocatalogElementJob(EditableElement... elements) {
		this.elements = elements;
	}

	public String getTaskName() {
		return "Opening " + elements[0].getId();
	}

	public void run(IProgressMonitor pm) {
		EditorManager em = Services.getService(EditorManager.class);
		for (EditableElement element : elements) {
			em.open(element, pm);
		}
	}

}