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

    private TableModel tableModel;
    private JTable tableau;

    /**
     * ChoroplethRangeTabPanel Constructor
     * @param freqChart 
     * @param freqChartDataModel the data to model draw
     */
    public ChoroplethRangeTabPanel(FreqChartDataModel freqChartDataModel, FreqChart freqChart) {

        JPanel tab = new JPanel();
        tableModel = new TableModel(freqChartDataModel);
        tableau = new JTable(tableModel);
        tableModel.addTableModelListener(new TableListener(tableau, freqChartDataModel, freqChart));

        tableau.setDefaultRenderer(Color.class, new ColorCellRenderer());

        tab.setLayout(new BorderLayout());
        tab.add(tableau.getTableHeader(), BorderLayout.NORTH);
        tab.add(tableau, BorderLayout.CENTER);
        tab.setPreferredSize(new Dimension(300, 180));

        this.add(tab);
    }

    /**
     * refresh the range table
     * @param freqChartDataModel the data model
     */
    public void refresh(FreqChartDataModel freqChartDataModel) {
        tableau.removeAll();
        tableModel.refreshDatas(freqChartDataModel);
        tableau.repaint();
    }
}
