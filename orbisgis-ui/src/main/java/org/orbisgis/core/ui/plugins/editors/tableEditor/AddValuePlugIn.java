/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.editors.tableEditor;

import org.gdms.data.DataSource;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.multiInputPanel.CheckBoxChoice;
import org.orbisgis.core.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.core.sif.multiInputPanel.DoubleType;
import org.orbisgis.core.sif.multiInputPanel.IntType;
import org.orbisgis.core.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.core.sif.multiInputPanel.StringType;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class AddValuePlugIn extends AbstractPlugIn {

	private DataSource dataSource;

	public boolean execute(PlugInContext context) throws Exception {
		IEditor editor = context.getActiveEditor();
		final TableEditableElement element = (TableEditableElement) editor
				.getElement();
		dataSource = element.getDataSource();
		MultiInputPanel mip = new MultiInputPanel(I18N
				.getString("orbisgis.ui.popupmenu.table.addvalue.path1"));
		try {
			Metadata metadata = dataSource.getMetadata();
			Type type = metadata.getFieldType(getSelectedColumn());
			int typeCode = type.getTypeCode();
			switch (typeCode) {
			case Type.BOOLEAN:
				mip
						.addInput(
								"value",
								I18N
										.getString("orbisgis.ui.popupmenu.table.addvalue.chooseAValue"),
								new ComboBoxChoice("true", "false"));
				break;
			case Type.DOUBLE:
			case Type.FLOAT:
				mip
						.addInput(
								"value",
								I18N
										.getString("orbisgis.ui.popupmenu.table.addvalue.putAValue"),
								new DoubleType());
				break;
			case Type.INT:
			case Type.LONG:
			case Type.SHORT:
				mip
						.addInput(
								"value",
								I18N
										.getString("orbisgis.ui.popupmenu.table.addvalue.putAValue"),
								"0", new IntType());
				break;
			case Type.STRING:
				mip
						.addInput(
								"value",
								I18N
										.getString("orbisgis.ui.popupmenu.table.addvalue.putAValue"),
								" text ", new StringType(10));
				break;
			default:
				throw new IllegalArgumentException(
						ErrorMessages.UnknownDataType + " : " + typeCode);
			}
			int size = element.getSelection().getSelectedRows().length;
			if (size > 0) {
				mip
						.addInput(
								"check",
								I18N
										.getString("orbisgis.ui.popupmenu.table.addvalue.applyOnSelectedRow"),
								null, new CheckBoxChoice(true));
			}
			if (UIFactory.showDialog(mip)) {
				int[] selectedRow = null;
				if (mip.getInput("check").equalsIgnoreCase("true")) {
					selectedRow = element.getSelection().getSelectedRows();

				} else {
					selectedRow = new int[(int) dataSource.getRowCount()];
				}

				switch (typeCode) {
				case Type.BOOLEAN:
					setValue(selectedRow, Boolean
							.valueOf(mip.getInput("value")),
							getSelectedColumn());
					break;
				case Type.DOUBLE:
				case Type.FLOAT:
					setValue(selectedRow, new Double(mip.getInput("value")),
							getSelectedColumn());
					break;
				case Type.INT:
				case Type.LONG:
				case Type.SHORT:
					setValue(selectedRow, new Integer(mip.getInput("value")),
							getSelectedColumn());
					break;
				case Type.STRING:
					setValue(selectedRow, mip.getInput("value"),
							getSelectedColumn());
					break;
				default:
					throw new IllegalArgumentException(
							ErrorMessages.UnknownDataType + ": " + typeCode);
				}

			}

		} catch (DriverException e) {
			ErrorMessages.error(ErrorMessages.DataError, e);
		} catch (NonEditableDataSourceException e) {
			ErrorMessages.error(ErrorMessages.CannotAddValue, e);
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbContext.getWorkbench()
				.getFrame().getTableEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_ADDVALUE_PATH1 },
				Names.POPUP_TABLE_ADDVALUE_GROUP, false,
				OrbisGISIcon.TABLE_ADDVALUE, wbContext);
	}

	private void setValue(int[] selectedRow, String value,
			int selectedColumnIndex) throws DriverException,
			NonEditableDataSourceException {
		for (int i = 0; i < selectedRow.length; i++) {
			dataSource.setString(i, selectedColumnIndex, value);
		}
		dataSource.commit();
	}

	private void setValue(int[] selectedRow, Integer value,
			int selectedColumnIndex) throws DriverException,
			NonEditableDataSourceException {
		for (int i = 0; i < selectedRow.length; i++) {
			dataSource.setInt(i, selectedColumnIndex, value);
		}
		dataSource.commit();
	}

	private void setValue(int[] selectedRow, double value,
			int selectedColumnIndex) throws DriverException,
			NonEditableDataSourceException {
		for (int i = 0; i < selectedRow.length; i++) {
			dataSource.setDouble(i, selectedColumnIndex, value);
		}
		dataSource.commit();
	}

	private void setValue(int[] selectedRow, boolean value,
			int selectedColumnIndex) throws DriverException,
			NonEditableDataSourceException {
		for (int i = 0; i < selectedRow.length; i++) {
			dataSource.setBoolean(i, selectedColumnIndex, value);
		}
		dataSource.commit();
	}

	public boolean isEnabled() {
		IEditor tableEditor = null;
		if ((tableEditor = getPlugInContext().getTableEditor()) != null) {
			final TableEditableElement element = (TableEditableElement) tableEditor
					.getElement();
			try {
				if (element != null && (getSelectedColumn() != -1)
						&& element.isEditable()) {
					Metadata metadata = element.getDataSource().getMetadata();
					Type type = metadata.getFieldType(getSelectedColumn());
					int typeCode = type.getTypeCode();
					if (typeCode != Type.GEOMETRY) {
						return true;
					}
				}

			} catch (DriverException e) {
				ErrorMessages.error(ErrorMessages.CannotAccessFieldInformation,
						e);
			}
		}
		return false;
	}
}
