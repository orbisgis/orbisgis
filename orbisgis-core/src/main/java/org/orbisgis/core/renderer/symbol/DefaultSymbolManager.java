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
package org.orbisgis.core.renderer.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.orbisgis.core.renderer.symbol.collection.persistence.Property;
import org.orbisgis.core.renderer.symbol.collection.persistence.SimpleSymbolType;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolCompositeType;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolType;

public class DefaultSymbolManager implements SymbolManager {

	private ArrayList<Symbol> availableSymbols = new ArrayList<Symbol>();

	@Override
	public ArrayList<Symbol> getAvailableSymbols() {
		ArrayList<Symbol> ret = new ArrayList<Symbol>();
		for (Symbol symbol : availableSymbols) {
			ret.add(symbol.cloneSymbol());
		}
		return ret;
	}

	@Override
	public Symbol createSymbol(String id) {
		for (Symbol sym : availableSymbols) {
			if (sym.getId().equals(id)) {
				return sym.cloneSymbol();
			}
		}

		return null;
	}

	@Override
	public boolean addSymbol(Symbol symbol) {
		if (createSymbol(symbol.getId()) != null) {
			throw new IllegalArgumentException(
					"There is already a symbol with the same id: "
							+ symbol.getId());
		}
		return availableSymbols.add(symbol);
	}

	@Override
	public SymbolType getJAXBSymbol(Symbol com) {
		if (com.acceptsChildren()) {
			SymbolCompositeType comp = new SymbolCompositeType();
			for (int i = 0; i < com.getSymbolCount(); i++) {
				SymbolType xmlSymbol = getJAXBSymbol(com.getSymbol(i));
				if (xmlSymbol != null) {
					comp.getSymbol().add(xmlSymbol);
				}
			}

			return comp;
		} else {
			if (com instanceof Symbol) {
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
			} else {
				return null;
			}
		}
	}

	@Override
	public Symbol getSymbolFromJAXB(SymbolType sym) {
		if (sym instanceof SimpleSymbolType) {
			SimpleSymbolType simpleSymbol = (SimpleSymbolType) sym;
			String id = simpleSymbol.getSymbolTypeId();
			Symbol ret = createSymbol(id);
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
			SymbolCompositeType sc = (SymbolCompositeType) sym;
			ArrayList<Symbol> ret = new ArrayList<Symbol>();
			List<SymbolType> symbols = sc.getSymbol();
			for (int i = 0; i < symbols.size(); i++) {
				Symbol symbolFromXML = getSymbolFromJAXB(symbols.get(i));
				if (symbolFromXML != null) {
					ret.add(symbolFromXML);
				}
			}

			return SymbolFactory.createSymbolComposite(ret
					.toArray(new Symbol[0]));
		}
	}

}
