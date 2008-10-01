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
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.views.geocatalog.newResourceWizards.db;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.resource.GdmsSource;
import org.orbisgis.resource.IResource;
import org.orbisgis.resource.ResourceFactory;
import org.orbisgis.views.geocatalog.newResourceWizard.INewResource;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class NewDB implements INewResource {

	public String getName() {
		return "Database source";
	}

	public IResource[] getResources() {
		final List<IResource> resources = new ArrayList<IResource>();
		final ConnectionPanel firstPanel = new ConnectionPanel();
		final TableSelectionPanel secondPanel = new TableSelectionPanel(firstPanel);

		if (UIFactory.showDialog(new UIPanel[] { firstPanel, secondPanel })) {
			for (DBSource dBSource : secondPanel.getSelectedDBSources()) {
				DataManager dm = (DataManager) Services
						.getService(DataManager.class);
				final String name = dm.registerWithUniqueName(dBSource
						.getTableName().toString(),
						new DBTableSourceDefinition(dBSource));
				resources.add(ResourceFactory.createResource(name,
						new GdmsSource()));
			}
		}
		return resources.toArray(new IResource[0]);
	}
}