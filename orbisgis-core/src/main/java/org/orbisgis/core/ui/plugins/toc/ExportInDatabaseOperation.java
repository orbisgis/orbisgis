/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *  
 *  Lead Erwan BOCHER, scientific researcher, 
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer. 
 *  
 *  User support lead : Gwendall Petit, geomatic engineer. 
 * 
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * This file is part of OrbisGIS.
 * 
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 * 
 * or contact directly: 
 * erwan.bocher _at_ ec-nantes.fr 
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.plugins.toc;

import javax.swing.JOptionPane;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.db.DBSource;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.progress.IProgressMonitor;

public class ExportInDatabaseOperation implements BackgroundJob {

	private DataSourceFactory dsf;
	private DBSource dbSource;
	private DBDriver dbDriver;
	private String schemaName;
	private String inSourceName;
	private String outSourceName;

	public ExportInDatabaseOperation(DataSourceFactory dsf,
			String inSourceName, String outSourceName, String schemaName,
			DBSource dbSource, DBDriver dbDriver) {
		this.outSourceName = outSourceName;
		this.inSourceName = inSourceName;
		this.dbSource = dbSource;
		this.dbDriver = dbDriver;
		this.schemaName = schemaName;
		this.dsf = dsf;
	}

	@Override
	public String getTaskName() {
		return "Exporting in a database";
	}

	@Override
	public void run(IProgressMonitor pm) {

		try {

			boolean changeName = false;
			String layerName = outSourceName;
			if (dsf.getSourceManager().exists(outSourceName)) {
				layerName = dsf.getSourceManager().getUniqueName(outSourceName);
				changeName = true;
			}

			// register both sources
			String registerDB = "select register('" + dbDriver.getDriverId()
					+ "' ,'" + dbSource.getHost() + "'," + " '"
					+ dbSource.getPort() + "','" + dbSource.getDbName() + "','"
					+ dbSource.getUser() + "','" + dbSource.getPassword()
					+ "','" + schemaName + "','" + outSourceName + "','"
					+ layerName + "');";

			dsf.executeSQL(registerDB);
			// Do the migration
			String load = "create table " + layerName + " as select * "
					+ "from " + inSourceName + " ;";
			dsf.executeSQL(load, pm);

			if (changeName) {
				JOptionPane.showMessageDialog(null,
						"The table has been registered in the GeoCatalog with the name "
								+ layerName + " because of existing name");
			} else {
				JOptionPane.showMessageDialog(null,
						"The table has been registered in the GeoCatalog with the name "
								+ layerName);
			}

		} catch (ParseException e) {
			Services.getErrorManager().error("Error in the SQL statement.", e);
		} catch (SemanticException e) {
			Services.getErrorManager().error("Error in the SQL statement.", e);
		} catch (DriverException e) {
			Services.getErrorManager()
					.error("Cannot create the datasource.", e);
		} catch (ExecutionException e) {
			Services.getErrorManager()
					.error("Cannot create the datasource.", e);
		}

	}

}
