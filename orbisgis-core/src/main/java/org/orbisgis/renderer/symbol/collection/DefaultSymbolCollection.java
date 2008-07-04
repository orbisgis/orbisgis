package org.orbisgis.renderer.symbol.collection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.orbisgis.IncompatibleVersionException;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;
import org.orbisgis.renderer.symbol.collection.persistence.Property;
import org.orbisgis.renderer.symbol.collection.persistence.SimpleSymbolType;
import org.orbisgis.renderer.symbol.collection.persistence.SymbolCompositeType;
import org.orbisgis.renderer.symbol.collection.persistence.SymbolList;
import org.orbisgis.renderer.symbol.collection.persistence.SymbolType;

public class DefaultSymbolCollection implements SymbolCollection {

	private File collectionFile;
	private ArrayList<Symbol> symbols = new ArrayList<Symbol>();

	public DefaultSymbolCollection(File collectionFile) {
		this.collectionFile = collectionFile;
	}

	/**
	 * @see org.orbisgis.renderer.symbol.collection.SymbolCollection#saveXML()
	 */
	public void saveXML() throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(
				"org.orbisgis.renderer.symbol.collection.persistence",
				DefaultSymbolCollection.class.getClassLoader());
		Marshaller m = jaxbContext.createMarshaller();

		BufferedOutputStream os = new BufferedOutputStream(
				new FileOutputStream(collectionFile));
		Symbol[] array = symbols.toArray(new Symbol[0]);
		Symbol symbolComposite = SymbolFactory.createSymbolComposite(array);
		SymbolType col = toXML(symbolComposite);
		SymbolList list = new SymbolList();
		list.setSymbolComposite((SymbolCompositeType) col);
		m.marshal(list, os);

		os.close();
	}

	/**
	 * creates a composite symbol of jaxb
	 *
	 * @param com
	 *            the symbol composite
	 * @return Compositesymboltype
	 */
	private SymbolType toXML(Symbol com) {

		if (com.acceptsChildren()) {
			SymbolCompositeType comp = new SymbolCompositeType();
			for (int i = 0; i < com.getSymbolCount(); i++) {
				SymbolType xmlSymbol = toXML(com.getSymbol(i));
				if (xmlSymbol != null) {
					comp.getSymbol().add(xmlSymbol);
				}
			}

			return comp;
		} else {
			if (com instanceof Symbol) {
				SimpleSymbolType ret = new SimpleSymbolType();
				ret.setSymbolTypeId(com.getId());
				ret.setVersion(com.getVersion());
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
			} else {
				return null;
			}
		}
	}

	/**
	 * @see org.orbisgis.renderer.symbol.collection.SymbolCollection#loadCollection()
	 */
	public void loadCollection() throws JAXBException, IOException,
			IncompatibleVersionException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				collectionFile));
		JAXBContext jaxbContext = JAXBContext.newInstance(
				"org.orbisgis.renderer.symbol.collection.persistence",
				DefaultSymbolCollection.class.getClassLoader());
		Unmarshaller u = jaxbContext.createUnmarshaller();

		SymbolList col = (SymbolList) u.unmarshal(bis);
		bis.close();
		symbols.clear();
		Symbol symbolCollection = getSymbolFromXML(col.getSymbolComposite());
		for (int i = 0; i < symbolCollection.getSymbolCount(); i++) {
			addSymbol(symbolCollection.getSymbol(i));
		}
	}

	private Symbol getSymbolFromXML(SymbolType sim)
			throws IncompatibleVersionException {
		if (sim instanceof SimpleSymbolType) {
			SimpleSymbolType simpleSymbol = (SimpleSymbolType) sim;
			String id = simpleSymbol.getSymbolTypeId();
			Symbol ret = SymbolFactory.getNewSymbol(id);
			if (ret == null) {
				return null;
			} else {
				List<Property> xmlProps = simpleSymbol.getProperty();
				HashMap<String, String> props = new HashMap<String, String>();
				for (Property property : xmlProps) {
					props.put(property.getName(), property.getValue());
				}
				ret.setPersistentProperties(props, simpleSymbol.getVersion());
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

	/**
	 * @see org.orbisgis.renderer.symbol.collection.SymbolCollection#addSymbol(org.orbisgis.renderer.symbol.Symbol)
	 */
	public void addSymbol(Symbol sym) {
		symbols.add(sym.cloneSymbol());
	}

	/**
	 * @see org.orbisgis.renderer.symbol.collection.SymbolCollection#getSymbol(int)
	 */
	public Symbol getSymbol(int index) {
		return symbols.get(index).cloneSymbol();
	}

	/**
	 * @see org.orbisgis.renderer.symbol.collection.SymbolCollection#getSymbolCount()
	 */
	public int getSymbolCount() {
		return symbols.size();
	}

}
