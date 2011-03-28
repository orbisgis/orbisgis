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
 * @author maxence
 */
public class ChoroplethWizardPanel extends JPanel implements UIPanel {


    private JSE_ChoroplethDatas ChoroDatas;

    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;

    /*
     * Create a Choropleth wizard panel
     * @param layer the layer to create a choropleth for
     */
    public ChoroplethWizardPanel(ILayer layer) throws DriverException {
        super();
        this.setLayout(new BorderLayout());
        this.setSize(800, 600);

        ChoroDatas = new JSE_ChoroplethDatas(layer);
        ChoroDatas.readData();
      
        topPanel=new JPanel();
        JSE_ChoroplethInputPanel inputPanel = new JSE_ChoroplethInputPanel(this, layer, ChoroDatas);
        topPanel.add(inputPanel);

        centerPanel=new JPanel();
        JSE_ChoroplethRangeTabPanel rangeTabPanel = new JSE_ChoroplethRangeTabPanel(ChoroDatas);
        centerPanel.add(rangeTabPanel);

        bottomPanel=new JPanel();
        JSE_ChoroplethChartPanel chartPanel = new JSE_ChoroplethChartPanel(ChoroDatas);
        bottomPanel.add(chartPanel);

        JSE_ChoroDatasChangedListener datasChangedListener = new JSE_ChoroDatasChangedListener(ChoroDatas, rangeTabPanel, chartPanel);
        ChoroDatas.addDataChangedListener(datasChangedListener);

        JPanel wisardPanel = new JPanel();
        wisardPanel.setLayout((new BorderLayout()));

        wisardPanel.add(topPanel,BorderLayout.NORTH);
        wisardPanel.add(centerPanel,BorderLayout.CENTER);
        wisardPanel.add(bottomPanel,BorderLayout.SOUTH);

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

    private AreaSymbolizer draw() {
        Categorize2Color choropleth = new Categorize2Color(new ColorLiteral(ChoroDatas.getBeginColor()), new ColorLiteral(ChoroDatas.getEndColor()), ChoroDatas.getField());
        Range[] ranges = ChoroDatas.getRange();
        for (int i = 0; i < ranges.length; i++) {
            choropleth.addClass(new RealLiteral(ranges[i].getMaxRange()+(1.0f/Integer.MAX_VALUE)), new ColorLiteral(ChoroDatas.getClassColor(i)));
        }
        SolidFill choroplethFill = new SolidFill();
        choroplethFill.setColor(choropleth);
        AreaSymbolizer as = new AreaSymbolizer();
        as.setFill(choroplethFill);
        return as;
    }

    /*
     * Is called after the panel has been closed (and validated)
     * This method return a new se:Rule based on the wizard values
     */
    public Rule getRule() throws DriverException {
        Rule r = new Rule();
        r.setName("Choropleth (" + ChoroDatas.getField().getColumnName() + ")");
        r.getCompositeSymbolizer().addSymbolizer(this.draw());
        return r;
        /*Metadata metadata = layer.getDataSource().getMetadata();

        // Quick (and hugly) step to fetch the first numeric attribute
        String retainedFiledName = null;
        for (int i = 0; i < metadata.getFieldCount(); i++) {
        int currentType = metadata.getFieldType(i).getTypeCode();

        if (currentType == 4 || (currentType >= 16 && currentType <= 256)) {
        retainedFiledName = metadata.getFieldName(i);
        break;
        }
        }


        if (retainedFiledName != null) {
        try {
        RealAttribute field = new RealAttribute(retainedFiledName);
        RangeMethod rangesHelper = new RangeMethod(layer.getDataSource(), field, 4);

        rangesHelper.disecMean();
        Range[] ranges = rangesHelper.getRanges();
        rangesHelper.getIntervals();

        Categorize2Color choropleth = new Categorize2Color(new ColorLiteral("#dd0000"), new ColorLiteral("#FFFF00"), field);
        choropleth.addClass(new RealLiteral(ranges[0].getMaxRange()), new ColorLiteral("#aa0000"));
        choropleth.addClass(new RealLiteral(ranges[1].getMaxRange()), new ColorLiteral("#770000"));
        choropleth.addClass(new RealLiteral(ranges[2].getMaxRange()), new ColorLiteral("#330000"));

        SolidFill choroplethFill = new SolidFill();
        choroplethFill.setColor(choropleth);
        AreaSymbolizer as = new AreaSymbolizer();
        as.setFill(choroplethFill);
        Rule r = new Rule();
        r.setName("Choropleth (" + retainedFiledName + ")");
        r.getCompositeSymbolizer().addSymbolizer(as);
        return r;
        } catch (ParameterException ex) {
        Logger.getLogger(ChoroplethWizardPanel.class.getName()).log(Level.SEVERE, null, ex);
        return null;
        }
        }*/
    }
}
