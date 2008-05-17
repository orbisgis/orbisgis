package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.LegendFactory;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolFactory;
import org.orbisgis.renderer.legend.UniqueSymbolLegend;

public class MainPruebasLegend {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UniqueSymbolLegend leg = LegendFactory.createUniqueSymbolLegend();
		leg.setName("Unique symbol legend");
		Symbol sym = SymbolFactory.createLineSymbol(Color.BLACK,
				new BasicStroke(new Float(2.0)));
		leg.setSymbol(sym);

		UniqueSymbolLegend leg2 = LegendFactory.createUniqueSymbolLegend();
		leg2.setName("Unique symbol legend 2");
		Symbol sym2 = SymbolFactory.createLineSymbol(Color.GREEN,
				new BasicStroke(new Float(4.0)));
		leg2.setSymbol(sym2);

		Legend[] legs = { leg, leg2 };
		JPanelLegendList ven = new JPanelLegendList(
				GeometryConstraint.LINESTRING, legs);
		ven.setPreferredSize(new Dimension(905, 500));

		JFrame fra = new JFrame();
		fra.add(ven);
		fra.setSize(new Dimension(905, 500));

		fra.pack();
		fra.setVisible(true);
		fra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
