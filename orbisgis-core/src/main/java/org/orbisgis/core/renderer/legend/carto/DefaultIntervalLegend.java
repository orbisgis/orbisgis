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
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RenderException;
import org.orbisgis.core.renderer.legend.carto.persistence.IntervalClassification;
import org.orbisgis.core.renderer.legend.carto.persistence.IntervalLegendType;
import org.orbisgis.core.renderer.legend.carto.persistence.LegendContainer;
import org.orbisgis.core.renderer.symbol.RenderUtils;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolManager;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.GeometryProperties;

import com.vividsolutions.jts.geom.Geometry;

public class DefaultIntervalLegend extends AbstractClassifiedLegend implements
		IntervalLegend {

	private ArrayList<Interval> intervals = new ArrayList<Interval>();

	public Symbol getSymbol(Interval inter) {
		for (int i = 0; i < intervals.size(); i++) {
			if (intervals.get(i).equals(inter)) {
				return getSymbols().get(i);
			}
		}
		return null;
	}

	public ArrayList<Interval> getIntervals() {
		return intervals;
	}

	public void addInterval(Value initialValue, boolean minIncluded,
			Value finalValue, boolean maxIncluded, Symbol symbol, String label) {
		intervals.add(new Interval(initialValue, minIncluded, finalValue,
				maxIncluded));
		getSymbols().add(symbol);
		getLabels().add(label);
		fireLegendInvalid();
	}

	public void addIntervalWithMaxLimit(Value finalValue, boolean included,
			Symbol symbol, String label) {
		intervals.add(new Interval(null, false, finalValue, included));
		getSymbols().add(symbol);
		getLabels().add(label);
		fireLegendInvalid();
	}

	public void addIntervalWithMinLimit(Value initialValue, boolean included,
			Symbol symbol, String label) {
		intervals.add(new Interval(initialValue, included, null, false));
		getSymbols().add(symbol);
		getLabels().add(label);
		fireLegendInvalid();
	}

	public Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException {
		try {
			Value value;
			if (GeometryProperties.isFieldName(getClassificationField())) {
				Geometry geom = sds.getGeometry(row);
				value = GeometryProperties.getPropertyValue(
						getClassificationField(), geom);
			} else {

				int fieldIndex = sds
						.getFieldIndexByName(getClassificationField());
				value = sds.getFieldValue(row, fieldIndex);
			}
			Symbol classificationSymbol = getSymbolFor(value);
			if (classificationSymbol != null) {
				Symbol symbol;
				symbol = RenderUtils.buildSymbolToDraw(classificationSymbol,
						sds.getGeometry(row));
				return symbol;
			} else {
				return getDefaultSymbol();
			}
		} catch (DriverException e) {
			throw new RenderException("Cannot access the layer contents", e);
		}
	}

	private Symbol getSymbolFor(Value value) {
		for (int i = 0; i < intervals.size(); i++) {
			if (intervals.get(i).contains(value)) {
				return getSymbols().get(i);
			}
		}

		return getDefaultSymbol();
	}

	public String getLegendTypeId() {
		return "org.orbisgis.legend.Intervals";
	}

	public Object getJAXBObject() {
		IntervalLegendType xmlLegend = new IntervalLegendType();
		super.fillDefaults(xmlLegend);
		List<IntervalClassification> classifications = xmlLegend
				.getIntervalClassification();
		for (int i = 0; i < intervals.size(); i++) {
			IntervalClassification classification = new IntervalClassification();
			classification.setLabel(getLabel(i));
			SymbolManager sm = (SymbolManager) Services
					.getService(SymbolManager.class);
			classification.setSymbol(sm.getJAXBSymbol(getSymbol(i)));
			Interval interval = intervals.get(i);
			Value minValue = interval.getMinValue();
			if ((minValue != null) && !minValue.isNull()) {
				classification.setInitValue(minValue.toString());
			} else {
				classification.setInitValue(null);
			}
			classification.setInitIncluded(interval.isMinIncluded());
			classification.setEndIncluded(interval.isMaxIncluded());
			Value maxValue = interval.getMaxValue();
			if ((maxValue != null) && !maxValue.isNull()) {
				classification.setEndValue(maxValue.toString());
			} else {
				classification.setEndValue(null);
			}
			classifications.add(classification);
		}
		LegendContainer xml = new LegendContainer();
		xml.setLegendDescription(xmlLegend);
		return xml;
	}

	@Override
	public void clear() {
		intervals.clear();
		super.clear();
	}

	public void setJAXBObject(Object jaxbObject) {
		LegendContainer xml = (LegendContainer) jaxbObject;
		IntervalLegendType xmlLegend = (IntervalLegendType) xml
				.getLegendDescription();
		super.getDefaults(xmlLegend);
		List<IntervalClassification> classifications = xmlLegend
				.getIntervalClassification();
		for (int i = 0; i < classifications.size(); i++) {
			IntervalClassification classification = classifications.get(i);
			String label = classification.getLabel();
			String initValue = classification.getInitValue();
			boolean initIncluded = false;
			Boolean initIncludedXML = classification.isInitIncluded();
			if ((initIncludedXML != null) && (initIncludedXML)) {
				initIncluded = true;
			}
			boolean endIncluded = false;
			Boolean endIncludedXML = classification.isEndIncluded();
			if ((endIncludedXML != null) && (endIncludedXML)) {
				endIncluded = true;
			}
			Value minValue = null;
			if (initValue != null) {
				try {
					minValue = createValueByType(initValue, getFieldType());
				} catch (NumberFormatException e) {
					Services.getErrorManager().error(
							"Cannot parse interval init in legend " + getName()
									+ ": " + initValue, e);
				} catch (ParseException e) {
					Services.getErrorManager().error(
							"Cannot parse interval init in legend " + getName()
									+ ": " + initValue, e);
				}
			}
			Value maxValue = null;
			String endValue = classification.getEndValue();
			if (endValue != null) {
				try {
					maxValue = createValueByType(endValue, getFieldType());
				} catch (NumberFormatException e) {
					Services.getErrorManager().error(
							"Cannot parse interval end: " + initValue, e);
				} catch (ParseException e) {
					Services.getErrorManager().error(
							"Cannot parse interval end: " + initValue, e);
				}
			}
			SymbolManager sm = (SymbolManager) Services
					.getService(SymbolManager.class);
			Symbol symbol = sm.getSymbolFromJAXB(classification.getSymbol());
			addInterval(minValue, initIncluded, maxValue, endIncluded, symbol,
					label);
		}
	}

	private Value createValueByType(String value, int fieldType)
			throws NumberFormatException, ParseException {
		Value doubleValue = ValueFactory.createValueByType(value, Type.DOUBLE);
		switch (fieldType) {
		case Type.BYTE:
			return ValueFactory.createValue(doubleValue.getAsByte());
		case Type.SHORT:
			return ValueFactory.createValue(doubleValue.getAsShort());
		case Type.INT:
			return ValueFactory.createValue(doubleValue.getAsInt());
		case Type.LONG:
			return ValueFactory.createValue(doubleValue.getAsLong());
		case Type.FLOAT:
			return ValueFactory.createValue(doubleValue.getAsFloat());
		case Type.DOUBLE:
			return ValueFactory.createValue(doubleValue.getAsDouble());
		}
		return null;
	}

	public Legend newInstance() {
		return new DefaultIntervalLegend();
	}

	public int getIntervalCount() {
		return intervals.size();
	}

	public Interval getInterval(int index) {
		return intervals.get(index);
	}

	public void setInterval(int rowIndex, Interval interval) {
		intervals.set(rowIndex, interval);
	}

	@Override
	public String getLegendTypeName() {
		return "Interval classification";
	}

	@Override
	public int getSymbolsToUpdateOnRowModification() {
		return ONLY_AFFECTED;
	}

}
