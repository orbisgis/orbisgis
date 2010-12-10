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

package org.orbisgis.core.ui.plugins.editors.tableEditor;

import java.awt.Color;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.OutputManager;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class ShowFieldStatisticsPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		IEditor editor = context.getActiveEditor();
		final TableEditableElement element = (TableEditableElement) editor
				.getElement();
		try {
			final DataSourceFactory dsf = ((DataManager) Services
					.getService(DataManager.class)).getDataSourceFactory();

			DataSource ds = element.getDataSource();

			Metadata metadata = ds.getMetadata();

			String fieldName = metadata.getFieldName(getSelectedColumn());
			DataSource dsResult = null;
			String query = null;
			int countSelection = element.getSelection().getSelectedRows().length;
			int[] selected = element.getSelection().getSelectedRows();
			if (selected.length > 0) {
				if (countSelection == ds.getRowCount()) {
					query = getQuery(fieldName, ds).append(" ;").toString();

					dsResult = dsf.getDataSourceFromSQL(query);
				} else {
					GenericObjectDriver subds = getSubData(fieldName, ds,
							selected);

					query = getQuery(fieldName, dsf.getDataSource(subds))
							.append(" ;").toString();

					dsResult = dsf.getDataSourceFromSQL(query);

				}

			} else {
				query = getQuery(fieldName, element.getDataSource()).append(
						" ;").toString();

				dsResult = dsf.getDataSourceFromSQL(query);

			}

			OutputManager om = Services.getService(OutputManager.class);

			dsResult.open();
			Metadata metadataResult = dsResult.getMetadata();

			om.println("Statistics on field : " + fieldName, Color.red);
			for (int i = 0; i < dsResult.getRowCount(); i++) {

				for (int k = 0; k < metadataResult.getFieldCount(); k++) {
					om.println(metadataResult.getFieldName(k) + " : "
							+ dsResult.getFieldValue(i, k).getAsDouble());

				}

			}
			om.println("----------------------------------", Color.red);
			dsResult.close();

		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot access field information", e);
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SemanticException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbContext.getWorkbench()
				.getFrame().getTableEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_SHOWFIELDSTAT_PATH1 },
				Names.POPUP_TABLE_SHOWFIELDSTAT_GROUP, false,
				OrbisGISIcon.TABLE_SHOWFIELDSTAT, wbContext);
	}

	private GenericObjectDriver getSubData(String fieldName, DataSource ds,
			int[] selected) throws DriverException {

		int fieldIndex = ds.getFieldIndexByName(fieldName);
		int fieldType = ds.getFieldType(fieldIndex).getTypeCode();

		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField(fieldName, fieldType);

		GenericObjectDriver driver = new GenericObjectDriver(metadata);

		for (int i = 0; i < selected.length; i++) {

			driver.addValues(new Value[] { ds.getFieldValue(selected[i],
					fieldIndex) });

		}

		return driver;
	}

	private StringBuffer getQuery(String fieldName, DataSource ds) {
		StringBuffer stringBuffer = new StringBuffer("SELECT ");

		stringBuffer.append("COUNT(" + fieldName + ") as count");
		stringBuffer.append(" , ");
		stringBuffer.append("SUM(" + fieldName + ") as sum");
		stringBuffer.append(" , ");
		stringBuffer.append("MIN(" + fieldName + ") as min");
		stringBuffer.append(" , ");
		stringBuffer.append("MAX(" + fieldName + ") as max");
		stringBuffer.append(" , ");
		stringBuffer.append("AVG(" + fieldName + ") as mean");
		stringBuffer.append(" , ");
		stringBuffer.append("StandardDeviation(" + fieldName + ") as std");
		stringBuffer.append(" FROM  " + ds.getName());

		return stringBuffer;
	}

	public boolean isEnabled() {
		boolean isEnabled = false;
		IEditor editor = null;
		if((editor=getPlugInContext().getTableEditor()) != null
				&& getSelectedColumn()!=-1){
			final TableEditableElement element = (TableEditableElement) editor
					.getElement();
			try {
				Metadata metadata = element.getDataSource().getMetadata();
				Type type = metadata.getFieldType(getSelectedColumn());
				int typeCode = type.getTypeCode();
				switch (typeCode) {
				case Type.BYTE:
				case Type.DOUBLE:
				case Type.FLOAT:
				case Type.INT:
				case Type.LONG:
				case Type.SHORT:
					isEnabled = true;
					break;
				default:
					isEnabled = false;
				}

			} catch (DriverException e) {
				Services.getService(ErrorManager.class).error(
						"Cannot access field information", e);
			}
		}
		return isEnabled;
	}
}
