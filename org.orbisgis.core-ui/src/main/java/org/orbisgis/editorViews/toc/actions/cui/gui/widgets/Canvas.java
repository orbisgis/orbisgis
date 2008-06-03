package org.orbisgis.editorViews.toc.actions.cui.gui.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.renderer.RenderPermission;
import org.orbisgis.renderer.legend.CircleSymbol;
import org.orbisgis.renderer.legend.LineSymbol;
import org.orbisgis.renderer.legend.NullSymbol;
import org.orbisgis.renderer.legend.PolygonSymbol;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolComposite;
import org.orbisgis.renderer.legend.SymbolFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class Canvas extends JPanel {

	Symbol s;
	int constraint;
	boolean isSelected=false;

	public Canvas( ){
		super();
		s = SymbolFactory.createNullSymbol();
		constraint=GeometryConstraint.MIXED;
		this.setSize(126, 70);
	}

	@Override
	 public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(2, 2, 123, 67);

		GeometryFactory gf = new GeometryFactory();
		Geometry geom = null;
		//constraint=getConstraint(s);

		int constr = getConstraintForCanvas(s);
		
		try {
			Stroke st = new BasicStroke();
			 if (isSelected){
				 g.setColor(Color.BLUE);
				 st = ((Graphics2D)g).getStroke();
				 ((Graphics2D)g).setStroke(new BasicStroke(new Float(2.0)));
			 }else{
				 g.setColor(Color.GRAY);
			 }
			 g.drawRect(1, 1, 124, 68); //Painting a Rectangle for the presentation and selection

			 ((Graphics2D)g).setStroke(st);

			switch (constr) {
				case GeometryConstraint.LINESTRING:
				case GeometryConstraint.MULTI_LINESTRING:
					geom = gf.createLineString(new Coordinate[] {
							 new Coordinate(20, 30), new Coordinate(40, 60),
							 new Coordinate(60, 30) , new Coordinate(80, 60)});

					s.draw((Graphics2D) g, geom, new AffineTransform(), new RenderPermission() {

						 public boolean canDraw(Envelope env) {
							 return true;
						 }

					 });

					break;
				case GeometryConstraint.POINT:
				case GeometryConstraint.MULTI_POINT:
					geom = gf.createPoint(new Coordinate(60, 35));

					s.draw((Graphics2D) g, geom, new AffineTransform(), new RenderPermission() {

						 public boolean canDraw(Envelope env) {
							 return true;
						 }

					 });

					break;
				case GeometryConstraint.POLYGON:
				case GeometryConstraint.MULTI_POLYGON:
					Coordinate[] coords = {new Coordinate(15,15), new Coordinate(75,15), new Coordinate(75, 35), new Coordinate(15,35), new Coordinate(15,15)};
					CoordinateArraySequence seq = new CoordinateArraySequence( coords );
					geom = gf.createPolygon(new LinearRing( seq, gf), null);

					s.draw((Graphics2D) g, geom, new AffineTransform(), new RenderPermission() {

						 public boolean canDraw(Envelope env) {
							 return true;
						 }

					 });

					break;
				case GeometryConstraint.MIXED:
					if (!(s instanceof SymbolComposite)) {
						return;
					}
					SymbolComposite comp = (SymbolComposite) s;
					Symbol sym;
					int numberOfSymbols = comp.getSymbolCount();
					for (int i=0; i<numberOfSymbols; i++){
						sym = comp.getSymbol(i);
						if (sym instanceof LineSymbol) {
							geom = gf.createLineString(new Coordinate[] {
									 new Coordinate(30, 20), new Coordinate(50, 50),
									 new Coordinate(70, 20) , new Coordinate(90, 50)});

							sym.draw((Graphics2D) g, geom, new AffineTransform(), new RenderPermission() {

								 public boolean canDraw(Envelope env) {
									 return true;
								 }

							 });
						}

						if (sym instanceof CircleSymbol) {
							geom = gf.createPoint(new Coordinate(60, 35));

							sym.draw((Graphics2D) g, geom, new AffineTransform(), new RenderPermission() {

								 public boolean canDraw(Envelope env) {
									 return true;
								 }

							 });
						}

						if (sym instanceof PolygonSymbol) {
							Coordinate[] coordsP = {new Coordinate(30,25), new Coordinate(90,25), new Coordinate(90, 45), new Coordinate(30,45), new Coordinate(30,25)};
							CoordinateArraySequence seqP = new CoordinateArraySequence( coordsP );
							geom = gf.createPolygon(new LinearRing( seqP, gf), null);

							sym.draw((Graphics2D) g, geom, new AffineTransform(), new RenderPermission() {

								 public boolean canDraw(Envelope env) {
									 return true;
								 }

							 });
						}

					}
					break;

			}

		 } catch (DriverException e) {
			 ((Graphics2D)g).drawString("Cannot generate preview", 0, 0);
		 } catch (NullPointerException e){
			 ((Graphics2D)g).drawString("Cannot generate preview: ", 0, 0);
			 System.out.println(e.getMessage());
		 }
	}


	public void setLegend( Symbol sym, int constraint ){
		this.s = sym;
		this.constraint = constraint;
	}

	public int getConstraint( Symbol sym ){
		if (sym instanceof LineSymbol) {
			System.out.println("isLine");
			return GeometryConstraint.LINESTRING;
		}
		if (sym instanceof CircleSymbol) {
			System.out.println("isCircle");
			return GeometryConstraint.POINT;
		}
		if (sym instanceof PolygonSymbol) {
			System.out.println("isPoly");
			return GeometryConstraint.POLYGON;
		}
		if (sym instanceof SymbolComposite) {
			SymbolComposite comp = (SymbolComposite) sym;
			int symbolCount = comp.getSymbolCount();
			
			boolean allEquals=true;
			int lastConstraint=0;
			int actualConstraint=0;
			
			if (symbolCount>0){
				System.out.println("have more than 0 simbols");
				lastConstraint=getConstraint(comp.getSymbol(0));
				
				if (symbolCount!=1){
					for (int i=1; i<symbolCount; i++){
						actualConstraint=getConstraint(comp.getSymbol(i));
						System.out.println("last: "+lastConstraint+"-- new: "+actualConstraint);
						
						if (lastConstraint!=actualConstraint){
							allEquals=false;
							System.out.println("not all equals");
							break;
						}
						
						lastConstraint=actualConstraint;
						
					}
				}
				
				if (allEquals==true){
					System.out.println("all equals");
					return lastConstraint;
				}
				else
					return GeometryConstraint.MIXED;
				
			}
			
			return GeometryConstraint.MIXED;
			
			
		}
		return GeometryConstraint.MIXED;
	}
	
	public int getConstraintForCanvas( Symbol sym ){
		if (sym instanceof LineSymbol) {
			return GeometryConstraint.LINESTRING;
		}
		if (sym instanceof CircleSymbol) {
			return GeometryConstraint.POINT;
		}
		if (sym instanceof PolygonSymbol) {
			return GeometryConstraint.POLYGON;
		}
		if (sym instanceof SymbolComposite) {
			return GeometryConstraint.MIXED;
		}
		return GeometryConstraint.MIXED;
	}

	public void setSelected(boolean selected){
		isSelected=selected;
	}

	public Symbol getSymbol(){
		return s;
	}
}