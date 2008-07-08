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

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.IncompatibleVersionException;
import org.orbisgis.renderer.legend.AbstractLegend;
import org.orbisgis.renderer.legend.carto.persistence.ClassifiedLegendType;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.collection.DefaultSymbolCollection;
import org.orbisgis.renderer.symbol.collection.persistence.SymbolType;

abstract class AbstractClassifiedLegend extends AbstractLegend implements
		ClassifiedLegend {

	private String fieldName;
	private Symbol defaultSymbol;
	private int fieldType;
	private String defaultLabel = "Rest of values";

	public void setClassificationField(String fieldName, DataSource ds)
			throws DriverException {
		this.fieldName = fieldName;
		int fi = ds.getFieldIndexByName(fieldName);
		this.fieldType = ds.getMetadata().getFieldType(fi).getTypeCode();
		fireLegendInvalid();
	}

	public void setDefaultSymbol(Symbol defaultSymbol) {
		this.defaultSymbol = defaultSymbol;
		fireLegendInvalid();
	}

	public Symbol getDefaultSymbol() {
		return defaultSymbol;
	}

	public String getClassificationField() {
		return fieldName;
	}

	public String getDefaultLabel() {
		return defaultLabel;
	}

	public void setDefaultLabel(String defaultLabel) {
		this.defaultLabel = defaultLabel;
	}

	public void fillDefaults(ClassifiedLegendType xmlLegend) {
		xmlLegend.setName(getName());
		if (getDefaultSymbol() != null) {
			xmlLegend.setDefaultSymbol(DefaultSymbolCollection
					.getXMLFromSymbol(getDefaultSymbol()));
		}
		if (getDefaultLabel() != null) {
			xmlLegend.setDefaultLabel(getDefaultLabel());
		}
		xmlLegend.setFieldName(fieldName);
		xmlLegend.setFieldType(fieldType);
	}

	public void getDefaults(ClassifiedLegendType xmlLegend)
			throws DriverException, IncompatibleVersionException {
		setName(xmlLegend.getName());
		setDefaultLabel(xmlLegend.getDefaultLabel());
		fieldType = xmlLegend.getFieldType();
		fieldName = xmlLegend.getFieldName();
		SymbolType defaultSymbolXML = xmlLegend.getDefaultSymbol();
		if (defaultSymbolXML != null) {
			setDefaultSymbol(DefaultSymbolCollection
					.getSymbolFromXML(defaultSymbolXML));
		}
	}

	protected int getFieldType() {
		return fieldType;
	}

}
