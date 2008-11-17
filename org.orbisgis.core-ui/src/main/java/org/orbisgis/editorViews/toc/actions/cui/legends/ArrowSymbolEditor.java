package org.orbisgis.editorViews.toc.actions.cui.legends;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.editorViews.toc.actions.cui.legend.ISymbolEditor;
import org.orbisgis.renderer.symbol.ArrowSymbol;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.ui.preview.JNumericSpinner;

public class ArrowSymbolEditor extends ClassicSymbolEditor implements
		ISymbolEditor {

	private JNumericSpinner spnArrowLength;

	@Override
	public boolean accepts(Symbol symbol) {
		return symbol instanceof ArrowSymbol;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public ISymbolEditor newInstance() {
		return new ArrowSymbolEditor();
	}

	@Override
	protected JPanel getPnlSizeControls() {
		JPanel ret = super.getPnlSizeControls();
		spnArrowLength = getSpinner(1, Integer.MAX_VALUE);
		ret.add(spnArrowLength);

		return ret;
	}

	@Override
	protected JPanel getPnlSizeTexts() {
		JPanel ret = super.getPnlSizeTexts();
		ret.add(new JLabel("Arrow length"));

		return ret;
	}

	@Override
	protected void symbolChanged() {
		if (!ignoreEvents) {
			super.symbolChanged();
			ArrowSymbol arrowSymbol = (ArrowSymbol) symbol;
			arrowSymbol.setArrowLength((int) spnArrowLength.getValue());
		}
	}

	@Override
	public void setSymbol(Symbol symbol) {
		super.setSymbol(symbol);
		ArrowSymbol arrowSymbol = (ArrowSymbol) symbol;
		spnArrowLength.setValue(arrowSymbol.getArrowLength());
	}
}
