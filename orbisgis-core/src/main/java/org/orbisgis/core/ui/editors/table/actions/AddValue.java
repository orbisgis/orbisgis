package org.orbisgis.core.ui.editors.table.actions;

import org.gdms.data.DataSource;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.editors.table.action.ITableColumnAction;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.CheckBoxChoice;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.DoubleType;
import org.orbisgis.sif.multiInputPanel.IntType;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.StringType;

public class AddValue implements ITableColumnAction {

	private DataSource dataSource;

	@Override
	public boolean accepts(TableEditableElement element, int selectedColumn) {

		try {


			if ((selectedColumn != -1) && element.isEditable() ){
			Metadata metadata = element.getDataSource().getMetadata();

			Type type = metadata.getFieldType(selectedColumn);

			int typeCode = type.getTypeCode();

			if (typeCode !=Type.GEOMETRY) {

				return true;
			}
			}

		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot access field information", e);
		}

		return false;

	}

	@Override
	public void execute(TableEditableElement element, int selectedColumnIndex) {
		dataSource = element.getDataSource();

		MultiInputPanel mip = new MultiInputPanel("Add value");

		try {

			Metadata metadata = dataSource.getMetadata();

			Type type = metadata.getFieldType(selectedColumnIndex);

			int typeCode = type.getTypeCode();

			switch (typeCode) {
			case Type.BOOLEAN:
				mip.addInput("value", "Choose a value", new ComboBoxChoice(
						"true", "false"));
				break;
			case Type.DOUBLE:
			case Type.FLOAT:
				mip.addInput("value", "Put a value", new DoubleType());
				break;
			case Type.INT:
			case Type.LONG:
			case Type.SHORT:
				mip.addInput("value", "Put a value", "0", new IntType());
				break;
			case Type.STRING:
				mip.addInput("value", "Put a value", " text ", new StringType(
						10));
				break;
			default:
				throw new IllegalArgumentException("Unknown data type: "
						+ typeCode);
			}

			int size = element.getSelection().getSelectedRows().length;

			if (size > 0) {

				mip.addInput("check", "Apply on selected row", null,
						new CheckBoxChoice(true));
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
					setValue(selectedRow, new Boolean(mip.getInput("value")),
							selectedColumnIndex);
					break;
				case Type.DOUBLE:
				case Type.FLOAT:
					setValue(selectedRow, new Double(mip.getInput("value")),
							selectedColumnIndex);
					break;
				case Type.INT:
				case Type.LONG:
				case Type.SHORT:
					setValue(selectedRow, new Integer(mip.getInput("value")),
							selectedColumnIndex);
					break;
				case Type.STRING:
					setValue(selectedRow, mip.getInput("value"),
							selectedColumnIndex);
					break;
				default:
					throw new IllegalArgumentException("Unknown data type: "
							+ typeCode);
				}

			}

		} catch (DriverException e) {
			e.printStackTrace();
		} catch (NonEditableDataSourceException e) {
			e.printStackTrace();
		}

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
}
