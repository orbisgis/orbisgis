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
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
package org.gdms.data;

import java.util.ArrayList;

import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.SourceManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.SqlDefinitionType;

public class SQLSourceDefinition extends AbstractDataSourceDefinition implements
		DataSourceDefinition {

	private String sql;

	public SQLSourceDefinition(String sql) {
		this.sql = sql;
	}

	public DataSource createDataSource(String tableName)
			throws DataSourceCreationException {
		try {
			return getDataSourceFactory().executeSQL(tableName, sql);
		} catch (SyntaxException e) {
			throw new DataSourceCreationException(e);
		} catch (DriverLoadException e) {
			throw new DataSourceCreationException(e);
		} catch (NoSuchTableException e) {
			throw new DataSourceCreationException(e);
		} catch (ExecutionException e) {
			throw new DataSourceCreationException(e);
		}
	}

	public void createDataSource(DataSource contents) throws DriverException {
		throw new DriverException("Read only source");
	}

	public DefinitionType getDefinition() {
		SqlDefinitionType ret = new SqlDefinitionType();
		ret.setSql(sql);

		return ret;
	}

	public static DataSourceDefinition createFromXML(
			SqlDefinitionType definitionType) {
		return new SQLSourceDefinition(definitionType.getSql());
	}

	@Override
	protected ReadOnlyDriver getDriverInstance() {
		return null;
	}

	@Override
	public ArrayList<String> getSourceDependencies() {
		ArrayList<String> ret = new ArrayList<String>();
		String[] sources = getDataSourceFactory().getSources(sql);
		for (String source : sources) {
			ret.add(source);
		}

		return ret;
	}

	public ReadOnlyDriver getDriver() {
		return null;
	}

	public String getSQL() {
		return sql;
	}

	public int getType() {
		/*
		 * TODO With new SQL processor we will be able to know the metadata of
		 * the result without the need to execute the query. Then we will have
		 * to change this code to guess if this source is actually a vectorial
		 * source or just alphanumerical
		 */
		return SourceManager.SQL | SourceManager.VECTORIAL;
	}
}
