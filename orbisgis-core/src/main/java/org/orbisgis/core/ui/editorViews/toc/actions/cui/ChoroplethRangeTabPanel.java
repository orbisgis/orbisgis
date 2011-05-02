/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * Panel with the range table
 * @author sennj
 */
class ChoroplethRangeTabPanel extends JPanel {

    private TableModel tableModel;
    private JTable tableau;

    /**
     * ChoroplethRangeTabPanel Constructor
     * @param choroDatas the datas to draw
     */
    public ChoroplethRangeTabPanel(ChoroplethDatas choroDatas) {

        JPanel tab = new JPanel();
        tableModel = new TableModel(choroDatas);
        tableau = new JTable(tableModel);
        tableModel.addTableModelListener(new TableListener(tableau, choroDatas));

        tableau.setDefaultRenderer(Color.class, new ColorCellRenderer());
        tableau.setDefaultEditor(Color.class, new ColorCellEditor(choroDatas));

        tab.setLayout(new BorderLayout());
        tab.add(tableau.getTableHeader(), BorderLayout.NORTH);
        tab.add(tableau, BorderLayout.CENTER);
        tab.setPreferredSize(new Dimension(600, 180));

        this.add(tab);

    }

    /**
     * refresh
     * refresh the range table
     * @param choroDatas the datas to draw
     */
    public void refresh(ChoroplethDatas choroDatas) {
        tableau.removeAll();
        tableModel.refreshData(choroDatas);
        tableau.repaint();
    }
}
