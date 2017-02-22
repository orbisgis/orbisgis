/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.view.toc.actions.cui.legend.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * @author Alexis Guéganno
 */
public abstract class KeyEditorMappedLegend<K,U extends LineParameters> extends AbstractCellEditor implements TableCellEditor, ActionListener {
    protected static final String EDIT = "edit";
    private JTextField field;
    private K val;
    private MappedLegend<K, U> rl;

    /**
     * Build a cell editor dedicated to the management of keys in a recoded legend.
     */
    public KeyEditorMappedLegend(){
        field = new JTextField(25);
        field.setActionCommand(EDIT);
        field.addActionListener(this);
    }

    @Override
    public boolean isCellEditable(EventObject event){
        if(event instanceof MouseEvent){
            MouseEvent me = (MouseEvent) event;
            return me.getClickCount()>=2;
        }
        return false;
    }

    @Override
    public Object getCellEditorValue() {
        return val;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(EDIT)){
            U lp = rl.get(val);
            K k = getNotUsedKey(val);
            rl.remove(val);
            rl.put(k, lp);
            fireEditingStopped();
        }
    }

    /**
     * Gets a key that is not already used in the inner map.
     * @param previous the previously used key. Will be used if the user input is invalid.
     * @return A key that is not already used in the inner map.
     */
    protected abstract K getNotUsedKey(K previous);

    /**
     * Sets the associated Legend.
     * @param rl The legend we want to associate.
     */
    protected void setLegend(MappedLegend<K, U> rl) {
        this.rl = rl;
    }

    /**
     * Gets the associated Legend.
     * @return The associated legend.
     */
    protected MappedLegend<K, U> getLegend() {
        return this.rl;
    }

    /**
     * Gets the text field used for edition.
     * @return
     */
    public JTextField getField() {
        return field;
    }

    /**
     * Gets the stored string value
     * @return the stored string value
     */
    public K getVal() {
        return val;
    }

    /**
     * Sets the stored string value
     * @param val the new stored string value.
     */
    public void setVal(K val) {
        this.val = val;
    }
}

