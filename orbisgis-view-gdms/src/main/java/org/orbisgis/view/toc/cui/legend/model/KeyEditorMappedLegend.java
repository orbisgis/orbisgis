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

