package org.orbisgis.core.geocognition.symbology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.core.renderer.symbol.SymbolManager;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.geocognition.AbstractExtensionElement;
import org.orbisgis.core.geocognition.GeocognitionElementContentListener;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.GeocognitionExtensionElement;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.core.renderer.symbol.collection.persistence.Property;
import org.orbisgis.core.renderer.symbol.collection.persistence.SimpleSymbolType;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolCompositeType;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolList;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolType;
import org.orbisgis.progress.IProgressMonitor;

public class GeocognitionSymbol extends AbstractExtensionElement implements
		GeocognitionExtensionElement {

	private Symbol symbol;
	private Object revertStatus;

	public GeocognitionSymbol(Symbol symbol, GeocognitionElementFactory factory) {
		super(factory);
		this.symbol = symbol;
	}

	public GeocognitionSymbol(SymbolList sl, GeocognitionElementFactory factory) {
		super(factory);
		this.symbol = getSymbolFromXML((SymbolType) sl.getSymbol());
	}

	private Symbol getSymbolFromXML(SymbolType sim) {
		if (sim instanceof SimpleSymbolType) {
			SimpleSymbolType simpleSymbol = (SimpleSymbolType) sim;
			String id = simpleSymbol.getSymbolTypeId();
			SymbolManager sm = (SymbolManager) Services
					.getService(SymbolManager.class);
			Symbol ret = sm.createSymbol(id);
			if (ret == null) {
				return null;
			} else {
				List<Property> xmlProps = simpleSymbol.getProperty();
				HashMap<String, String> props = new HashMap<String, String>();
				for (Property property : xmlProps) {
					props.put(property.getName(), property.getValue());
				}
				ret.setPersistentProperties(props);
				return ret;
			}
		} else {
			SymbolCompositeType sc = (SymbolCompositeType) sim;
			ArrayList<Symbol> ret = new ArrayList<Symbol>();
			List<SymbolType> symbols = sc.getSymbol();
			for (int i = 0; i < symbols.size(); i++) {
				Symbol symbolFromXML = getSymbolFromXML(symbols.get(i));
				if (symbolFromXML != null) {
					ret.add(symbolFromXML);
				}
			}

			return SymbolFactory.createSymbolComposite(ret
					.toArray(new Symbol[0]));
		}
	}

	@Override
	public Object getJAXBObject() {
		Symbol symbol = (Symbol) getObject();
		SymbolType col = getXMLFromSymbol(symbol);
		SymbolList sl = new SymbolList();
		sl.setSymbol(col);
		return sl;
	}

	/**
	 * creates a jaxb symbol object
	 *
	 * @param com
	 *            the symbol composite
	 * @return Compositesymboltype
	 */
	private SymbolType getXMLFromSymbol(Symbol com) {
		if (com.acceptsChildren()) {
			SymbolCompositeType comp = new SymbolCompositeType();
			for (int i = 0; i < com.getSymbolCount(); i++) {
				SymbolType xmlSymbol = getXMLFromSymbol(com.getSymbol(i));
				if (xmlSymbol != null) {
					comp.getSymbol().add(xmlSymbol);
				}
			}

			return comp;
		} else {
			SimpleSymbolType ret = new SimpleSymbolType();
			ret.setSymbolTypeId(com.getId());
			Map<String, String> props = com.getPersistentProperties();
			Iterator<String> keys = props.keySet().iterator();
			while (keys.hasNext()) {
				String name = keys.next();
				String value = props.get(name);
				Property prop = new Property();
				prop.setName(name);
				prop.setValue(value);
				ret.getProperty().add(prop);
			}
			return ret;
		}
	}

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return symbol;
	}

	@Override
	public String getTypeId() {
		return GeocognitionSymbolFactory.ID;
	}

	@Override
	public void close(IProgressMonitor progressMonitor) {
		symbol.setPersistentProperties(getPersistentProperties(revertStatus));
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
		revertStatus = getJAXBObject();
	}

	@Override
	public void save() {
		revertStatus = getJAXBObject();
	}

	@Override
	public Object getRevertJAXBObject() {
		return revertStatus;
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void setElementListener(GeocognitionElementContentListener listener) {

	}

	@Override
	public void setJAXBObject(Object jaxbObject)
			throws IllegalArgumentException, GeocognitionException {
		Map<String, String> persistentProperties = getPersistentProperties(jaxbObject);
		symbol.setPersistentProperties(persistentProperties);
	}

	private Map<String, String> getPersistentProperties(Object jaxbObject) {
		GeocognitionSymbolFactory symbolFactory = (GeocognitionSymbolFactory) getFactory();
		Symbol symbol2 = (Symbol) symbolFactory.createElementFromXML(
				jaxbObject, getTypeId()).getObject();
		Map<String, String> persistentProperties = symbol2
				.getPersistentProperties();
		return persistentProperties;
	}

}
