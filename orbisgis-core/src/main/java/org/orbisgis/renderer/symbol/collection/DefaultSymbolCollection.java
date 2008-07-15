/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
		SymbolType col = getXMLFromSymbol(symbolComposite);
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
	public static SymbolType getXMLFromSymbol(Symbol com) {

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

	public static Symbol getSymbolFromXML(SymbolType sim)
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
