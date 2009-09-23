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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.carto.persistence.ClassifiedLegendType;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolManager;
import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolType;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.GeometryProperties;

abstract class AbstractClassifiedLegend extends AbstractCartoLegend implements
		ClassifiedLegend {

	private String fieldName;
	private Symbol defaultSymbol;
	private int fieldType;
	private String defaultLabel = "Rest of values";
	private ArrayList<Symbol> symbols = new ArrayList<Symbol>();
	private ArrayList<String> labels = new ArrayList<String>();

	public void setClassificationField(String fieldName, DataSource ds)
			throws DriverException {
		this.fieldName = fieldName;
		if (GeometryProperties.isFieldName(fieldName)) {
			this.fieldType = Type.DOUBLE;
		} else {
			int fi = ds.getFieldIndexByName(fieldName);
			this.fieldType = ds.getMetadata().getFieldType(fi).getTypeCode();
		}
		fireLegendInvalid();
	}

	public Symbol getSymbol(int index) {
		return symbols.get(index);
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
		save(xmlLegend);
		if (getDefaultSymbol() != null) {
			SymbolManager sm = (SymbolManager) Services
					.getService(SymbolManager.class);
			xmlLegend.setDefaultSymbol(sm.getJAXBSymbol(getDefaultSymbol()));
		}
		if (getDefaultLabel() != null) {
			xmlLegend.setDefaultLabel(getDefaultLabel());
		}
		xmlLegend.setFieldName(fieldName);
		xmlLegend.setFieldType(fieldType);
	}

	public void getDefaults(ClassifiedLegendType xmlLegend) {
		load(xmlLegend);
		setDefaultLabel(xmlLegend.getDefaultLabel());
		fieldType = xmlLegend.getFieldType();
		fieldName = xmlLegend.getFieldName();
		SymbolType defaultSymbolXML = xmlLegend.getDefaultSymbol();
		if (defaultSymbolXML != null) {
			SymbolManager sm = (SymbolManager) Services
					.getService(SymbolManager.class);
			setDefaultSymbol(sm.getSymbolFromJAXB(defaultSymbolXML));
		}
	}

	protected int getFieldType() {
		return fieldType;
	}

	public String getLabel(int index) throws IllegalArgumentException {
		return labels.get(index);
	}

	public void setLabel(int index, String label)
			throws IllegalArgumentException {
		labels.set(index, label);
	}

	public void setSymbol(int index, Symbol symbol)
			throws IllegalArgumentException {
		symbols.set(index, symbol);
	}

	public void removeClassification(int index) {
		symbols.remove(index);
		labels.remove(index);
	}

	public void clear() {
		symbols.clear();
		labels.clear();
	}

	protected ArrayList<Symbol> getSymbols() {
		return symbols;
	}

	protected ArrayList<String> getLabels() {
		return labels;
	}

	public int getClassificationCount() {
		return symbols.size();
	}

	public int getClassificationFieldType() {
		return fieldType;
	}

	public void drawImage(Graphics2D g) {
		String text = getHeader();
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);
		int start = 0;
		if (getClassificationCount() > 0) {
			LegendLine testLine = new LegendLine(getSymbols().get(0),
					getLabels().get(0));
			int firstLineWidth = testLine.getImageSize(g)[0];
			start = (int) ((firstLineWidth - bounds.getWidth()) / 2);
		}
		if (start < 0) {
			start = 0;
		}
		g.setColor(Color.black);
		Font oldFont = g.getFont();
		g.setFont(g.getFont().deriveFont(Font.BOLD));
		g.drawString(text, start, (int) bounds.getHeight());
		g.setFont(oldFont);
		AffineTransform originalTrans = g.getTransform();
		g.translate(0, (int) bounds.getHeight());

		LegendLine ll = null;
		for (int i = 0; i < symbols.size(); i++) {
			if (ll != null) {
				int[] imageSize = ll.getImageSize(g);
				g.translate(0, imageSize[1]);
			}
			ll = new LegendLine(getSymbols().get(i), getLabels().get(i));
			ll.drawImage(g);
		}
		if (defaultSymbol != null) {
			if (ll != null) {
				int[] imageSize = ll.getImageSize(g);
				g.translate(0, imageSize[1]);
			}
			ll = new LegendLine(defaultSymbol, defaultLabel);
			ll.drawImage(g);
		}
		g.setTransform(originalTrans);
	}

	private String getHeader() {
		return "Field: " + getClassificationField();
	}

	public int[] getImageSize(Graphics2D g) {
		int height = 0;
		int width = 0;
		for (int i = 0; i < symbols.size(); i++) {
			LegendLine ll = new LegendLine(getSymbols().get(i), getLabels()
					.get(i));
			int[] imageSize = ll.getImageSize(g);
			height += imageSize[1];
			width = Math.max(width, imageSize[0]);
		}
		if (defaultSymbol != null) {
			LegendLine ll = new LegendLine(defaultSymbol, defaultLabel);
			int[] imageSize = ll.getImageSize(g);
			height += imageSize[1];
			width = Math.max(width, imageSize[0]);
		}
		Rectangle2D bounds = g
				.getFontMetrics(g.getFont().deriveFont(Font.BOLD))
				.getStringBounds(getHeader(), g);
		return new int[] { Math.max(width, (int) bounds.getWidth()),
				(int) (height + bounds.getHeight()) };
	}

}
