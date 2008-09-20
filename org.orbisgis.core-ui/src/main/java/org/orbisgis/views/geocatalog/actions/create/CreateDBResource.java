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
package org.orbisgis.views.geocatalog.actions.create;

import java.util.ArrayList;

import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.ReadWriteDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.resource.Folder;
import org.orbisgis.resource.IResource;
import org.orbisgis.views.geocatalog.Catalog;
import org.orbisgis.views.geocatalog.action.IResourceAction;

public class CreateDBResource implements IResourceAction {

	public boolean accepts(IResource resource) {
		return resource.getResourceType() instanceof Folder;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount <= 1;
	}

	public void execute(Catalog catalog, IResource selectedNode) {
		// Get the non raster writable drivers
		DataManager dm = (DataManager) Services
				.getService(DataManager.class);
		DriverManager driverManager = dm.getSourceManager().getDriverManager();
		String[] driverNames = driverManager.getDriverNames();
		ArrayList<String> filtered = new ArrayList<String>();
		for (String driverName : driverNames) {
			ReadOnlyDriver rod = (ReadOnlyDriver) driverManager
					.getDriver(driverName);
			if ((rod.getType() & SourceManager.RASTER) == 0) {
				if ((rod.getType() & SourceManager.DB) == SourceManager.DB) {
					if (rod instanceof ReadWriteDriver) {
						filtered.add(driverName);
					}
				}
			}
		}
		CreateFileResource.createSource(dm, driverManager, filtered);
	}
}
