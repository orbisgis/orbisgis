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
import org.orbisgis.core.layerModel.WMSConnection;
import org.orbisgis.core.renderer.symbol.Symbol;


@Deprecated
public class WMSLegend extends AbstractLegend {

	private WMSConnection wmsConnection;
	private String layerName;
	private File file;

	public WMSLegend(WMSConnection wmsConnection, String layerName) {
		this.wmsConnection = wmsConnection;
		this.layerName = layerName;
	}

	@Override
	public void drawImage(Graphics2D g) {
		if ((wmsConnection != null) || (layerName != null)) {
			BufferedImage img = getWMSLegend(wmsConnection, layerName);
			g.drawImage(img, 0, 0, null);
		} else {
			g.drawImage(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB),
					0, 0, null);
		}
	}

	@Override
	public int[] getImageSize(Graphics2D g) {
		if ((wmsConnection != null) || (layerName != null)) {
			BufferedImage img = getWMSLegend(wmsConnection, layerName);
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
		return new WMSLegend(null, null);
	}

	@Override
	public void setJAXBObject(Object jaxbObject) {

	}

	private BufferedImage getWMSLegend(WMSConnection connection,
			String layerName) {
		WMSStatus status = connection.getStatus();
		BufferedImage image = null;
		try {
			if (file == null) {
				file = connection.getClient().getLegendGraphic(status,
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
