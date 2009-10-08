package org.gdms.data.wms;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.object.ObjectDataSourceAdapter;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.SourceManager;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.WmsDefinitionType;
import org.orbisgis.progress.IProgressMonitor;

public class WMSSourceDefinition extends AbstractDataSourceDefinition implements
		DataSourceDefinition {

	private WMSSource wmsSource;

	public WMSSourceDefinition(WMSSource wmsSource) {
		this.wmsSource = wmsSource;
	}

	@Override
	protected ReadOnlyDriver getDriverInstance() {
		try {
			ObjectMemoryDriver ret = new ObjectMemoryDriver(getWMSMetadata());
			ret.addValues(new Value[] {
					ValueFactory.createValue(wmsSource.getHost()),
					ValueFactory.createValue(wmsSource.getLayer()),
					ValueFactory.createValue(wmsSource.getSrs()),
					ValueFactory.createValue(wmsSource.getFormat()) });
			return ret;
		} catch (DriverException e) {
			// Access to DefaultMetadata doesn't give any exception
			throw new RuntimeException("bug!");
		}
	}

	private Metadata getWMSMetadata() throws DriverException {
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("host", Type.STRING);
		metadata.addField("layer", Type.STRING);
		metadata.addField("srs", Type.STRING);
		metadata.addField("format", Type.STRING);
		return metadata;
	}

	@Override
	public DataSource createDataSource(String tableName, IProgressMonitor pm)
			throws DataSourceCreationException {
		((ReadOnlyDriver) getDriver())
				.setDataSourceFactory(getDataSourceFactory());

		ObjectDataSourceAdapter ds = new ObjectDataSourceAdapter(
				getSource(tableName), (ObjectDriver) getDriver());
		return ds;
	}

	@Override
	public void createDataSource(DataSource contents, IProgressMonitor pm)
			throws DriverException {
		throw new UnsupportedOperationException("Cannot create WMS sources");
	}

	@Override
	public int getType() {
		return SourceManager.WMS;
	}

	@Override
	public String getTypeName() {
		return "WMS";
	}

	public WMSSource getWMSSource() {
		return wmsSource;
	}

	@Override
	public DefinitionType getDefinition() {
		WmsDefinitionType def = new WmsDefinitionType();
		def.setHost(wmsSource.getHost());
		def.setLayerName(wmsSource.getLayer());
		def.setSrs(wmsSource.getSrs());
		def.setFormat(wmsSource.getFormat());
		return def;
	}

	public static DataSourceDefinition createFromXML(DataSourceFactory dsf,
			WmsDefinitionType definitionType) {
		WMSSource wmsSource = new WMSSource(definitionType.getHost(),
				definitionType.getLayerName(), definitionType.getSrs(),
				definitionType.getFormat());
		return new WMSSourceDefinition(wmsSource);
	}

	@Override
	public boolean equals(DataSourceDefinition obj) {
		if (obj instanceof WMSSourceDefinition) {
			WMSSourceDefinition dsd = (WMSSourceDefinition) obj;
			if (wmsSource.getHost().equals(dsd.getWMSSource().getHost())
					&& wmsSource.getLayer().equals(
							dsd.getWMSSource().getLayer())
					&& wmsSource.getSrs().equals(dsd.getWMSSource().getSrs())
					&& wmsSource.getFormat().equals(
							dsd.getWMSSource().getFormat())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
