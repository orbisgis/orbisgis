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
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Collections;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for constraints in {@link ConstraintTableAction#showConstraintInputPanel(int, String)}
 * @author Nicolas Fortin
 */
public class ConstraintTableModel extends AbstractTableModel {
    private List<Constraint> constraints = new ArrayList<Constraint>();
    private static final int CONSTRAINT_TYPE_COLUMN = 0;
    private static final int CONSTRAINT_VALUE_COLUMN = 1;
    private static final I18n I18N = I18nFactory.getI18n(ConstraintTableModel.class);
    @Override
    public int getRowCount() {
        return constraints.size();
    }

    /**
     * @return The list of model constraints
     */
    public List<Constraint> getConstraints() {
        return Collections.unmodifiableList(constraints);
    }

    /**
     * Remove all constraints
     */
    public void clear() {
        if(!constraints.isEmpty()) {
            int oldSize = getRowCount();
            constraints.clear();
            fireTableRowsDeleted(0,oldSize-1);
        }
    }
    @Override
    public int getColumnCount() {
        return 2;
    }
    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
        fireTableRowsInserted(constraints.size()-1,constraints.size()-1);
    }

    /**
     * @param row Row to remove
     */
    public void removeConstraint(int row) {
        if(row>=0 && row<getRowCount()) {
            constraints.remove(row);
            fireTableRowsDeleted(row,row);
        }
    }

    /**
     * Replace the constraint at the specified row
     * @param row Row id
     * @param constraint New value
     */
    public void setConstraintAt(int row, Constraint constraint) {
        constraints.set(row,constraint);
        fireTableRowsUpdated(row,row);
    }
    /**
     * @param i Row id
     * @return Constraint instance
     */
    public Constraint getConstraintAt(int i) {
        return constraints.get(i);
    }
    @Override
    public String getColumnName(int i) {
        switch (i) {
            case CONSTRAINT_TYPE_COLUMN:
                return I18N.tr("Type");
            case CONSTRAINT_VALUE_COLUMN:
                return I18N.tr("Value");
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int i, int i2) {
        if(i>=0 && i<constraints.size()) {
            Constraint constraint = constraints.get(i);
            switch (i2) {
                case CONSTRAINT_TYPE_COLUMN:
                    return ConstraintFactory.getConstraintName(constraint.getConstraintCode());
                case CONSTRAINT_VALUE_COLUMN:
                    return constraint.getConstraintHumanValue();
                default:
                    return null;
            }
        } else {
            return null;
        }
    }
}
