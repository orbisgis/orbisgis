/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
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
package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.SourceManager;

public class RegisterCall implements CustomQuery {

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		try {
			SourceManager sourceManager = dsf.getSourceManager();
			if (values.length == 1) {
				String name = values[0].toString();
				sourceManager.register(name, new ObjectSourceDefinition(
						new ObjectMemoryDriver()));
			} else if (values.length == 2) {
				String file = values[0].toString();
				String name = values[1].toString();
				sourceManager.register(name, new FileSourceDefinition(file));
			} else if ((values.length == 6) || (values.length == 8)) {
				String vendor = values[0].toString();
				String host = values[1].toString();
				String port = values[2].toString();
				String dbName = values[3].toString();
				String user = values[4].toString();
				String password = values[5].toString();
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
						+ "1) call register ('name');\n"
						+ "2) call register ('path_to_file', 'name');\n"
						+ "3) call register ('vendor', 'host', port, "
						+ "dbName, user, password, tableName, name);\n");
			}
		} catch (SourceAlreadyExistsException e) {
			throw new ExecutionException(e);
		}
		return null;
	}

	public String getName() {
		return "register";
	}
}