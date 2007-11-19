package org.orbisgis.geoview.renderer.style;

import java.awt.BasicStroke;
import java.awt.Color;

import org.opengis.go.display.style.ArrowStyle;
import org.opengis.go.display.style.DashArray;
import org.opengis.go.display.style.FillPattern;
import org.opengis.go.display.style.FillStyle;
import org.opengis.go.display.style.GraphicStyle;
import org.opengis.go.display.style.LineCap;
import org.opengis.go.display.style.LineJoin;
import org.opengis.go.display.style.LinePattern;
import org.opengis.go.display.style.LineStyle;
import org.opengis.go.display.style.Mark;
import org.opengis.go.display.style.event.GraphicStyleListener;
import org.orbisgis.pluginManager.VTD;

public class PointSymbolizer implements Symbolizer {

	/** to be complete
	 * 
	 *
	 */
	
	
	private VTD vtd;
	private String rootXpathQuery;

	/** SLD tags
	 * 
	 * <PointSymbolizer>
            <Graphic>
                 <Mark>
                     <WellKnownName>circle</WellKnownName>
                           <Fill>
                             <CssParameter name="fill">#ff5500</CssParameter>
                             </Fill>
                            </Mark>
                            <Size>20.0</Size>
                        </Graphic>
        </PointSymbolizer>
	 * 
	 */
	public PointSymbolizer(VTD vtd, String rootXpathQuery){
		
		this.vtd = vtd;
		this.rootXpathQuery = rootXpathQuery;
	}
	

	public Color getFillColor() {
		
		return null;
	}


	public Mark getMark() {
		
		return null;
	}
	

	public float getSize() {
		
		return 0;
	}

	public BasicStroke getBasicStroke(){
		return null;
		
	}

	
	
	
	

}
