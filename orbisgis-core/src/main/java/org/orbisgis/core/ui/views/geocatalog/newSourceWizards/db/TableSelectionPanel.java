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
package org.orbisgis.core.ui.views.geocatalog.newSourceWizards.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.db.DBSource;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.orbisgis.sif.multiInputPanel.ListChoice;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;

public class TableSelectionPanel extends MultiInputPanel {
	private static final String TABLESNAMES = "tablesnames";
	private ConnectionPanel firstPanel;

	public TableSelectionPanel(final ConnectionPanel firstPanel) {
		super("Select table(s) name(s)...");
		addInput(TABLESNAMES, null, null, new ListChoice(new String[0]));
		addValidationExpression("strlength(" + TABLESNAMES + ") is not null",
				"Select at least one table");
		this.firstPanel = firstPanel;
	}

	public String initialize() {

		try {
			DBDriver dbDriver = firstPanel.getDBDriver();
			final Connection connection = firstPanel.getConnection();
			final TableDescription[] tableDescriptions = dbDriver
					.getTables(connection);

			final StringBuilder sbAllTablesNames = new StringBuilder();

			for (int i = 0; i < tableDescriptions.length; i++) {
				final String tblName = tableDescriptions[i].getName();
				sbAllTablesNames.append(tblName);
				if (i + 1 != tableDescriptions.length) {
					sbAllTablesNames.append(ListChoice.SEPARATOR);
				}
			}
			setValue(TABLESNAMES, sbAllTablesNames.toString());
			connection.close();
			return null;
		} catch (SQLException e) {
			return e.getMessage();
		} catch (DriverException e) {
			return e.getMessage();
		}
	}

	public DBSource[] getSelectedDBSources() {
		final String[] tablesNames = getInput(TABLESNAMES).split(
				ListChoice.SEPARATOR);
		final DBSource[] dbSources = new DBSource[tablesNames.length];
		for (int i = 0; i < tablesNames.length; i++) {
			dbSources[i] = firstPanel.getDBSource(tablesNames[i]);
		}
		return dbSources;
	}
}