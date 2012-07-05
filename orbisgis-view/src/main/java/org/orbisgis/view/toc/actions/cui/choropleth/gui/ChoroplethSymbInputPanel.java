package org.orbisgis.view.toc.actions.cui.choropleth.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Choropleth symbology input panel
 * @author sennj
 */
public class ChoroplethSymbInputPanel extends JPanel {

    /** I18n */
    private final static I18n I18N = I18nFactory.getI18n(ChoroplethSymbInputPanel.class);

    /** The frequence chart data model */
    private FreqChartDataModel freqChartDataModel;
    /** The frequency chart panel */
    private FreqChart freqChart;
    /** The choropleth range table panel */
    private ChoroplethRangeTabPanel choroplethRangeTabPanel;
    /** The color stroke JPanel */
    private JPanel pnlColorStroke;
    /** The color begin JPanel */
    private JPanel pnlColorBegin;
    /** The color end JPanel */
    private JPanel pnlColorEnd;

    /**
     * ChoroplethSymbInputPanel constructor
     * @param freqChartDataModel The frequence chart data model
     * @param freqChart The frequency chart panel
     * @param choroplethRangeTabPanel The choropleth range table panel
     */
    public ChoroplethSymbInputPanel(FreqChartDataModel freqChartDataModel, FreqChart freqChart, ChoroplethRangeTabPanel choroplethRangeTabPanel) {
        this.freqChartDataModel = freqChartDataModel;
        this.freqChart = freqChart;
        this.choroplethRangeTabPanel = choroplethRangeTabPanel;
        this.initPanel();
    }

    /**
     * Initialization of the panel
     */
    private void initPanel() {

        JPanel north = initNorthPanel();
        JPanel center = initCenterPanel();
        JPanel south = new JPanel();
        south.add(choroplethRangeTabPanel);

        this.setLayout(new BorderLayout());
        this.add(north, BorderLayout.NORTH);
        this.add(center, BorderLayout.CENTER);
        this.add(south, BorderLayout.SOUTH);
    }

    /**
     * Init the North Symbology Panel
     */
    private JPanel initNorthPanel() {
        JPanel north = new JPanel();

        String[] styleCmbStr = {"Area/SolidFill/Color", "Area/HachedFill/Color", "Point/centroid/Marks/Sf/Color", "Point/centroid/Marks/Vb/Width"};
        JComboBox styleCmb = new JComboBox(styleCmbStr);

        north.add(styleCmb);

        return north;
    }

    /**
     * Init the Center Symbology Panel
     */
    private JPanel initCenterPanel() {
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());

        JPanel centerGlobal = initCenterGlobalPanel();
        center.add(centerGlobal, BorderLayout.NORTH);

        JPanel centerStroke = initCenterStrokePanel();
        center.add(centerStroke, BorderLayout.CENTER);

        JPanel centerFill = initCenterFillPanel();
        center.add(centerFill, BorderLayout.SOUTH);

        return center;
    }

    /**
     * Init the Center Global Symbology Panel
     */
    private JPanel initCenterGlobalPanel() {
        JPanel centerGlobal = new JPanel();
        centerGlobal.setLayout(new BorderLayout());

        JLabel lblGlobal = new JLabel(I18N.tr("Global"));
        lblGlobal.setPreferredSize(new Dimension(280, 25));

        JPanel centerGlobalInput = new JPanel();

        JLabel lblUnit = new JLabel(I18N.tr("Units"));

        String[] unitsCmbStr = {"Pixel"};
        JComboBox unitsCmb = new JComboBox(unitsCmbStr);

        JLabel lblParam = new JLabel(I18N.tr("Other param"));

        centerGlobalInput.add(lblUnit);
        centerGlobalInput.add(unitsCmb);
        centerGlobalInput.add(lblParam);

        centerGlobal.add(lblGlobal, BorderLayout.NORTH);
        centerGlobal.add(centerGlobalInput, BorderLayout.CENTER);

        return centerGlobal;
    }

    /**
     * Init the Center Stroke Symbology Panel
     */
    private JPanel initCenterStrokePanel() {
        JPanel centerStroke = new JPanel();
        centerStroke.setLayout(new BorderLayout());

        JLabel lblStroke = new JLabel(I18N.tr("Stroke"));
        lblStroke.setPreferredSize(new Dimension(280, 25));

        JPanel centerStrokeInput = new JPanel();

        JLabel lblWidth = new JLabel(I18N.tr("Width"));

        SpinnerModel modelWidth =
                new SpinnerNumberModel(0.5, 0, 0.5, 0.5);

        JSpinner spinnerWidth = new JSpinner();
        spinnerWidth.setModel(modelWidth);
        spinnerWidth.setPreferredSize(new Dimension(50, 25));
        spinnerWidth.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner currentSpinner = (JSpinner) (e.getSource());
                double value = Double.parseDouble(currentSpinner.getModel().getValue().toString()) * 100;
                freqChartDataModel.setStrokeWidth(value);
            }
        });

        JLabel lblColor = new JLabel(I18N.tr("Color"));

        pnlColorStroke = new JPanel();
        pnlColorStroke.setBackground(Color.BLACK);
        pnlColorStroke.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                Color color = JColorChooser.showDialog(pnlColorStroke, I18N.tr("Pick a stroke color"), Color.BLACK);
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

        return centerStroke;
    }

    /**
     * Init the Center Fill Symbology Panel
     */
    private JPanel initCenterFillPanel() {
        JPanel centerFill = new JPanel();
        centerFill.setLayout(new BorderLayout());
        JLabel lblFill = new JLabel(I18N.tr("Fill"));
        lblFill.setPreferredSize(new Dimension(280, 25));

        centerFill.add(lblFill, BorderLayout.NORTH);

        JPanel centerFillInput = new JPanel();
        centerFillInput.setLayout(new BorderLayout());

        JPanel centerFillInputOP = new JPanel();
        JLabel lblOpacity = new JLabel(I18N.tr("Opacity"));

        SpinnerModel modelOpacity =
                new SpinnerNumberModel(1, 0, 1, 0.1);

        JSpinner spinnerOpacity = new JSpinner();
        spinnerOpacity.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner currentSpinner = (JSpinner) (e.getSource());
                double value = Double.parseDouble(currentSpinner.getModel().getValue().toString()) * 100;
                freqChartDataModel.setOpacity(value);
            }
        });
        spinnerOpacity.setModel(modelOpacity);
        spinnerOpacity.setPreferredSize(new Dimension(50, 25));

        JLabel lblStart = new JLabel(I18N.tr("Start"));

        pnlColorBegin = new JPanel();
        pnlColorBegin.setName("COLOR_BEGIN");
        pnlColorBegin.setBackground(freqChartDataModel.getColorInit().get(0));
        pnlColorBegin.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                changeColor(pnlColorBegin);
            }
        });

        JLabel lblEnd = new JLabel(I18N.tr("End"));
        pnlColorEnd = new JPanel();
        pnlColorEnd.setName("COLOR_END");
        pnlColorEnd.setBackground(freqChartDataModel.getColorInit().get(1));
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
        JLabel lblScheme = new JLabel(I18N.tr("Scheme"));
        String[] schemeCmbStr = {"blue2red"};
        JComboBox schemeCmb = new JComboBox(schemeCmbStr);

        JCheckBox revert = new JCheckBox(I18N.tr("revert"));
        revert.setSelected(true);

        centerFillInputSch.add(lblScheme);
        centerFillInputSch.add(schemeCmb);
        centerFillInputSch.add(revert);

        centerFillInput.add(centerFillInputSch, BorderLayout.SOUTH);

        centerFill.add(centerFillInput, BorderLayout.CENTER);

        return centerFill;
    }

    /**
     * The changeColor graphic element
     * @param sender The name of the graphic element
     */
    private void changeColor(JPanel sender) {
        List<Color> color = freqChartDataModel.getColorInit();
        if (sender.getName().equals("COLOR_BEGIN")) {
            color.set(0,JColorChooser.showDialog(sender, I18N.tr("Pick a begin color"), freqChartDataModel.getColorInit().get(0)));
            freqChartDataModel.setColorInit(color);
            pnlColorBegin.setBackground(color.get(0));
            pnlColorBegin.invalidate();
        } else {
            color.set(1,JColorChooser.showDialog(sender, I18N.tr("Pick a end color"), freqChartDataModel.getColorInit().get(1)));
            freqChartDataModel.setColorInit(color);
            pnlColorEnd.setBackground(color.get(1));
            pnlColorEnd.invalidate();
        }

        freqChartDataModel.computeChartData();
        choroplethRangeTabPanel.refresh(freqChartDataModel);
        freqChart.repaint();
    }
}
