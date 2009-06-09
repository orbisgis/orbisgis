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
package org.orbisgis.core.renderer.legend;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

import org.gdms.data.SpatialDataSourceDecorator;
import org.grap.lut.LutDisplay;
import org.grap.lut.LutGenerator;
import org.orbisgis.core.renderer.symbol.Symbol;

public class RasterLegend extends AbstractLegend implements Legend {

	private ColorModel colorModel = null;
	private float opacity = 1.0f;

	private String bandsCode = null;
	private boolean visible = true;

	/**
	 * @param colorModel
	 * @param opacity
	 *            0 for transparent, 1 for opaque
	 */
	public RasterLegend(ColorModel colorModel, float opacity) {
		this.colorModel = colorModel;
		this.opacity = opacity;
	}

	public ColorModel getColorModel() {
		return colorModel;
	}

	public void setColorModel(ColorModel colorModel) {
		this.colorModel = colorModel;
	}

	public float getOpacity() {
		return opacity;
	}

	public int getNumLayers() {
		return 1;
	}

	public Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException {
		return null;
	}

	public void setBands(String bandsCode) {

		this.bandsCode = bandsCode;

	}

	public String getBands() {
		return bandsCode;
	}

	public Object getJAXBObject() {
		return null;
	}

	public void setJAXBObject(Object jaxbObject) {
	}

	public String getLegendTypeId() {
		return "org.orbisgis.legend.RasterColorModel";
	}

	public Legend newInstance() {
		return new RasterLegend(LutGenerator.colorModel("gray"), 1);
	}

	public void drawImage(Graphics2D g) {
		if (bandsCode != null) {
			g.setColor(Color.black);
			FontMetrics fm = g.getFontMetrics();
			String text = getDrawingText();
			Rectangle2D r = fm.getStringBounds(text, g);
			g.drawString(text, 5, (int) (r.getHeight() * 1.2));
		} else {
			Image img = new LutDisplay(colorModel).getImage();
			g.drawImage(img, 0, 0, img.getWidth(null) / 2, img.getHeight(null),
					null);
		}
	}

	public int[] getImageSize(Graphics2D g) {
		if (bandsCode != null) {
			FontMetrics fm = g.getFontMetrics();
			String text = getDrawingText();
			Rectangle2D r = fm.getStringBounds(text, g);
			return new int[] { 5 + (int) r.getWidth(),
					(int) (r.getHeight() * 1.4) };
		} else {
			Image img = new LutDisplay(colorModel).getImage();
			return new int[] { img.getWidth(null) / 2, img.getHeight(null) };
		}
	}

	private String getDrawingText() {
		return bandsCode + " composition";
	}

	@Override
	public String getJAXBContext() {
		return null;
	}

	@Override
	public String getLegendTypeName() {
		return "Raster";
	}

	@Override
	public int getSymbolsToUpdateOnRowModification() {
		return ONLY_AFFECTED;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible  = visible;
	}

}