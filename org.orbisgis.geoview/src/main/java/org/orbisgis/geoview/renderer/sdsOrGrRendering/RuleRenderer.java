package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;

import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.sql.evaluator.Evaluator;
import org.gdms.sql.instruction.IncompatibleTypesException;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.renderer.style.sld.Rule;
import org.orbisgis.geoview.renderer.style.sld.Symbolizer;

import com.ximpleware.NavException;
import com.ximpleware.xpath.XPathEvalException;
import com.ximpleware.xpath.XPathParseException;

public class RuleRenderer {


	private MapControl mapControl;

	public RuleRenderer(final MapControl mapControl) {
		this.mapControl = mapControl;
	}

	public static void paint(Graphics2D graphics,final SpatialDataSourceDecorator sds, Rule rule, MapControl mapControl) {


		Symbolizer symbolizer = null;
		try {
			symbolizer = rule.getSymbolizer();


		} catch (XPathParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathEvalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NavException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (rule.getFilterCount() > 0){

				/**
				 * todo: filter the datasource here
				 *
				 * FilterRenderer.paint maybe
				 */			
				

				DataSource filtered = Evaluator.filter(sds, rule.getFilter(0).getExpression());
				filtered.open();
				
				SymbolizerRenderer.paint(graphics, new SpatialDataSourceDecorator(filtered), symbolizer, mapControl);
				
				filtered.cancel();

			}
			else {
				SymbolizerRenderer.paint(graphics, sds, symbolizer, mapControl);

			}
		} catch (XPathParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathEvalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NavException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncompatibleTypesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
}
