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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.core.Services;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.AllowAllRenderContext;
import org.orbisgis.core.renderer.RenderContext;
import org.orbisgis.core.renderer.classification.ClassificationMethodException;
import org.orbisgis.core.renderer.classification.ProportionalMethod;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RenderException;
import org.orbisgis.core.renderer.legend.carto.persistence.LegendContainer;
import org.orbisgis.core.renderer.legend.carto.persistence.ProportionalLegendType;
import org.orbisgis.core.renderer.symbol.StandardPointSymbol;
import org.orbisgis.core.renderer.symbol.StandardSymbol;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.core.renderer.symbol.SymbolManager;
import org.orbisgis.core.renderer.symbol.collection.persistence.SimpleSymbolType;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.GeometryProperties;
import org.orbisgis.utils.FormatUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class DefaultProportionalPointLegend extends AbstractCartoLegend
		implements ProportionalLegend {

	private String field;
	private int maxSize = 3000;
	private int method = LINEAR;
	private double sqrtFactor;
	private StandardPointSymbol symbol;
	private ProportionalMethod proportionnalMethod;
	private int bigSize = 60;
	private int xOffset = 7;
	private boolean visible = true;

	public DefaultProportionalPointLegend() {
		symbol = (StandardPointSymbol) SymbolFactory
				.createPolygonCentroidCircleSymbol(Color.BLACK, 1, Color.pink,
						10, true);
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
		fireLegendInvalid();
	}

	private void setLinearMethod() {
		method = LINEAR;
		fireLegendInvalid();
	}

	private void setSquareMethod(double sqrtFactor) {
		method = SQUARE;
		this.sqrtFactor = sqrtFactor;
		fireLegendInvalid();
	}

	private void setLogarithmicMethod() {
		method = LOGARITHMIC;
		fireLegendInvalid();
	}

	@Override
	public void preprocess(SpatialDataSourceDecorator sds)
			throws RenderException, ClassificationMethodException {
		proportionnalMethod = new ProportionalMethod(sds, field);
		proportionnalMethod.setMethod(method);
		try {
			proportionnalMethod.build(Math.pow(maxSize, 2));

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

			double value;
			if (GeometryProperties.isFieldName(field)) {
				Geometry geom = sds.getGeometry(row);
				value = GeometryProperties.getPropertyValue(
						getClassificationField(), geom).getAsDouble();
			} else {

				int fieldIndex = sds.getFieldIndexByName(field);
				value = sds.getFieldValue(row, fieldIndex).getAsDouble();
			}

			double symbolSize = getSize(value, coefType);

			StandardPointSymbol ret = (StandardPointSymbol) symbol
					.cloneSymbol();
			ret.setSize((int) Math.round(symbolSize));
			return ret;
		} catch (IncompatibleTypesException e) {
			throw new RenderException("Cannot calculate proportionalities", e);
		} catch (DriverException e) {
			throw new RenderException("Cannot access layer contents", e);
		}
	}

	private double getSize(double value, int coefType)
			throws ClassificationMethodException {
		double symbolSize = 0;
		switch (method) {

		case LINEAR:
			symbolSize = proportionnalMethod.getLinearSize(value, coefType);

			break;

		case SQUARE:
			symbolSize = proportionnalMethod.getSquareSize(value, sqrtFactor,
					coefType);

			break;

		case LOGARITHMIC:

			symbolSize = proportionnalMethod
					.getLogarithmicSize(value, coefType);

			break;
		}
		return symbolSize;
	}

	public String getLegendTypeId() {
		return "org.orbisgis.legend.ProportionalPoint";
	}

	public Legend newInstance() {
		return new DefaultProportionalPointLegend();
	}

	public Object getJAXBObject() {
		ProportionalLegendType xmlLegend = new ProportionalLegendType();
		save(xmlLegend);
		SymbolManager sm = (SymbolManager) Services
				.getService(SymbolManager.class);
		xmlLegend.setSampleSymbol(sm.getJAXBSymbol(symbol));
		xmlLegend.setMethod(getMethod());
		xmlLegend.setMaxSize(getMaxSize());
		xmlLegend.setFieldName(getClassificationField());
		LegendContainer xml = new LegendContainer();
		xml.setLegendDescription(xmlLegend);
		return xml;
	}

	public void setJAXBObject(Object jaxbObject) {
		LegendContainer xml = (LegendContainer) jaxbObject;
		ProportionalLegendType xmlLegend = (ProportionalLegendType) xml
				.getLegendDescription();
		load(xmlLegend);
		setMethod(xmlLegend.getMethod());
		setMaxSize(xmlLegend.getMaxSize());
		SymbolManager sm = (SymbolManager) Services
				.getService(SymbolManager.class);
		Symbol symbol = sm.getSymbolFromJAXB(xmlLegend.getSampleSymbol());
		if (symbol != null) {
			setSampleSymbol((StandardPointSymbol) symbol);
		} else {
			Services.getErrorManager().error(
					"Unknown symbol: "
							+ ((SimpleSymbolType) xmlLegend.getSampleSymbol())
									.getSymbolTypeId() + ". Using default");
		}
		setClassificationField(xmlLegend.getFieldName());
	}

	public void setClassificationField(String fieldName) {
		this.field = fieldName;
	}

	public String getClassificationField() {
		return field;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public StandardSymbol getSampleSymbol() {
		return symbol;
	}

	public void setSampleSymbol(StandardSymbol symbol) {
		this.symbol = (StandardPointSymbol) symbol;
	}

	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
		switch (method) {
		case LINEAR:
			setLinearMethod();
			break;
		case LOGARITHMIC:
			setLogarithmicMethod();
			break;
		case SQUARE:
			setSquareMethod(2);
			break;
		}
	}

	public void drawImage(Graphics2D g) {
		drawImage(g, bigSize);
	}

	public void drawImage(Graphics2D g, int bigSize) {
		String text = getHeader();
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D bounds = fm.getStringBounds(text, g);
		g.setColor(Color.black);
		Font oldFont = g.getFont();
		g.setFont(g.getFont().deriveFont(Font.BOLD));
		g.drawString(text, 0, (int) bounds.getHeight());
		g.setFont(oldFont);
		g.translate(0, 5);
		StandardPointSymbol big = (StandardPointSymbol) symbol.cloneSymbol();
		big.setSize(bigSize);
		GeometryFactory gf = new GeometryFactory();

		double maxValue = proportionnalMethod.getMaxValue();
		String maxText = Double.toString(FormatUtils.round(maxValue, 3));
		Rectangle2D r = fm.getStringBounds(maxText, g);
		int textOffset = (int) r.getHeight();

		// lines x dimension
		int lineStartX = xOffset + bigSize / 2;
		int lineEndX = xOffset + bigSize + 5;
		// Draw max text
		r = fm.getStringBounds(maxText, g);

		g.setColor(Color.black);
		g.drawString(maxText, lineEndX + 5,
				(int) (textOffset + r.getHeight() / 2));

		try {
			Point geom = gf.createPoint(new Coordinate(lineStartX, textOffset
					+ bigSize / 2));
			RenderContext renderPermission = new AllowAllRenderContext();
			big
					.draw((Graphics2D) g, geom, new MapTransform(),
							renderPermission);

			double realMaxSize = getSize(maxValue, 1);
			double minValue = proportionnalMethod.getMinValue();
			double meanValue = (minValue + maxValue) / 3;
			double realMeanSize = getSize(meanValue, 1);
			double meanSize = bigSize * (realMeanSize / realMaxSize);
			drawCircle(g, bigSize, meanSize, textOffset, lineStartX, lineEndX,
					renderPermission, NumberFormat.getInstance().format(
							FormatUtils.round(meanValue, 3)));

			double realSmallSize = getSize(minValue, 1);
			if (!Double.isInfinite(realSmallSize)) {
				// proportional can give infinity sizes
				double smallSize = bigSize * (realSmallSize / realMaxSize);
				drawCircle(g, bigSize, smallSize, textOffset, lineStartX,
						lineEndX, renderPermission, Double.toString(FormatUtils
								.round(minValue, 3)));

			}
			g.drawLine(lineStartX, textOffset, lineEndX, textOffset);
		} catch (DriverException e) {
			Services.getErrorManager()
					.error("Cannot get proportional image", e);
		}

	}

	private void drawCircle(Graphics2D g, int bigSize, double smallSize,
			int textOffset, int lineStartX, int lineEndX,
			RenderContext renderPermission, String text)
			throws DriverException {
		StandardPointSymbol small = (StandardPointSymbol) symbol.cloneSymbol();
		small.setSize((int) smallSize);
		int topSmall = (int) (bigSize - smallSize + textOffset);
		String minText = text;
		g.setColor(Color.black);
		Rectangle2D r = g.getFontMetrics().getStringBounds(minText, g);
		g.drawString(minText, lineEndX + 5,
				(int) (topSmall + r.getHeight() / 2));
		Point geom2 = new GeometryFactory().createPoint(new Coordinate(
				lineStartX, textOffset + bigSize - smallSize / 2));

		small.draw((Graphics2D) g, geom2, new MapTransform(), renderPermission);
		g.setColor(Color.black);
		g.drawLine(lineStartX, topSmall, lineEndX, topSmall);

	}

	public int[] getImageSize(Graphics2D g) {
		return getImageSize(g, bigSize);
	}

	public int[] getImageSize(Graphics g, int bigSize) {
		FontMetrics fm = g.getFontMetrics();
		if (proportionnalMethod != null) {
			double maxValue = proportionnalMethod.getMaxValue();
			String maxText = Double.toString(maxValue);
			Rectangle2D r = fm.getStringBounds(maxText, g);
			int height = (int) (r.getHeight() + bigSize + 10);
			int maxWidth = (int) r.getWidth();
			double minValue = proportionnalMethod.getMinValue();
			String minText = Double.toString(minValue);
			r = fm.getStringBounds(minText, g);
			maxWidth = (int) Math.max(maxWidth, r.getWidth());

			String meanText = Double.toString((minValue + maxValue) / 2)
					+ " (mean)";
			r = fm.getStringBounds(meanText, g);
			maxWidth = (int) Math.max(maxWidth, r.getWidth());

			r = fm.getStringBounds(getHeader(), g);

			double wHeader = r.getWidth();

			if (wHeader > maxWidth) {
				maxWidth = (int) wHeader;
			}

			return new int[] { bigSize + xOffset + 10 + maxWidth,
					(int) (height + r.getHeight() / 2) };
		} else {
			return new int[] { 0, 0 };
		}
	}

	@Override
	public String getLegendTypeName() {
		return "Proportional point";
	}

	@Override
	public int getSymbolsToUpdateOnRowModification() {
		return ALL;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;

	}

	private String getHeader() {
		return "Field: " + getClassificationField();
	}

}
