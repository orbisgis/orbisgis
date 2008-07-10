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
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.IncompatibleVersionException;
import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.renderer.RenderPermission;
import org.orbisgis.renderer.classification.ProportionalMethod;
import org.orbisgis.renderer.legend.AbstractLegend;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.RenderException;
import org.orbisgis.renderer.legend.carto.persistence.LegendContainer;
import org.orbisgis.renderer.legend.carto.persistence.ProportionalLegendType;
import org.orbisgis.renderer.symbol.EditablePointSymbol;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;
import org.orbisgis.renderer.symbol.collection.DefaultSymbolCollection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class DefaultProportionalLegend extends AbstractLegend implements
		ProportionalLegend {

	private String field;
	private int minSymbolArea = 3000;
	private int method = LINEAR;
	private double sqrtFactor;
	private EditablePointSymbol symbol;
	private ProportionalMethod proportionnalMethod;
	private int bigSize = 80;
	private int smallSize = 30;
	private int xOffset = 10;

	public DefaultProportionalLegend() {
		symbol = (EditablePointSymbol) SymbolFactory.createCirclePolygonSymbol(
				Color.BLACK, Color.pink, 10);
	}

	public void setMinSymbolArea(int minSymbolArea) {
		this.minSymbolArea = minSymbolArea;
		fireLegendInvalid();
	}

	private void setLinearMethod() throws DriverException {
		method = LINEAR;
		fireLegendInvalid();
	}

	private void setSquareMethod(double sqrtFactor) throws DriverException {
		method = SQUARE;
		this.sqrtFactor = sqrtFactor;
		fireLegendInvalid();
	}

	private void setLogarithmicMethod() throws DriverException {
		method = LOGARITHMIC;
		fireLegendInvalid();
	}

	@Override
	public void preprocess(SpatialDataSourceDecorator sds)
			throws RenderException {
		proportionnalMethod = new ProportionalMethod(sds, field);
		try {
			proportionnalMethod.build(minSymbolArea);
		} catch (DriverException e) {
			throw new RenderException("Cannot compute the proportional method",
					e);
		}
	}

	public Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException {
		try {
			// TODO what's the use of this variable
			int coefType = 1;

			double symbolSize = 0;
			int fieldIndex = sds.getFieldIndexByName(field);
			double value = sds.getFieldValue(row, fieldIndex).getAsDouble();

			switch (method) {

			case LINEAR:
				symbolSize = proportionnalMethod.getLinearSize(value, coefType);

				break;

			case SQUARE:
				symbolSize = proportionnalMethod.getSquareSize(value,
						sqrtFactor, coefType);

				break;

			case LOGARITHMIC:

				symbolSize = proportionnalMethod.getLogarithmicSize(value,
						coefType);

				break;
			}

			EditablePointSymbol ret = (EditablePointSymbol) symbol
					.cloneSymbol();
			ret.setSize((int) Math.round(symbolSize));
			return ret;
		} catch (IncompatibleTypesException e) {
			throw new RenderException("Cannot calculate proportionalities", e);
		} catch (DriverException e) {
			throw new RenderException("Cannot access layer contents", e);
		}
	}

	public String getLegendTypeId() {
		return "org.orbisgis.legend.ProportionaPoint";
	}

	public Legend newInstance() {
		return new DefaultProportionalLegend();
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
			ProportionalLegendType xmlLegend = new ProportionalLegendType();
			xmlLegend.setName(getName());
			xmlLegend.setSampleSymbol(DefaultSymbolCollection
					.getXMLFromSymbol(symbol));
			xmlLegend.setMethod(getMethod());
			xmlLegend.setMinArea(getMinSymbolArea());
			xmlLegend.setFieldName(getClassificationField());
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
				ProportionalLegendType xmlLegend = (ProportionalLegendType) xml
						.getLegendDescription();
				os.close();
				setName(xmlLegend.getName());
				setMethod(xmlLegend.getMethod());
				setMinSymbolArea(xmlLegend.getMinArea());
				setSampleSymbol((EditablePointSymbol) DefaultSymbolCollection
						.getSymbolFromXML(xmlLegend.getSampleSymbol()));
				setClassificationField(xmlLegend.getFieldName());
			} catch (JAXBException e) {
				throw new PersistenceException("Cannot recover legend", e);
			} catch (IOException e) {
				throw new PersistenceException("Cannot recover legend", e);
			} catch (IncompatibleVersionException e) {
				throw new PersistenceException("Cannot recover legend symbol",
						e);
			} catch (DriverException e) {
				throw new PersistenceException("Cannot perform classification",
						e);
			}
		}
	}

	public void setClassificationField(String fieldName) {
		this.field = fieldName;
	}

	public String getClassificationField() {
		return field;
	}

	public int getMinSymbolArea() {
		return minSymbolArea;
	}

	public EditablePointSymbol getSampleSymbol() {
		return symbol;
	}

	public void setSampleSymbol(EditablePointSymbol symbol) {
		this.symbol = symbol;
	}

	public int getMethod() {
		return method;
	}

	public void setMethod(int method) throws DriverException {
		switch (method) {
		case LINEAR:
			setLinearMethod();
			break;
		case LOGARITHMIC:
			setLogarithmicMethod();
			break;
		case SQUARE:
			setSquareMethod(1);
			break;
		}
	}

	public void drawImage(Graphics g) {
		EditablePointSymbol big = (EditablePointSymbol) symbol.cloneSymbol();
		big.setSize(bigSize);
		EditablePointSymbol small = (EditablePointSymbol) symbol.cloneSymbol();
		small.setSize(smallSize);
		GeometryFactory gf = new GeometryFactory();

		FontMetrics fm = g.getFontMetrics();
		String maxText = Double.toString(proportionnalMethod.getMaxValue());
		Rectangle2D r = fm.getStringBounds(maxText, g);
		int textOffset = (int) r.getHeight();

		// Draw lines
		int topSmall = bigSize - smallSize + textOffset;
		g.setColor(Color.black);
		int lineStartX = xOffset + bigSize / 2;
		int lineEndX = xOffset + bigSize + 5;
		g.drawLine(lineStartX, topSmall, lineEndX, topSmall);
		g.setColor(Color.black);
		g.drawLine(lineStartX, textOffset, lineEndX, textOffset);

		// Draw max text
		r = fm.getStringBounds(maxText, g);
		g.drawString(maxText, lineEndX + 5,
				(int) (textOffset + r.getHeight() / 2));

		// Draw min text
		String minText = Double.toString(proportionnalMethod.getMinValue());
		r = fm.getStringBounds(minText, g);
		g.drawString(minText, lineEndX + 5,
				(int) (topSmall + r.getHeight() / 2));

		try {
			Point geom = gf.createPoint(new Coordinate(lineStartX, textOffset
					+ bigSize / 2));

			big.draw((Graphics2D) g, geom, new AffineTransform(),
					new RenderPermission() {

						public boolean canDraw(Envelope env) {
							return true;
						}

					});

			Point geom2 = gf.createPoint(new Coordinate(lineStartX, textOffset
					+ bigSize - smallSize / 2));

			small.draw((Graphics2D) g, geom2, new AffineTransform(),
					new RenderPermission() {

						public boolean canDraw(Envelope env) {
							return true;
						}

					});

		} catch (DriverException e) {
			Services.getErrorManager()
					.error("Cannot get proportional image", e);
		}
	}

	public int[] getImageSize(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		String maxText = Double.toString(proportionnalMethod.getMaxValue());
		Rectangle2D r = fm.getStringBounds(maxText, g);
		int height = (int) (r.getHeight() + bigSize);
		int maxWidth = (int) r.getWidth();
		String minText = Double.toString(proportionnalMethod.getMinValue());
		r = fm.getStringBounds(minText, g);
		maxWidth = (int) Math.max(maxWidth, r.getWidth());

		return new int[] { bigSize + xOffset + 10 + maxWidth, height };
	}
}
