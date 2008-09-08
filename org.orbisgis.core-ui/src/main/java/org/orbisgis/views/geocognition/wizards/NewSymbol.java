package org.orbisgis.views.geocognition.wizards;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.Icon;

import org.orbisgis.Services;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.images.IconLoader;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolManager;
import org.orbisgis.ui.sif.ChoosePanel;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.views.geocognition.wizard.INewGeocognitionElement;
import org.sif.UIFactory;

public class NewSymbol implements INewGeocognitionElement {

	private Symbol symbol;

	@Override
	public GeocognitionElementFactory getFactory() {
		return new GeocognitionSymbolFactory();
	}

	@Override
	public void runWizard() {
		SymbolManager symbolManager = Services.getService(SymbolManager.class);
		ArrayList<Symbol> availableSymbols = symbolManager
				.getAvailableSymbols();
		String[] names = new String[availableSymbols.size()];
		String[] ids = new String[availableSymbols.size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = availableSymbols.get(i).getId();
			names[i] = availableSymbols.get(i).getClassName();
		}
		ChoosePanel cp = new ChoosePanel("Select the legend type", names, ids);
		if (UIFactory.showDialog(cp)) {
			Symbol symbol = symbolManager.createSymbol(ids[cp
					.getSelectedIndex()]);
			this.symbol = symbol;
		} else {
			this.symbol = null;
		}
	}

	@Override
	public String getName() {
		return "Symbol";
	}

	@Override
	public ElementRenderer getElementRenderer() {
		return new ElementRenderer() {

			@Override
			public Icon getIcon(String contentTypeId,
					Map<String, String> properties) {
				return getDefaultIcon(contentTypeId);
			}

			@Override
			public Icon getDefaultIcon(String contentTypeId) {
				if (getFactory().acceptContentTypeId(contentTypeId)) {
					return IconLoader.getIcon("point.png");
				} else {
					return null;
				}
			}

		};
	}

	@Override
	public Object getElement(int index) {
		return symbol;
	}

	@Override
	public int getElementCount() {
		return (symbol != null) ? 1 : 0;
	}

	@Override
	public String getFixedName(int index) {
		return null;
	}

	@Override
	public boolean isUniqueIdRequired(int index) {
		return false;
	}

	@Override
	public String getBaseName() {
		return "Symbol";
	}

}
