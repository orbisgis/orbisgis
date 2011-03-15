/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.classification.RangeMethod;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;

/**
 *
 * @author maxence
 */
public class ChoroplethWizardPanel extends JPanel implements UIPanel {

	private ILayer layer;
        private JSE_ChoroplethDatas ChoroDatas;
        private JButton btnApply;

	/*
	 * Create a Choropleth wizard panel
	 * @param layer the layer to create a choropleth for
	 */
	public ChoroplethWizardPanel(ILayer layer) throws DriverException {
            super();

            btnApply = new JButton("Apply");
            btnApply.setMargin(new Insets(0, 0, 0, 0));
            btnApply.addActionListener(new btnApplyListener(this));
            this.add(btnApply);
            
            ChoroDatas = new JSE_ChoroplethDatas(layer);
            ChoroDatas.readData();
            Range[] ranges = ChoroDatas.getRange();
            Value value = ChoroDatas.getValue();
            this.add(new JSE_ChoroplethChartPanel(ranges,value));
            this.layer = layer;
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

        private AreaSymbolizer draw(){
            Categorize2Color choropleth = new Categorize2Color(new ColorLiteral(ChoroDatas.getBeginColor()), new ColorLiteral(ChoroDatas.getEndColor()), ChoroDatas.getField());
            Range[] ranges = ChoroDatas.getRange();
            for(int i= 0; i< ranges.length ; i++){
                choropleth.addClass(new RealLiteral(ranges[i].getMaxRange()), new ColorLiteral(ChoroDatas.getClassColor(i)));
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
        private class btnApplyListener implements ActionListener {

		private final ChoroplethWizardPanel metaPanel;

		public btnApplyListener(ChoroplethWizardPanel metaPanel) {
			super();
			this.metaPanel = metaPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
                    try{
                        Rule r = this.metaPanel.getRule();
                        if (r != null) {
                            // Add the rule in the current featureTypeStyle
                            layer.getFeatureTypeStyle().clear();
                            layer.getFeatureTypeStyle().addRule(r);
                            // And finally redraw the map
                            layer.fireStyleChangedPublic();
                        }
                    }catch(DriverException ex){
                        Logger.getLogger(ChoroplethWizardPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
		}
	}
}
