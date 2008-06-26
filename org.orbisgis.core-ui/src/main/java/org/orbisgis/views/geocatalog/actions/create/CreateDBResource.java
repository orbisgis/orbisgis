package org.orbisgis.views.geocatalog.actions.create;

import java.util.ArrayList;

import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.ReadWriteDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.resource.Folder;
import org.orbisgis.resource.IResource;
import org.orbisgis.views.geocatalog.Catalog;
import org.orbisgis.views.geocatalog.action.IResourceAction;

public class CreateDBResource implements IResourceAction {

	public boolean accepts(IResource resource) {
		return resource.getResourceType() instanceof Folder;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount <= 1;
	}

	public void execute(Catalog catalog, IResource selectedNode) {
		// Get the non raster writable drivers
		DataManager dm = (DataManager) Services
				.getService("org.orbisgis.DataManager");
		DriverManager driverManager = dm.getSourceManager().getDriverManager();
		String[] driverNames = driverManager.getDriverNames();
		ArrayList<String> filtered = new ArrayList<String>();
		for (String driverName : driverNames) {
			ReadOnlyDriver rod = (ReadOnlyDriver) driverManager
					.getDriver(driverName);
			if ((rod.getType() & SourceManager.RASTER) == 0) {
				if ((rod.getType() & SourceManager.DB) == SourceManager.DB) {
					if (rod instanceof ReadWriteDriver) {
						filtered.add(driverName);
					}
				}
			}
		}
		CreateFileResource.createSource(dm, driverManager, filtered);
	}
}
