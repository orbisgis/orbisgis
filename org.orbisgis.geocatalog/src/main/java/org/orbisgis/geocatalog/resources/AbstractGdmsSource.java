package org.orbisgis.geocatalog.resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.gdms.data.NoSuchTableException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.OrbisgisCore;

public abstract class AbstractGdmsSource extends AbstractResourceType implements
		IResourceType {

	private Icon icon = null;

	private final Icon memory = new ImageIcon(getClass().getResource(
	"memory.png"));

	private final Icon asc_file = new ImageIcon(getClass().getResource(
	"asc_file.png"));

	private final Icon tif_file = new ImageIcon(getClass().getResource(
			"tif_file.png"));

	private final Icon h2_db = new ImageIcon(getClass().getResource("h2.png"));

	private final Icon postgis_db = new ImageIcon(getClass().getResource("postgis.png"));

	private final Icon database = new ImageIcon(getClass().getResource(
			"database.png"));

	private final Icon shp_file = new ImageIcon(this.getClass().getResource(
			"shp_file.png"));

	private final Icon csv_file = new ImageIcon(this.getClass().getResource(
			"csv_file.png"));

	private final Icon dbf_file = new ImageIcon(this.getClass().getResource(
			"dbf_file.png"));

	public AbstractGdmsSource() {
	}

	private int getSourceType(String name) {
		int type;
		try {
			type = OrbisgisCore.getDSF().getSourceManager()
					.getSourceType(name);
		} catch (NoSuchTableException e) {
			type = SourceManager.UNKNOWN;
		}
		return type;
	}

	public Icon getIcon(INode node, boolean isExpanded) {
		if (icon == null) {
			// Set the right icon
			int sourceType = getSourceType(node.getName());
			if ((sourceType & SourceManager.DB) == SourceManager.DB) {
				icon = database;
			}
			if ((sourceType & SourceManager.FILE) == SourceManager.FILE) {
				icon = database;
			}
			if ((sourceType & SourceManager.RASTER) == SourceManager.RASTER) {
				icon = database;
			}
			if ((sourceType & SourceManager.MEMORY) == SourceManager.MEMORY) {
				icon = memory;
			}

			switch (sourceType) {
			case SourceManager.ASC_GRID:
				icon = asc_file;
				break;
			case SourceManager.H2:
				icon = h2_db;
				break;
			case SourceManager.CSV:
				icon = csv_file;
				break;
			case SourceManager.DBF:
				icon = dbf_file;
				break;
			case SourceManager.SHP:
				icon = shp_file;
				break;
			case SourceManager.TFW:
				icon = tif_file;
				break;
			case SourceManager.POSTGRESQL:
				icon = postgis_db;
				break;
			}
			icon = null;
		}

		return icon;
	}

	public void removeFromTree(INode toRemove) throws ResourceTypeException {
		OrbisgisCore.getDSF().getSourceManager().remove(toRemove.getName());
		super.removeFromTree(toRemove);
	}

	public void setName(INode node, String newName)
			throws ResourceTypeException {
		OrbisgisCore.getDSF().getSourceManager()
				.rename(node.getName(), newName);
		super.setName(node, newName);
	}
}
