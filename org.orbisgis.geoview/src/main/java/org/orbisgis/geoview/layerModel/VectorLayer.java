package org.orbisgis.geoview.layerModel;

import java.awt.Color;
import java.util.Random;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Envelope;

public class VectorLayer extends GdmsLayer {

	private SpatialDataSourceDecorator dataSource;

	VectorLayer(String name, DataSource ds,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		this.dataSource = new SpatialDataSourceDecorator(ds);

		Random rand = new Random();

		Color fillColor = new Color(rand.nextInt(256),
                rand.nextInt(256),
                rand.nextInt(256));

		Color lineColor = new Color(rand.nextInt(256),
                rand.nextInt(256),
                rand.nextInt(256));

		BasicStyle basicStyle = new BasicStyle(fillColor,lineColor, 1 );

		setStyle(basicStyle);
	}

	public SpatialDataSourceDecorator getDataSource() {
		return dataSource;
	}

	public Envelope getEnvelope() {
		Envelope result = new Envelope();

		if (null != dataSource) {
			try {
				result = dataSource.getFullExtent();
			} catch (DriverException e) {
				PluginManager.error("Cannot get the extent of the layer: "
						+ dataSource.getName(), e);
			}
		}
		return result;
	}

	public void close() throws LayerException {
		super.close();
		try {
			dataSource.cancel();
		} catch (AlreadyClosedException e) {
			throw new RuntimeException("Bug!");
		} catch (DriverException e) {
			throw new LayerException(e);
		}
	}

	public void open() throws LayerException {
		try {
			dataSource.open();
		} catch (DriverException e) {
			throw new LayerException(e);
		}
	}
}