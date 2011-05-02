/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.net.URL;
import javax.swing.JPanel;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;

/**
 *
 * @author maxence, sennj, mairep
 */
public class ChoroplethWizardPanel extends JPanel implements UIPanel {

    private ChoroplethDatas choroDatas;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private ILayer layer;

    /*
     * Create a Choropleth wizard panel
     * @param layer the layer to create a choropleth for
     */
    public ChoroplethWizardPanel(ILayer layer) throws DriverException {
        super();

        this.layer = layer;
        choroDatas = new ChoroplethDatas(layer);

        topPanel = new JPanel();
        ChoroplethInputPanel inputPanel = new ChoroplethInputPanel(this, layer, choroDatas);
        topPanel.add(inputPanel);

        centerPanel = new JPanel();
        ChoroplethRangeTabPanel rangeTabPanel = new ChoroplethRangeTabPanel(choroDatas);
        centerPanel.add(rangeTabPanel);

        bottomPanel = new JPanel();
        ChoroplethChartPanel chartPanel = new ChoroplethChartPanel(choroDatas,"","","");
        bottomPanel.add(chartPanel);

        ChoroDatasChangedListener datasChangedListener = new ChoroDatasChangedListener(choroDatas, rangeTabPanel, chartPanel);
        choroDatas.addDataChangedListener(datasChangedListener);

        JPanel wisardPanel = new JPanel();
        wisardPanel.setLayout((new BorderLayout()));

        wisardPanel.add(topPanel, BorderLayout.NORTH);
        wisardPanel.add(centerPanel, BorderLayout.CENTER);
        wisardPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.add(wisardPanel);
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

    /**
     * Is called by the getRule() method
     * Creates an AreaSymbolizer with ranges and colors
     * @return AreaSymbolizer
     */
    private AreaSymbolizer draw() {
        AreaSymbolizer as = new AreaSymbolizer();
        Categorize2Color choropleth = null;
        
        Range[] ranges = choroDatas.getRange();
        choropleth = new Categorize2Color(new ColorLiteral(choroDatas.getClassColor(0)), new ColorLiteral(choroDatas.getClassColor(choroDatas.getClassesColors().length - 1)), choroDatas.getField());
        for (int i = 1; i <= ranges.length; i++) {
            choropleth.addClass(new RealLiteral(ranges[i - 1].getMinRange()), new ColorLiteral(choroDatas.getClassColor(i - 1)));
        }

        SolidFill choroplethFill = new SolidFill();
        choroplethFill.setColor(choropleth);
        as.setFill(choroplethFill);
        return as;
    }

    /*
     * Is called after the panel has been closed (and validated)
     * This method return a new se:Rule based on the wizard values
     */
    public Rule getRule() throws DriverException {
        Rule r = new Rule();
        r.setName("Choropleth (" + choroDatas.getField().getColumnName() + ")");
        r.getCompositeSymbolizer().addSymbolizer(this.draw());
        return r;
    }
}
