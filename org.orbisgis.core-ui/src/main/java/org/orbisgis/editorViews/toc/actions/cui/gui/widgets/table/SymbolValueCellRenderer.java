package org.orbisgis.editorViews.toc.actions.cui.gui.widgets.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.orbisgis.renderer.legend.Symbol;

public class SymbolValueCellRenderer implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component obj = null;

		ButtonCanvas objB = new ButtonCanvas();

		objB.setLegend((Symbol) value, objB.getConstraint((Symbol) value));

		obj = objB;

		return obj;
	}

}
