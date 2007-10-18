package org.orbisgis.core;

import org.gdms.data.DataSourceFactory;

public class OrbisgisCore {
	private static final DataSourceFactory dsf = new DataSourceFactory();

	public static DataSourceFactory getDSF() {
		return dsf;
	}
}
