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

import java.awt.Component;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.db.ConnectionPanel;
import org.orbisgis.utils.I18N;

public class SchemaSelectionPanel extends JPanel implements UIPanel {

	private ConnectionPanel firstPanel;
	private String layerName = "myLayer";
	private JTextField layerText;
	private JComboBox schemasCb;
	private JPanel jPanelSchema;
	private TableDescription[] tables;

	public SchemaSelectionPanel(final ConnectionPanel firstPanel,
			String layerName) {
		this.firstPanel = firstPanel;
		this.layerName = layerName;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public URL getIconURL() {
		return null;
	}

	@Override
	public String getInfoText() {
		return null;
	}

	@Override
	public String getTitle() {
		return I18N.getText("orbisgis.org.orbisgis.db.setTableAndSchema");
	}

	@Override
	public String initialize() {

		jPanelSchema = getJPanelSchema();

		this.add(jPanelSchema);
		return null;

	}

	public JPanel getJPanelSchema() {

		if (jPanelSchema == null) {
			DBDriver dbDriver = firstPanel.getDBDriver();
			Connection connection;
			try {
				connection = firstPanel.getConnection();

				final String[] schemas = dbDriver.getSchemas(connection);
				tables = dbDriver.getTables(connection);

				layerText = new JTextField(20);
				layerText.setText(layerName);

				schemasCb = new JComboBox(schemas);

				jPanelSchema = new JPanel();
				jPanelSchema.setLayout(new CRFlowLayout());
				jPanelSchema.add(new JLabel(I18N
						.getText("orbisgis.org.orbisgis.core.db.tableName")
						+ " :"));
				jPanelSchema.add(layerText);
				jPanelSchema.add(new CarriageReturn());
				jPanelSchema.add(new JLabel(I18N
						.getText("orbisgis.org.orbisgis.core.db.schemaName")
						+ " :"));
				jPanelSchema.add(schemasCb);

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (DriverException e) {
				e.printStackTrace();
			}
		}
		return jPanelSchema;
	}

	public boolean ifTableExists(String table) {
		boolean exists = false;
		for (int i = 0; i < tables.length && !exists; i++) {
			if (tables[i].getName().equals(table)) {
				exists = true;
			}
		}
		return exists;
	}

	@Override
	public String postProcess() {
		return null;
	}

	@Override
	public String validateInput() {
		String validateInput = null;

		if (getSourceName().length() == 0) {
			validateInput = I18N
					.getText("orbisgis.org.orbisgis.core.db.selectTableName");
		} else if (ifTableExists(getSourceName())) {
			validateInput = I18N
					.getText("orbisgis.org.orbisgis.core.db.tableAlreadyExistsInDb");
		}
		return validateInput;
	}

	public String getSourceName() {
		return layerText.getText();
	}

	public String getSelectedSchema() {
		return (String) schemasCb.getSelectedItem();
	}

}