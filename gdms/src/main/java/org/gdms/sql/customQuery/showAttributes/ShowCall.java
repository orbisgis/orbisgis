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
package org.gdms.sql.customQuery.showAttributes;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.progress.IProgressMonitor;

public class ShowCall implements CustomQuery {

	public ObjectDriver evaluate(final DataSourceFactory dsf,
			final DataSource[] tables, final Value[] values, IProgressMonitor pm)
			throws ExecutionException {
		String query = null;
		String tableName = null;

		if (values.length == 1) {
			query = values[0].toString();
		} else if (values.length == 2) {
			query = values[0].toString();
			tableName = values[1].toString();
		} else {
			throw new ExecutionException("Syntax error");
		}

		if (query.substring(0, 6).equalsIgnoreCase("select")) {
			try {
				final DataSource dsResult = dsf.getDataSourceFromSQL(query);
				dsResult.open();
				final Table table = new Table(dsResult);
				final JDialog dlg = new JDialog();

				if (tableName != null) {
					dlg.setTitle("Attributes for " + tableName);
				} else {
					dlg.setTitle("Attributes for " + dsResult.getName());
				}

				final java.net.URL url = this.getClass().getResource(
						"mini_orbisgis.png");
				dlg.setIconImage(new ImageIcon(url).getImage());

				dlg.setModal(true);
				dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dlg.getContentPane().add(table);
				dlg.pack();
				dlg.setVisible(true);

				dsResult.cancel();
			} catch (AlreadyClosedException e) {
				throw new ExecutionException(e);
			} catch (DriverException e) {
				throw new ExecutionException("Problem when accessing data", e);
			} catch (DataSourceCreationException e) {
				throw new ExecutionException("Cannot create the source", e);
			} catch (ParseException e) {
				throw new ExecutionException("Cannot parse the query", e);
			} catch (SemanticException e) {
				throw new ExecutionException("Semantic error in the query", e);
			}
		} else {
			throw new ExecutionException("Show only operates on select");
		}
		return null;
	}

	public String getName() {
		return "SHOW";
	}

	public String getSqlOrder() {
		return "select show('select * from myTable');";
	}

	public String getDescription() {
		return "";
	}

	public Metadata getMetadata(Metadata[] tables) {
		// TODO Auto-generated method stub
		return null;
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		// TODO Auto-generated method stub

	}

	public void validateTables(Metadata[] tables) throws SemanticException {
		// TODO Auto-generated method stub

	}
}