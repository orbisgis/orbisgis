/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URL;
import java.util.List;


/**
 * A dialog to add or edit a DataSource field.
 */
public class FieldEditor extends JPanel implements UIPanel, ConstraintHolder {
    private static final int DEFAULT_TABLE_WIDTH = 400;
    private static final int DEFAULT_TABLE_HEIGHT = 100;
    private static final int EMPTY_TABLE_BORDER = 3;
    private static final int DEFAULT_FIELD_NAME_CHARS = 8;
    private JTextField txtName;
	private TypeDefinition[] types;
	private JComboBox cmbTypes;
	private int lastSelectedType;
    private JTable constraintTable;
    private static final I18n I18N = I18nFactory.getI18n(FieldEditor.class);
    private ConstraintTableModel constraintTableModel = new ConstraintTableModel();
    private ConstraintTableAction[] actions;
    /**
     * Constructor
     * @param types Selectable field types
     */
	public FieldEditor(TypeDefinition[] types) {
		this("", null, types);
	}
    private JPanel getConstraintPanel() {
        JPanel constraintPanel = new JPanel(new BorderLayout());
        constraintPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(I18N.tr("Constraints")),
                BorderFactory.createEmptyBorder(EMPTY_TABLE_BORDER, EMPTY_TABLE_BORDER, EMPTY_TABLE_BORDER,
                        EMPTY_TABLE_BORDER)));

        constraintTable = new JTable(constraintTableModel);
        constraintPanel.add(getConstraintsButtonsPanel(),BorderLayout.NORTH);
        constraintPanel.add(new JScrollPane(constraintTable),BorderLayout.CENTER);
        constraintTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        constraintTable.setFillsViewportHeight(true);
        constraintTable.getTableHeader().setReorderingAllowed(false);
        constraintTable.setPreferredScrollableViewportSize(new Dimension(DEFAULT_TABLE_WIDTH, DEFAULT_TABLE_HEIGHT));
        // Listener for button enabled buttons state
        constraintTable.getModel().addTableModelListener(EventHandler.create(TableModelListener.class,this,"checkActions"));
        constraintTable.getSelectionModel().addListSelectionListener(EventHandler.create(ListSelectionListener.class,this,"checkActions"));
        return constraintPanel;
    }
    private JPanel getConstraintsButtonsPanel() {
        JPanel buttonsConstraintPanel = new JPanel(new FlowLayout());
        actions = ConstraintTableAction.getActions();
        for(ConstraintTableAction action : actions) {
            action.setConstraintHolder(this);
            buttonsConstraintPanel.add(new JButton(action));
            action.checkState();
        }
        return buttonsConstraintPanel;
    }

    /**
     * Update Buttons enable status
     */
    public void checkActions() {
        for(ConstraintTableAction action : actions) {
            action.checkState();
        }
    }
    /**
     * Constructor
     * @param name Default field name
     * @param type Default field type
     * @param types Selectable types
     */
	public FieldEditor(String name, Type type, TypeDefinition[] types) {
        super(new BorderLayout());
        JPanel topInputs = new JPanel();
		FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);
        topInputs.setLayout(flowLayout);
		add(new JLabel("Name:"));
		txtName = new JTextField(DEFAULT_FIELD_NAME_CHARS);
		txtName.setText(name);
        topInputs.add(txtName);
        topInputs.add(new JLabel("Type:"));
		this.types = types.clone();
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
        topInputs.add(cmbTypes);
        add(topInputs,BorderLayout.NORTH);
        add(getConstraintPanel(),BorderLayout.CENTER);
	}

    /**
     * The user change the type of the field.
     */
    public void onTypeSelected() {
        if (cmbTypes.getSelectedIndex() != lastSelectedType) {
            if (constraintTableModel.getRowCount() > 0) {
                int option = JOptionPane.showConfirmDialog(FieldEditor.this,
                        I18N.tr("Changing the "
                                + "type will remove all constraints"
                                + ". Do you want to continue?"),
                        I18N.tr("Continue?"), JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.NO_OPTION) {
                    cmbTypes.setSelectedIndex(lastSelectedType);
                    return;
                } else {
                    constraintTableModel.clear();
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

    @Override
	public Component getComponent() {
		return this;
	}

    @Override
	public String getTitle() {
		return "Configure type";
	}

    @Override
	public String validateInput() {
		if (txtName.getText().trim().isEmpty()) {
			return "A field name must be specified";
		}

		return null;
	}

    /**
     * @return The type of the edited field
     */
	public Type getType() {
        List<Constraint> constraints = constraintTableModel.getConstraints();
        return getCurrentTypeDefinition().createType(constraints.toArray(new Constraint[constraints.size()]));
	}

    @Override
	public TypeDefinition getCurrentTypeDefinition() {
		return types[cmbTypes.getSelectedIndex()];
	}

    @Override
    public ConstraintTableModel getConstraintModel() {
        return constraintTableModel;
    }

    @Override
    public JTable getConstraintTable() {
        return constraintTable;
    }

    /**
     * @return Field name
     */
	public String getFieldName() {
		return txtName.getText().trim();
	}

}
