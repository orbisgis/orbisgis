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
package org.orbisgis.core.renderer.legend;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.gdms.data.DataSource;
import org.gvsig.remoteClient.exceptions.ServerErrorException;
import org.gvsig.remoteClient.exceptions.WMSException;
import org.gvsig.remoteClient.wms.WMSStatus;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSStatus;

public class WMSLegend extends AbstractLegend {

	private WMSClient wmsClient;
        private WMSStatus wmsStatus;
	private String layerName;
	private File file;

	public WMSLegend(WMSClient wmsClient, WMSStatus wmsStatus, String layerName) {
		this.wmsClient = wmsClient;
                this.wmsStatus = wmsStatus;
		this.layerName = layerName;
	}

	@Override
	public void drawImage(Graphics2D g) {
		if ((wmsClient != null) || (wmsStatus != null) || (layerName != null)) {
			BufferedImage img = getWMSLegend(wmsClient, wmsStatus, layerName);
			g.drawImage(img, 0, 0, null);
		} else {
			g.drawImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB),
					0, 0, null);
		}
	}

	@Override
	public int[] getImageSize(Graphics2D g) {
		if ((wmsClient != null) || (wmsStatus != null) || (layerName != null)) {
			BufferedImage img = getWMSLegend(wmsClient, wmsStatus, layerName);
			return new int[] { img.getWidth(), img.getHeight() };
		} else {
			FontMetrics fm = g.getFontMetrics();
			String text = "No legend available";
			Rectangle2D r = fm.getStringBounds(text, g);
			return new int[] { 5 + (int) r.getWidth(),
					(int) (r.getHeight() * 1.4) };
		}
	}

	@Override
	public String getJAXBContext() {
		return null;
	}

	@Override
	public Object getJAXBObject() {
		return null;
	}

	@Override
	public String getLegendTypeId() {
		return "org.orbisgis.legend.WMSLegend";
	}

	@Override
	public String getLegendTypeName() {
		return "WMS";
	}

	@Override
	public Symbol getSymbol(DataSource sds, long row)
			throws RenderException {
		return null;
	}

	@Override
	public int getSymbolsToUpdateOnRowModification() {
		return ONLY_AFFECTED;
	}

	@Override
	public Legend newInstance() {
		return new WMSLegend(null, null, null);
	}

	@Override
	public void setJAXBObject(Object jaxbObject) {

	}

	private BufferedImage getWMSLegend(WMSClient client, WMSStatus status,
			String layerName) {
		BufferedImage image = null;
		try {
			if (file == null) {
				file = client.getLegendGraphic(status,
						layerName, null);
			}
			image = ImageIO.read(file);
		} catch (WMSException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot get WMS legend", e);
		} catch (ServerErrorException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot get WMS legend", e);
		} catch (IOException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot get WMS legend", e);
		}
		return image;
	}

}
