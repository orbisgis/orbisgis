/*
 * ImageCellRenderer.java
 *
 * Created on 24 de abril de 2008, 14:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.orbisgis.editorViews.toc.actions.cui.gui.widgets;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.renderer.legend.CircleSymbol;
import org.orbisgis.renderer.legend.LineSymbol;
import org.orbisgis.renderer.legend.NullSymbol;
import org.orbisgis.renderer.legend.PolygonSymbol;
import org.orbisgis.renderer.legend.Symbol;
import org.orbisgis.renderer.legend.SymbolFactory;

/**
 *
 * @author david
 */
public class ImageCellRenderer implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable jTable,
			Object object, boolean isSelected, boolean b0, int i, int i0) {
		Canvas can = new Canvas();

		Symbol sym;

		if (object != null)
			sym = (Symbol) object;
		else
			sym = SymbolFactory.createNullSymbol();

		int constraint = GeometryConstraint.MIXED;
		if (sym instanceof LineSymbol) {
			constraint = GeometryConstraint.LINESTRING;
		}
		if ((sym instanceof CircleSymbol) || (sym instanceof NullSymbol)) {
			constraint = GeometryConstraint.POINT;
		}
		if (sym instanceof PolygonSymbol) {
			constraint = GeometryConstraint.POLYGON;
		}

		can.setLegend(sym, constraint);
		can.setSelected(isSelected);

		return can;
	}

}
