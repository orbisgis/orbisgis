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
package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.ObjectDriver;
import org.gdms.source.SourceManager;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

public class RegisterCall implements CustomQuery {

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			final SourceManager sourceManager = dsf.getSourceManager();
			if (values.length == 1) {
				String name = values[0].toString();
				sourceManager.register(name, new FileSourceDefinition(dsf
						.getResultFile()));
			} else if (values.length == 2) {
				final String file = values[0].toString();
				final String name = values[1].toString();
				sourceManager.register(name, new FileSourceDefinition(file));
			} else if ((values.length == 6) || (values.length == 8)) {
				final String vendor = values[0].toString();
				final String host = values[1].toString();
				final String port = values[2].toString();
				final String dbName = values[3].toString();
				final String user = values[4].toString();
				final String password = values[5].toString();
				String tableName = null;
				String name = null;
				if (values.length == 8) {
					tableName = values[6].toString();
					name = values[7].toString();
				}

				if (tableName == null) {
					throw new ExecutionException("Not implemented yet");
				}
				sourceManager.register(name, new DBTableSourceDefinition(
						new DBSource(host, Integer.parseInt(port), dbName,
								user, password, tableName, "jdbc:" + vendor)));
			} else {
				throw new ExecutionException("Usage: \n"
						+ "1) select register ('name');\n"
						+ "2) select register ('path_to_file', 'name');\n"
						+ "3) select register ('vendor', 'host', port, "
						+ "dbName, user, password, tableName, dsEntryName);\n");
			}
		} catch (SourceAlreadyExistsException e) {
			throw new ExecutionException(e);
		}
		return null;
	}

	public String getName() {
		return "Register";
	}

	public String getDescription() {
		return "Register a new file or a database.";
	}

	public String getSqlOrder() {
		return "select register('C:\\Temp\\myShape.shp','myTable');";
	}

	public Metadata getMetadata(Metadata[] tables) {
		return null;
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] {};
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] {
				new Arguments(Argument.STRING),
				new Arguments(Argument.STRING, Argument.STRING),
				new Arguments(Argument.STRING, Argument.STRING,
						Argument.STRING, Argument.STRING, Argument.STRING,
						Argument.STRING, Argument.STRING, Argument.STRING) };
	}
}