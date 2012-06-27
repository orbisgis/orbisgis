/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.view.toc.actions.cui;

import java.awt.Component;
import java.awt.Container;
import java.net.URL;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
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
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.choropleth.dataModel.ChoroplethDataModel;
import org.orbisgis.core.ui.choropleth.gui.ChoroplethDistInputPanel;
import org.orbisgis.core.ui.choropleth.gui.ChoroplethRangeTabPanel;
import org.orbisgis.core.ui.choropleth.gui.ChoroplethSymbInputPanel;
import org.orbisgis.core.ui.choropleth.listener.DataChangeListener;
import org.orbisgis.core.ui.choropleth.listener.RangeAxisListener;
import org.orbisgis.core.ui.freqChart.FreqChart;
import org.orbisgis.core.ui.freqChart.dataModel.FreqChartDataModel;

/**
 * Choropleth class
 * @author sennj
 */
public class ChoroplethWizardPanel extends JPanel implements UIPanel {

    private ChoroplethDataModel choroplethDataModel;
    private FreqChartDataModel freqChartDataModel;

    /**
     * Choropleth constructor
     * @param layer
     */
    public ChoroplethWizardPanel(ILayer layer) {
        initChoropleth(layer);
    }

    /**
     * initChoropleth init the choropleth
     * @param layer
     */
    private void initChoropleth(ILayer layer) {

        choroplethDataModel = new ChoroplethDataModel(layer);

        List<String> fields = choroplethDataModel.getFields();
        choroplethDataModel.setField(fields.get(0));
        double[] data = choroplethDataModel.getData();

        freqChartDataModel = new FreqChartDataModel(fields, data);

        FreqChart freqChart = new FreqChart(freqChartDataModel);

        freqChart.getPanel();

        ChoroplethRangeTabPanel choroplethRangeTabPanel = new ChoroplethRangeTabPanel(freqChartDataModel, freqChart);

        ChoroplethDistInputPanel dist = new ChoroplethDistInputPanel(freqChartDataModel, choroplethDataModel, freqChart, choroplethRangeTabPanel);

        ChoroplethSymbInputPanel symb = new ChoroplethSymbInputPanel(freqChartDataModel, freqChart, choroplethRangeTabPanel);

        freqChart.addAxisListener(new RangeAxisListener(freqChart,
                freqChartDataModel));
        freqChart.addDataListener(new DataChangeListener(dist, freqChartDataModel));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Distribution", dist);
        tabbedPane.addTab("Symbology", symb);

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
        System.out.println("***initialize***");
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
        // Todo make sure the choropleth is valid !
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
        choropleth = new Categorize2Color(new ColorLiteral(freqChartDataModel.getColorInit()[0]), new ColorLiteral(freqChartDataModel.getColorInit()[1]), new RealAttribute(choroplethDataModel.getField()));
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
