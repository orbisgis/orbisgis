package org.orbisgis.geocatalog.resources;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.csvstring.CSVStringDriver;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.orbisgis.core.OrbisgisCore;

public class GdmsSource extends BasicResource {

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

	public GdmsSource(String name) {
		super(name);

		// Set the right icon
		String driverName = getDriverName(name);
		icon = null;
		if (ShapefileDriver.DRIVER_NAME.equalsIgnoreCase(driverName)) {
			icon = shp_file;
		} else if (CSVStringDriver.DRIVER_NAME.equalsIgnoreCase(driverName)) {
			icon = csv_file;
		} else if (DBFDriver.DRIVER_NAME.equalsIgnoreCase(driverName)) {
			icon = dbf_file;
		}

	}

	private String getDriverName(String name) {
		String driverName;
		try {
			driverName = OrbisgisCore.getDSF().getDriver(name);
		} catch (NoSuchTableException e) {
			driverName = null;
		}
		return driverName;
	}

	public Icon getIcon(boolean isExpanded) {
		return icon;
	}

	public JMenuItem[] getPopupActions() {
		JMenuItem[] items = new JMenuItem[1];

		items[0] = getOpenAttributes();

		return items;
	}

	private JMenuItem getOpenAttributes() {
		JMenuItem menuItem = new JMenuItem("Open attributes");
		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					OrbisgisCore.getDSF().executeSQL(
							"call SHOW('select * from " + getName() + "' , ' "
									+ getName() + " ')");
				} catch (SyntaxException e1) {
					e1.printStackTrace();
				} catch (DriverLoadException e1) {
					e1.printStackTrace();
				} catch (NoSuchTableException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}

			}

		});
		return menuItem;
	}

}
