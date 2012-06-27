package org.orbisgis.view.toc.actions.cui.choropleth.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.orbisgis.view.toc.actions.cui.freqChart.FreqChart;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;


/**
 * Choropleth symbology input panel
 * @author sennj
 */
public class ChoroplethSymbInputPanel extends JPanel {

    private FreqChartDataModel freqChartDataModel;
    private FreqChart freqChart;
    private ChoroplethRangeTabPanel choroplethRangeTabPanel;
    private JPanel pnlColorStroke;
    private JPanel pnlColorBegin;
    private JPanel pnlColorEnd;

    /**
     * ChoroplethSymbInputPanel constructor
     * @param freqChartDataModel
     * @param freqChart
     * @param choroplethRangeTabPanel
     */
    public ChoroplethSymbInputPanel(FreqChartDataModel freqChartDataModel, FreqChart freqChart, ChoroplethRangeTabPanel choroplethRangeTabPanel) {
        this.freqChartDataModel = freqChartDataModel;
        this.freqChart = freqChart;
        this.choroplethRangeTabPanel = choroplethRangeTabPanel;
        this.initPanel();
    }

    /**
     * initialization of the panel
     */
    private void initPanel() {
        JPanel north = new JPanel();

        String[] styleCmbStr = {"Area/SolidFill/Color", "Area/HachedFill/Color", "Point/centroid/Marks/Sf/Color", "Point/centroid/Marks/Vb/Width"};
        JComboBox styleCmb = new JComboBox(styleCmbStr);

        north.add(styleCmb);

        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());

        JPanel centerGlobal = new JPanel();
        centerGlobal.setLayout(new BorderLayout());

        JLabel lblGlobal = new JLabel(" _ Global _________________________________________________________");
        lblGlobal.setPreferredSize(new Dimension(280, 25));

        JPanel centerGlobalInput = new JPanel();

        JLabel lblUnit = new JLabel("Units");

        String[] unitsCmbStr = {"Pixel"};
        JComboBox unitsCmb = new JComboBox(unitsCmbStr);

        JLabel lblParam = new JLabel("Other param");

        centerGlobalInput.add(lblUnit);
        centerGlobalInput.add(unitsCmb);
        centerGlobalInput.add(lblParam);

        centerGlobal.add(lblGlobal, BorderLayout.NORTH);
        centerGlobal.add(centerGlobalInput, BorderLayout.CENTER);

        center.add(centerGlobal, BorderLayout.NORTH);

        JPanel centerStroke = new JPanel();
        centerStroke.setLayout(new BorderLayout());

        JLabel lblStroke = new JLabel(" _ Stroke _________________________________________________________");
        lblStroke.setPreferredSize(new Dimension(280, 25));

        JPanel centerStrokeInput = new JPanel();

        JLabel lblWidth = new JLabel("Width");

        SpinnerModel modelWidth =
                new SpinnerNumberModel(0.5, 0, 0.5, 0.5);

        JSpinner spinnerWidth = new JSpinner();
        spinnerWidth.setModel(modelWidth);
        spinnerWidth.setPreferredSize(new Dimension(50, 25));
        spinnerWidth.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner currentSpinner = (JSpinner) (e.getSource());
                double value = Double.parseDouble(currentSpinner.getModel().getValue().toString())*100;
                freqChartDataModel.setStrokeWidth(value);
            }
        });

        JLabel lblColor = new JLabel("Color");

        pnlColorStroke = new JPanel();
        pnlColorStroke.setBackground(Color.BLACK);
        pnlColorStroke.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                Color color = JColorChooser.showDialog(pnlColorStroke, "Pick a begin color", Color.BLACK);
                pnlColorStroke.setBackground(color);
                freqChartDataModel.setStrokeColor(color);
                pnlColorStroke.invalidate();
            }
        });

        centerStrokeInput.add(lblWidth);
        centerStrokeInput.add(spinnerWidth);
        centerStrokeInput.add(lblColor);
        centerStrokeInput.add(pnlColorStroke);

        centerStroke.add(lblStroke, BorderLayout.NORTH);
        centerStroke.add(centerStrokeInput, BorderLayout.CENTER);

        center.add(centerStroke, BorderLayout.CENTER);

        JPanel centerFill = new JPanel();
        centerFill.setLayout(new BorderLayout());

        JLabel lblFill = new JLabel(" _ Fill ___________________________________________________________");
        lblFill.setPreferredSize(new Dimension(280, 25));

        centerFill.add(lblFill, BorderLayout.NORTH);

        JPanel centerFillInput = new JPanel();
        centerFillInput.setLayout(new BorderLayout());

        JPanel centerFillInputOP = new JPanel();

        JLabel lblOpacity = new JLabel("Opacity");

        SpinnerModel modelOpacity =
                new SpinnerNumberModel(1, 0, 1, 0.1);

        JSpinner spinnerOpacity = new JSpinner();
        spinnerOpacity.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner currentSpinner = (JSpinner) (e.getSource());
                double value = Double.parseDouble(currentSpinner.getModel().getValue().toString())*100;
                freqChartDataModel.setOpacity(value);
            }
        });
        spinnerOpacity.setModel(modelOpacity);
        spinnerOpacity.setPreferredSize(new Dimension(50, 25));

        JLabel lblStart = new JLabel("Start");

        pnlColorBegin = new JPanel();
        pnlColorBegin.setName("COLOR_BEGIN");
        pnlColorBegin.setBackground(freqChartDataModel.getColorInit()[0]);
        pnlColorBegin.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                changeColor(pnlColorBegin);
            }
        });

        JLabel lblEnd = new JLabel("End");
        pnlColorEnd = new JPanel();
        pnlColorEnd.setName("COLOR_END");
        pnlColorEnd.setBackground(freqChartDataModel.getColorInit()[1]);
        pnlColorEnd.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                changeColor(pnlColorEnd);
            }
        });

        centerFillInputOP.add(lblOpacity);
        centerFillInputOP.add(spinnerOpacity);
        centerFillInputOP.add(lblStart);
        centerFillInputOP.add(pnlColorBegin);
        centerFillInputOP.add(lblEnd);
        centerFillInputOP.add(pnlColorEnd);

        centerFillInput.add(centerFillInputOP, BorderLayout.NORTH);

        JPanel centerFillInputSch = new JPanel();

        JLabel lblScheme = new JLabel("Scheme");

        String[] schemeCmbStr = {"blue2red"};
        JComboBox schemeCmb = new JComboBox(schemeCmbStr);

        JCheckBox revert = new JCheckBox("revert");
        revert.setSelected(true);

        centerFillInputSch.add(lblScheme);
        centerFillInputSch.add(schemeCmb);
        centerFillInputSch.add(revert);

        centerFillInput.add(centerFillInputSch, BorderLayout.SOUTH);

        centerFill.add(centerFillInput, BorderLayout.CENTER);

        center.add(centerFill, BorderLayout.SOUTH);

        JPanel south = new JPanel();

        south.add(choroplethRangeTabPanel);

        this.setLayout(new BorderLayout());
        this.add(north, BorderLayout.NORTH);
        this.add(center, BorderLayout.CENTER);
        this.add(south, BorderLayout.SOUTH);

    }

    /**
     * the changeColor graphic element
     * @param sender the name of the graphic element
     */
    private void changeColor(JPanel sender) {
        Color[] color = freqChartDataModel.getColorInit();
        if (sender.getName().equals("COLOR_BEGIN")) {
            color[0] = JColorChooser.showDialog(sender, "Pick a begin color", freqChartDataModel.getColorInit()[0]);
            freqChartDataModel.setColorInit(color);
            pnlColorBegin.setBackground(color[0]);
            pnlColorBegin.invalidate();
        } else {
            color[1] = JColorChooser.showDialog(sender, "Pick an end color", freqChartDataModel.getColorInit()[1]);
            freqChartDataModel.setColorInit(color);
            pnlColorEnd.setBackground(color[1]);
            pnlColorEnd.invalidate();
        }

        freqChartDataModel.computeChartData();
        choroplethRangeTabPanel.refresh(freqChartDataModel);
        freqChart.repaint();
    }
}
