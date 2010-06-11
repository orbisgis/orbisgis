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

package org.orbisgis.core.ui.plugins.views.information;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;

public class Table extends JPanel implements InformationManager {

	private JTable tbl;
	private DataSourceTableModel dataSourceTableModel = null;

	public Table() {
		this.setLayout(new BorderLayout());
		tbl = new JTable();
		tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.add(new JScrollPane(tbl));
	}

	@Override
	public void setContents(DataSource ds) throws DriverException {
		ds.open();
		if (dataSourceTableModel != null) {
			dataSourceTableModel.getDataSource().close();
		}
		dataSourceTableModel = new DataSourceTableModel(ds);
		tbl.setModel(dataSourceTableModel);
		WorkbenchContext wbContext = Services
				.getService(WorkbenchContext.class);
		wbContext.getWorkbench().getFrame().showView("Information");
	}

	public DataSource getContents() {
		if (dataSourceTableModel == null) {
			return null;
		} else {
			return dataSourceTableModel.getDataSource();
		}
	}

}
