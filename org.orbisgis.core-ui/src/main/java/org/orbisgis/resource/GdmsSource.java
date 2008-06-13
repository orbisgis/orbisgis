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
package org.orbisgis.resource;

import javax.swing.Icon;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.images.IconLoader;

public class GdmsSource extends AbstractResourceType implements IResourceType {

	private Icon icon = null;

	private final Icon raster = IconLoader.getIcon("image.png");

	private final Icon alphanumeric_database = IconLoader.getIcon("db.png");

	private final Icon spatial = IconLoader.getIcon("geofile.png");

	private final Icon alphanumeric_file = IconLoader.getIcon("flatfile.png");

	private DataSourceDefinition def;

	public GdmsSource() {
	}

	private int getSourceType(String name) {
		int type;
		try {
			type = ((DataManager) Services
					.getService("org.orbisgis.DataManager")).getDSF()
					.getSourceManager().getSourceType(name);
		} catch (NoSuchTableException e) {
			type = SourceManager.UNKNOWN;
		} catch (DriverLoadException e) {
			type = SourceManager.UNKNOWN;
		}
		return type;
	}

	public Icon getIcon(INode node, boolean isExpanded) {
		if (icon == null) {
			// Set the right icon
			int sourceType = getSourceType(node.getName());
			if ((sourceType & SourceManager.VECTORIAL) == SourceManager.VECTORIAL) {
				icon = spatial;
			} else if ((sourceType & SourceManager.RASTER) == SourceManager.RASTER) {
				icon = raster;
			} else if ((sourceType & SourceManager.FILE) == SourceManager.FILE) {
				icon = alphanumeric_file;
			} else if ((sourceType & SourceManager.DB) == SourceManager.DB) {
				icon = alphanumeric_database;
			}
		}

		return icon;
	}

	public void removeFromTree(INode toRemove) throws ResourceTypeException {
		SourceManager sourceManager = ((DataManager) Services
				.getService("org.orbisgis.DataManager")).getDSF()
				.getSourceManager();
		Source source = sourceManager.getSource(toRemove.getName());
		if (source != null) {
			def = source.getDataSourceDefinition();
			sourceManager.remove(toRemove.getName());
		}
		super.removeFromTree(toRemove);
	}

	public void setName(INode node, String newName)
			throws ResourceTypeException {
		((DataManager) Services.getService("org.orbisgis.DataManager"))
				.getDSF().getSourceManager().rename(node.getName(), newName);
		super.setName(node, newName);
	}

	public void addToTree(INode parent, INode toAdd)
			throws ResourceTypeException {
		super.addToTree(parent, toAdd);
		if (!((DataManager) Services.getService("org.orbisgis.DataManager"))
				.getDSF().getSourceManager().exists(toAdd.getName())) {
			if (def != null) {
				((DataManager) Services.getService("org.orbisgis.DataManager"))
						.getDSF().getSourceManager().register(toAdd.getName(),
								def);
			} else {
				throw new ResourceTypeException(
						"The resource doesn't have source information");
			}
		}
	}

}
