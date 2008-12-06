package org.orbisgis.views.geocatalog.newSourceWizards.wms;

import java.util.Vector;

import org.gdms.data.wms.WMSSource;
import org.gdms.source.SourceManager;
import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSLayer;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.views.geocatalog.newSourceWizard.INewSource;
import org.orbisgis.views.geocatalog.newSourceWizard.SourceRenderer;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class WMSWizard implements INewSource {

	@Override
	public void registerSources() {
		WMSConnectionPanel wmsConnection = new WMSConnectionPanel();
		LayerConfigurationPanel layerConfiguration = new LayerConfigurationPanel(
				wmsConnection);
		SRSPanel srsPanel = new SRSPanel(wmsConnection);
		if (UIFactory.showDialog(new UIPanel[] { wmsConnection,
				layerConfiguration, srsPanel })) {
			WMSClient client = wmsConnection.getWMSClient();
			String validImageFormat = getFirstImageFormat(client.getFormats());
			if (validImageFormat == null) {
				Services.getService(ErrorManager.class).error(
						"Cannot find a suitable image format");
			} else {
				Object[] layers = layerConfiguration.getSelectedLayers();
				for (Object layer : layers) {
					String layerName = ((WMSLayer) layer).getName();
					WMSSource source = new WMSSource(client.getHost(),
							layerName, srsPanel.getSRS(), validImageFormat);
					SourceManager sourceManager = Services.getService(
							DataManager.class).getSourceManager();
					String uniqueName = sourceManager.getUniqueName(layerName);
					sourceManager.register(uniqueName, source);
				}
			}

		}
	}

	private String getFirstImageFormat(Vector<?> formats) {
		String[] preferredFormats = new String[] { "image/png", "image/jpeg",
				"image/gif", "image/tiff" };
		for (int i = 0; i < preferredFormats.length; i++) {
			if (formats.contains(preferredFormats[i])) {
				return preferredFormats[i];
			}
		}

		for (Object object : formats) {
			String format = object.toString();
			if (format.startsWith("image/")) {
				return format;
			}
		}

		return null;
	}

	@Override
	public String getName() {
		return "WMS source";
	}

	@Override
	public SourceRenderer getRenderer() {
		return null;
	}

	@Override
	public void initialize() {
	}

}
