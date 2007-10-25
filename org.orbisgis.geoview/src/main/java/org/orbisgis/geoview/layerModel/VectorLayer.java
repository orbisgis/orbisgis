package org.orbisgis.geoview.layerModel;

import java.awt.Color;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.geoview.renderer.style.BasicStyle;

import com.vividsolutions.jts.geom.Envelope;

public class VectorLayer extends BasicLayer {

	private SpatialDataSourceDecorator dataSource;

	public VectorLayer(String name, DataSource ds,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		this.dataSource = new SpatialDataSourceDecorator(ds);
		setStyle(new BasicStyle(Color.BLUE, Color.RED));
	}

	public SpatialDataSourceDecorator getDataSource() {
		return dataSource;
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