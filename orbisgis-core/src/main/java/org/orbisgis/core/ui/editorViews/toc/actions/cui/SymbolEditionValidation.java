package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import org.orbisgis.core.renderer.symbol.Symbol;

public interface SymbolEditionValidation {

	/**
	 * Return null if the symbol being edited is valid and the dialog can be
	 * closed. Otherwise return a message describing the condition for the
	 * dialog to valid
	 *
	 * @return
	 */
	String isValid(Symbol symbol);
}
