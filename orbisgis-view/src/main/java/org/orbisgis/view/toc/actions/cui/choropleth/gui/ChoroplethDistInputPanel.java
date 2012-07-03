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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Choropleth distribution input panel
 * @author sennj
 */
public class ChoroplethDistInputPanel extends JPanel {

    /** I18n */
    private final static I18n I18N = I18nFactory.getI18n(ChoroplethDistInputPanel.class);

    /** The frequence chart data model */
    private FreqChartDataModel freqChartDataModel;
    /** The choropleth data model */
    private ChoroplethDataModel statModel;
    /** The frequency chart panel */
    private FreqChart freqChart;
    /** The choropleth range table panel */
    private ChoroplethRangeTabPanel choroplethRangeTabPanel;
    /** The class combobox listener */
    private CmbClassListener cmbClassListener;
    /** The input area JPanel */
    private JPanel chartInput;
    /** The field combobox */
    private JComboBox cmbField;
    /** The class combobox */
    private JComboBox cmbClass;
    /** The method combobox */
    private JComboBox cmbMethod;
    /** The yule checkbox */
    private JCheckBox yule;
    /** The current number of row */
    private int nbRow;

    /** The first combobox field element */
    private String firstCmbFieldElem = I18N.tr("1st num attrib");

    /** The method string name*/
    private String quantileStr = I18N.tr("quantile");
    private String meanStr = I18N.tr("mean");
    private String jenksStr = I18N.tr("jenks");
    private String manualStr = I18N.tr("manual");

    /**
     * ChoroplethDistInputPanel constructor
     * @param freqChartDataModel The frequence chart data model
     * @param statModel The choropleth data model
     * @param freqChart The frequency chart panel
     * @param choroplethRangeTabPanel The choropleth range table panel
     */
    public ChoroplethDistInputPanel(FreqChartDataModel freqChartDataModel, ChoroplethDataModel statModel, FreqChart freqChart, ChoroplethRangeTabPanel choroplethRangeTabPanel) {
        this.freqChartDataModel = freqChartDataModel;
        this.statModel = statModel;
        this.freqChart = freqChart;
        this.choroplethRangeTabPanel = choroplethRangeTabPanel;
        this.initPanel();
    }

    /**
     * Init the Distribution Panel
     */
    private void initPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel north = initNorthPanel();
        JPanel center = initCenterPanel();
        JPanel chartInputPan = initChartInputPanel();

        this.add(north);
        this.add(center);
        this.add(freqChart.getPanel());
        this.add(chartInputPan);

        updateComboField();
        updateComboBoxNbrClasses();

    }

     /**
     * Init the North Distribution Panel
     */
    private JPanel initNorthPanel() {
        JPanel north = new JPanel(new SpringLayout());

        JLabel lblField = new JLabel(I18N.tr("LookupValue"));
        cmbField = new JComboBox();
        cmbField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String fields = String.valueOf(cmbField.getSelectedItem());

                if (fields.equals(firstCmbFieldElem)) {
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

        JLabel lblClass = new JLabel(I18N.tr("Nb classes"));
        cmbClass = new JComboBox();
        cmbClassListener = new CmbClassListener();
        cmbClass.addActionListener(cmbClassListener);

        JLabel lblMethod = new JLabel(I18N.tr("Method"));
        String[] cmbMethodString = {quantileStr, meanStr, jenksStr, manualStr};
        cmbMethod = new JComboBox(cmbMethodString);
        cmbMethod.setSelectedIndex(3);
        cmbMethod.addActionListener(new CmbMethodListener());

        north.add(lblField);
        north.add(cmbField);
        north.add(lblClass);
        north.add(cmbClass);
        north.add(lblMethod);
        north.add(cmbMethod);

        GridTools.generateGrid(north, 3, 2);

        return north;
    }

     /**
     * Init the Center Distribution Panel
     */
    private JPanel initCenterPanel() {
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());

        JLabel lblHistogram = new JLabel(I18N.tr("Histogram"));
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

        return center;
    }

     /**
     * Init the ChartInput Distribution Panel
     */
    private JPanel initChartInputPanel() {
        chartInput = new JPanel(new SpringLayout());

        int nbRangeMax = freqChartDataModel.getMaxThreshold();

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
        GridTools.generateGrid(chartInput, nbRow, 4);

        updateChartInput();

        return chartInput;
    }

    /**
     * Update the comboBox Field
     */
    private void updateComboField() {
        List<String> fields = statModel.getFields();

        cmbField.addItem(firstCmbFieldElem);

        for (int i = 0; i < fields.size(); i++) {
            cmbField.addItem(fields.get(i));
        }
        if (cmbField.getItemCount() > 0) {
            cmbField.setSelectedIndex(0);
        }
    }

    /**
     * Update the comboBox number of classes
     */
    private void updateComboBoxNbrClasses() {
        int selectedNbClass = freqChartDataModel.getThresholdNumber();
        int end = freqChartDataModel.getMaxThreshold();
        if (selectedNbClass > end) {
            end = selectedNbClass;
        }
        for (int i = 1; i <= 10; i++) {
            cmbClass.addItem(i);
        }
        cmbClass.setSelectedItem(selectedNbClass);
    }

    /**
     * Update the chart spinner
     */
    public void updateChartInput() {

        List<List<Double>> rangeList = freqChartDataModel.getThresholdList();

        for (int i = 1; i <= nbRow * 4; i++) {

            JSpinner spinner = (JSpinner) chartInput.getComponent(i - 1);

            if (i <= rangeList.size() - 1) {
                SpinnerModel model =
                        new SpinnerNumberModel(rangeList.get(i - 1).get(1).doubleValue(),
                        rangeList.get(i - 1).get(0).doubleValue(), rangeList.get(i).get(1).doubleValue(), 1);
                spinner.setModel(model);
                spinner.setVisible(true);
            } else {
                spinner.setVisible(false);
            }
        }
    }

    /**
     * Chart spinner listener class
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
            List<List<Double>> rangeList = freqChartDataModel.getThresholdList();

            JSpinner currentSpinner = (JSpinner) (e.getSource());
            double value = Double.parseDouble(currentSpinner.getModel().getValue().toString());

            List<Double> rangeDown = rangeList.get(rangeId);
            rangeDown.set(1, value);

            List<Double> rangeUp = rangeList.get(rangeId + 1);
            rangeUp.set(0, value);

            freqChart.repaint();
        }
    }

    /**
     * Chart method listener class
     */
    private class CmbMethodListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String method = (String) cmbMethod.getSelectedItem();

            ChoroplethDataModel.StatisticMethod methode = ChoroplethDataModel.StatisticMethod.MANUAL;

            if (method.equals(quantileStr)) {
                methode = ChoroplethDataModel.StatisticMethod.QUANTILES;
            } else if (method.equals(meanStr)) {
                methode = ChoroplethDataModel.StatisticMethod.MEAN;
            } else if (method.equals(jenksStr)) {
                methode = ChoroplethDataModel.StatisticMethod.JENKS;
            } else if (method.equals(manualStr)) {
                methode = ChoroplethDataModel.StatisticMethod.MANUAL;
            }

            cmbClass.removeActionListener(cmbClassListener);
            cmbClass.removeAllItems();
            int[] allowed = statModel.getNumberOfClassesAllowed(freqChartDataModel, methode);
            for (int i = 0; i < allowed.length; i++) {
                cmbClass.addItem(allowed[i]);
            }
            if (method.equals(meanStr)) {
                cmbClass.setSelectedIndex(1);
            } else {
                cmbClass.setSelectedIndex(4);
            }
            freqChartDataModel.setThresholdNumber(Integer.parseInt(cmbClass.getSelectedItem().toString()));
            cmbClass.addActionListener(cmbClassListener);

            statModel.setStatisticMethod(freqChartDataModel, methode);

            freqChart.repaint();
            updateChartInput();
            choroplethRangeTabPanel.refresh(freqChartDataModel);
        }
    }

    /**
     * Chart number of range listener class
     */
    private class CmbClassListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            freqChartDataModel.setThresholdNumber(Integer.parseInt(cmbClass.getSelectedItem().toString()));
            freqChartDataModel.generateChartData();
            choroplethRangeTabPanel.refresh(freqChartDataModel);
            freqChart.repaint();
            updateChartInput();
        }
    }
}
