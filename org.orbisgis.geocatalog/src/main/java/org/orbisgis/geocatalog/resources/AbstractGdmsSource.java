package org.orbisgis.geocatalog.resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.gdms.data.NoSuchTableException;
import org.gdms.driver.csvstring.CSVStringDriver;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.h2.H2spatialDriver;
import org.gdms.driver.hsqldb.HSQLDBDriver;
import org.gdms.driver.postgresql.PostgreSQLDriver;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.resourceTree.BasicResource;
import org.orbisgis.core.resourceTree.IResource;

public abstract class AbstractGdmsSource extends BasicResource {

	public static final String TIF = "tif";

	public static final String ASC = "asc";

	private Icon icon = null;

	private final Icon database = new ImageIcon(getClass().getResource(
			"database.png"));

	private final Icon shp_file = new ImageIcon(this.getClass().getResource(
			"shp_file.png"));

	private final Icon csv_file = new ImageIcon(this.getClass().getResource(
			"csv_file.png"));

	private final Icon dbf_file = new ImageIcon(this.getClass().getResource(
			"dbf_file.png"));

	public AbstractGdmsSource(String name) {
		super(name);
	}

	private String getDriverName(String name) {
		String driverName;
		try {
			driverName = OrbisgisCore.getDSF().getSourceManager()
					.getDriverName(name);
		} catch (NoSuchTableException e) {
			driverName = null;
		}
		return driverName;
	}

	public Icon getIcon(boolean isExpanded) {
		if (icon == null) {
			// Set the right icon
			try {
				String driverName = getDriverName(getName());
				if (ShapefileDriver.DRIVER_NAME.equalsIgnoreCase(driverName)) {
					icon = shp_file;
				} else if (CSVStringDriver.DRIVER_NAME
						.equalsIgnoreCase(driverName)) {
					icon = csv_file;
				} else if (DBFDriver.DRIVER_NAME.equalsIgnoreCase(driverName)) {
					icon = dbf_file;
				} else if ((H2spatialDriver.DRIVER_NAME
						.equalsIgnoreCase(driverName))
						|| (HSQLDBDriver.DRIVER_NAME
								.equalsIgnoreCase(driverName))
						|| (PostgreSQLDriver.DRIVER_NAME
								.equalsIgnoreCase(driverName))) {
					icon = database;
				}
			} catch (DriverLoadException e) {
				icon = null;
			}
		}

		return null;
	}

	@Override
	public void setName(String newName) {
		OrbisgisCore.getDSF().getSourceManager().rename(getName(), newName);
		updateNameTo(newName);
	}

	public void updateNameTo(String newName) {
		super.setName(newName);
	}

	@Override
	public void removeFrom(IResource parent) {
		OrbisgisCore.getDSF().getSourceManager().remove(getName());
		super.removeFrom(parent);
	}

	@Override
	public void move(IResource dropNode) {
		super.removeFrom(getParent());
		super.addTo(dropNode);
	}
}
