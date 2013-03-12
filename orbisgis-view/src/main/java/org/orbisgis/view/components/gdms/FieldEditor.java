/*
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
package org.orbisgis.view.components.gdms;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.Input;
import org.orbisgis.sif.multiInputPanel.InputPanel;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.multiInputPanel.InputType;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.TextBoxType;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * A dialog to add or edit a DataSource field.
 */
public class FieldEditor extends JPanel implements UIPanel {

	private JTextField txtName;
	private ConstraintTableModel constraintModel;
	private TypeDefinition[] types;
	private JComboBox cmbTypes;
	private int lastSelectedType;
    private static final I18n I18N = I18nFactory.getI18n(FieldEditor.class);
    private static final String CONSTRAINT_INPUT_NAME = "constraint";

	public FieldEditor(TypeDefinition[] types) {
		this("", null, types);
	}

	public FieldEditor(String name, Type type, TypeDefinition[] types) {
		constraintModel = new ConstraintTableModel(type);
		CRFlowLayout flowLayout = new CRFlowLayout();
		flowLayout.setAlignment(CRFlowLayout.LEFT);
		setLayout(flowLayout);
		add(new JLabel("Name:"));
		txtName = new JTextField(8);
		txtName.setText(name);
		add(txtName);
		add(new JLabel("Type:"));
		this.types = types;
		String[] typeDesc = new String[types.length];
		for (int i = 0; i < typeDesc.length; i++) {
			typeDesc[i] = types[i].getTypeName();
		}
		cmbTypes = new JComboBox(typeDesc);
		int index = getCorrespondingType(type, types);
		cmbTypes.setSelectedIndex(index);
		lastSelectedType = index;
		updateNameIfEmpty();
		cmbTypes.addActionListener(EventHandler.create(ActionListener.class,this,"onTypeSelected"));
		add(cmbTypes);
		add(new CarriageReturn());
        /*
		ListManager constraintList = new ListManager(new ListManagerListener() {

			public void removeElement(int selectedRow) {
				constraintModel.removeElement(selectedRow);
			}

			public void modifyElement(int selectedRow) {
				Constraint selectedConstraint = constraintModel
						.getConstraint(selectedRow);
				UIConstraintPanel constraintPanel = getUIPanel(
						selectedConstraint.getConstraintCode(),
						selectedConstraint.getConstraintHumanValue());
				if (constraintPanel == null) {
					JOptionPane.showMessageDialog(panel,
							"This constraint cannot be modified.");
				} else {
					if (UIFactory.showDialog(constraintPanel)) {
						constraintModel.modify(selectedRow, constraintPanel
								.getConstraint());
					}
				}
			}

			public void addNewElement() {
				int[] codes = getAvailableConstraints();
				if (codes.length == 0) {
					JOptionPane.showMessageDialog(panel, "All available "
							+ "constraints are already added.");
				} else {
					String[] constraints = new String[codes.length];
					String[] ids = new String[codes.length];
					for (int i = 0; i < constraints.length; i++) {
						constraints[i] = ConstraintFactory
								.getConstraintName(codes[i]);
						ids[i] = codes[i] + "";
					}
					ChoosePanel cp = new ChoosePanel("Select the constraint",
							constraints, ids);
					if (UIFactory.showDialog(cp)) {
						int constraintCode = codes[cp.getSelectedIndex()];
						UIConstraintPanel constraintPanel = getUIPanel(
								constraintCode, "");
						if (constraintPanel == null) {
							constraintModel.add(ConstraintFactory
									.createConstraint(constraintCode,
                                            new byte[0]));
						} else {
							if (UIFactory.showDialog(constraintPanel)) {
								constraintModel.add(constraintPanel
										.getConstraint());
							}
						}
					}
				}
			}

			private int[] getAvailableConstraints() {
				int[] typeConstraints = getCurrentTypeDefinition()
						.getValidConstraints();
				int[] filter = new int[typeConstraints.length];
				int filterIndex = 0;
				for (int i = 0; i < typeConstraints.length; i++) {
					if (!constraintModel.contains(typeConstraints[i])) {
						filter[filterIndex] = typeConstraints[i];
						filterIndex++;
					}
				}

				int[] ret = new int[filterIndex];
				System.arraycopy(filter, 0, ret, 0, filterIndex);
				return ret;
			}

		}, constraintModel);

		constraintList.setPreferredSize(new Dimension(250, 100));
		panel.add(constraintList);
		*/
	}

    /**
     * The user change the type of the field.
     */
    public void onTypeSelected() {
        if (cmbTypes.getSelectedIndex() != lastSelectedType) {
            if (constraintModel.getRowCount() > 0) {
                int option = JOptionPane.showConfirmDialog(FieldEditor.this,
                        "Changing the "
                                + "type will remove all constraints"
                                + ". Do you want to continue?",
                        "Continue?", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.NO_OPTION) {
                    cmbTypes.setSelectedIndex(lastSelectedType);
                    return;
                } else {
                    constraintModel.clear();
                }
            }
        }
        lastSelectedType = cmbTypes.getSelectedIndex();
        updateNameIfEmpty();
    }
    @Override
    public URL getIconURL() {
        return OrbisGISIcon.class.getResource("add_field");
    }

	private void updateNameIfEmpty() {
		String fieldName = txtName.getText();
		if ((fieldName.trim().isEmpty()) || fieldName.startsWith("the_")) {
			int typeCode = getType().getTypeCode();
			String suggestion = "the_" + TypeFactory.getTypeName(typeCode);
			if (typeCode == Type.GEOMETRY) {
				suggestion = "the_geom";
			}
			txtName.setText(suggestion);
		}
	}

	private int getCorrespondingType(Type type, TypeDefinition[] types) {
		if (type == null) {
			return 0;
		} else {
			for (int i = 0; i < types.length; i++) {
				TypeDefinition typeDefinition = types[i];
				if (typeDefinition.createType().getTypeCode() == type
						.getTypeCode()) {
					return i;
				}
			}

			return 0;
		}
	}

	public Component getComponent() {
		return this;
	}

	public String getTitle() {
		return "Configure type";
	}

	public String validateInput() {
		if (txtName.getText().trim().length() == 0) {
			return "A field name must be specified";
		}

		return null;
	}

    /**
     * @return The type of the edited field
     */
	public Type getType() {
		Constraint[] constraints = constraintModel.list
				.toArray(new Constraint[0]);
		return getCurrentTypeDefinition().createType(constraints);
	}

	private TypeDefinition getCurrentTypeDefinition() {
		return types[cmbTypes.getSelectedIndex()];
	}

    /**
     * @return Field name
     */
	public String getFieldName() {
		return txtName.getText().trim();
	}

    /**
     * Show the input panel in order to choose the constraint parameter.
     * @param defaultConstraint {@link Constraint} instance
     * @return The new Constraint value, or defaultConstraint if this constraint has no parameters, null if the user cancel.
     */
	private static Constraint showConstraintInputPanel(Constraint defaultConstraint) {
        int constraintCode = defaultConstraint.getConstraintCode();
        String inputTitle = I18N.tr("Enter the {0} constraint parameter",
                ConstraintFactory.getConstraintName(constraintCode));
        MultiInputPanel inputPanel =new MultiInputPanel(inputTitle);
		if (ConstraintFactory.getType(constraintCode) == Constraint.CONSTRAINT_TYPE_FIELD) {
			return defaultConstraint;
		} else if (ConstraintFactory.getType(constraintCode) == Constraint.CONSTRAINT_TYPE_CHOICE) {
			String[] options = ConstraintFactory
					.getChoiceStrings(constraintCode);
            List<String> ids = new LinkedList<String>();
			for(int codeId : ConstraintFactory.getChoiceCodes(constraintCode)) {
                ids.add(Integer.toString(codeId));
            }
            ComboBoxChoice choice = new ComboBoxChoice(ids.toArray(new String[ids.size()]),options);
            inputPanel.addInput(CONSTRAINT_INPUT_NAME,ConstraintFactory.getConstraintName(constraintCode),defaultConstraint.getConstraintValue(),
                    choice);
		} else if (ConstraintFactory.getType(constraintCode) == Constraint.CONSTRAINT_TYPE_STRING_LITERAL ||
                ConstraintFactory.getType(constraintCode) == Constraint.CONSTRAINT_TYPE_INTEGER_LITERAL) {
            inputPanel.addInput(CONSTRAINT_INPUT_NAME,ConstraintFactory.getConstraintName(constraintCode),
                    defaultConstraint.getConstraintValue(),new TextBoxType());
		} else if (constraintCode == Constraint.RASTER_TYPE) {
			throw new UnsupportedOperationException(I18N.tr("Raster not supported"));
		} else {
			throw new RuntimeException("bug. Unsupported constraint type");
		}
        if(UIFactory.showDialog(inputPanel,true,true)) {
            // Update the constraint value
            return ConstraintFactory.createConstraint(defaultConstraint.getType(),inputPanel.getInput(CONSTRAINT_INPUT_NAME));
        } else {
            return null;
        }
	}

	private class ConstraintTableModel extends AbstractTableModel implements
			TableModel {

		private ArrayList<Constraint> list = new ArrayList<Constraint>();

		public ConstraintTableModel(Type type) {
			if (type != null) {
				for (Constraint constraint : type.getConstraints()) {
					list.add(constraint);
				}
			}
		}

		public void clear() {
			int top = list.size() - 1;
			list.clear();
			fireTableRowsDeleted(0, top);
		}

		public boolean contains(int constraintCode) {
			for (Constraint constraint : list) {
				if (constraint.getConstraintCode() == constraintCode) {
					return true;
				}
			}

			return false;
		}

		public void add(Constraint constraint) {
			list.add(constraint);
			fireTableRowsInserted(list.size() - 1, list.size() - 1);
		}

		public void modify(int selectedRow, Constraint constraint) {
			list.set(selectedRow, constraint);
			fireTableRowsUpdated(selectedRow, selectedRow);
		}

		public Constraint getConstraint(int selectedRow) {
			return list.get(selectedRow);
		}

		public int getColumnCount() {
			return 2;
		}

		public void removeElement(int selectedRow) {
			list.remove(selectedRow);
			fireTableRowsDeleted(selectedRow, selectedRow);
		}

		public int getRowCount() {
			return list.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return ConstraintFactory.getConstraintName(list.get(rowIndex)
                        .getConstraintCode());
			} else {
				return list.get(rowIndex).getConstraintHumanValue();
			}
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0) {
				return "Constraint";
			} else {
				return "Value";
			}
		}

	}
}
