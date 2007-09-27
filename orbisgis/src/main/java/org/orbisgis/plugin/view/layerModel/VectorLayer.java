package org.orbisgis.plugin.view.layerModel;

import java.awt.Color;

import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.plugin.renderer.style.BasicStyle;
import org.orbisgis.plugin.renderer.style.Style;

import com.vividsolutions.jts.geom.Envelope;

public class VectorLayer extends BasicLayer {

	private SpatialDataSourceDecorator dataSource;

	public VectorLayer(String name,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		setStyle(new BasicStyle(Color.BLUE,Color.RED));
	}

	public void set(SpatialDataSourceDecorator dataSource, Style style)
			throws Exception {
		this.dataSource = dataSource;
		setStyle(style);
	}

	public SpatialDataSourceDecorator getDataSource() {
		return dataSource;
	}

	public void setDataSource(SpatialDataSourceDecorator dataSource) {
		this.dataSource = dataSource;
	}

	public Envelope getEnvelope() {
		Envelope result = new Envelope();

		if (null != dataSource) {
			try {
				dataSource.open();
				result = dataSource.getFullExtent();
				dataSource.cancel();
			} catch (DriverException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}