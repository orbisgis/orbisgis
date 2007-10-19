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
import org.orbisgis.geocatalog.resources.BasicResource;
import org.orbisgis.geocatalog.resources.IResource;

public class Datasource extends BasicResource {

	public static final String TIF = "tif";

	public static final String ASC = "asc";

	private String driverName = null;

	private Icon icon = null;

	private final Icon database = new ImageIcon(getClass().getResource(
			"database.png"));

	private final Icon shp_file = new ImageIcon(this.getClass().getResource(
			"shp_file.png"));

	private final Icon csv_file = new ImageIcon(this.getClass().getResource(
			"csv_file.png"));

	private final Icon tif_file = new ImageIcon(this.getClass().getResource(
			"tif_file.png"));

	private final Icon asc_file = new ImageIcon(this.getClass().getResource(
			"asc_file.png"));

	private final Icon dbf_file = new ImageIcon(this.getClass().getResource(
			"dbf_file.png"));

	private final Icon openAttributesIcon = new ImageIcon(this.getClass()
			.getResource("openattributes.png"));

	public Datasource(String name, String driverName) {
		super(name);
		this.driverName = driverName;

		// Set the right icon
		icon = database;
		if (ShapefileDriver.DRIVER_NAME.equalsIgnoreCase(driverName)) {
			icon = shp_file;
		} else if (CSVStringDriver.DRIVER_NAME.equalsIgnoreCase(driverName)) {
			icon = csv_file;
		} else if (DBFDriver.DRIVER_NAME.equalsIgnoreCase(driverName)) {
			icon = dbf_file;
		} else if (ASC.equalsIgnoreCase(driverName)) {
			icon = asc_file;
		} else if (TIF.equalsIgnoreCase(driverName)) {
			icon = tif_file;
		}

	}

	// We need to add SLDLinks
	public void addChild(IResource child) {
		if (child instanceof SLDFile) {
			addChild(new SLDLink(child.getName(), (SLDFile) child), 0);
		}
	}

	public Icon getIcon(boolean isExpanded) {
		return icon;
	}

	public JMenuItem[] getPopupActions() {
		JMenuItem[] items = new JMenuItem[1];

		items[0] = getOpenAttributes();

		return items;
	}

	public String getDriverName() {
		return driverName;
	}

	private JMenuItem getOpenAttributes() {
		JMenuItem menuItem = new JMenuItem("Open attributes");
		menuItem.setIcon(openAttributesIcon);
		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					OrbisgisCore.getDSF().executeSQL("call SHOW('select * from "
							+ getName() + "' , ' " + getName() + " ')");
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
