package org.orbisgis.geoview.renderer.style;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.RectangularShape;

import org.orbisgis.geoview.renderer.liteShape.LiteShape;

public class LineStyle implements Style{

	
	
	
	private String stringLineColor;
	private int fillOpacity;
	private BasicStroke stroke;
	static String defaultShape = "circle";
	static int size = 1;
	
	
	public  LineStyle (String stringLineColor) {
		this(stringLineColor,size );
		
	}
	
	
		
	
	public  LineStyle (String stringLineColor, int size) {
		
		this.stringLineColor = stringLineColor;
		this.size =size;
		stroke = new BasicStroke(size,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	}
		
	
		
	public Color getLineColor() {
			
			if (stringLineColor.length()>0){
				return Color.decode(stringLineColor);
			}
		
		
		return null;
	}
	
	
	public Color getDefaultLineColor() {
		
		return Color.GREEN;
	}
	
	public BasicStroke getBasicStroke() {
		return stroke;
	}

	
	 public void setSize(int size) {
	        this.size = size;
	    }

	    public int getSize() {
	        return size;
	    }


		public Color getFillColor() {
			// TODO Auto-generated method stub
			return null;
		}

	
}
