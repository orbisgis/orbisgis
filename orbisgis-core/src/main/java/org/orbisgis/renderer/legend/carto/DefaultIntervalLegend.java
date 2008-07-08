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
package org.orbisgis.renderer.legend.carto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.IncompatibleVersionException;
import org.orbisgis.PersistenceException;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.RenderException;
import org.orbisgis.renderer.legend.carto.persistence.IntervalClassification;
import org.orbisgis.renderer.legend.carto.persistence.IntervalLegendType;
import org.orbisgis.renderer.legend.carto.persistence.LegendContainer;
import org.orbisgis.renderer.symbol.RenderUtils;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.collection.DefaultSymbolCollection;

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
			int fieldIndex = sds.getFieldIndexByName(getClassificationField());
			Value value = sds.getFieldValue(row, fieldIndex);
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

	public String getVersion() {
		return "1.0";
	}

	public void save(File file) throws PersistenceException {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(
							"org.orbisgis.renderer.legend.carto.persistence:"
									+ "org.orbisgis.renderer.symbol.collection.persistence",
							DefaultSymbolCollection.class.getClassLoader());
			Marshaller m = jaxbContext.createMarshaller();

			BufferedOutputStream os = new BufferedOutputStream(
					new FileOutputStream(file));
			IntervalLegendType xmlLegend = new IntervalLegendType();
			super.fillDefaults(xmlLegend);
			List<IntervalClassification> classifications = xmlLegend
					.getIntervalClassification();
			for (int i = 0; i < intervals.size(); i++) {
				IntervalClassification classification = new IntervalClassification();
				classification.setLabel(getLabel(i));
				classification.setSymbol(DefaultSymbolCollection
						.getXMLFromSymbol(getSymbol(i)));
				Interval interval = intervals.get(i);
				Value minValue = interval.getMinValue();
				if (minValue != null) {
					classification.setInitValue(minValue.toString());
				}
				classification.setInitIncluded(interval.isMinIncluded());
				classification.setEndIncluded(interval.isMaxIncluded());
				Value maxValue = interval.getMaxValue();
				if (maxValue != null) {
					classification.setEndValue(maxValue.toString());
				}
				classifications.add(classification);
			}
			LegendContainer xml = new LegendContainer();
			xml.setLegendDescription(xmlLegend);
			m.marshal(xml, os);
			os.close();
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot save legend", e);
		} catch (IOException e) {
			throw new PersistenceException("Cannot save legend", e);
		}
	}

	public void load(File file, String version) throws PersistenceException {
		if (version.equals("1.0")) {
			try {
				JAXBContext jaxbContext = JAXBContext
						.newInstance(
								"org.orbisgis.renderer.legend.carto.persistence:"
										+ "org.orbisgis.renderer.symbol.collection.persistence",
								DefaultSymbolCollection.class.getClassLoader());
				Unmarshaller m = jaxbContext.createUnmarshaller();
				BufferedInputStream os = new BufferedInputStream(
						new FileInputStream(file));
				LegendContainer xml = (LegendContainer) m.unmarshal(os);
				IntervalLegendType xmlLegend = (IntervalLegendType) xml
						.getLegendDescription();
				os.close();
				super.getDefaults(xmlLegend);
				List<IntervalClassification> classifications = xmlLegend
						.getIntervalClassification();
				for (int i = 0; i < classifications.size(); i++) {
					IntervalClassification classification = classifications
							.get(i);
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
						minValue = createValueByType(initValue, getFieldType());
					}
					Value maxValue = null;
					String endValue = classification.getEndValue();
					if (endValue != null) {
						maxValue = createValueByType(endValue, getFieldType());
					}
					Symbol symbol = DefaultSymbolCollection
							.getSymbolFromXML(classification.getSymbol());
					addInterval(minValue, initIncluded, maxValue, endIncluded,
							symbol, label);
				}
			} catch (JAXBException e) {
				throw new PersistenceException("Cannot recover legend", e);
			} catch (IOException e) {
				throw new PersistenceException("Cannot recover legend", e);
			} catch (IncompatibleVersionException e) {
				throw new PersistenceException("Cannot recover legend symbol",
						e);
			} catch (DriverException e) {
				throw new PersistenceException("Cannot recover legend", e);
			} catch (NumberFormatException e) {
				throw new PersistenceException("Cannot recover value", e);
			} catch (ParseException e) {
				throw new PersistenceException("Cannot recover value", e);
			}
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

}
