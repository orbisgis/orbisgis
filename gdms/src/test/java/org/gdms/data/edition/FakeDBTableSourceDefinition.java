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
package org.gdms.data.edition;

import org.gdms.data.AbstractDataSource;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableDataSourceAdapter;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.driver.DBDriver;
import org.gdms.driver.ReadOnlyDriver;
import org.orbisgis.progress.IProgressMonitor;

public class FakeDBTableSourceDefinition extends DBTableSourceDefinition {

	protected Object driver;

	private String prefix;

	public FakeDBTableSourceDefinition(Object driver, String prefix) {
		super(new DBSource(null, 0, null, null, null, null, null));
		this.driver = driver;
		this.prefix = prefix;
	}

	@Override
	public DataSource createDataSource(String tableName, IProgressMonitor pm)
			throws DataSourceCreationException {

		((ReadOnlyDriver) driver).setDataSourceFactory(getDataSourceFactory());

		DBSource dbs = new DBSource(null, 0, null, null, null, null, null);
		AbstractDataSource adapter = new DBTableDataSourceAdapter(
				getDataSourceFactory().getSourceManager().getSource(tableName),
				dbs, (DBDriver) driver);
		adapter.setDataSourceFactory(getDataSourceFactory());

		return adapter;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public boolean equals(DataSourceDefinition obj) {
		return false;
	}

}
