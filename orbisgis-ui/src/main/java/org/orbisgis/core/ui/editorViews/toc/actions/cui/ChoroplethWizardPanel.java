/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Component;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.gdms.data.schema.Metadata;
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

	/*
	 * Create a Choropleth wizard panel
	 * @param layer the layer to create a choropleth for
	 */
	public ChoroplethWizardPanel(ILayer layer) {
		super();
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

	/*
	 * Is called after the panel has been closed (and validated)
	 * This method return a new se:Rule based on the wizard values
	 */
	public Rule getRule() throws DriverException {


		Metadata metadata = layer.getDataSource().getMetadata();

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

				// Create a 4-class red-progression choropleth
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
		}
		return null;
	}
}
