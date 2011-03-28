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
class JSE_ChoroplethRangeTabPanel extends JPanel {

    private JSE_TableModel tableModel;
    private JTable tableau;

    public JSE_ChoroplethRangeTabPanel(JSE_ChoroplethDatas ChoroDatas) {

        JPanel tab = new JPanel();

        tableModel = new JSE_TableModel(ChoroDatas);

        tableau = new JTable(tableModel);

        tableModel.addTableModelListener(new JSE_TableListener(this, tableau, ChoroDatas));

        tableau.setDefaultRenderer(Color.class, new JSE_ColorCellRenderer());
        tableau.setDefaultEditor(Color.class, new JSE_ColorCellEditor(ChoroDatas));

        tab.setLayout(new BorderLayout());

        tab.add(tableau.getTableHeader(), BorderLayout.NORTH);
        tab.add(tableau, BorderLayout.CENTER);
        tab.setPreferredSize(new Dimension(600,180));

        this.add(tab);

    }

    public void refresh(JSE_ChoroplethDatas ChoroDatas) {
        tableau.removeAll();
        tableModel.refreshData(ChoroDatas);
        tableau.repaint();
    }
}
