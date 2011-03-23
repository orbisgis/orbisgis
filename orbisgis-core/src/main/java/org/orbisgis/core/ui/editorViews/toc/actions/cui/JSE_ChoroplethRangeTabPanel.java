/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author sennj
 */
class JSE_ChoroplethRangeTabPanel extends JPanel{

    private JSE_ChoroplethDatas ChoroDatas;

    private JSE_TableModel tableModel;
    private JTable tableau;

    public JSE_ChoroplethRangeTabPanel(JSE_ChoroplethDatas ChoroDatas) {

        JPanel tab = new JPanel();

        tableModel = new JSE_TableModel(ChoroDatas);

        tableau = new JTable(tableModel);

        tableModel.addTableModelListener(new JSE_TableListener(this,tableau,ChoroDatas));

        tableau.setDefaultRenderer(Color.class, new JSE_ColorCellRenderer());
        tableau.setDefaultEditor(Color.class, new JSE_ColorCellEditor(ChoroDatas));        

        tab.setLayout(new BorderLayout());

        tab.add(tableau.getTableHeader(), BorderLayout.NORTH);
        tab.add(tableau, BorderLayout.CENTER);

        this.add(tab);

    }

    public void refresh()
    {
        tableModel.refreshData();
        tableau.repaint();
    }
}
