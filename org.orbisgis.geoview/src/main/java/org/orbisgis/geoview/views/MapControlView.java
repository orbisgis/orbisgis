package org.orbisgis.geoview.views;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;
import org.orbisgis.geoview.MapControl;
import org.orbisgis.geoview.OGMapControlModel;
import org.orbisgis.tools.ViewContext;

public class MapControlView implements IView {

	private MapControl map;

	public void delete() {

	}

	public Component getComponent(GeoView2D geoview) {
		return map;
	}

	public void initialize(GeoView2D geoView2D) {
		map = new MapControl();
		ViewContext viewContext = geoView2D.getViewContext();
		OGMapControlModel mapModel = new OGMapControlModel(viewContext
				.getLayerModel());
		mapModel.setMapControl((MapControl) map);
		((MapControl) map).setMapControlModel(mapModel);
		viewContext.setMapControl(map);
		map.setEditionContext(viewContext);
	}

	public void loadStatus(InputStream ois) {

	}

	public void saveStatus(OutputStream oos) {

	}

}
