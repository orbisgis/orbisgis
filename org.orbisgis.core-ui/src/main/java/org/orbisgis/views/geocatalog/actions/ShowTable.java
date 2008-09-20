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
package org.orbisgis.views.geocatalog.actions;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.SourceManager;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.resource.GdmsSource;
import org.orbisgis.resource.IResource;
import org.orbisgis.resource.IResourceType;
import org.orbisgis.view.ViewManager;
import org.orbisgis.views.geocatalog.Catalog;
import org.orbisgis.views.geocatalog.action.IResourceAction;
import org.orbisgis.views.table.Table;

public class ShowTable implements IResourceAction {

	public boolean accepts(IResource selectedNode) {
		IResourceType resourceType = selectedNode.getResourceType();
		if (resourceType instanceof GdmsSource) {
			int sourceType;
			try {
				sourceType = ((DataManager) Services
						.getService(DataManager.class)).getDSF()
						.getSourceManager().getSourceType(
								selectedNode.getName());
			} catch (NoSuchTableException e) {
				throw new RuntimeException("bug!");
			}
			return (sourceType & SourceManager.RASTER) == 0;
		}

		return false;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		try {
			DataManager dataManager = (DataManager) Services
					.getService(DataManager.class);
			DataSourceFactory dsf = dataManager.getDSF();
			DataSource ds = dsf.getDataSource(currentNode.getName());

			ViewManager viewManager = (ViewManager) Services
					.getService(ViewManager.class);
			Table table = (Table) viewManager
					.getView("org.orbisgis.views.Table");
			table.setContents(ds);
		} catch (DriverLoadException e) {
			throw new RuntimeException("bug", e);
		} catch (NoSuchTableException e) {
			throw new RuntimeException("bug", e);
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the contents of the source", e);
		} catch (DataSourceCreationException e) {
			Services.getErrorManager().error(
					"Cannot access the source", e);
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount > 0;
	}

}
