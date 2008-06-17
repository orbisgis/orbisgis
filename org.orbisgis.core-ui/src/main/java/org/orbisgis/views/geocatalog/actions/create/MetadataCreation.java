package org.orbisgis.views.geocatalog.actions.create;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadWriteDriver;
import org.orbisgis.ui.listManager.ListManager;
import org.orbisgis.ui.listManager.ListManagerListener;
import org.sif.AbstractUIPanel;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class MetadataCreation extends AbstractUIPanel implements UIPanel {

	private JPanel panel;
	private ListManager listManager;
	private FieldModel fieldModel;
	private ReadWriteDriver driver;

	public MetadataCreation(final ReadWriteDriver driver) {
		this.driver = driver;
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		fieldModel = new FieldModel();
		listManager = new ListManager(new ListManagerListener() {

			public void removeElement(int selectedRow) {
				fieldModel.removeElement(selectedRow);
			}

			public void modifyElement(int selectedRow) {
				FieldEditor te = new FieldEditor(fieldModel
						.getName(selectedRow), fieldModel.getType(selectedRow),
						driver.getTypesDefinitions());
				if (UIFactory.showDialog(te)) {
					String fieldName = te.getFieldName();
					Type type = te.getType();
					fieldModel.modify(selectedRow, fieldName, type);
				}
			}

			public void addNewElement() {
				FieldEditor te = new FieldEditor(driver.getTypesDefinitions());
				if (UIFactory.showDialog(te)) {
					String fieldName = te.getFieldName();
					Type type = te.getType();
					fieldModel.add(fieldName, type);
				}
			}

		}

		, fieldModel);

		panel.add(listManager, BorderLayout.CENTER);
	}

	public Component getComponent() {
		return panel;
	}

	public String getTitle() {
		return "Configure fields";
	}

	public String validateInput() {
		if (fieldModel.getRowCount() == 0) {
			return "At least one field have to be created";
		}

		try {
			String driverValidation = driver.validateMetadata(getMetadata());
			if (driverValidation != null) {
				return driverValidation;
			}
		} catch (DriverException e) {
			return e.getMessage();
		}
		return null;
	}

	public Metadata getMetadata() {
		return fieldModel.getMetadata();
	}

	private class FieldModel extends AbstractTableModel implements TableModel {

		private ArrayList<String> names = new ArrayList<String>();
		private ArrayList<Type> types = new ArrayList<Type>();

		public int getColumnCount() {
			return 2;
		}

		public Metadata getMetadata() {
			DefaultMetadata metadata = new DefaultMetadata();
			for (int i = 0; i < names.size(); i++) {
				metadata.addField(names.get(i), types.get(i));
			}

			return metadata;
		}

		public void add(String fieldName, Type type) {
			names.add(fieldName);
			types.add(type);

			fireTableRowsInserted(names.size() - 1, names.size() - 1);
		}

		public void modify(int selectedRow, String fieldName, Type type) {
			names.set(selectedRow, fieldName);
			types.set(selectedRow, type);

			fireTableRowsUpdated(selectedRow, selectedRow);
		}

		public Type getType(int selectedRow) {
			return types.get(selectedRow);
		}

		public String getName(int selectedRow) {
			return names.get(selectedRow);
		}

		public void removeElement(int selectedRow) {
			names.remove(selectedRow);
			types.remove(selectedRow);

			fireTableRowsDeleted(selectedRow, selectedRow);
		}

		public int getRowCount() {
			return names.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return names.get(rowIndex);
			} else {
				int typeCode = types.get(rowIndex).getTypeCode();
				return TypeFactory.getTypeName(typeCode);
			}
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0) {
				return "Name";
			} else {
				return "Type";
			}
		}

	}

}
