package org.orbisgis.plugin.view.ui.workbench.geocatalog;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.gdms.driver.csvstring.CSVStringDriver;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.driver.h2.H2spatialDriver;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.driver.shapefile.ShapefileDriver;

/**
 * This is the renderer for GeoCatalog Tree. It manages the icons of each kind
 * of node. To add an icon, 1)declare it with the others class fields. 2)add a
 * if (condition) in the getIcon() method.
 * 
 * @author Samuel CHEMLA
 * 
 */
public class CatalogRenderer extends DefaultTreeCellRenderer {

	private Icon folder = new ImageIcon(this.getClass().getResource(
			"folder.png"));

	private Icon open_folder = new ImageIcon(this.getClass().getResource(
			"open_folder.png"));

	private Icon datasource = new ImageIcon(this.getClass().getResource(
			"datasource.png"));

	private Icon sldfile = new ImageIcon(this.getClass().getResource(
			"sldStyle.png"));

	private Icon sqlquery = new ImageIcon(this.getClass().getResource(
			"sqlquery.png"));

	private Icon sldlink = new ImageIcon(this.getClass().getResource(
			"sldlink.png"));

	private Icon shpfile = new ImageIcon(this.getClass().getResource(
			"shp_file.png"));

	private Icon csvfile = new ImageIcon(this.getClass().getResource(
			"csv_file.png"));

	private Icon tiffile = new ImageIcon(this.getClass().getResource(
			"tif_file.png"));

	private Icon ascfile = new ImageIcon(this.getClass().getResource(
			"asc_file.png"));

	private Icon dbffile = new ImageIcon(this.getClass().getResource(
			"dbf_file.png"));

	private Icon h2db = new ImageIcon(this.getClass().getResource("h2.png"));

	private Icon memory = new ImageIcon(this.getClass().getResource(
			"memory.png"));

	public CatalogRenderer() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		MyNode myNode = (MyNode) value;
		setIcon(getIcon(myNode, expanded, leaf));
		return this;
	}

	/**
	 * Returns the right icon for myNode
	 * 
	 * @param myNode
	 * @param expanded
	 * @param leaf
	 * @return
	 */
	public Icon getIcon(MyNode myNode, boolean expanded, boolean leaf) {
		Icon icon = null;
		int type = myNode.getType();
		switch (type) {

		case MyNode.folder:
			if (leaf) {
				icon = open_folder;
			} else if (!expanded) {
				icon = folder;
			} else
				icon = open_folder;
			break;

		case MyNode.datasource:
			icon = datasource;
			if (ShapefileDriver.DRIVER_NAME.equalsIgnoreCase(myNode
					.getDriverName())) {
				icon = shpfile;
			} else if (CSVStringDriver.DRIVER_NAME.equalsIgnoreCase(myNode
					.getDriverName())) {
				icon = csvfile;
			} else if (DBFDriver.DRIVER_NAME.equalsIgnoreCase(myNode
					.getDriverName())) {
				icon = dbffile;
			} else if (ObjectMemoryDriver.DRIVER_NAME.equalsIgnoreCase(myNode
					.getDriverName())) {
				icon = memory;
			} else if (H2spatialDriver.DRIVER_NAME.equalsIgnoreCase(myNode
					.getDriverName())) {
				icon = h2db;
			}
			break;

		case MyNode.sldfile:
			icon = sldfile;
			break;

		case MyNode.sldlink:
			icon = sldlink;
			break;

		case MyNode.sqlquery:
			icon = sqlquery;
			break;

		case MyNode.raster:
			if (Catalog.ASC.equalsIgnoreCase(myNode.getDriverName())) {
				icon = ascfile;
			} else if (Catalog.TIF.equalsIgnoreCase(myNode.getDriverName())) {
				icon = tiffile;
			}
			break;

		default:

		}
		return icon;
	}

}
