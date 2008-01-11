/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geocatalog.resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.core.OrbisgisCore;

public class AbstractGdmsSource extends AbstractResourceType implements
		IResourceType {

	private Icon icon = null;

	private final Icon memory = new ImageIcon(AbstractGdmsSource.class
			.getResource("drive.png"));

	private final Icon asc_file = new ImageIcon(AbstractGdmsSource.class
			.getResource("images.png"));

	private final Icon tif_file = new ImageIcon(AbstractGdmsSource.class
			.getResource("images.png"));

	private final Icon h2_db = new ImageIcon(AbstractGdmsSource.class
			.getResource("geodatabase.png"));

	private final Icon postgis_db = new ImageIcon(AbstractGdmsSource.class
			.getResource("geodatabase.png"));

	private final Icon database = new ImageIcon(AbstractGdmsSource.class
			.getResource("database.png"));

	private final Icon shp_file = new ImageIcon(AbstractGdmsSource.class
			.getResource("geofile.png"));

	private final Icon csv_file = new ImageIcon(AbstractGdmsSource.class
			.getResource("page.png"));

	private final Icon dbf_file = new ImageIcon(AbstractGdmsSource.class
			.getResource("page.png"));

	private DataSourceDefinition def;

	public AbstractGdmsSource() {
	}

	private int getSourceType(String name) {
		int type;
		try {
			type = OrbisgisCore.getDSF().getSourceManager().getSourceType(name);
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
			if ((sourceType & SourceManager.DB) == SourceManager.DB) {
				icon = database;
			}
			if ((sourceType & SourceManager.FILE) == SourceManager.FILE) {
				icon = database;
			}
			if ((sourceType & SourceManager.RASTER) == SourceManager.RASTER) {
				icon = database;
			}
			if ((sourceType & SourceManager.MEMORY) == SourceManager.MEMORY) {
				icon = memory;
			}

			switch (sourceType) {
			case SourceManager.ASC_GRID:
				icon = asc_file;
				break;
			case SourceManager.H2:
				icon = h2_db;
				break;
			case SourceManager.CSV:
				icon = csv_file;
				break;
			case SourceManager.DBF:
				icon = dbf_file;
				break;
			case SourceManager.SHP:
				icon = shp_file;
				break;
			case SourceManager.TFW:
				icon = tif_file;
				break;
			case SourceManager.POSTGRESQL:
				icon = postgis_db;
				break;
			}
		}

		return icon;
	}

	public void removeFromTree(INode toRemove) throws ResourceTypeException {
		SourceManager sourceManager = OrbisgisCore.getDSF().getSourceManager();
		Source source = sourceManager.getSource(toRemove.getName());
		if (source != null) {
			def = source.getDataSourceDefinition();
			sourceManager.remove(toRemove.getName());
		}
		super.removeFromTree(toRemove);
	}

	public void setName(INode node, String newName)
			throws ResourceTypeException {
		OrbisgisCore.getDSF().getSourceManager()
				.rename(node.getName(), newName);
		super.setName(node, newName);
	}

	public void addToTree(INode parent, INode toAdd)
			throws ResourceTypeException {
		super.addToTree(parent, toAdd);
		if (!OrbisgisCore.getDSF().getSourceManager().exists(toAdd.getName())) {
			if (def != null) {
				OrbisgisCore.getDSF().getSourceManager().register(
						toAdd.getName(), def);
			} else {
				throw new ResourceTypeException(
						"The resource doesn't have source information");
			}
		}
	}

}
