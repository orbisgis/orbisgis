/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.ColorPicker;

/**
 * Classe that edit the color of the cell from the range table
 * @author sennj
 */
class ColorCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private ChoroplethDatas choroDatas;
    private Color couleur;
    private int row;
    private JButton bouton;
    private ColorPicker dialog;

    /**
     * ColorCellEditor Constructor
     * @param choroDatas the datas to draw
     */
    public ColorCellEditor(ChoroplethDatas choroDatas) {
        super();

        this.choroDatas = choroDatas;
        this.bouton = new JButton();
        bouton.setActionCommand("change");
        bouton.addActionListener(this);
        bouton.setBorderPainted(false);
        this.dialog = new ColorPicker();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        bouton.setBackground(Color.LIGHT_GRAY);
        if ("change".equals(e.getActionCommand())) {

            if (UIFactory.showDialog(dialog)) {
                couleur = dialog.getColor();
                choroDatas.setClassColor(couleur, row);
                bouton.setBackground(couleur);
            }
            fireEditingStopped();
        }
    }

    @Override
    public Object getCellEditorValue() {
        return couleur;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.couleur = (Color) value;
        this.row = row;
        return bouton;
    }
}
