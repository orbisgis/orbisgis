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
package org.gdms.sql.customQuery.showAttributes;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.parser.ParseException;
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

				dsResult.close();
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
		return "Show";
	}

	public String getSqlOrder() {
		return "select Show('select * from myTable');";
	}

	public String getDescription() {
		return "Display the query result in a table";
	}

	public Metadata getMetadata(Metadata[] tables) {
		// TODO Auto-generated method stub
		return null;
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[0];
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.STRING),
				new Arguments(Argument.STRING, Argument.STRING) };
	}

}