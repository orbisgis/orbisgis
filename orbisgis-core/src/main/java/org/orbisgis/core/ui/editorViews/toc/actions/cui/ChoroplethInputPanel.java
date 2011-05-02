/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.ChoroplethDatas.DataChanged;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.ChoroplethDatas.DataChangedListener;

/**
 * Panel with the input element
 * @author sennj, mairep
 */
class ChoroplethInputPanel extends JPanel {

    private ChoroplethDatas choroDatas;
    private ILayer layer;
    private JComboBox cbxNbrOfClasses;
    private JLabel lblField;
    private JComboBox cbxField;
    private JLabel lblNbrOfClasses;
    private JButton btnApply;
    private JLabel lblStatMethod;
    private JRadioButton rdbxStatMethodQuantiles;
    private JRadioButton rdbxStatMethodMean;
    private JRadioButton rdbxStatMethodJenks;
    private JRadioButton rdbxStatMethodManual;
    private JLabel lblColorBegin;
    private JLabel lblColorEnd;
    private JPanel pnlColorBegin;
    private JPanel pnlColorEnd;

    /**
     * ChoroplethInputPanel Constructor
     * @param metaPanel the parent WizardPanel
     * @param layer the ILayer
     * @param choroPlethDatas the datas to draw
     */
    public ChoroplethInputPanel(ChoroplethWizardPanel metaPanel, ILayer layer, ChoroplethDatas choroPlethDatas) {
        this.layer = layer;
        this.choroDatas = choroPlethDatas;
        this.choroDatas.addDataChangedListener(new DataChangedListener() {

            @Override
            public void dataChangedOccurred(DataChanged evt) {
                if (evt.dataType == ChoroplethDatas.DataChangedType.RANGES && choroDatas.getStatisticMethod() != ChoroplethDatas.StatisticMethod.MANUAL) {
                    rdbxStatMethodManual.setSelected(true);
                }
            }
        });
        this.initPanel(metaPanel);
    }

    /**
     * initPanel
     * Initialyse the Panel
     * @param metaPanel the parent WizardPanel
     */
    public void initPanel(ChoroplethWizardPanel metaPanel) {
        lblField = new JLabel("Field");
        cbxField = new JComboBox();
        cbxField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                choroDatas.setFieldIndex(cbxField.getSelectedIndex());
            }
        });
        lblNbrOfClasses = new JLabel("Number Of Classes");
        cbxNbrOfClasses = new JComboBox();
        cbxNbrOfClasses.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cbxNbrOfClasses.getSelectedItem() != null) {
                    choroDatas.setNbrClasses(Integer.parseInt(cbxNbrOfClasses.getSelectedItem().toString()));
                }
            }
        });

        lblStatMethod = new JLabel("Statistic Method");
        rdbxStatMethodQuantiles = new JRadioButton("Quantile");
        rdbxStatMethodQuantiles.setSelected(true);
        rdbxStatMethodMean = new JRadioButton("Mean");
        rdbxStatMethodJenks = new JRadioButton("Jenks");
        rdbxStatMethodManual = new JRadioButton("Manual");
        //Add radio button listener
        RadioListener rListener = new RadioListener();
        rdbxStatMethodQuantiles.addItemListener(rListener);
        rdbxStatMethodMean.addItemListener(rListener);
        rdbxStatMethodJenks.addItemListener(rListener);
        rdbxStatMethodManual.addItemListener(rListener);
        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(rdbxStatMethodQuantiles);
        group.add(rdbxStatMethodMean);
        group.add(rdbxStatMethodJenks);
        group.add(rdbxStatMethodManual);

        pnlColorBegin = new JPanel();
        pnlColorBegin.setName("COLOR_BEGIN");
        pnlColorBegin.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                changeColor(pnlColorBegin);
            }
        });
        pnlColorEnd = new JPanel();
        pnlColorEnd.setName("COLOR_END");
        pnlColorEnd.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                changeColor(pnlColorEnd);
            }
        });
        lblColorBegin = new JLabel("Starting Color");
        lblColorEnd = new JLabel("Ending Color");


        btnApply = new JButton("Apply");
        btnApply.setMargin(new Insets(0, 0, 0, 0));
        btnApply.addActionListener(new btnApplyListener(metaPanel));

        JPanel north = new JPanel();
        north.setLayout(new FlowLayout());
        JPanel west = new JPanel();
        west.setLayout(new FlowLayout());
        JPanel east = new JPanel();
        east.setLayout(new FlowLayout());

        north.add(lblField);
        north.add(cbxField);
        north.add(lblNbrOfClasses);
        north.add(cbxNbrOfClasses);
        west.add(lblStatMethod);
        west.add(rdbxStatMethodQuantiles);
        west.add(rdbxStatMethodMean);
        west.add(rdbxStatMethodJenks);
        west.add(rdbxStatMethodManual);
        east.add(pnlColorBegin);
        east.add(lblColorBegin);
        east.add(pnlColorEnd);
        east.add(lblColorEnd);
        east.add(btnApply);

        this.setLayout(new BorderLayout());
        this.add(north, BorderLayout.NORTH);
        this.add(west, BorderLayout.WEST);
        this.add(east, BorderLayout.EAST);

        pnlColorBegin.setBackground(choroDatas.getBeginColor());
        pnlColorEnd.setBackground(choroDatas.getEndColor());
        for (int i = 0; i < choroDatas.getFields().size(); i++) {
            cbxField.addItem(choroDatas.getFields().get(i));
        }
        if (cbxField.getItemCount() > 0) {
            cbxField.setSelectedIndex(0);
        }
        updateComboBoxNbrClasses(choroDatas.getStatisticMethod());
    }

    /**
    * updateComboBoxNbrClasses
    * update the comboBox with the numer of classes
    * @param statMethod the StatisticMethod
    */
    private void updateComboBoxNbrClasses(ChoroplethDatas.StatisticMethod statMethod) {
        if (choroDatas != null) {
            int savedValue = 0;
            if (cbxNbrOfClasses.getSelectedItem() != null) {
                savedValue = Integer.parseInt(cbxNbrOfClasses.getSelectedItem().toString());
            }

            cbxNbrOfClasses.removeAllItems();
            int[] allowed = choroDatas.getNumberOfClassesAllowed(statMethod);
            for (int i = 0; i < allowed.length; i++) {
                cbxNbrOfClasses.addItem(allowed[i]);
                if (allowed[i] == savedValue) {
                    cbxNbrOfClasses.setSelectedIndex(i);
                }
            }

            if (cbxNbrOfClasses.getSelectedItem() != null) {
                choroDatas.setNbrClasses(Integer.parseInt(cbxNbrOfClasses.getSelectedItem().toString()));
            }
        }
    }

    /**
     * changeColor
     * the changeColor graphic elem
     * @param sender the name of the graphic elem
     */
    private void changeColor(JPanel sender) {
        if (sender.getName().equals("COLOR_BEGIN")) {
            choroDatas.setBeginColor(JColorChooser.showDialog(sender, "Pick a begin color", choroDatas.getBeginColor()));
            pnlColorBegin.setBackground(choroDatas.getBeginColor());
            pnlColorBegin.invalidate();
        } else {
            choroDatas.setEndColor(JColorChooser.showDialog(sender, "Pick an end color", choroDatas.getEndColor()));
            pnlColorEnd.setBackground(choroDatas.getEndColor());
            pnlColorEnd.invalidate();
        }
    }

    class RadioListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getItem().equals(rdbxStatMethodQuantiles) && e.getStateChange() == ItemEvent.SELECTED) {
                //System.out.println("Quantiles Selected");
                updateComboBoxNbrClasses(ChoroplethDatas.StatisticMethod.QUANTILES);
                choroDatas.setStatisticMethod(ChoroplethDatas.StatisticMethod.QUANTILES, true);
            } else if (e.getItem().equals(rdbxStatMethodMean) && e.getStateChange() == ItemEvent.SELECTED) {
                //System.out.println("Mean Selected");
                updateComboBoxNbrClasses(ChoroplethDatas.StatisticMethod.AVERAGE);
                choroDatas.setStatisticMethod(ChoroplethDatas.StatisticMethod.AVERAGE, true);
            }
            if (e.getItem().equals(rdbxStatMethodJenks) && e.getStateChange() == ItemEvent.SELECTED) {
                //System.out.println("Jenks Selected");
                updateComboBoxNbrClasses(ChoroplethDatas.StatisticMethod.JENKS);
                choroDatas.setStatisticMethod(ChoroplethDatas.StatisticMethod.JENKS, true);
            }
            if (e.getItem().equals(rdbxStatMethodManual) && e.getStateChange() == ItemEvent.SELECTED) {
                //System.out.println("Manual Selected");
                updateComboBoxNbrClasses(ChoroplethDatas.StatisticMethod.MANUAL);
                choroDatas.setStatisticMethod(ChoroplethDatas.StatisticMethod.MANUAL, false);
            }
        }
    }

    private class btnApplyListener implements ActionListener {

        private final ChoroplethWizardPanel metaPanel;

        public btnApplyListener(ChoroplethWizardPanel metaPanel) {
            super();
            this.metaPanel = metaPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Rule r = this.metaPanel.getRule();
                if (r != null) {
                    // Add the rule in the current featureTypeStyle
                    layer.getFeatureTypeStyle().clear();
                    layer.getFeatureTypeStyle().addRule(r);
                    // And finally redraw the map
                    layer.fireStyleChangedPublic();
                }
            } catch (DriverException ex) {
                Logger.getLogger(ChoroplethWizardPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
