package org.orbisgis.geoview.cui.gui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.opengis.sld.LineSymbol;
import org.orbisgis.renderer.RenderPermission;
import org.orbisgis.renderer.legend.CircleSymbol;
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
		this.setSize(100, 50);
	}

	@Override
	 public void paintComponent(Graphics g) {
		GeometryFactory gf = new GeometryFactory();
		Geometry geom = null;

		switch (constraint) {
			case GeometryConstraint.LINESTRING:
			case GeometryConstraint.MULTI_LINESTRING:
				geom = gf.createLineString(new Coordinate[] {
						 new Coordinate(20, 10), new Coordinate(40, 40),
						 new Coordinate(60, 10) , new Coordinate(80, 40)});

				break;
			case GeometryConstraint.POINT:
			case GeometryConstraint.MULTI_POINT:
				geom = gf.createPoint(new Coordinate(25, 25));

				break;
			case GeometryConstraint.POLYGON:
			case GeometryConstraint.MULTI_POLYGON:
				Coordinate[] coords = {new Coordinate(15,15), new Coordinate(75,15), new Coordinate(75, 35), new Coordinate(15,35), new Coordinate(15,15)};
				CoordinateArraySequence seq = new CoordinateArraySequence( coords );
				geom = gf.createPolygon(new LinearRing( seq, gf), null);
				//geom = gf.createPolygon

				break;
			case GeometryConstraint.MIXED:
				SymbolComposite comp = (SymbolComposite) s;
				int numberOfSymbols = comp.getSymbolCount();
				Geometry[] geoms = new Geometry[numberOfSymbols];
				for (int i=0; i<numberOfSymbols; i++){
					s = comp.getSymbol(i);
					if (s instanceof LineSymbol) {
						geoms[i] = gf.createLineString(new Coordinate[] {
								 new Coordinate(20, 10), new Coordinate(40, 40),
								 new Coordinate(60, 10) , new Coordinate(80, 40)});
					}

					if (s instanceof CircleSymbol) {
						geoms[i] = gf.createPoint(new Coordinate(25, 25));
					}

					if (s instanceof PolygonSymbol) {
						Coordinate[] coordsP = {new Coordinate(40,10), new Coordinate(90,5), new Coordinate(90, 30), new Coordinate(40,30), new Coordinate(40,20)};
						CoordinateArraySequence seqP = new CoordinateArraySequence( coordsP );
						geoms[i] = gf.createPolygon(new LinearRing( seqP, gf), null);
					}

				}
				geom = gf.createGeometryCollection(geoms);
				break;

		}



		 try {
			 if (isSelected){
				 g.setColor(Color.BLUE);
				 g.fillRect(0, 0, 100, 100);
			 }
			 s.draw((Graphics2D) g, geom, new AffineTransform(), new RenderPermission() {

																	 public boolean canDraw(Envelope env) {
																		 return true;
																	 }

																 });
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

	public void setSelected(boolean selected){
		isSelected=selected;
	}

	public static void main(String[] args) {
		JFrame frm = new JFrame();
		frm.getContentPane().add(new Canvas());
		frm.setSize(new Dimension(100, 100));
		frm.setPreferredSize(new Dimension(100, 100));
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setVisible(true);
	}
}
