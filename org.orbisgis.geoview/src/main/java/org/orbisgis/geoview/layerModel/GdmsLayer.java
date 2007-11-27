package org.orbisgis.geoview.layerModel;

import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.source.SourceManager;
import org.gdms.sql.instruction.TableNotFoundException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.core.OrbisgisCore;

public abstract class GdmsLayer extends BasicLayer {

	private String mainName;

	public GdmsLayer(String name,
			CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		this.mainName = name;
	}

	@Override
	public void setName(String name) throws LayerException {
		SourceManager sourceManager = OrbisgisCore.getDSF().getSourceManager();

		// Remove previous alias
		if (!mainName.equals(getName())) {
			sourceManager.removeName(getName());
		}
		if (!name.equals(mainName)) {
			super.setName(name);
			try {
				sourceManager.addName(mainName, name);
			} catch (TableNotFoundException e) {
				throw new RuntimeException("bug!", e);
			} catch (SourceAlreadyExistsException e) {
				throw new LayerException("Source already exists", e);
			}
		}
	}

	public void close() throws LayerException {
		SourceManager sourceManager = OrbisgisCore.getDSF().getSourceManager();

		// Remove alias
		if (!mainName.equals(getName())) {
			sourceManager.removeName(getName());
		}
	}

}
