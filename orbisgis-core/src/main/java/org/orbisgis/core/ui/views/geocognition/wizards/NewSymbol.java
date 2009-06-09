package org.orbisgis.core.ui.views.geocognition.wizards;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolManager;
import org.orbisgis.core.ui.components.sif.ChoosePanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.SymbolBuilder;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.StandardSymbolEditor;
import org.orbisgis.core.ui.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.core.ui.views.geocognition.wizard.INewGeocognitionElement;
import org.orbisgis.core.ui.views.geocognition.wizards.symbol.GeoCognitionSymbolBuilder;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.images.IconLoader;
import org.orbisgis.sif.UIFactory;

public class NewSymbol implements INewGeocognitionElement {

	private Symbol symbol;

	@Override
	public GeocognitionElementFactory[] getFactory() {
		return new GeocognitionElementFactory[] { new GeocognitionSymbolFactory() };
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

			GeoCognitionSymbolBuilder symbolBuilder = new GeoCognitionSymbolBuilder();
			symbolBuilder.setSymbol(symbol);

			if(UIFactory.showDialog(symbolBuilder)){
				this.symbol = symbolBuilder.getSymbolComposite();
			}

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
				if (getFactory()[0].acceptContentTypeId(contentTypeId)) {
					return IconLoader.getIcon("palette.png");
				} else {
					return null;
				}
			}

			@Override
			public String getTooltip(GeocognitionElement element) {
				return null;
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
	public String getBaseName(int elementIndex) {
		return "Symbol";
	}

}
