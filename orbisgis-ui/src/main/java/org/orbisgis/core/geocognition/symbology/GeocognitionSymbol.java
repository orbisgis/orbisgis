/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.geocognition.symbology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.orbisgis.core.OrbisGISPersitenceConfig;
import org.orbisgis.core.Services;
import org.orbisgis.core.edition.EditableElementException;
import org.orbisgis.core.geocognition.AbstractExtensionElement;
import org.orbisgis.core.geocognition.GeocognitionElementContentListener;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.GeocognitionExtensionElement;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.core.renderer.symbol.SymbolManager;
import org.orbisgis.core.renderer.symbol.collection.persistence.Property;
import org.orbisgis.core.renderer.symbol.collection.persistence.SimpleSymbolType;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolCompositeType;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolList;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolType;
import org.orbisgis.progress.ProgressMonitor;

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
		this.symbol = getSymbolFromXML(sl.getSymbol());
	}

	private Symbol getSymbolFromXML(SymbolType sim) {
		if (sim instanceof SimpleSymbolType) {
			SimpleSymbolType simpleSymbol = (SimpleSymbolType) sim;
			String id = simpleSymbol.getSymbolTypeId();
			SymbolManager sm = Services
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
					.toArray(new Symbol[ret.size()]));
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
		return OrbisGISPersitenceConfig.GEOCOGNITION_SYMBOL_FACTORY_ID;
	}

	@Override
	public void close(ProgressMonitor progressMonitor) {
		symbol.setPersistentProperties(getPersistentProperties(revertStatus));
	}

	@Override
	public void open(ProgressMonitor progressMonitor)
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
