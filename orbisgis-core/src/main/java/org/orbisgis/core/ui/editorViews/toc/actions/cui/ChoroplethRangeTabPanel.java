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
 *
 * @author sennj
 */
class ChoroplethRangeTabPanel extends JPanel {

    private TableModel tableModel;
    private JTable tableau;

    public ChoroplethRangeTabPanel(ChoroplethDatas ChoroDatas) {

        JPanel tab = new JPanel();
        tableModel = new TableModel(ChoroDatas);
        tableau = new JTable(tableModel);
        tableModel.addTableModelListener(new TableListener(this, tableau, ChoroDatas));

        tableau.setDefaultRenderer(Color.class, new ColorCellRenderer());
        tableau.setDefaultEditor(Color.class, new ColorCellEditor(ChoroDatas));

        tab.setLayout(new BorderLayout());
        tab.add(tableau.getTableHeader(), BorderLayout.NORTH);
        tab.add(tableau, BorderLayout.CENTER);
        tab.setPreferredSize(new Dimension(600,180));

        this.add(tab);

    }

    public void refresh(ChoroplethDatas ChoroDatas) {
        tableau.removeAll();
        tableModel.refreshData(ChoroDatas);
        tableau.repaint();
    }
}
