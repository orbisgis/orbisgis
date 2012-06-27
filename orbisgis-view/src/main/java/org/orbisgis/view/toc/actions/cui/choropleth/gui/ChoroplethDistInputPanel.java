package org.orbisgis.view.toc.actions.cui.choropleth.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.orbisgis.view.toc.actions.cui.choropleth.dataModel.ChoroplethDataModel;
import org.orbisgis.view.toc.actions.cui.freqChart.FreqChart;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;

/**
 * Choropleth distribution input panel
 * @author sennj
 */
public class ChoroplethDistInputPanel extends JPanel {

    private FreqChartDataModel freqChartDataModel;
    private ChoroplethDataModel statModel;
    private FreqChart freqChart;
    private ChoroplethRangeTabPanel choroplethRangeTabPanel;
    private CmbClassListener cmbClassListener;
    private JPanel chartInput;
    private JComboBox cmbField;
    private JComboBox cmbClass;
    private JComboBox cmbMethod;
    private JCheckBox yule;
    private int nbRow;

    /**
     * ChoroplethDistInputPanel constructor
     * @param freqChartDataModel
     * @param statModel
     * @param freqChart
     * @param choroplethRangeTabPanel
     */
    public ChoroplethDistInputPanel(FreqChartDataModel freqChartDataModel, ChoroplethDataModel statModel, FreqChart freqChart, ChoroplethRangeTabPanel choroplethRangeTabPanel) {
        this.freqChartDataModel = freqChartDataModel;
        this.statModel = statModel;
        this.freqChart = freqChart;
        this.choroplethRangeTabPanel = choroplethRangeTabPanel;
        this.initPanel();
    }

    /**
     * init the Distribution Panel
     */
    private void initPanel() {

        JPanel north = new JPanel(new SpringLayout());

        JLabel lblField = new JLabel("LookupValue");
        cmbField = new JComboBox();
        cmbField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String fields = String.valueOf(cmbField.getSelectedItem());

                if (fields.equals("1st num attrib")) {
                    statModel.setField(statModel.getFields().get(0));
                    freqChartDataModel.setData(statModel.getData());
                } else {
                    statModel.setField(fields);
                    freqChartDataModel.setData(statModel.getData());
                }
                yule.setSelected(true);
                freqChartDataModel.setClassNumber(freqChartDataModel.getClassNumberGen());
                freqChart.clearData();
                freqChart.repaint();
                updateChartInput();
            }
        });

        JLabel lblClass = new JLabel("Nb classes");
        cmbClass = new JComboBox();
        cmbClassListener = new CmbClassListener();
        cmbClass.addActionListener(cmbClassListener);

        JLabel lblMethod = new JLabel("Method");
        String[] cmbMethodString = {"quantile", "mean", "jenks", "manual"};
        cmbMethod = new JComboBox(cmbMethodString);
        cmbMethod.setSelectedIndex(3);
        cmbMethod.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String method = (String) cmbMethod.getSelectedItem();

                ChoroplethDataModel.StatisticMethod methode = ChoroplethDataModel.StatisticMethod.MANUAL;

                if (method.equals("quantile")) {
                    methode = ChoroplethDataModel.StatisticMethod.QUANTILES;
                }
                if (method.equals("mean")) {
                    methode = ChoroplethDataModel.StatisticMethod.MEAN;
                }
                if (method.equals("jenks")) {
                    methode = ChoroplethDataModel.StatisticMethod.JENKS;
                }
                if (method.equals("manual")) {
                    methode = ChoroplethDataModel.StatisticMethod.MANUAL;
                }

                cmbClass.removeActionListener(cmbClassListener);
                cmbClass.removeAllItems();
                int[] allowed = statModel.getNumberOfClassesAllowed(freqChartDataModel, methode);
                for (int i = 0; i < allowed.length; i++) {
                    cmbClass.addItem(allowed[i]);
                }
                if (method.equals("mean")) {
                    cmbClass.setSelectedIndex(1);
                } else {
                    cmbClass.setSelectedIndex(4);
                }
                freqChartDataModel.setNbSeuil(Integer.parseInt(cmbClass.getSelectedItem().toString()));
                cmbClass.addActionListener(cmbClassListener);

                statModel.setStatisticMethod(freqChartDataModel, methode);

                freqChart.repaint();
                updateChartInput();
                choroplethRangeTabPanel.refresh(freqChartDataModel);

            }
        });

        north.add(lblField);
        north.add(cmbField);
        north.add(lblClass);
        north.add(cmbClass);
        north.add(lblMethod);
        north.add(cmbMethod);

        SpringUtilities.makeGrid(north, 3, 2, 5, 5, 5, 5);

        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());

        JLabel lblHistogram = new JLabel(" _ Histogram ______________________________________________________");
        lblHistogram.setPreferredSize(new Dimension(280, 25));

        JPanel centerInputPanel = new JPanel();

        yule = new JCheckBox("Yule");
        yule.setSelected(true);
        yule.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                boolean selected = abstractButton.getModel().isSelected();
                if (selected) {
                    freqChartDataModel.setClassNumber(freqChartDataModel.getClassNumberGen());
                    freqChart.clearData();
                    freqChart.repaint();
                }
            }
        });
        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                yule.setSelected(false);
                freqChartDataModel.setClassNumber(freqChartDataModel.getClassNumber() + 1);
                freqChart.clearData();
                freqChart.repaint();
            }
        });

        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                yule.setSelected(false);
                int classMin = freqChartDataModel.getClassNumber() - 1;
                if (classMin > 0) {
                    freqChartDataModel.setClassNumber(classMin);
                    freqChart.clearData();
                    freqChart.repaint();
                }
            }
        });

        centerInputPanel.add(yule);
        centerInputPanel.add(plus);
        centerInputPanel.add(minus);

        center.add(lblHistogram, BorderLayout.NORTH);
        center.add(centerInputPanel, BorderLayout.EAST);

        chartInput = new JPanel(new SpringLayout());

        int nbRangeMax = freqChartDataModel.getMaxSeuil();

        int nbRange = nbRangeMax - 1;
        nbRow = (nbRange / 4);
        if (nbRange % 4 != 0) {
            nbRow++;
        }
        for (int i = 1; i <= nbRow * 4; i++) {
            JSpinner spinner = new JSpinner();
            spinner.setPreferredSize(new Dimension(25, 25));
            spinner.addChangeListener(new SpinnerListener(i - 1));
            chartInput.add(spinner);
        }
        SpringUtilities.makeCompactGrid(chartInput, nbRow, 4, 1, 1, 5, 5);

        updateChartInput();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(north);
        this.add(center);
        this.add(freqChart.getPanel());
        this.add(chartInput);

        updateComboField();
        updateComboBoxNbrClasses();
    }

    /**
     * update the comboBox Field
     */
    private void updateComboField() {
        List<String> fields = statModel.getFields();

        cmbField.addItem("1st num attrib");

        for (int i = 0; i < fields.size(); i++) {
            cmbField.addItem(fields.get(i));
        }
        if (cmbField.getItemCount() > 0) {
            cmbField.setSelectedIndex(0);
        }
    }

     /**
     * update the comboBox number of classes
     */
    private void updateComboBoxNbrClasses() {
        int selectedNbClass = freqChartDataModel.getNbSeuil();
        int end = freqChartDataModel.getMaxSeuil();
        if (selectedNbClass > end) {
            end = selectedNbClass;
        }
        for (int i = 1; i <= 10; i++) {
            cmbClass.addItem(i);
        }
        cmbClass.setSelectedItem(selectedNbClass);
    }

    /**
     * update the chart spinner
     */
    public void updateChartInput() {

        List<double[]> rangeList = freqChartDataModel.getSeuilList();

        for (int i = 1; i <= nbRow * 4; i++) {

            JSpinner spinner = (JSpinner) chartInput.getComponent(i - 1);

            if (i <= rangeList.size() - 1) {
                SpinnerModel model =
                        new SpinnerNumberModel(rangeList.get(i - 1)[1],
                        rangeList.get(i - 1)[0], rangeList.get(i)[1], 1);
                spinner.setModel(model);
                spinner.setVisible(true);
            } else {
                spinner.setVisible(false);
            }
        }
    }

    /**
     * chart spinner listener class
     */
    private class SpinnerListener implements ChangeListener {

        int rangeId;

        /**
         * chart spinner listener constructor
         * @param rangeId
         */
        public SpinnerListener(int rangeId) {
            this.rangeId = rangeId;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            List<double[]> rangeList = freqChartDataModel.getSeuilList();

            JSpinner currentSpinner = (JSpinner) (e.getSource());
            double value = Double.parseDouble(currentSpinner.getModel().getValue().toString());

            double[] rangeDown = rangeList.get(rangeId);
            rangeDown[1] = value;

            double[] rangeUp = rangeList.get(rangeId + 1);
            rangeUp[0] = value;

            freqChart.repaint();
        }
    }

    /**
     * chart number of range listener class
     */
    private class CmbClassListener implements ActionListener {

        public CmbClassListener() {}

        @Override
        public void actionPerformed(ActionEvent e) {
            freqChartDataModel.setNbSeuil(Integer.parseInt(cmbClass.getSelectedItem().toString()));
            freqChartDataModel.generateChartData();
            choroplethRangeTabPanel.refresh(freqChartDataModel);
            freqChart.repaint();
            updateChartInput();
        }
    }
}
