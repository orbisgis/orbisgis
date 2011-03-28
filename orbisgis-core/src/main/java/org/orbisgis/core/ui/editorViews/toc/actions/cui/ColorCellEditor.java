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
 *
 * @author sennj
 */
class ColorCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private ChoroplethDatas ChoroDatas;
    private Color couleur;
    private int row;
    private JButton bouton;
    private ColorPicker dialog;

    public ColorCellEditor(ChoroplethDatas ChoroDatas) {
        super();

        this.ChoroDatas = ChoroDatas;
        bouton = new JButton();
        bouton.setActionCommand("change");
        bouton.addActionListener(this);
        bouton.setBorderPainted(false);
        dialog = new ColorPicker();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        bouton.setBackground(Color.LIGHT_GRAY);
        if ("change".equals(e.getActionCommand())) {

            if (UIFactory.showDialog(dialog)) {
                couleur = dialog.getColor();
                ChoroDatas.setClassColor(couleur, row);
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
