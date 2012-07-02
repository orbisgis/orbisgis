package org.orbisgis.view.toc.actions.cui.choropleth.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTable;
import org.orbisgis.view.toc.actions.cui.choropleth.listener.TableListener;
import org.orbisgis.view.toc.actions.cui.freqChart.FreqChart;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;

/**
 * Panel with the range table
 * @author sennj
 */
public class ChoroplethRangeTabPanel extends JPanel {

    /** The model of the table */
    private TableModel tableModel;
    /** The range table JTable */
    private JTable table;

    /**
     * ChoroplethRangeTabPanel Constructor
     * @param freqChartDataModel The frequence chart data model
     * @param freqChart The frequency chart panel
     */
    public ChoroplethRangeTabPanel(FreqChartDataModel freqChartDataModel, FreqChart freqChart) {

        JPanel tab = new JPanel();
        tableModel = new TableModel(freqChartDataModel);
        table = new JTable(tableModel);
        tableModel.addTableModelListener(new TableListener(table, freqChartDataModel, freqChart));

        table.setDefaultRenderer(Color.class, new ColorCellRenderer());

        tab.setLayout(new BorderLayout());
        tab.add(table.getTableHeader(), BorderLayout.NORTH);
        tab.add(table, BorderLayout.CENTER);
        tab.setPreferredSize(new Dimension(300, 180));

        this.add(tab);
    }

    /**
     * Refresh the range table
     * @param freqChartDataModel The frequence chart data model
     */
    public void refresh(FreqChartDataModel freqChartDataModel) {
        table.removeAll();
        tableModel.refreshDatas(freqChartDataModel);
        table.repaint();
    }
}
