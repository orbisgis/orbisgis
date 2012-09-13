/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.view.toc.actions.cui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.net.URL;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.apache.log4j.Logger;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;

import org.orbisgis.view.toc.actions.cui.choropleth.dataModel.ChoroplethDataModel;
import org.orbisgis.view.toc.actions.cui.choropleth.gui.ChoroplethDistInputPanel;
import org.orbisgis.view.toc.actions.cui.choropleth.gui.ChoroplethRangeTabPanel;
import org.orbisgis.view.toc.actions.cui.choropleth.gui.ChoroplethSymbInputPanel;
import org.orbisgis.view.toc.actions.cui.choropleth.listener.DataChangeListener;
import org.orbisgis.view.toc.actions.cui.choropleth.listener.RangeAxisListener;
import org.orbisgis.view.toc.actions.cui.freqChart.FreqChart;
import org.orbisgis.view.toc.actions.cui.freqChart.dataModel.FreqChartDataModel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


/**
 * Choropleth class
 * @author maxence
 */
public class ChoroplethWizardPanel extends JPanel implements UIPanel {

    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(ChoroplethWizardPanel.class);
    /** I18n */
    private final static I18n I18N = I18nFactory.getI18n(ChoroplethWizardPanel.class);
    
    private ChoroplethDataModel choroplethDataModel;
    private FreqChartDataModel freqChartDataModel;
    private FreqChart freqChart;

    /**
     * Choropleth constructor
     * @param layer the display panel
     */
    public ChoroplethWizardPanel(ILayer layer) {
        initChoropleth(layer);
    }

    /**
     * initChoropleth init the choropleth
     * @param layer the display panel
     */
    private void initChoropleth(ILayer layer) {

        choroplethDataModel = new ChoroplethDataModel(layer);

        List<String> fields = choroplethDataModel.getFields();
        choroplethDataModel.setField(fields.get(0));
        double[] data = choroplethDataModel.getData();

        freqChartDataModel = new FreqChartDataModel(data);

        freqChart = new FreqChart(freqChartDataModel);

        freqChart.getPanel();

        ChoroplethRangeTabPanel choroplethRangeTabPanel = new ChoroplethRangeTabPanel(freqChartDataModel, freqChart);

        ChoroplethDistInputPanel dist = new ChoroplethDistInputPanel(freqChartDataModel, choroplethDataModel, freqChart, choroplethRangeTabPanel);

        ChoroplethSymbInputPanel symb = new ChoroplethSymbInputPanel(freqChartDataModel, freqChart, choroplethRangeTabPanel);

        freqChart.addAxisListener(new RangeAxisListener(freqChart,
                freqChartDataModel));
        freqChart.addDataListener(new DataChangeListener(dist));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(I18N.tr("Distribution"), dist);
        tabbedPane.addTab(I18N.tr("Symbology"), symb);
        tabbedPane.setPreferredSize(new Dimension(500,500));

        this.add(tabbedPane);

    }

    @Override
    public URL getIconURL() {
        return UIFactory.getDefaultIcon();
    }

    @Override
    public String getTitle() {
        return "Choropleth Wizard";
    }

    @Override
    public String initialize() {
        LOGGER.info(I18N.tr("***initialize***"));
        Container parent = this.getParent();
        if (parent != null) {
        }
        return null;
    }

    @Override
    public String postProcess() {
        return null;
    }

    @Override
    public String validateInput() {
        return null;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getInfoText() {
        return UIFactory.getDefaultOkMessage();
    }

    /*
     * Is called after the panel has been closed (and validated)
     * This method return a new se:Rule based on the wizard values
     */
    public Rule getRule() throws DriverException {
        Rule r = new Rule();
        r.setName("Choropleth");
        r.getCompositeSymbolizer().addSymbolizer(this.draw());
        return r;
    }

    /**
     * Is called by the getRule() method
     * Creates an AreaSymbolizer with ranges and colors
     * @return AreaSymbolizer
     */
    private AreaSymbolizer draw() {
        AreaSymbolizer as = new AreaSymbolizer();
        Categorize2Color choropleth = null;

        Range[] ranges = freqChartDataModel.getRange();
        choropleth = new Categorize2Color(new ColorLiteral(freqChartDataModel.getColorInit().get(0)), new ColorLiteral(freqChartDataModel.getColorInit().get(1)), new RealAttribute(choroplethDataModel.getField()));
        for (int i = 1; i <= ranges.length; i++) {
            choropleth.addClass(new RealLiteral(ranges[i - 1].getMinRange()), new ColorLiteral(freqChartDataModel.getColor().get(i - 1)));
        }

        SolidFill choroplethFill = new SolidFill();
        choroplethFill.setColor(choropleth);
        choroplethFill.setOpacity(new RealLiteral(freqChartDataModel.getOpacity()));
        as.setStroke(freqChartDataModel.getStroke());
        as.setFill(choroplethFill);
        return as;
    }
}

