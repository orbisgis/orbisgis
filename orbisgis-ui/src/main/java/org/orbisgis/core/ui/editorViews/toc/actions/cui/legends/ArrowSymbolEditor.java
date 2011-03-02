package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.core.renderer.symbol.ArrowSymbol;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.ui.components.preview.JNumericSpinner;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;

public class ArrowSymbolEditor extends StandardSymbolEditor implements
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
