package org.orbisgis.geoview.layerModel;

import java.awt.Color;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.instruction.TableNotFoundException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.renderer.style.BasicStyle;
import org.orbisgis.pluginManager.PluginManager;

import com.vividsolutions.jts.geom.Envelope;

public class VectorLayer extends BasicLayer {

	private SpatialDataSourceDecorator dataSource;
	private String mainName;

	VectorLayer(String name, DataSource ds,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		this.mainName = name;
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
				result = dataSource.getFullExtent();
			} catch (DriverException e) {
				PluginManager.error("Cannot get the extent of the layer: "
						+ dataSource.getName(), e);
			}
		}
		return result;
	}

	@Override
	public void setName(String name) throws LayerException {
		SourceManager sourceManager = OrbisgisCore.getDSF().getSourceManager();

		// Remove previous alias
		if (!mainName.equals(getName())) {
			sourceManager.removeName(getName());
		}
		if (!name.equals(mainName)) {
			super.setName(name);
			try {
				sourceManager.addName(mainName, name);
			} catch (TableNotFoundException e) {
				throw new RuntimeException("bug!", e);
			} catch (SourceAlreadyExistsException e) {
				throw new LayerException("Source already exists", e);
			}
		}
	}

	public void close() throws LayerException {
		SourceManager sourceManager = OrbisgisCore.getDSF().getSourceManager();

		// Remove alias
		if (!mainName.equals(getName())) {
			sourceManager.removeName(getName());
		}
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