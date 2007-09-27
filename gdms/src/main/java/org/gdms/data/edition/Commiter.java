package org.gdms.data.edition;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.FreeingResourcesException;
import org.gdms.driver.DriverException;

public interface Commiter {

	public abstract void commit(List<PhysicalDirection> rowsDirections,
			String[] fieldNames, ArrayList<EditionInfo> schemaActions,
			ArrayList<EditionInfo> editionActions,
			ArrayList<DeleteEditionInfo> deletedPKs, DataSource modifiedSource)
			throws DriverException, FreeingResourcesException;

}
