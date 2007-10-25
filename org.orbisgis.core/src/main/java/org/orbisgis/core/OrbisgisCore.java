package org.orbisgis.core;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;

public class OrbisgisCore {
	private static final DataSourceFactory dsf = new DataSourceFactory();

	public static DataSourceFactory getDSF() {
		return dsf;
	}

	/**
	 * Registers the source in the DataSourceFactory. If the name collides
	 * with some existing name, a derivation of it is used
	 *
	 * @param tmpName
	 * @param fileSourceDefinition
	 *
	 * @return The name used to register
	 */
	public static String registerInDSF(String name,
			DataSourceDefinition dsd) {
		String extension = FileUtility.getFileExtension(name);
		String nickname = name.substring(0, name.indexOf("." + extension));
		String tmpName = nickname;
		int i = 0;
		while (OrbisgisCore.getDSF().existDS(tmpName)) {
			i++;
			tmpName = nickname + "_" + i;
		}

		OrbisgisCore.getDSF().registerDataSource(tmpName, dsd);

		return tmpName;
	}
}
