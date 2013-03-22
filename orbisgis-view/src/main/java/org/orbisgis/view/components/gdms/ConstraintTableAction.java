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
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.ListChoice;
import org.orbisgis.sif.multiInputPanel.MIPValidationLong;
import org.orbisgis.sif.multiInputPanel.MIPValidationNumeric;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.TextBoxType;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import javax.swing.AbstractAction;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Controls associated with the constraint table.
 * @author Nicolas Fortin
 */
public abstract class ConstraintTableAction extends AbstractAction {
    protected ConstraintHolder constraintHolder;
    private static final I18n I18N = I18nFactory.getI18n(ConstraintTableAction.class);
    private static final String CONSTRAINT_INPUT_NAME = "constraint";

    /**
     * @return Actions associated with the table.
     */
    public static ConstraintTableAction[] getActions() {
        return new ConstraintTableAction[] {new AddConstraint(),new RemoveConstraint(),new EditConstraint()};
    }
    /**
     * Constructor
     * @param s Action name
     */
    protected ConstraintTableAction(String s) {
        super(s);
    }

    /**
     * Check the Action enabled state
     */
    public abstract void checkState();

    /**
     * Show the input panel in order to choose the constraint parameter.
     * @param constraintCode {@see ConstraintFactory}
     * @param defaultValue Default constraint value, can be empty
     * @return The new {@link org.gdms.data.types.Constraint} value, null if the user cancel.
     */
    private static Constraint showConstraintInputPanel(int constraintCode,String defaultValue) {
        String inputTitle = I18N.tr("Enter the {0} constraint parameter",
                ConstraintFactory.getConstraintName(constraintCode));
        MultiInputPanel inputPanel =new MultiInputPanel(inputTitle);
        int contraintParameterType = ConstraintFactory.getType(constraintCode);

        // Create Inputs
        if (contraintParameterType == Constraint.CONSTRAINT_TYPE_FIELD) {
            return ConstraintFactory.createConstraint(constraintCode);
        } else if (contraintParameterType == Constraint.CONSTRAINT_TYPE_CHOICE) {
            String[] options = ConstraintFactory
                    .getChoiceStrings(constraintCode);
            List<String> ids = new LinkedList<String>();
            for(int codeId : ConstraintFactory.getChoiceCodes(constraintCode)) {
                ids.add(Integer.toString(codeId));
            }
            ComboBoxChoice choice = new ComboBoxChoice(ids.toArray(new String[ids.size()]),options);
            inputPanel.addInput(CONSTRAINT_INPUT_NAME,ConstraintFactory.getConstraintName(constraintCode),defaultValue,
                    choice);
        } else if (contraintParameterType == Constraint.CONSTRAINT_TYPE_STRING_LITERAL ||
                contraintParameterType == Constraint.CONSTRAINT_TYPE_INTEGER_LITERAL) {
            inputPanel.addInput(CONSTRAINT_INPUT_NAME,ConstraintFactory.getConstraintName(constraintCode),
                    defaultValue,new TextBoxType());
        } else if (constraintCode == Constraint.RASTER_TYPE) {
            throw new UnsupportedOperationException(I18N.tr("Raster not supported"));
        } else {
            throw new RuntimeException("bug. Unsupported constraint type");
        }

        // Create input validations
        if(contraintParameterType == Constraint.CONSTRAINT_TYPE_INTEGER_LITERAL) {
            // The field must be number
            inputPanel.addValidation(new MIPValidationLong(CONSTRAINT_INPUT_NAME,ConstraintFactory.getConstraintName(constraintCode)));
        }

        // Get result
        if(UIFactory.showDialog(inputPanel, true, true)) {
            // Update the constraint value
            if (contraintParameterType == Constraint.CONSTRAINT_TYPE_INTEGER_LITERAL) {
                return ConstraintFactory.createConstraint(constraintCode,Integer.valueOf(inputPanel.getInput(CONSTRAINT_INPUT_NAME)));
            } else {
                return ConstraintFactory.createConstraint(constraintCode,inputPanel.getInput(CONSTRAINT_INPUT_NAME));
            }
        } else {
            return null;
        }
    }
    /**
     * @return Valid and not already set constraints for this field
     */
    protected List<Integer> getAvailableConstraints() {
        // Get valid constraints
        int[] typeConstraints = constraintHolder.getCurrentTypeDefinition()
                .getValidConstraints();
        List<Integer> availableConstraints = new LinkedList<Integer>();
        // Make table constraints type code set
        Set<Integer> shownConstraints = new HashSet<Integer>();
        for(Constraint constraint : constraintHolder.getConstraintModel().getConstraints()) {
            shownConstraints.add(constraint.getConstraintCode());
        }
        // Add only not already set constrains
        for (int typeConstraint : typeConstraints) {
            if (!shownConstraints.contains(typeConstraint)) {
                availableConstraints.add(typeConstraint);
            }
        }
        return availableConstraints;
    }
    protected Constraint showConstraintSelectionPanel(List<Integer> availableConstraints,int defaultConstraint) {
        if(!availableConstraints.isEmpty()) {
            ConstraintItem[] listItems = new ConstraintItem[availableConstraints.size()];
            int index=0;
            int defaultRow=-1;
            for(int constraint: availableConstraints) {
                listItems[index] = new ConstraintItem(constraint, ConstraintFactory.getConstraintName(constraint));
                if(constraint==defaultConstraint) {
                    defaultRow = index;
                }
                index++;
            }
            // Sort items
            Arrays.sort(listItems);
            // Create control
            ListChoice choice = new ListChoice(listItems);
            if(defaultRow!=-1) {
                choice.setSelectedIndex(defaultRow);
            }
            choice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            MultiInputPanel panel = new MultiInputPanel(I18N.tr("Select the constraint"));
            panel.addInput("list","",null,choice);
            if(UIFactory.showDialog(panel, true, true) && choice.getSelectedValue() instanceof ContainerItem) {
                // define constraint parameter
                return showConstraintInputPanel(((ConstraintItem) choice.getSelectedValue()).getKey(),"");
            }
        }
        return null;
    }
    /**
     * Link this action with the constraint environment
     * @param constraintHolder Set the main component
     */
    public void setConstraintHolder(ConstraintHolder constraintHolder) {
        this.constraintHolder = constraintHolder;
    }

    private static class AddConstraint extends ConstraintTableAction {
        private AddConstraint() {
            super(I18N.tr("Add"));
        }

        @Override
        public void checkState() {
            setEnabled(!getAvailableConstraints().isEmpty());
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            // Show available constraints
            List<Integer> availableConstraints =  getAvailableConstraints();
            Constraint selectedConstraint = showConstraintSelectionPanel(availableConstraints,-1);
            if(selectedConstraint!=null) {
                constraintHolder.getConstraintModel().addConstraint(selectedConstraint);
            }
        }
    }
    private static class RemoveConstraint extends ConstraintTableAction {
        private RemoveConstraint() {
            super(I18N.tr("Delete"));
        }

        @Override
        public void checkState() {
            setEnabled(constraintHolder.getConstraintTable().getSelectedRow()!=-1);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int selectedRow = constraintHolder.getConstraintTable().getSelectedRow();
            if(selectedRow>=0) {
                constraintHolder.getConstraintModel().removeConstraint(selectedRow);
            }
        }
    }

    private static class EditConstraint extends ConstraintTableAction {
        private EditConstraint() {
            super(I18N.tr("Edit"));
        }

        @Override
        public void checkState() {
            setEnabled(constraintHolder.getConstraintTable().getSelectedRow()!=-1);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            // Show available constraints
            int selectedRow = constraintHolder.getConstraintTable().getSelectedRow();
            List<Integer> availableConstraints =  getAvailableConstraints();
            int currentConstraintCode = constraintHolder.getConstraintModel().getConstraintAt(selectedRow).getConstraintCode();
            availableConstraints.add(currentConstraintCode);
            Constraint selectedConstraint = showConstraintSelectionPanel(availableConstraints,currentConstraintCode);
            if(selectedConstraint!=null) {
                constraintHolder.getConstraintModel().setConstraintAt(selectedRow,selectedConstraint);
            }
        }
    }
}