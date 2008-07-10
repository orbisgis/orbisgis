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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.PersistenceException;
import org.orbisgis.renderer.legend.AbstractLegend;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.RenderException;
import org.orbisgis.renderer.legend.carto.persistence.LabelLegendType;
import org.orbisgis.renderer.legend.carto.persistence.LegendContainer;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;
import org.orbisgis.renderer.symbol.collection.DefaultSymbolCollection;

public class DefaultLabelLegend extends AbstractLegend implements LabelLegend {

	private String labelSizeField;

	private int fontSize = 10;

	private String fieldName;

	private int getSize(SpatialDataSourceDecorator sds, long row)
			throws RenderException, DriverException {
		if (labelSizeField == null) {
			return fontSize;
		} else {
			int fieldIndex = sds.getFieldIndexByName(labelSizeField);
			if (fieldIndex != -1) {
				throw new RenderException("The label size field '"
						+ labelSizeField + "' does not exist");
			} else {
				return sds.getFieldValue(row, fieldIndex).getAsInt();
			}
		}
	}

	public void setLabelSizeField(String fieldName) throws DriverException {
		this.labelSizeField = fieldName;
		fireLegendInvalid();
	}

	public String getLabelSizeField() {
		return this.labelSizeField;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
		fireLegendInvalid();
	}

	public int getFontSize() {
		return this.fontSize;
	}

	public Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException {
		try {
			int fieldIndex = sds.getFieldIndexByName(fieldName);
			Value v = sds.getFieldValue(row, fieldIndex);
			return SymbolFactory.createLabelSymbol(v.toString(), getSize(sds,
					row));
		} catch (DriverException e) {
			throw new RenderException("Cannot access layer contents" + e);
		}

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
			LabelLegendType xmlLegend = new LabelLegendType();
			xmlLegend.setName(getName());
			xmlLegend.setFieldFontSize(getLabelSizeField());
			xmlLegend.setFieldName(getClassificationField());
			xmlLegend.setFontSize(getFontSize());
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
				LabelLegendType xmlLegend = (LabelLegendType) xml
						.getLegendDescription();
				os.close();
				setName(xmlLegend.getName());
				setClassificationField(xmlLegend.getFieldName());
				setFontSize(xmlLegend.getFontSize());
				setLabelSizeField(xmlLegend.getFieldFontSize());
			} catch (JAXBException e) {
				throw new PersistenceException("Cannot recover legend", e);
			} catch (IOException e) {
				throw new PersistenceException("Cannot recover legend", e);
			} catch (DriverException e) {
				throw new PersistenceException("Cannot compute label sizes", e);
			}
		}
	}

	public Legend newInstance() {
		return new DefaultLabelLegend();
	}

	public String getLegendTypeId() {
		return "org.orbisgis.legend.Label";
	}

	public String getClassificationField() {
		return fieldName;
	}

	public void setClassificationField(String fieldName) {
		this.fieldName = fieldName;
	}

	public void drawImage(Graphics g) {
		g.setColor(Color.black);
		FontMetrics fm = g.getFontMetrics();
		String text = getDrawingText();
		Rectangle2D r = fm.getStringBounds(text, g);
		g.drawString(text, 5, (int) (r.getHeight() * 1.2));
	}

	public int[] getImageSize(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		String text = getDrawingText();
		Rectangle2D r = fm.getStringBounds(text, g);
		return new int[] { 5 + (int) r.getWidth(), (int) (r.getHeight() * 1.4) };
	}

	private String getDrawingText() {
		return "abc  Label on " + fieldName;
	}

}
