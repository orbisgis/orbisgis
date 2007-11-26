package org.orbisgis.geoview.renderer.style;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.RectangularShape;

import org.orbisgis.geoview.renderer.liteShape.LiteShape;

public class PointStyle implements Style{

	
	
	private static String stringFillColor = "#FFFFFF";
	private static String stringLineColor = "#FFFF00";
	private int fillOpacity;
	private String pointShape;
	private int pointSize;
	private Color fillColor;
	static String defaultShape = "circle";
	static int size = 4;
	
	
	public PointStyle() {
		this(defaultShape,size,  stringFillColor,stringLineColor,1 );
		
	}

	public PointStyle(int size){
		this(defaultShape,size,  stringFillColor,stringLineColor,1 );
		
	}
	
	public  PointStyle (String stringFillColor, String stringLineColor) {
		this(defaultShape,size,  stringFillColor,stringLineColor,1 );
		
	}
	
	
	
	public  PointStyle (String pointShape, String stringFillColor, String stringLineColor) {
		this(pointShape,size,  stringFillColor,stringLineColor,1 );
		
	}
	
	
	public  PointStyle (String pointShape, int pointSize, String stringFillColor, String stringLineColor, int fillOpacity) {
		this.pointShape =pointShape;
		this.pointSize= pointSize;
		this.stringFillColor = stringFillColor;
		this.stringLineColor = stringLineColor;
		this.fillOpacity =fillOpacity;
	}
	
	

	public Shape getDefaultShape(double x, double y){
		
		double r = (getSize() / 2d);
			 
		
		return new Ellipse2D.Double(x, y, r, r);
		
		
	}
	
	public Shape getDefaultShape(double x, double y, float size){
		
		double r = (size / 2d);
			 
		
		return new Ellipse2D.Double(x, y, r, r);
		
		
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

	
	 public void setSize(int size) {
	        this.size = size;
	    }

	    public int getSize() {
	        return size;
	    }

	
}
