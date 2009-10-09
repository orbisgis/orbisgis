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
package org.orbisgis.core.ui.views.geocatalog.actions.create;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadWriteDriver;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.components.listManager.ListManager;
import org.orbisgis.core.ui.components.listManager.ListManagerListener;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.sif.AbstractUIPanel;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;

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

		}, fieldModel);

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

		HashSet<String> names = new HashSet<String>();
		for (int i = 0; i < fieldModel.names.size(); i++) {
			String name = fieldModel.getName(i);
			if (names.contains(name)) {
				return "Cannot have duplicated field names";
			} else {
				names.add(name);
			}
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
				try {
					metadata.addField(names.get(i), types.get(i));
				} catch (DriverException e) {
					Services.getService(ErrorManager.class).error("The field already exits. ", e);
				}
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
