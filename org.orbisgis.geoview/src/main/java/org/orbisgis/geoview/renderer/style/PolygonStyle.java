package org.orbisgis.geoview.renderer.style;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;

public class PolygonStyle implements Style{

	
	
	private String stringFillColor;
	private String stringLineColor;
	private int fillOpacity;

	public  PolygonStyle (String stringFillColor, String stringLineColor) {
		this(stringFillColor,stringLineColor,1 );
		
	}
	
	
	public  PolygonStyle (String stringFillColor, String stringLineColor, int fillOpacity) {
		this.stringFillColor = stringFillColor;
		this.stringLineColor = stringLineColor;
		this.fillOpacity =fillOpacity;
	}
	
	public Color getFillColor(){
		
		
			if (stringFillColor.length()>0){
				return Color.decode(stringFillColor);
			}
		
		
		
		return null;	
		
	}
	
	
	public Color getLineColor() {
			
			if (stringLineColor.length()>0){
				return Color.decode(stringLineColor);
			}
		
		
		
		return null;
	}
	

	
	public AlphaComposite getAlphaComposite(){
		
		
			if ((fillOpacity > 0)&& (fillOpacity < 1)){
				return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fillOpacity);
			}
			
		
		
		return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
		
		
	}
	
	public Color getDefaultLineColor() {
		
		return Color.BLACK;
	}
	
	public BasicStroke getBasicStroke() {
		return null;
	}


	


	
}
