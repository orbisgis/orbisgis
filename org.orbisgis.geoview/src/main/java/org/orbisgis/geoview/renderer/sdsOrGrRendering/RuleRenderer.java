package org.orbisgis.geoview.renderer.sdsOrGrRendering;

import java.awt.Graphics2D;

import org.gdms.spatial.SpatialDataSourceDecorator;
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
		
		if (rule.getFilter()!=null){
			
			/**
			 * todo: filter the datasource here
			 * 
			 * FilterRenderer.paint maybe
			 */
			 
			
		}
		else {
			SymbolizerRenderer.paint(graphics, sds, symbolizer, mapControl);
			
		}
		
		
		
	}
}
