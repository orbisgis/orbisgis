/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.plugins.toc;

import javax.swing.JOptionPane;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.driver.DriverException;
import org.gdms.sql.engine.ParseException;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.I18N;

public class ExportInDatabaseOperation implements BackgroundJob {

	private DataSourceFactory dsf;
	private DBSource dbSource;
	private String schemaName;
	private String inSourceName;
	private String outSourceName;

	public ExportInDatabaseOperation(DataSourceFactory dsf,
			String inSourceName, String outSourceName, String schemaName,
			DBSource dbSource) {
		this.outSourceName = outSourceName;
		this.inSourceName = inSourceName;
		this.dbSource = dbSource;
		this.schemaName = schemaName;
		this.dsf = dsf;
	}

	@Override
	public String getTaskName() {
		return I18N
				.getString("orbisgis.org.orbisgis.ui.exportInDatabaseOperation.exportingInDB"); //$NON-NLS-1$
	}

	@Override
	public void run(ProgressMonitor pm) {

		try {
			boolean changeName = false;
			String layerName = outSourceName;
			if (dsf.getSourceManager().exists(outSourceName)) {
				layerName = dsf.getSourceManager().getUniqueName(outSourceName);
				changeName = true;
			}

			// Do the migration
                        
                        // 2012-06-07
                        // this is ugly - please, do not migrate this into the new 4.0!
                        // this should die with the orbisgis-ui module
                        
			String load = "CALL EXPORT(" + inSourceName + ", '" + dbSource.getPrefix()
					+ "' ,'" + dbSource.getHost() + "', "
					+ dbSource.getPort() + ",'" + dbSource.getDbName() + "','"
					+ dbSource.getUser() + "','" + dbSource.getPassword()
					+ "','" + schemaName + "','" + outSourceName + "');";

			dsf.executeSQL(load);
                        
                        // register the result
                        dsf.getSourceManager().register(layerName, dbSource);
			
			if (changeName) {
				JOptionPane.showMessageDialog(UIFactory.getMainFrame(),
						ErrorMessages.SourceAlreadyExists);
			}

		} catch (ParseException e) {
			ErrorMessages.error(ErrorMessages.SQLStatementError, e);
		}catch (DriverException e) {
			ErrorMessages.error(ErrorMessages.CannotCreateDataSource, e);
		}

	}

}
