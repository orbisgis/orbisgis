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
package org.orbisgis.core.renderer.legend.carto;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RenderException;
import org.orbisgis.core.renderer.legend.carto.persistence.LegendContainer;
import org.orbisgis.core.renderer.legend.carto.persistence.UniqueValueLegendType;
import org.orbisgis.core.renderer.legend.carto.persistence.ValueClassification;
import org.orbisgis.core.renderer.symbol.RenderUtils;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolManager;

import com.vividsolutions.jts.geom.Geometry;

public class DefaultUniqueValueLegend extends AbstractClassifiedLegend
		implements UniqueValueLegend {

	private ArrayList<Value> values = new ArrayList<Value>();

	public void addClassification(Value value, Symbol symbol, String label) {
		values.add(value);
		getSymbols().add(symbol);
		getLabels().add(label);
		fireLegendInvalid();
	}

	private int getValueIndex(Value value) {
		return values.indexOf(value);
	}

	public Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException {
		try {
			int fieldIndex = sds.getFieldIndexByName(getClassificationField());
			Value value = sds.getFieldValue(row, fieldIndex);
			Geometry geom = sds.getGeometry(row);
			int symbolIndex = getValueIndex(value);
			if (symbolIndex != -1) {
				Symbol classificationSymbol = getSymbols().get(symbolIndex);
				Symbol symbol = RenderUtils.buildSymbolToDraw(
						classificationSymbol, geom);
				return symbol;
			} else {
				return getDefaultSymbol();
			}
		} catch (DriverException e) {
			throw new RenderException("Cannot access the layer contents", e);
		}
	}

	public Object getJAXBObject() {
		UniqueValueLegendType xmlLegend = new UniqueValueLegendType();
		super.fillDefaults(xmlLegend);
		List<ValueClassification> classifications = xmlLegend
				.getValueClassification();
		SymbolManager sm = (SymbolManager) Services
				.getService(SymbolManager.class);
		for (int i = 0; i < values.size(); i++) {
			ValueClassification classification = new ValueClassification();
			classification.setLabel(getLabel(i));
			classification.setSymbol(sm.getJAXBSymbol(getSymbol(i)));
			classification.setValue(values.get(i).toString());
			classifications.add(classification);
		}
		LegendContainer xml = new LegendContainer();
		xml.setLegendDescription(xmlLegend);
		return xml;
	}

	public void setJAXBObject(Object jaxbObject) {
		LegendContainer xml = (LegendContainer) jaxbObject;
		UniqueValueLegendType xmlLegend = (UniqueValueLegendType) xml
				.getLegendDescription();
		super.getDefaults(xmlLegend);
		List<ValueClassification> classifications = xmlLegend
				.getValueClassification();
		SymbolManager sm = (SymbolManager) Services
				.getService(SymbolManager.class);
		for (int i = 0; i < classifications.size(); i++) {
			ValueClassification classification = classifications.get(i);
			String label = classification.getLabel();
			Value value = null;
			String xmlValue = classification.getValue();
			try {
				value = ValueFactory
						.createValueByType(xmlValue, getFieldType());
			} catch (NumberFormatException e) {
				Services.getErrorManager().error(
						"Cannot parse legend value in " + getName() + ": "
								+ xmlValue, e);
			} catch (ParseException e) {
				Services.getErrorManager().error(
						"Cannot parse legend value in " + getName() + ": "
								+ xmlValue, e);
			}
			Symbol symbol = sm.getSymbolFromJAXB(classification.getSymbol());
			addClassification(value, symbol, label);
		}
	}

	public String getLegendTypeId() {
		return "org.orbisgis.legend.ValueClassification";
	}

	public Legend newInstance() {
		return new DefaultUniqueValueLegend();
	}

	public Value getValue(int index) {
		return values.get(index);
	}

	public void setValue(int index, Value value) {
		values.set(index, value);
	}

	@Override
	public void clear() {
		values.clear();
		super.clear();
	}

	@Override
	public void removeClassification(int index) throws IllegalArgumentException {
		values.remove(index);
		super.removeClassification(index);
	}

	@Override
	public String getLegendTypeName() {
		return "Value classification";
	}

	@Override
	public int getSymbolsToUpdateOnRowModification() {
		return ONLY_AFFECTED;
	}
}
